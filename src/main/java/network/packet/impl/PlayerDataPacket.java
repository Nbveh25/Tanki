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
    private final boolean isDead;

    public PlayerDataPacket(int x, int y, Direction direction, int spriteNum, boolean isDead) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.isDead = isDead;
    }

    public PlayerDataPacket(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get(); // Пропускаем тип пакета
        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.direction = Direction.values()[buffer.get()];
        this.spriteNum = buffer.getInt();
        this.isDead = buffer.get() == 1; // Читаем состояние мертвого игрока
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(15); // 1 + 4 + 4 + 1 + 4 + 1
        buffer.put((byte) PacketType.PLAYER_DATA.ordinal());
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.put((byte) direction.ordinal());
        buffer.putInt(spriteNum);
        buffer.put((byte) (isDead ? 1 : 0)); // Записываем состояние мертвого игрока
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
}