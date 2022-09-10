package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;

public class ClientEvent extends Event {

    private String name = "";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
