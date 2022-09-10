package com.krazzzzymonkey.catalyst.utils;

import net.minecraft.client.Minecraft;

public class MovementUtil {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static float getPlayerSpeedNoY() {
        return (float) Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

    public static boolean isMoving() {
        return mc.player != null && (mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f);
    }

    public static boolean hasMotion() {
        return mc.player.motionX != 0.0 && mc.player.motionZ != 0.0 && mc.player.motionY != 0.0;
    }


    public static void strafe() {
        if (!isMoving()) return;
        double yaw = getDirection();
        mc.player.motionX = -Math.sin(yaw) * getPlayerSpeedNoY();
        mc.player.motionZ = Math.cos(yaw) * getPlayerSpeedNoY();
    }


    public static void portForward(double length) {
        double yaw = Math.toRadians(mc.player.rotationYaw);
        mc.player.setPosition(mc.player.posX + Math.sin(yaw) * length, mc.player.posY, mc.player.posZ + Math.cos(yaw) * length);
    }


    public static double[] directionSpeed(final double speed) {
        final Minecraft mc = Minecraft.getMinecraft();
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }

    public static double getDirection() {
        float yaw = mc.player.rotationYaw;
        if (mc.player.rotationYaw < 0f) yaw += 180f;
        float forward = 1f;
        if (mc.player.moveForward < 0f) {
            forward = -0.5f;
        } else if (mc.player.moveForward > 0f) {
            forward = 0.5f;
        }
        if (mc.player.moveStrafing > 0f) yaw -= 90f * forward;
        if (mc.player.moveStrafing < 0f) yaw += 90f * forward;
        return Math.toRadians(yaw);
    }

}
