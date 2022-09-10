package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;

public class RenderWorldLastEvent extends ClientEvent {
    private final float partialTicks;

    public RenderWorldLastEvent(float partialTicks){
        this.partialTicks = partialTicks;
        setName("RenderWorldLastEvent");
    }

    public float getPartialTicks(){
        return this.partialTicks;
    }

}
