package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.Dropdown;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;

import java.awt.*;


public class DarkDropDown extends ComponentRenderer {

    public DarkDropDown(Theme theme) {

        super(ComponentType.DROPDOWN, theme);
    }

    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {
        int mainColor =  ColorUtils.color(0, 0, 0, 150);
        Dropdown dropdown = (Dropdown) component;
        String text = dropdown.getText();

        Color backgroundColor = new Color(20, 20, 20, 255);
        if(dropdown.getSubMenu() != null){
            backgroundColor = new Color(11, 11, 11, 255);
        }

        RenderUtils.drawBorderedRect(dropdown.getX() +1, dropdown.getY()-1, dropdown.getX() + dropdown.getDimension().width-2, dropdown.getY() + 14, 2, ColorUtils.color(0f, 0f , 0f, 1f), backgroundColor.getRGB());
        Main.smallFontRenderer.drawString(text, dropdown.getX() + 5, dropdown.getY()-2 + (dropdown.getDropdownHeight() / 2f - Main.smallFontRenderer.getHeight() / 4f), -1);
       // RenderUtils.drawRect(dropdown.getX(), dropdown.getY()-2, dropdown.getX() + 2, dropdown.getY()+15 , dropdown.getButtonColor().getRGB());
        //RenderUtils.drawRect(dropdown.getX() + dropdown.getDimension().width - 2, dropdown.getY()-2, dropdown.getX() +dropdown.getDimension().width, dropdown.getY()+15 , dropdown.getButtonColor().getRGB());

        if (dropdown.isMaximized()) {
            Main.smallFontRenderer.drawString("-", dropdown.getX() + dropdown.getDimension().width - 9, dropdown.getY()-2 + (dropdown.getDropdownHeight() / 2f - Main.smallFontRenderer.getHeight() / 4f)-1, -1);
            dropdown.renderChildren(mouseX, mouseY);
        }else{
            Main.smallFontRenderer.drawString("+", dropdown.getX() + dropdown.getDimension().width - 10, dropdown.getY()-2 + (dropdown.getDropdownHeight() / 2f - Main.smallFontRenderer.getHeight() / 4f), -1);
        }
    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {

    }
}
