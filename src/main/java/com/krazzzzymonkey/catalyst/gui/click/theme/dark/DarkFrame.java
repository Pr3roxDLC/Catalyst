package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.Frame;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.managers.FontManager;
import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;

import java.awt.*;

public class DarkFrame extends ComponentRenderer {

    public DarkFrame(Theme theme) {

        super(ComponentType.FRAME, theme);
    }

    public static CFontRenderer fontRenderer = new CFontRenderer(new Font(FontManager.font, Font.PLAIN, 20), true, false);

    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {

        Frame frame = (Frame) component;
        Dimension dimension = frame.getDimension();

        if (frame.isMaximized()) {
            isMaximized(frame, dimension, mouseX, mouseY);
        }
        if (ClickGui.rainbow.getValue()) {
            RenderUtils.drawRect(frame.getX(), frame.getY() + 3, frame.getX() + dimension.width, frame.getY() + 15, ClickGui.rainbowMode.getMode("RainbowFlow").isToggled() ? frame.getButtonColor().getRGB() : ColorUtils.rainbow().getRGB());
        } else {
            RenderUtils.drawRect(frame.getX(), frame.getY() + 3, frame.getX() + dimension.width, frame.getY() + 15, ClickGui.getColor());
        }
        if (frame.isMaximizible()) {
            isMaximizible(frame, dimension, mouseX, mouseY);
        }
        fontRenderer.drawStringWithShadow(frame.getText(), frame.getX() + (frame.getDimension().width / 2) - (Main.fontRenderer.getStringWidth(frame.getText()) / 2), frame.getY() + 4, ColorUtils.color(1.0f, 1.0f, 1.0f, 1.0f));

    }


    private void isMaximizible(Frame frame, Dimension dimension, int mouseX, int mouseY) {

        Color color;

        if (mouseX >= frame.getX()   && mouseY >= frame.getY() && mouseY <= frame.getY() + 19 && mouseX <= frame.getX() + dimension.width) {
            color = new Color(255, 255, 255, 255);
        } else {
            color = new Color(155, 155, 155, 255);
        }

        theme.fontRenderer.drawStringWithShadow(frame.isMaximized() ? "-" : "+", frame.getX() + dimension.width - 12, frame.getY() + 3, color.getRGB());
    }

    private void isMaximized(Frame frame, Dimension dimension, int mouseX, int mouseY) {
        float offset = 0f;
        for (Component component : frame.getComponents()) {
            offset += ClickGui.rainbowHue.getValue() / 10;
            component.setxPos(frame.getX());
            component.setButtonColor(offset, ClickGui.rainbowSpeed.getValue());
        }

        float height = 5;
        float maxHeight = 0;
        height = dimension.height - 16;

        for (Component component : frame.getComponents()) {
            maxHeight += component.getDimension().height;
        }
        float barHeight = height * (height / maxHeight);
        double y = (frame.getDimension().getHeight() - 16 - barHeight) * ((double) frame.getScrollAmmount() / (double) frame.getMaxScroll());
        y += frame.getY() + 16;
        frame.renderChildren(mouseX, mouseY);

        if (!(barHeight >= height)) {
            if (ClickGui.rainbow.getValue()) {
                RenderUtils.drawRect((int) (frame.getX() + dimension.getWidth() - 1), (int) y, (int) (frame.getX() + frame.getDimension().getWidth()), (int) (y + barHeight), ClickGui.rainbowMode.getMode("RainbowFlow").isToggled() ? frame.getButtonColor().getRGB() : ColorUtils.rainbow().getRGB());
            } else {
                RenderUtils.drawRect((int) (frame.getX() + dimension.getWidth() - 1), (int) y, (int) (frame.getX() + frame.getDimension().getWidth()), (int) (y + barHeight), ClickGui.getColor());
            }
        }

    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {
        Frame frame = (Frame) component;
        Dimension area = frame.getDimension();

        if (MouseUtils.isRightClicked() && mouseX >= frame.getX() && frame.isMaximizible() && mouseY >= frame.getY() && mouseY <= frame.getY() + 16 && mouseX <= frame.getX() + area.width) {
            frame.setMaximized(!frame.isMaximized());
        }

        if (MouseUtils.isRightClicked() && mouseX >= frame.getX() + area.width - 38 && mouseY >= frame.getY() && mouseY <= frame.getY() + 16 && mouseX <= frame.getX() + area.width - 16) {
            frame.setPinned(!frame.isPinned());
        }
    }

}
