package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ItemESP extends Modules {

    private ColorValue color;
    private BooleanValue rainbow;

    public ItemESP() {
        super("ItemESP", ModuleCategory.RENDER, "Shows you where items are");
        this.color = new ColorValue("EspColor", Color.CYAN, "The color of item esp");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes item esp cycle through colors");
        this.addValue(color, rainbow);
    }

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {

        for (Object object : Wrapper.INSTANCE.world().loadedEntityList) {
            if (object instanceof EntityItem || object instanceof EntityArrow) {
                Entity item = (Entity) object;
                if (rainbow.getValue()) {
                    RenderUtils.drawESP(item, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 1, e.getPartialTicks());
                } else
                    RenderUtils.drawESP(item, color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f, 1, e.getPartialTicks());
            }
        }

    });

}
