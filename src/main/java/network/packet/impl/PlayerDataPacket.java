package network.packet.impl;

import enums.Direction;
import network.packet.Packet;
import network.packet.enums.PacketType;

import java.nio.ByteBuffer;

public class PlayerDataPacket implements Packet {
    private final int x;
    private final int y;
    private final Direction direction;
    private final int spriteNum;

    public PlayerDataPacket(int x, int y, Direction direction, int spriteNum) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
    }

    public PlayerDataPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Пропускаем тип пакета
        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.direction = Direction.values()[buffer.get()];
        this.spriteNum = buffer.getInt();
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(14); // 1 + 4 + 4 + 1 + 4
        buffer.put((byte) PacketType.PLAYER_DATA.ordinal());
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.put((byte) direction.ordinal());
        buffer.putInt(spriteNum);
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
}