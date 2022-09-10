package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.CrystalUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TunnelESP extends Modules {

    private static final ColorValue espColor = new ColorValue("ESPColor", new Color(0, 127, 64), "Block color");
    private static final DoubleValue espHeight = new DoubleValue("ESPHeight", 0.1f, 0.1f, 1f, "Block height");
    private static final IntegerValue radius = new IntegerValue("Radius", 64, 8, 128, "Detection radius");
    private static final IntegerValue height = new IntegerValue("Height", 32, 8, 256, "Detection height");
    private static final IntegerValue blocksPerTick = new IntegerValue("BlocksPerTick", 10000, 1000, 25000, "Block scans per tick");

    private final Set<BlockPos> tunnelBlocks = ConcurrentHashMap.newKeySet();
    private final Set<BlockPos> scanQueue = ConcurrentHashMap.newKeySet();

    // TODO: clear tunnelBlocks on dimension change

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (mc.player == null || mc.world == null) return;
        for (BlockPos block : tunnelBlocks)
            RenderUtils.drawBlockESP(
                block,
                espColor.getColor().getRed(),
                espColor.getColor().getGreen(),
                espColor.getColor().getBlue(),
                espHeight.value
            );
    });

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (mc.player == null || mc.world == null) return;
        if (scanQueue.isEmpty()) {
            scanQueue.addAll(
                CrystalUtils.getSphere(
                    new BlockPos(
                        mc.player.posX,
                        mc.player.posY,
                        mc.player.posZ
                    ),
                    radius.getValue(),
                    height.getValue(),
                    false,
                    true,
                    0
                )
            );
        }
        int scanCounter = 0;
        for (BlockPos block : scanQueue) {
            if (scanCounter >= blocksPerTick.getValue()) return;
            scanCounter++;
            if (isTunnelPart(block)) tunnelBlocks.add(block);
            scanQueue.remove(block);
        }
    });

    public TunnelESP() {
        super("TunnelESP", ModuleCategory.RENDER, "Highlight tunnels");
        this.addValue(espColor, espHeight, radius, height, blocksPerTick);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        tunnelBlocks.clear();
        scanQueue.clear();
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean isTunnelPart(BlockPos pos) {
        if (!isAir(pos) || !isAir(pos.up())) return false;
        if (isAir(pos.down()) || isAir(pos.up().up())) return false;
        if (isAir(pos.north())
            && isAir(pos.south())
            && isAir(pos.up().north())
            && isAir(pos.up().south())) {
            if (isAir(pos.east())
                || isAir(pos.west())
                || isAir(pos.up().east())
                || isAir(pos.up().west())) return false;
            else return true;
        }
        if (isAir(pos.east())
            && isAir(pos.west())
            && isAir(pos.up().east())
            && isAir(pos.up().west())) {
            if (isAir(pos.north())
                || isAir(pos.south())
                || isAir(pos.up().north())
                || isAir(pos.up().south())) return false;
            else return true;
        }
        return false;
    }

    private boolean isAir(BlockPos pos) {
        return mc.world.getBlockState(pos).getMaterial() == Material.AIR;
    }

}
