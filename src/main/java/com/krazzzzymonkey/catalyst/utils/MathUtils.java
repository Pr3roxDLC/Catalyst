package com.krazzzzymonkey.catalyst.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector2f;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
    public static boolean isPointInQuad(Vector2f pointP, Vector2f pointA, Vector2f pointB, Vector2f pointC, Vector2f pointD) {
        float apd = getTriangleArea(pointA, pointP, pointD);
        float dpc = getTriangleArea(pointD, pointP, pointC);
        float cpb = getTriangleArea(pointC, pointP, pointB);
        float pba = getTriangleArea(pointP, pointB, pointA);

        float rectArea = getQuadArea(pointA, pointB, pointC);

        float sum = apd + dpc + cpb + pba;

        return !(sum > rectArea);
    }

    public static double square(float input) {
        return input * input;
    }


    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static float getQuadArea(Vector2f pointA, Vector2f pointB, Vector2f pointC) {
        Vector2f abVec = (Vector2f) pointB.clone();
        abVec.sub(pointA);

        float ab = abVec.length();

        Vector2f bcVec = (Vector2f) pointC.clone();
        bcVec.sub(pointB);

        float bc = bcVec.length();

        return ab * bc;
    }

    public static float getTriangleArea(Vector2f pointA, Vector2f pointB, Vector2f pointC) {
        Vector2f ab = (Vector2f) pointB.clone();
        ab.sub(pointA);

        float a = ab.length();

        Vector2f bc = (Vector2f) pointC.clone();
        bc.sub(pointB);

        float b = bc.length();

        Vector2f ca = (Vector2f) pointA.clone();
        ca.sub(pointC);

        float c = ca.length();

        float s = (a + b + c) / 2;

        return (float) Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    public static Vec3d roundVec(Vec3d vec3d, int places) {
        return new Vec3d(MathUtils.round(vec3d.x, places), MathUtils.round(vec3d.y, places), MathUtils.round(vec3d.z, places));
    }


    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }

    public static int getMiddle(int i, int j) {

        return (i + j) / 2;
    }

    public static double getMiddleDouble(int i, int j) {

        return ((double) i + (double) j) / 2.0;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float getAngleDifference(float direction, float rotationYaw) {
        float phi = Math.abs(rotationYaw - direction) % 360.0F;
        float distance = phi > 180.0F ? 360.0F - phi : phi;
        return distance;
    }

    public static double[] directionSpeed(double speed) {
        final Minecraft mc = Minecraft.getMinecraft();
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0) {
            if (side > 0) {
                yaw += (forward > 0 ? -45 : 45);
            } else if (side < 0) {
                yaw += (forward > 0 ? 45 : -45);
            }
            side = 0;

            //forward = clamp(forward, 0, 1);
            if (forward > 0) {
                forward = 1;
            } else if (forward < 0) {
                forward = -1;
            }
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]{posX, posZ};
    }

}
