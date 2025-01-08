package network.packet.impl;

import network.packet.Packet;
import network.packet.enums.PacketType;

public class DisconnectPacket implements Packet {
    @Override
    public byte[] getData() {
        return new byte[] { (byte) PacketType.DISCONNECT.ordinal() };
    }
} 