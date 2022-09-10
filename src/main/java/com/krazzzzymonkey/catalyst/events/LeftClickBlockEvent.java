package com.krazzzzymonkey.catalyst.events;

import dev.tigr.simpleevents.event.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LeftClickBlockEvent extends ClientEvent {
    private final EntityPlayer player;
    private final BlockPos pos;
    private final EnumFacing face;
    private final Vec3d hitVec;

    public LeftClickBlockEvent(EntityPlayer player, BlockPos pos, EnumFacing face, Vec3d hitVec) {
        this.player = player;
        this.pos = pos;
        this.face = face;
        this.hitVec = hitVec;
        setName("LeftClickBlockEvent");
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getFace() {
        return face;
    }

    public Vec3d getHitVec() {
        return hitVec;
    }
}
