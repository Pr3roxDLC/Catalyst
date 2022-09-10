package com.krazzzzymonkey.catalyst.module.modules.render;


import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class BreakESP extends Modules {

    private DoubleValue range;
    private ColorValue colorValue;
    private BooleanValue rainbow;

    public BreakESP() {
        super("BreakESP", ModuleCategory.RENDER, "Highlights what blocks are being broken by players");
        this.range = new DoubleValue("Range", 80D, 1D, 100D, "The max range for break ESP to render");
        this.colorValue = new ColorValue("Color", Color.CYAN, "The color of the break ESP");
        this.rainbow = new BooleanValue("Rainbow", false, "Make the break ESP cycle through colors");
        this.addValue(range, colorValue, rainbow);
    }

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {

        if (mc.player == null || mc.world == null) return;

        mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress != null) {

                BlockPos blockPos = destroyBlockProgress.getPosition();

                if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR ||mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK) {
                    return;
                }

                if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {

                    float progress = destroyBlockProgress.getPartialBlockDamage()/8f;
                    if (rainbow.getValue()) {
                        RenderUtils.drawBlockESP(blockPos, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 1-progress);
                    } else {
                        RenderUtils.drawBlockESP(blockPos, colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1-progress);
                    }
                }
            }
        });
    });


}
