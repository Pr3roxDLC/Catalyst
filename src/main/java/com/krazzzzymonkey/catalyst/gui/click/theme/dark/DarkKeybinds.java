package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.KeybindMods;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import org.lwjgl.input.Keyboard;

public class DarkKeybinds extends ComponentRenderer {

    public DarkKeybinds(Theme theme) {

        super(ComponentType.KEYBIND, theme);
    }

    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {

        KeybindMods keybind = (KeybindMods) component;




        int nameWidth = Main.smallFontRenderer.getStringWidth("Bind") + 7;

        RenderUtils.drawBorderedRect(keybind.getX() +1 , keybind.getY()-1, keybind.getX() + keybind.getDimension().width-2, keybind.getY() + 12,2 ,ColorUtils.color(0f,0f,0f,1f) , ColorUtils.color(20, 20, 20, 255));

        if(keybind.getMod().isBindHold()){
            Main.smallFontRenderer.drawString("Hold", keybind.getX() + 5, keybind.getY() + 3, -1);
        }else {
            Main.smallFontRenderer.drawString("Bind", keybind.getX() + 5, keybind.getY() + 3, -1);
        }

        if(keybind.getMod().getKey() == -1) {
        	Main.smallFontRenderer.drawString(keybind.isEditing() ? "|" : "NONE", keybind.getX() + keybind.getDimension().width / 2 + nameWidth / 2 - theme.fontRenderer.getStringWidth("NONE") / 2, keybind.getY() + 3, keybind.isEditing() ? -1 : ColorUtils.color(0.6f, 0.6f, 0.6f, 1.0f));
        }
        else {
        	Main.smallFontRenderer.drawString(keybind.isEditing() ? "|" : Keyboard.getKeyName(keybind.getMod().getKey()), keybind.getX() + keybind.getDimension().width / 2 + nameWidth / 2 - theme.fontRenderer.getStringWidth(Keyboard.getKeyName(keybind.getMod().getKey())) / 2, keybind.getY() + 3, -1);
        }
    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {

    }

}
