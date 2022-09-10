package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;
import net.minecraftforge.event.world.NoteBlockEvent;

public class PlayerUpdateEvent extends ClientEvent {

    public PlayerUpdateEvent(){
        setName("PlayerUpdateEvent");
    }

}
