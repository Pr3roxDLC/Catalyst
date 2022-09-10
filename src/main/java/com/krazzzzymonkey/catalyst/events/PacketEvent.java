package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;
import net.minecraft.network.Packet;


public class PacketEvent extends ClientEvent {

    private final Packet<?> packet;
    private final Side side;


    public PacketEvent(Packet<?> packet, Side side) {
        this.packet = packet;
        this.side = side;
        setName("PacketEvent");
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public Side getSide() {
        return side;
    }


    public enum Side {
        IN,
        OUT
    }
}
