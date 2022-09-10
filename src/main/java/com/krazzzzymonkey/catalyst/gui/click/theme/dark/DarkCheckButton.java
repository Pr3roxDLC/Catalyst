package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.Tooltip;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.CheckButton;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.MathUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class DarkCheckButton extends ComponentRenderer {

    public DarkCheckButton(Theme theme) {

        super(ComponentType.CHECK_BUTTON, theme);
    }

    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {

        CheckButton button = (CheckButton) component;
        String text = button.getText();

        Color backgroundColor = new Color(20, 20, 20, 255);
        if(button.getSubMenu() != null){
            backgroundColor = new Color(11, 11, 11, 255);
        }


        if (button.getModeValue() == null) {
            RenderUtils.drawBorderedRect(button.getX() + 1, button.getY() - 1, button.getX() + button.getDimension().width - 2, button.getY() + 13, 2, ColorUtils.color(0f, 0f, 0f, 1f), backgroundColor.getRGB());
            Main.smallFontRenderer.drawString(text, button.getX() + 5, MathUtils.getMiddle(button.getY() - 1, button.getY() + button.getDimension().height) - Main.smallFontRenderer.getHeight() / 3 - 1,
                button.isEnabled() ? -1 : ColorUtils.color(0.5f, 0.5f, 0.5f, 1.0f));

            String description = button.getDescription();
            if (description != null && !description.equals("") && button.isMouseOver(mouseX, mouseY) && ModuleManager.getModule("ClickGui").isToggledValue("Tooltip")) {
                if (Minecraft.getMinecraft().currentScreen instanceof ClickGuiScreen) {
                    ClickGuiScreen.tooltip = new Tooltip(description, (int) (mouseX * com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), (int) (mouseY * com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), fontRenderer);
                } else {
                    ClickGuiScreen.tooltip = new Tooltip(description, mouseX, mouseY, fontRenderer);
                }
            }
            return;
        }

        for (Mode mode : button.getModeValue().getModes()) {
            if (mode.getName().equals(text)) {
                RenderUtils.drawBorderedRect(button.getX() + 1, button.getY() - 2, button.getX() + button.getDimension().width - 2, button.getY() + 13, 2, ColorUtils.color(0f, 0f, 0f, 1f), ColorUtils.color(8, 8, 8, 255));
                Main.smallFontRenderer.drawString(text, button.getX() + 5, MathUtils.getMiddle(button.getY() - 2, button.getY() + button.getDimension().height) - Main.smallFontRenderer.getHeight() / 3 - 1, mode.isToggled() ? -1 : ColorUtils.color(0.5f, 0.5f, 0.5f, 1.0f));
            }
        }
    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {

    }
}
