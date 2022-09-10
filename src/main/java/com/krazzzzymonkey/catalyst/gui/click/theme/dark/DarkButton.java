package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.Button;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.GLUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;

public class DarkButton extends ComponentRenderer {

    public DarkButton(Theme theme) {

        super(ComponentType.BUTTON, theme);
    }

    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {

        Button button = (Button) component;
        String text = button.getText();
        int color = ColorUtils.color(50, 50, 50, 100);
        int enable = ColorUtils.color(255, 255, 255, 255);

        if (GLUtils.isHovered(button.getX(), button.getY(), button.getDimension().width, button.getDimension().height, mouseX, mouseY)) {
            color = ColorUtils.color(70, 70, 70, 255);
        }

        if (button.isEnabled()) {
            RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + button.getDimension().height, enable);
        } else {
            RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + button.getDimension().height, color);
        }

        Main.fontRenderer.drawString(text, button.getX() + 5, button.getY() + (button.getDimension().height / 2 - Main.fontRenderer.getHeight() / 4), ColorUtils.color(255, 255, 255, 255));
    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {}
}
