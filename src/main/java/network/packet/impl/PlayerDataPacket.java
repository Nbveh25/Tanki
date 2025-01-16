package network.packet.impl;

import enums.Direction;
import network.packet.Packet;
import network.packet.enums.PacketType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PlayerDataPacket implements Packet {
    private final int x;
    private final int y;
    private final Direction direction;
    private final int spriteNum;
    private final boolean isDead;
    private final String name;

    public PlayerDataPacket(int x, int y, Direction direction, int spriteNum, boolean isDead, String name) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.isDead = isDead;
        this.name = name;
    }

    public PlayerDataPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Пропускаем тип пакета
        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.direction = Direction.values()[buffer.get()];
        this.spriteNum = buffer.getInt();
        this.isDead = buffer.get() == 1; // Читаем состояние мертвого игрока
        
        // Читаем имя
        byte[] nameBytes = new byte[buffer.get()];
        buffer.get(nameBytes);
        this.name = new String(nameBytes, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] getData() {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(16 + nameBytes.length);
        buffer.put((byte) PacketType.PLAYER_DATA.ordinal());
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.put((byte) direction.ordinal());
        buffer.putInt(spriteNum);
        buffer.put((byte) (isDead ? 1 : 0)); // Записываем состояние мертвого игрока
        buffer.put((byte) nameBytes.length);
        buffer.put(nameBytes);
        return buffer.array();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSpriteNum() {
        return spriteNum;
    }

    public boolean isDead() {
        return isDead; // Метод для получения состояния мертвого игрока
    }

    public String getName() {
        return name;
    }
}