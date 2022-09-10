package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.managers.FontManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;

import java.awt.*;

public class CustomChat extends Modules {

//todo yeet all the other chat altering things in here


    public static BooleanValue customFont;
    public static BooleanValue background;

    public static CFontRenderer fontRenderer = new CFontRenderer(new Font(FontManager.font, Font.PLAIN, 18), true, true);

    public CustomChat() {
        super("CustomChat", ModuleCategory.CHAT, "Allows you to customise the chat");
        customFont = new BooleanValue("CustomFont", true, "Makes chat render with a custom font");
        background = new BooleanValue("Background", false, "Renders the background behind chat");

        this.addValue(customFont, background);
    }



}
