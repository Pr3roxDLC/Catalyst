package com.krazzzzymonkey.catalyst.events;


import dev.tigr.simpleevents.event.Event;

public class StopUsingItemEvent extends ClientEvent {
    private boolean packet = false;

    public StopUsingItemEvent(){
        setName("StopUsingItemEvent");
    }
    public boolean isPacket() {
        return packet;
    }

    public void setPacket(boolean packet) {
        this.packet = packet;
    }
}
