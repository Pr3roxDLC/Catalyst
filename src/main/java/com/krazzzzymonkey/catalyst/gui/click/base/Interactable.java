package com.krazzzzymonkey.catalyst.gui.click.base;

import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;

import java.awt.*;

public class Interactable {

    private int xPos, yPos, yBase;
    private float colorOffset = 0.01f;
    private double colorSpeed = 20;

    private Color color;
    private Dimension dimension;

    public Interactable(int xPos, int yPos, int width, int height) {

        this.xPos = xPos;
        this.yPos = yPos;
        this.dimension = new Dimension(width, height);
    }

    public void onMousePress(int x, int y, int buttonID) {

    }

    public void onMouseRelease(int x, int y, int buttonID) {

    }

    public void onMouseDrag(int x, int y) {

    }

    public void onMouseScroll(int scroll) {

    }

    public boolean isMouseOver(int x, int y) {

        return (x >= xPos && y >= yPos && x <= xPos + dimension.width && y <= yPos + dimension.height);
    }

    public void onKeyPressed(int key, char character) {

    }

    public void onKeyReleased(int key, char character) {

    }

    public int getX() {

        return xPos;
    }

    public void setxPos(int xPos) {

        this.xPos = xPos;
    }

    public void setButtonColor(float offset, double speed) {
        this.colorOffset = offset;
        this.colorSpeed = speed;
        this.color = ColorUtils.rainbow(offset, colorSpeed);
    }

    public Color getButtonColor() {
        if (this.color != null) {
            return this.color;
        } else return ColorUtils.rainbow(colorOffset, colorSpeed);
    }

    public int getY() {

        return yPos;
    }

    public void setyPos(int yPos) {

        this.yPos = yPos;
    }

    public int getyBase() {

        return yBase;
    }

    public void setyBase(int yBase) {

        this.yBase = yBase;
    }

    public Dimension getDimension() {

        return dimension;
    }

    public void setDimension(Dimension dimension) {

        this.dimension = dimension;
    }
}
