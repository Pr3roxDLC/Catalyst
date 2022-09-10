package com.krazzzzymonkey.catalyst.xray;

import net.minecraft.util.math.BlockPos;

public class XRayBlock {

    private final BlockPos blockPos;
    private final XRayData xRayData;

    public XRayBlock(BlockPos blockPos, XRayData xRayData){
        this.blockPos = blockPos;
        this.xRayData = xRayData;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public XRayData getxRayData() {
        return xRayData;
    }
}
