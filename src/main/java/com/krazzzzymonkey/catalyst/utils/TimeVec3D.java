package com.krazzzzymonkey.catalyst.utils;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class TimeVec3D extends Vec3d {
    private final long time;

    public TimeVec3D(double xIn, double yIn, double zIn, long time) {
        super(xIn, yIn, zIn);
        this.time = time;
    }

    public TimeVec3D(Vec3i vector, long time) {
        super(vector);
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
