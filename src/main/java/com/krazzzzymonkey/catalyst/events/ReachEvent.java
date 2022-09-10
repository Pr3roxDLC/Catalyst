package com.krazzzzymonkey.catalyst.events;


import dev.tigr.simpleevents.event.Event;

public class ReachEvent extends ClientEvent {
    public float distance;

    public ReachEvent(float distance) {
        this.distance = distance;
        setName("ReachEvent");
    }
}
