package network;

import config.NetworkConfig;
import network.packet.impl.BulletDataPacket;
import network.packet.impl.PlayerDataPacket;
import network.packet.enums.PacketType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;

public class GameServer implements Runnable {
    private final DatagramSocket socket;
    private final byte[] receiveData;
    private final ConcurrentHashMap<String, ClientInfo> clients;
    private boolean running;

    public GameServer() throws IOException {
        System.out.println("Starting server on port " + NetworkConfig.SERVER_PORT);
        this.socket = new DatagramSocket(NetworkConfig.SERVER_PORT);
        this.receiveData = new byte[NetworkConfig.BUFFER_SIZE];
        this.clients = new ConcurrentHashMap<>();
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Server is running...");
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

    private void handlePacket(DatagramPacket packet) throws IOException {
        byte[] data = packet.getData();
        PacketType type = PacketType.values()[data[0]];
        String clientId = packet.getAddress().getHostAddress() + ":" + packet.getPort();

        //System.out.println("Received packet type: " + type + " from " + clientId);

        switch (type) {
            case BULLET_DATA -> {
                //BulletDataPacket bulletPacket = new BulletDataPacket(data);
                broadcastToOtherClients(packet, clientId);
            }
            case PLAYER_DATA -> {
                //PlayerDataPacket posPacket = new PlayerDataPacket(data);
                broadcastToOtherClients(packet, clientId);
            }
            case CONNECT -> {
                // Добавляем нового клиента
                ClientInfo clientInfo = new ClientInfo(packet.getAddress(), packet.getPort());
                clients.put(clientId, clientInfo);
                System.out.println("New client connected: " + clientId);
                // Отправляем подтверждение подключения
                sendConnectionConfirmation(packet.getAddress(), packet.getPort());
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
                System.out.println("Broadcasting to client: " + entry.getKey());
            }
        }
    }

    public void stop() {
        running = false;
        socket.close();
        System.out.println("Server stopped");
    }

    private record ClientInfo(InetAddress address, int port) {}
}