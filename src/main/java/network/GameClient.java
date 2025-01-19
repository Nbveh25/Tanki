package network;

import config.NetworkConfig;
import entity.Bullet;
import entity.Player;
import manager.ObjectManager;
import manager.TileManager;
import network.packet.Packet;
import network.packet.impl.ConnectPacket;
import network.packet.impl.DisconnectPacket;
import network.packet.impl.PlayerDataPacket;
import network.packet.impl.BulletDataPacket;
import network.packet.impl.BonusPacket;
import network.packet.enums.PacketType;
import object.OBJ_Heart;
import util.CollisionChecker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameClient implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress serverAddress;
    private final byte[] receiveData;
    private boolean running;
    private boolean connected;
    private final CollisionChecker collisionChecker;

    private final Player localPlayer;
    private final Map<String, Player> otherPlayers;
    private final ObjectManager objectManager;

    public GameClient(String serverIp, Player localPlayer) throws IOException {
        System.out.println("Initializing client, connecting to server: " + serverIp);
        this.socket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverIp);
        this.receiveData = new byte[NetworkConfig.BUFFER_SIZE];
        this.otherPlayers = new ConcurrentHashMap<>();
        this.localPlayer = localPlayer;
        this.running = true;
        this.connected = false;
        this.collisionChecker = new CollisionChecker();
        this.objectManager = new ObjectManager(this);

        // Отправляем пакет подключения
        sendConnectPacket();
    }

    private void sendConnectPacket() {
        try {
            System.out.println("Sending connect packet to server...");
            sendPacket(new ConnectPacket());
        } catch (IOException e) {
            System.err.println("Failed to send connect packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Client is running...");
        while (running) {
            try {
                Arrays.fill(receiveData, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );
                socket.receive(receivePacket);
                handlePacket(receivePacket);
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error receiving packet: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePacket(DatagramPacket packet) {
        byte[] data = packet.getData();
        PacketType type = PacketType.values()[data[0]];
        String playerId = packet.getAddress().getHostAddress() + ":" + packet.getPort();

        switch (type) {
            case PLAYER_DATA -> {
                PlayerDataPacket posPacket = new PlayerDataPacket(data);
                updatePlayerPosition(playerId, posPacket);
            }
            case BULLET_DATA -> {
                BulletDataPacket bulletPacket = new BulletDataPacket(data);
                handleBulletData(playerId, bulletPacket);
            }
            case CONNECT_CONFIRM -> {
                connected = true;
                System.out.println("Connected to server successfully!");
            }
            case BONUS -> {
                BonusPacket bonusPacket = new BonusPacket(data);
                handleBonus(bonusPacket);
            }
        }
    }

    private void updatePlayerPosition(String playerId, PlayerDataPacket posPacket) {
        Player player = otherPlayers.computeIfAbsent(playerId, k -> 
            new Player(null, collisionChecker, posPacket.getName())
        );
        player.updatePlayer(
            posPacket.getX(),
            posPacket.getY(),
            posPacket.getDirection(),
            posPacket.getSpriteNum(),
            posPacket.getHealth()
        );
        player.setName(posPacket.getName());
        player.updateState(posPacket.isDead());

        // Обновляем пули этого игрока с корректным CollisionChecker
        player.getBullets().forEach(bullet -> {
            bullet.update(collisionChecker);

            // Проверяем коллизии с локальным игроком
            if (bullet.isActive() && collisionChecker.checkBulletPlayerCollision(bullet, localPlayer.getWorldSolidArea())) {
                bullet.setActive(false);
                // Здесь можно добавить логику урона по игроку
                localPlayer.takeDamage(bullet.getDamage());
                System.out.println("Health: " + localPlayer.getHealth());
            }

            // Проверяем коллизии с другими игроками
            otherPlayers.forEach((otherId, otherPlayer) -> {
                if (!otherId.equals(playerId) && // Не проверяем владельца пули
                        bullet.isActive() &&
                        collisionChecker.checkBulletPlayerCollision(bullet, otherPlayer.getWorldSolidArea())) {
                    bullet.setActive(false);
                    // Здесь можно добавить логику урона по игроку
                }
            });
        });

        // Удаляем неактивные пули
        player.getBullets().removeIf(bullet -> !bullet.isActive());
    }

    public void sendPlayerPosition() {
        if (!connected) {
            System.out.println("Not connected to server, attempting to reconnect...");
            sendConnectPacket();
            return;
        }

        try {
            PlayerDataPacket packet = new PlayerDataPacket(
                localPlayer.getWorldX(),
                localPlayer.getWorldY(),
                localPlayer.getDirection(),
                localPlayer.getSpriteNum(),
                localPlayer.isDead(),
                localPlayer.getName(),
                localPlayer.getHealth()
            );
            sendPacket(packet);

            // Отправляем данные о пулях и проверяем коллизии
            for (Bullet bullet : localPlayer.getBullets()) {
                if (bullet.isActive()) {
                    // Проверяем коллизии с другими игроками
                    otherPlayers.values().forEach(otherPlayer -> {
                        if (collisionChecker.checkBulletPlayerCollision(bullet, otherPlayer.getWorldSolidArea()) && !otherPlayer.isDead()) {
                            bullet.setActive(false);
                        }
                    });

                    if (bullet.isActive()) {
                        BulletDataPacket bulletPacket = new BulletDataPacket(
                            bullet.getWorldX(),
                            bullet.getWorldY(),
                            bullet.getDirection()
                        );
                        sendPacket(bulletPacket);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to send player position: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendPacket(Packet packet) throws IOException {
        byte[] data = packet.getData();
        DatagramPacket sendPacket = new DatagramPacket(
                data,
                data.length,
                serverAddress,
                NetworkConfig.SERVER_PORT
        );
        socket.send(sendPacket);
    }

    public void stop() {
        try {
            sendPacket(new DisconnectPacket());
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
        socket.close();
        System.out.println("Client stopped");
    }

    public Map<String, Player> getOtherPlayers() {
        return otherPlayers;
    }

    private void handleBulletData(String playerId, BulletDataPacket packet) {
        Player player = otherPlayers.get(playerId);
        if (player != null) {
            // Проверяем, нет ли уже такой пули
            boolean bulletExists = player.getBullets().stream()
                    .anyMatch(b -> b.getWorldX() == packet.getX() &&
                            b.getWorldY() == packet.getY() &&
                            b.getDirection() == packet.getDirection());

            if (!bulletExists) {
                player.addBullet(packet.getX(), packet.getY(), packet.getDirection());
            }
        }
    }

    public void sendBonusPickup(int x, int y) {
        if (!connected) return;
        
        try {
            BonusPacket packet = new BonusPacket(x, y, false, 0); // isActive = false означает, что бонус подобран
            sendPacket(packet);
            System.out.println("Sent bonus pickup at: " + x + ", " + y);
        } catch (IOException e) {
            System.err.println("Failed to send bonus pickup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleBonus(BonusPacket packet) {
        if (packet.getType() == 0) { // Сердце
            if (packet.isActive()) {
                // Создаем новое сердце
                OBJ_Heart heart = new OBJ_Heart();
                heart.worldX = packet.getX();
                heart.worldY = packet.getY();
                heart.isActive = true;
                objectManager.addHeart(heart);
                System.out.println("Received heart at: " + packet.getX() + ", " + packet.getY());
            } else {
                // Деактивируем существующее сердце
                objectManager.deactivateHeart(packet.getX(), packet.getY());
                System.out.println("Deactivated heart at: " + packet.getX() + ", " + packet.getY());
            }
        }
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }
}