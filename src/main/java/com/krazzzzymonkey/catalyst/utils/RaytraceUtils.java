package com.krazzzzymonkey.catalyst.utils;

import com.krazzzzymonkey.catalyst.module.modules.combat.AutoCrystal;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RaytraceUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean raytraceBlock(BlockPos blockPos, ModeValue raytrace) {
       // NONE(-1), BASE(0.5), NORMAL(1.5), DOUBLE(2.5), TRIPLE(3.5);
        if(raytrace.getMode("None").isToggled()){
            return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() -1, blockPos.getZ() + 0.5), false, true, false) != null;
        }else if(raytrace.getMode("Base").isToggled()){
            return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), false, true, false) != null;
        } else if(raytrace.getMode("Normal").isToggled()){
            return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5), false, true, false) != null;
        }  else if (raytrace.getMode("Double").isToggled()){
            return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 2.5, blockPos.getZ() + 0.5), false, true, false) != null;
        }  else if (raytrace.getMode("Triple").isToggled()){
            return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 3.5, blockPos.getZ() + 0.5), false, true, false) != null;
        }
        return false;
    }

    public static boolean raytraceEntity(Entity entity, double offset) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entity.posX, entity.posY + offset, entity.posZ), false, true, false) == null;
    }
}
