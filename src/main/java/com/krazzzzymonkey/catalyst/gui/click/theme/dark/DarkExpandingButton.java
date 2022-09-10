package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.gui.Tooltip;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.ExpandingButton;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.GLUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class DarkExpandingButton extends ComponentRenderer {

    public DarkExpandingButton(Theme theme) {

        super(ComponentType.EXPANDING_BUTTON, theme);
    }

    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {

        ExpandingButton button = (ExpandingButton) component;
        String text = button.getText();

        int mainColor = ColorUtils.color(0, 0, 0, 130);
        int mainColorInv = ColorUtils.color(200, 200, 200, 255);

        if (GLUtils.isHovered(button.getX(), button.getY(), button.getDimension().width, 14, mouseX, mouseY)) {
            RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getDimension().width, button.getY() + button.getButtonHeight(), ColorUtils.color(20, 20, 20, 100));
        }

        if (button.isEnabled()) {
            RenderUtils.drawBorderedRect(button.getX() + 1, button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + 14, 2, ColorUtils.color(20, 20, 20, 100), mainColor);
            if (ClickGui.rainbow.getValue()) {
                RenderUtils.drawBorderedRect(button.getX() + 1, button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + 14, 2, ColorUtils.color(20, 20, 20, 100), ClickGui.rainbowMode.getMode("RainbowFlow").isToggled() ? ColorUtils.color(button.getButtonColor().getRed(), button.getButtonColor().getGreen(), button.getButtonColor().getBlue(), 100) : ColorUtils.color(ColorUtils.rainbow().getRed(), ColorUtils.rainbow().getGreen(), ColorUtils.rainbow().getBlue(), 100)/*ColorUtils.color(ClickGui.red.getValue().intValue(), ClickGui.green.getValue().intValue(), ClickGui.blue.getValue().intValue(), 100)*/);
            } else {
                RenderUtils.drawBorderedRect(button.getX() + 1, button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + 14, 2, ColorUtils.color(20, 20, 20, 100), ColorUtils.color(ClickGui.clickGuiToggledColor.getColor().getRed(), ClickGui.clickGuiToggledColor.getColor().getGreen(), ClickGui.clickGuiToggledColor.getColor().getBlue(), 100) /*ColorUtils.color(ColorUtils.rainbow().getRed(), ColorUtils.rainbow().getGreen(), ColorUtils.rainbow().getBlue(), 100)*/);
            }
            fontRenderer.drawString(text, button.getX() + 6, button.getY() + (button.getButtonHeight() / 2 - fontRenderer.getHeight() / 4) - 2, -1);
        } else {
            RenderUtils.drawBorderedRect(button.getX() + 1, button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + 14, 2, ColorUtils.color(20, 20, 20, 100), mainColor);
            if (ClickGui.rainbow.getValue()) {
                RenderUtils.drawBorderedRect(button.getX() + 1, button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + 14, 2, ColorUtils.color(20, 20, 20, 100), ClickGui.rainbowMode.getMode("RainbowFlow").isToggled() ? ColorUtils.color(button.getButtonColor().getRed(), button.getButtonColor().getGreen(), button.getButtonColor().getBlue(), 50) : ColorUtils.color(ColorUtils.rainbow().getRed(), ColorUtils.rainbow().getGreen(), ColorUtils.rainbow().getBlue(), 50) /*ColorUtils.color(ClickGui.red.getValue().intValue(), ClickGui.green.getValue().intValue(), ClickGui.blue.getValue().intValue(), 26)*/);
            } else {
                RenderUtils.drawBorderedRect(button.getX() + 1, button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + 14, 2, ColorUtils.color(20, 20, 20, 100), ColorUtils.color(ClickGui.clickGuiBackGroundColor.getColor().getRed(), ClickGui.clickGuiBackGroundColor.getColor().getGreen(), ClickGui.clickGuiBackGroundColor.getColor().getBlue(), 50)/*ColorUtils.color(ColorUtils.rainbow().getRed(), ColorUtils.rainbow().getGreen(), ColorUtils.rainbow().getBlue(), 26)*/);
            }
            fontRenderer.drawString(text, button.getX() + 6, button.getY() + (button.getButtonHeight() / 2 - fontRenderer.getHeight() / 4) - 2, mainColorInv);
        }
        if (ClickGui.rainbow.getValue()) {
            RenderUtils.drawRect(button.getX(), button.getY() - 1, button.getX() + 2, button.getY() + 15, ClickGui.rainbowMode.getMode("RainbowFlow").isToggled() ? button.getButtonColor().getRGB() : ColorUtils.rainbow().getRGB());
            RenderUtils.drawRect(button.getX() + button.getDimension().width - 2, button.getY() - 1, button.getX() + button.getDimension().width, button.getY() + 15, ClickGui.rainbowMode.getMode("RainbowFlow").isToggled() ? button.getButtonColor().getRGB() : ColorUtils.rainbow().getRGB());
        } else {
            RenderUtils.drawRect(button.getX(), button.getY() - 1, button.getX() + 2, button.getY() + 15, ClickGui.getColor());
            RenderUtils.drawRect(button.getX() + button.getDimension().width - 2, button.getY() - 1, button.getX() + button.getDimension().width, button.getY() + 15, ClickGui.getColor());
        }

        if (!ClickGuiScreen.searchField.getText().equals("") && !button.getText().toLowerCase().contains(ClickGuiScreen.searchField.getText().toLowerCase())) {
            RenderUtils.drawRect(button.getX() + 1, button.getY(), button.getX() + button.getDimension().width - 1, button.getY() + 14, ColorUtils.color(0, 0, 0, 200));
        }

        if (button.isMaximized()) {
            RenderUtils.drawRect(button.getX(), button.getY() + button.getButtonHeight() - 1, button.getX() + button.getDimension().width, button.getY() + button.getButtonHeight(), ClickGui.rainbow.getValue() ? ClickGui.rainbowMode.getMode("RainbowFlow").isToggled() ? button.getButtonColor().getRGB() : ColorUtils.rainbow().getRGB() : ClickGui.getColor());
            RenderUtils.drawRect(button.getX(), button.getY() + button.getDimension().height - 1, button.getX() + button.getDimension().width, button.getY() + button.getDimension().height, ClickGui.rainbow.getValue() ? ClickGui.rainbowMode.getMode("RainbowFlow").isToggled() ? button.getButtonColor().getRGB() : ColorUtils.rainbow().getRGB() : ClickGui.getColor());
        }

        drawExpanded(button.getX() + button.getDimension().width - 15, button.getY() + 3, 13, button.isMaximized(), new Color(255, 255, 255, 100).hashCode());

        if (button.isMaximized()) {
            button.renderChildren(mouseX, mouseY);
        }
        String description = button.getMod().getDescription();
        if (description != null && button.isMouseOver(mouseX, mouseY) && !button.isMaximized() && ModuleManager.getModule("ClickGui").isToggledValue("Tooltip")) {
            if (Minecraft.getMinecraft().currentScreen instanceof ClickGuiScreen) {
                ClickGuiScreen.tooltip = new Tooltip(description, (int) (mouseX * com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), (int) (mouseY * com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), fontRenderer);
            }else {
                ClickGuiScreen.tooltip = new Tooltip(description, mouseX, mouseY, fontRenderer);

            }
        }
    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {

    }
}
