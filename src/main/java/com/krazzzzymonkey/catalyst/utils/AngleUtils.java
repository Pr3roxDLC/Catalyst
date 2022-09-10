package com.krazzzzymonkey.catalyst.utils;

import com.krazzzzymonkey.catalyst.module.modules.combat.AutoCrystal;
import com.krazzzzymonkey.catalyst.module.modules.combat.AutoCrystalRewrite;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AngleUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();


    public static float[] calculateAngles(Entity entity) {
        return calculateAngle(InterpolationUtils.interpolateEntityTime(mc.player, mc.getRenderPartialTicks()), InterpolationUtils.interpolateEntityTime(entity, mc.getRenderPartialTicks()));
    }

    public static float[] calculateAngles(BlockPos blockPos) {
        return calculateAngle(InterpolationUtils.interpolateEntityTime(mc.player, mc.getRenderPartialTicks()), new Vec3d(blockPos));
    }

    public static float[] calculateCenter(Entity entity) {
        return calculateAngle(InterpolationUtils.interpolateEntityTime(mc.player, mc.getRenderPartialTicks()), InterpolationUtils.interpolateEntityTime(entity, mc.getRenderPartialTicks()).add(new Vec3d(entity.width / 2, entity.getEyeHeight() / 2, entity.width / 2)));
    }

    public static float[] calculateCenter(BlockPos blockPos) {
        return calculateAngle(InterpolationUtils.interpolateEntityTime(mc.player, mc.getRenderPartialTicks()), new Vec3d(blockPos).add(new Vec3d(0.5, 0, 0.5)));
    }

    public static float[] calculateAngle(Vec3d from, Vec3d to) {
        return new float[] {
            (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(to.z - from.z, to.x - from.x)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2((to.y - from.y) * -1.0, MathHelper.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.z - from.z, 2)))))
        };
    }

    public static Vec3d getVectorForRotation(AutoCrystalRewrite.Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static float calculateAngleDifference(float serverValue, float currentValue, double divisions, int steps) {
        return (float) (serverValue - currentValue / (divisions * steps));
    }

    public static float calculateAngleDifference(float direction, float rotationYaw) {
        float phi = Math.abs(rotationYaw - direction) % 360.0f;
        return (phi > 180.0f) ? (360.0f - phi) : phi;
    }

}
