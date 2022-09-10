package com.krazzzzymonkey.catalyst.gui;

import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import org.lwjgl.opengl.GL11;

public class Tooltip {

    private final CFontRenderer fontRenderer;
    private final String text;
    private final int x;
    private final int y;

    public Tooltip(String text, int x, int y, CFontRenderer fontRenderer) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.fontRenderer = fontRenderer;
    }

    public String getText() {
        return text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void render() {
        int textColor = -1;
        int rectColor = ColorUtils.color(0, 0, 0, 180);

        int aboveMouse = 8;
        GL11.glScaled(com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue(), (com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), 1.0D);
        RenderUtils.drawCustomStringWithRect(getText(), (int) ((getX() + 2) / ClickGui.clickGuiScale.getValue()), (int) ((getY() - aboveMouse + 3) / ClickGui.clickGuiScale.getValue()), textColor, ClickGui.getColor(), rectColor);
        GL11.glScaled(1.0D / (com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), 1.0D / (com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), 1.0D);
    }
}
