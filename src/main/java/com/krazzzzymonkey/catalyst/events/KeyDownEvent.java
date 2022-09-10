package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;

public class KeyDownEvent extends ClientEvent {

    private final int keyId;

    public KeyDownEvent(int keyId) {
        this.keyId = keyId;
        setName("KeyDownEvent");
    }

    public int getKeyId() {
        return keyId;
    }

}
