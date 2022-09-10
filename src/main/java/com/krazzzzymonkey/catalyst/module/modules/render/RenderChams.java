package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class RenderChams extends Modules {

    public static ModeValue Mode;
    public static BooleanValue Chests;
    public static ColorValue visibleColor;
    public static ColorValue hiddenColor;
    public static ColorValue singleColor;
    public static BooleanValue raindbowColor = new BooleanValue("RainbowColor", false, "");

    public RenderChams() {

        super("Chams", ModuleCategory.RENDER, "See entities through walls");

        hiddenColor = new ColorValue("HiddenColor", Color.MAGENTA, "The color of entities when behind walls when in two color mode");
        singleColor = new ColorValue("SingleColor", Color.CYAN, "The color of entities when in color mode");
        visibleColor = new ColorValue("VisibleColor", Color.CYAN, "The color of entities when in visible");
        Chests = new BooleanValue("Chests", false, "Render chests through walls");
        Mode = new ModeValue("Mode", new Mode("Basic", true), new Mode("Color", false), new Mode("TwoColor", false));
        this.addValue(Mode, visibleColor, hiddenColor, singleColor,raindbowColor);
    }

}
