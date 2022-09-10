package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;
import net.minecraft.entity.MoverType;

public class PlayerMoveEvent extends ClientEvent {

    public MoverType type;
    public double x,y,z = 0;

    public PlayerMoveEvent(MoverType type, double x , double y, double z){
            this.type = type;
            this.x = x;
            this.z = z;
            this.y = y;
            setName("PlayerMoveEvent");
    }

}
