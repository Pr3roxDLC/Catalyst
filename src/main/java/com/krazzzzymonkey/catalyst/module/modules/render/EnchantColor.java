package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;

import java.awt.*;

public class EnchantColor extends Modules {

    public static BooleanValue rainbow = new BooleanValue("Rainbow", false, "Makes enchant glint cycle through colors");
    public static ColorValue colorValue;


    // MixinRenderItem

    public EnchantColor() {
        super("EnchantColor", ModuleCategory.RENDER, "Changes color of enchants");
        colorValue = new ColorValue("Color", Color.CYAN, "Changes Color of the of the enchant glint");
        this.addValue(colorValue, rainbow);
    }

}
