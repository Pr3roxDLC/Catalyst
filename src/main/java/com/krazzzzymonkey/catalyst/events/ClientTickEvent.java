package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientTickEvent extends ClientEvent {

    private final TickEvent.Phase phase;

    public TickEvent.Phase getPhase() {
        return this.phase;
    }

    public ClientTickEvent(TickEvent.Phase phase) {
        this.phase = phase;
        setName("ClientTickEvent");
    }

}
