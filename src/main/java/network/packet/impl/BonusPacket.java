package network.packet.impl;

import network.packet.Packet;
import network.packet.enums.PacketType;

import java.nio.ByteBuffer;

public class BonusPacket implements Packet {
    private final int x;
    private final int y;
    private final boolean isActive;
    private final int type; // 0 - сердце

    public BonusPacket(int x, int y, boolean isActive, int type) {
        this.x = x;
        this.y = y;
        this.isActive = isActive;
        this.type = type;
    }

    public BonusPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Пропускаем тип пакета
        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.isActive = buffer.get() == 1;
        this.type = buffer.getInt();
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(14); // 1 + 4 + 4 + 1 + 4
        buffer.put((byte) PacketType.BONUS.ordinal());
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.put((byte) (isActive ? 1 : 0));
        buffer.putInt(type);
        return buffer.array();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isActive() { return isActive; }
    public int getType() { return type; }
} 