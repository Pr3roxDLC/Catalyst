package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.Tooltip;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.Slider;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class DarkSlider extends ComponentRenderer {

    public DarkSlider(Theme theme) {

        super(ComponentType.SLIDER, theme);
    }

    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {


        Slider slider = (Slider) component;
        int width = (int) ((slider.getDimension().getWidth()) * slider.getPercent());
        int mainColorInv = ColorUtils.color(255, 255, 255, 255);

        String value = slider.getRenderValue();

        Color backgroundColor = new Color(20, 20, 20, 255);
        if(slider.getSubMenu() != null){
            backgroundColor = new Color(11, 11, 11, 255);
        }

        RenderUtils.drawBorderedRect(slider.getX(), slider.getY() - 1, slider.getX() + slider.getDimension().width, slider.getY() + 14, 2, ColorUtils.color(0f, 0f, 0f, 1f), backgroundColor.getRGB());
        //slider
        if (ClickGui.rainbow.getValue()) {
            RenderUtils.drawRect(slider.getX(), slider.getY(), slider.getX() + (width), slider.getY() + 14, ClickGui.rainbowMode.getMode("Static").isToggled() ? ColorUtils.rainbow().getRGB() : ColorUtils.color(slider.getButtonColor().getRed(), slider.getButtonColor().getGreen(), slider.getButtonColor().getBlue(), 100));
        } else {
            RenderUtils.drawRect(slider.getX(), slider.getY(), slider.getX() + (width), slider.getY() + 14, ColorUtils.color(ClickGui.clickGuiColor.getColor().getRed(), ClickGui.clickGuiColor.getColor().getGreen(), ClickGui.clickGuiColor.getColor().getBlue(), 100));
        }
        Main.smallFontRenderer.drawString(slider.getText(), slider.getX() + 5, slider.getY() + 4, -1);

        Main.smallFontRenderer.drawString(value + "", slider.getX() + slider.getDimension().width - theme.fontRenderer.getStringWidth(value + "") - 2, slider.getY() + 4,
            mainColorInv);


        String description = slider.getDescription();
        if (description != null && !description.equals("") && slider.isMouseOver(mouseX, mouseY) && ModuleManager.getModule("ClickGui").isToggledValue("Tooltip")) {
            if (Minecraft.getMinecraft().currentScreen instanceof ClickGuiScreen) {
                ClickGuiScreen.tooltip = new Tooltip(description, (int) (mouseX * com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), (int) (mouseY * com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), fontRenderer);
            } else {
                ClickGuiScreen.tooltip = new Tooltip(description, mouseX, mouseY, fontRenderer);
            }
        }

    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {

    }
}
