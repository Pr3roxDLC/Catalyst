package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

//TODO ADD SETTINGS
public class BlockOverlay extends Modules {

    private ColorValue colorValue;
    private BooleanValue rainbow;

    public BlockOverlay() {
        super("BlockOverlay", ModuleCategory.RENDER, "Highlights block you are looking at");
        this.colorValue = new ColorValue("Color", Color.CYAN, "The color for block overlay");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes the block overlay cycle through colors");
        this.addValue(colorValue, rainbow);

    }

    BlockPos blockPos;
    double x;
    double y;
    double z;

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (Wrapper.INSTANCE.mc().objectMouseOver == null) {
            return;
        }
        if (Wrapper.INSTANCE.mc().objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            Block block = Wrapper.INSTANCE.world().getBlockState(Wrapper.INSTANCE.mc().objectMouseOver.getBlockPos()).getBlock();
            blockPos = Wrapper.INSTANCE.mc().objectMouseOver.getBlockPos();

            if (Block.getIdFromBlock(block) == 0) {
                return;
            }
            if (rainbow.getValue()) {
                RenderUtils.drawBlockESP(blockPos, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 1);
            } else {
                RenderUtils.drawBlockESP(blockPos, colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1);
            }
        }
        x = Wrapper.INSTANCE.mc().getRenderManager().viewerPosX;
        y = Wrapper.INSTANCE.mc().getRenderManager().viewerPosY;
        z = Wrapper.INSTANCE.mc().getRenderManager().viewerPosZ;

    });


}
