package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class Crosshair extends Modules {

    ColorValue color = new ColorValue("Color", Color.CYAN, "The color of the crosshair");
    BooleanValue colorRainbow = new BooleanValue("Rainbow", false, "Makes the crosshair cycle through colors");
    DoubleValue crosshairGap = new DoubleValue("CrosshairGap", 2, 0, 20, "The gap in the middle of the crosshair");
    DoubleValue crosshairWidth = new DoubleValue("CrosshairWidth", 1, 0.1, 5, "The width of the crosshair");
    DoubleValue crosshairSize = new DoubleValue("CrosshairSize", 3, 1, 50, "The size of the crosshair");


    public Crosshair() {
        super("Crosshair", ModuleCategory.RENDER, "Renders a custom crosshair on your screen");
        this.addValue(color, colorRainbow, crosshairGap, crosshairWidth, crosshairSize);
    }

    @EventHandler
    EventListener<RenderGameOverlayEvent.Text> renderGameOverlay = new EventListener<>(event -> {
        ScaledResolution sr = new ScaledResolution(mc);
        float cX = (float) (sr.getScaledWidth_double() / 2F + 0.5F);
        float cY = (float) (sr.getScaledHeight_double() / 2F + 0.5F);
        float gap = crosshairGap.getValue().floatValue();
        float width = Math.max(crosshairWidth.getValue().floatValue(), 0.5F);
        float size = crosshairSize.getValue().floatValue();
        RenderUtils.drawRect(cX - gap - size, cY - width / 2.0F, cX - gap, cY + width / 2.0F, colorRainbow.getValue() ? ColorUtils.rainbow().getRGB() : color.getColor().getRGB());
        RenderUtils.drawRect(cX + gap + size, cY - width / 2.0F, cX + gap, cY + width / 2.0F, colorRainbow.getValue() ? ColorUtils.rainbow().getRGB() : color.getColor().getRGB());
        RenderUtils.drawRect(cX - width / 2.0F, cY + gap + size, cX + width / 2.0F, cY + gap, colorRainbow.getValue() ? ColorUtils.rainbow().getRGB() : color.getColor().getRGB());
        RenderUtils.drawRect(cX - width / 2.0F, cY - gap - size, cX + width / 2.0F, cY - gap, colorRainbow.getValue() ? ColorUtils.rainbow().getRGB() : color.getColor().getRGB());

    });

}
