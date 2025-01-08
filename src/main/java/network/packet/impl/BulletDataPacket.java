package network.packet.impl;

import enums.Direction;
import network.packet.Packet;
import network.packet.enums.PacketType;

import java.nio.ByteBuffer;

public class BulletDataPacket implements Packet {
    private final int x;
    private final int y;
    private final Direction direction;

    public BulletDataPacket(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public BulletDataPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Пропускаем тип пакета
        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.direction = Direction.values()[buffer.get()];
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(10); // 1 + 4 + 4 + 1
        buffer.put((byte) PacketType.BULLET_DATA.ordinal());
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.put((byte) direction.ordinal());
        return buffer.array();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDirection() { return direction; }
}
