package network;

import config.GameConfig;
import config.NetworkConfig;
import network.packet.Packet;
import network.packet.impl.BulletDataPacket;
import network.packet.impl.PlayerDataPacket;
import network.packet.impl.BonusPacket;
import network.packet.enums.PacketType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameServer implements Runnable {
    private static final int MAX_BONUSES = 8;
    private final DatagramSocket socket;
    private final byte[] receiveData;
    private final ConcurrentHashMap<String, ClientInfo> clients;
    private boolean running;
    private final List<BonusInfo> bonuses;

    public GameServer() throws IOException {
        System.out.println("Starting server on port " + NetworkConfig.SERVER_PORT);
        this.socket = new DatagramSocket(NetworkConfig.SERVER_PORT);
        this.receiveData = new byte[NetworkConfig.BUFFER_SIZE];
        this.clients = new ConcurrentHashMap<>();
        this.bonuses = new ArrayList<>();
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Server is running...");
        startBonusSpawnTimer();
        while (running) {
            try {
                Arrays.fill(receiveData, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );
                socket.receive(receivePacket);

                // Обработка входящего пакета
                handlePacket(receivePacket);

            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startBonusSpawnTimer() {
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(10000); // Каждые 10 секунд
                    // Очищаем список от неактивных бонусов
                    bonuses.removeIf(bonus -> !bonus.isActive);
                    // Спавним новые бонусы
                    for(int i = 0; i < 3 && bonuses.size() < MAX_BONUSES; i++) {
                        spawnBonus();
                    }
                } catch (InterruptedException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void spawnBonus() {
        if (clients.isEmpty()) return;
        
        // Проверяем количество активных бонусов
        long activeCount = bonuses.stream().filter(b -> b.isActive).count();
        if (activeCount >= MAX_BONUSES) {
            return; // Не спавним новые бонусы, если достигнут лимит
        }

        // Спавним бонус в случайной позиции на карте
        Random random = new Random();
        int x = random.nextInt(GameConfig.MAX_WORLD_COL) * GameConfig.TILE_SIZE;
        int y = random.nextInt(GameConfig.MAX_WORLD_ROW) * GameConfig.TILE_SIZE;

        BonusInfo bonus = new BonusInfo(x, y);
        bonuses.add(bonus);

        // Отправляем всем клиентам
        BonusPacket packet = new BonusPacket(x, y, true, 0);
        broadcastPacket(packet);
        System.out.println("Spawned bonus at: " + x + ", " + y + " (tile: " + x/GameConfig.TILE_SIZE + ", " + y/GameConfig.TILE_SIZE + ")");
        System.out.println("Active bonuses: " + (activeCount + 1) + "/" + MAX_BONUSES);
    }

    private void broadcastPacket(Packet packet) {
        for (ClientInfo client : clients.values()) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(
                    packet.getData(),
                    packet.getData().length,
                    client.address(),
                    client.port()
                );
                socket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePacket(DatagramPacket packet) throws IOException {
        byte[] data = packet.getData();
        PacketType type = PacketType.values()[data[0]];
        String clientId = packet.getAddress().getHostAddress() + ":" + packet.getPort();

        switch (type) {
            case BULLET_DATA, PLAYER_DATA, BONUS -> {
                broadcastToOtherClients(packet, clientId);
                
                // Если это пакет о подборе бонуса, обновляем состояние на сервере
                if (type == PacketType.BONUS) {
                    BonusPacket bonusPacket = new BonusPacket(data);
                    if (!bonusPacket.isActive()) {
                        // Находим и деактивируем бонус в списке
                        for (BonusInfo bonus : bonuses) {
                            if (bonus.x == bonusPacket.getX() && bonus.y == bonusPacket.getY()) {
                                bonus.isActive = false;
                                break;
                            }
                        }
                    }
                }
            }
            case CONNECT -> {
                // Добавляем нового клиента
                ClientInfo clientInfo = new ClientInfo(packet.getAddress(), packet.getPort());
                clients.put(clientId, clientInfo);
                System.out.println("New client connected: " + clientId);
                // Отправляем подтверждение подключения
                sendConnectionConfirmation(packet.getAddress(), packet.getPort());
                
                // Отправляем информацию о существующих бонусах
                for (BonusInfo bonus : bonuses) {
                    if (bonus.isActive) {
                        BonusPacket bonusPacket = new BonusPacket(
                            bonus.x, bonus.y, true, 0
                        );
                        DatagramPacket bonusDataPacket = new DatagramPacket(
                            bonusPacket.getData(),
                            bonusPacket.getData().length,
                            packet.getAddress(),
                            packet.getPort()
                        );
                        socket.send(bonusDataPacket);
                    }
                }
            }
            case DISCONNECT -> {
                clients.remove(clientId);
                System.out.println("Client disconnected: " + clientId);
            }
        }
    }

    private void sendConnectionConfirmation(InetAddress address, int port) throws IOException {
        byte[] confirmData = new byte[]{(byte) PacketType.CONNECT_CONFIRM.ordinal()};
        DatagramPacket confirmPacket = new DatagramPacket(
                confirmData,
                confirmData.length,
                address,
                port
        );
        socket.send(confirmPacket);
        System.out.println("Sent connection confirmation to " + address + ":" + port);
    }

    private void broadcastToOtherClients(DatagramPacket sourcePacket, String sourceClientId) throws IOException {
        for (var entry : clients.entrySet()) {
            if (!entry.getKey().equals(sourceClientId)) {
                ClientInfo client = entry.getValue();
                DatagramPacket broadcastPacket = new DatagramPacket(
                        sourcePacket.getData(),
                        sourcePacket.getLength(),
                        client.address(),
                        client.port()
                );
                socket.send(broadcastPacket);
                //System.out.println("Broadcasting to client: " + entry.getKey());
            }
        }
    }

    public void stop() {
        running = false;
        socket.close();
        System.out.println("Server stopped");
    }

    private record ClientInfo(InetAddress address, int port) {}

    private static class BonusInfo {
        final int x;
        final int y;
        boolean isActive;

        BonusInfo(int x, int y) {
            this.x = x;
            this.y = y;
            this.isActive = true;
        }
    }
}