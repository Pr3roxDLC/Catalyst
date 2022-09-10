package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.Text;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;


public class DarkText extends ComponentRenderer {

    public DarkText(Theme theme) {
        super(ComponentType.TEXT, theme);
    }

    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {
        Text text = (Text) component;
        String[] message = text.getMessage();

        int y = text.getY();

        for (String s : message) {
            Main.fontRenderer.drawString(s, text.getX() - 4, y - 4, -1);
            y += 10;
        }
    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {}
}
