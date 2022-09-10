package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;


public class DamageBlockEvent extends ClientEvent {
    private BlockPos pos;

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    private EnumFacing facing;
    public DamageBlockEvent(BlockPos pos, EnumFacing facing){
        this.facing = facing;
        this.pos = pos;
        setName("DamageBlockEvent");
    }

}
