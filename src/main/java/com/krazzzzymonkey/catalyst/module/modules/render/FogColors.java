package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

/**
 * Created by hub on 08 July 2020
 */
public class FogColors extends Modules {

    private final ColorValue color;
    private final BooleanValue rainbow;

    public FogColors() {
        super("FogColors", ModuleCategory.RENDER, "Changes the color of fog");
        this.color = new ColorValue("FogColor", Color.CYAN, "Changes the color of the fog");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes the fog color cycle through colors");
        this.addValue(color,rainbow);
    }

    @SubscribeEvent
    public void onEntityViewRenderEventFogColors(EntityViewRenderEvent.FogColors event) {
        if(rainbow.getValue()){
            event.setBlue(ColorUtils.rainbow().getBlue() / 255f);
            event.setGreen(ColorUtils.rainbow().getGreen() / 255f);
            event.setRed(ColorUtils.rainbow().getRed() / 255f);
        }else {
            event.setBlue(color.getColor().getBlue() / 255f);
            event.setGreen(color.getColor().getGreen() / 255f);
            event.setRed(color.getColor().getRed() / 255f);
        }
    }

    @SubscribeEvent
    public void onEntityViewRenderEventFogDensity(EntityViewRenderEvent.FogDensity event) {
        event.setDensity(1);
    }

}
