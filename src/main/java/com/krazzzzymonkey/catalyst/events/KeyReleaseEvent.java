package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;

public class KeyReleaseEvent extends ClientEvent {
    private final int key;

    public KeyReleaseEvent(int key) {
        this.key = key;
        setName("KeyReleaseEvent");
    }

    public int getKey() {
        return this.key;
    }
}
