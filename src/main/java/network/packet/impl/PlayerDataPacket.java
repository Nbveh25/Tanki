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
    private final int health;

    public PlayerDataPacket(int x, int y, Direction direction, int spriteNum, boolean isDead, String name, int health) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.isDead = isDead;
        this.name = name;
        this.health = health;
    }

    public PlayerDataPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Skip packet type
        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.direction = Direction.values()[buffer.get()];
        this.spriteNum = buffer.get();
        this.isDead = buffer.get() == 1;
        
        byte[] nameBytes = new byte[buffer.get()];
        buffer.get(nameBytes);
        this.name = new String(nameBytes, StandardCharsets.UTF_8);
        
        this.health = buffer.getInt();
    }

    @Override
    public byte[] getData() {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(1 + 4 + 4 + 1 + 1 + 1 + 1 + nameBytes.length + 4);
        
        buffer.put((byte) PacketType.PLAYER_DATA.ordinal());
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.put((byte) direction.ordinal());
        buffer.put((byte) spriteNum);
        buffer.put((byte) (isDead ? 1 : 0));
        buffer.put((byte) nameBytes.length);
        buffer.put(nameBytes);
        buffer.putInt(health);
        
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
        return isDead;
    }

    public String getName() {
        return name;
    }
    
    public int getHealth() {
        return health;
    }
}