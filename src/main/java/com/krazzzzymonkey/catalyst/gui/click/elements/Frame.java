package com.krazzzzymonkey.catalyst.gui.click.elements;

import com.krazzzzymonkey.catalyst.gui.click.ClickGui;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.base.Container;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;

import java.awt.*;

public class Frame extends Container {

    private boolean pinned, maximized, maximizible = true, visable = true, pinnable = true;

    private int ticksSinceScroll = 100, scrollAmmount = 0;


    public Frame(int xPos, int yPos, int width, int height, String title) {

        super(xPos, yPos, width, height, ComponentType.FRAME, null, title);
    }

    public static boolean isDragging = false;

    @Override
    public void renderChildren(int mouseX, int mouseY) {

        if (this.isMaximized()) {
            for (Component component : getComponents()) {
                component.render(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onMousePress(int x, int y, int buttonID) {

        if (isMouseOverBar(x, y)) {
            ClickGui.getTheme().getRenderer().get(getComponentType()).doInteractions(this, x, y);
            isDragging = true;

        }

        if (x >= getX() && y >= getY() + this.getFrameBoxHeight() && x <= getX() + getDimension().getWidth() && y <= getY() + getDimension().getHeight()) {
            for (Component c : this.getComponents()) {
                if (c.isMouseOver(x, y) && maximized) {
                    c.onMousePress(x, y, buttonID);
                    ClickGui.getTheme().getRenderer().get(getComponentType()).doInteractions(this, x, y);

                }
            }
        }
    }

    @Override
    public void onMouseRelease(int x, int y, int buttonID) {

        if (x >= getX() && y >= getY() + this.getFrameBoxHeight() && x <= getX() + getDimension().getWidth() && y <= getY() + getDimension().getHeight()) {
            for (Component c : this.getComponents()) {
                if (c.isMouseOver(x, y) && maximized) {
                    c.onMouseRelease(x, y, buttonID);
                }
            }
        }
        isDragging = false;
    }

    @Override
    public void onMouseDrag(int x, int y) {

        //if (isMouseOverBar(x, y)) {
        if(x >= this.getX() && x <= this.getX() + 80 && y >= this.getY() && y<= this.getY()+ 20){
            ClickGui.getTheme().getRenderer().get(getComponentType()).doInteractions(this, x, y);
            isDragging = true;
        }

        if(x >= this.getX() && x <= this.getX() + 80 && y >= this.getY() && y<= this.getY()+ 20){
            for (Component c : this.getComponents()) {
                if (c.isMouseOver(x, y) && maximized) {
                    c.onMouseDrag(x, y);
                    ClickGui.getTheme().getRenderer().get(getComponentType()).doInteractions(this, x, y);
                    isDragging = true;

                }
            }
        }
    }

    @Override
    public void onKeyPressed(int key, char character) {

        for (Component c : this.getComponents()) {
            c.onKeyPressed(key, character);
        }
    }

    @Override
    public void onKeyReleased(int key, char character) {

        for (Component c : this.getComponents()) {
            c.onKeyReleased(key, character);
        }
    }

    public boolean isMouseOverBar(int x, int y) {

        return (x >= getX() && y >= getY() && x <= getX() + getDimension().getWidth() && y <= getY() + 15);
    }

    public void scrollFrame(int ammount) {

        this.scrollAmmount += ammount;
        this.ticksSinceScroll = 0;
    }

    public void updateComponents() {

        this.ticksSinceScroll++;

        if (this.scrollAmmount < this.getMaxScroll()) {
            this.scrollAmmount = this.getMaxScroll();
        }

        if (this.scrollAmmount > 0) {
            this.scrollAmmount = 0;
        }

        for (Component c : this.getComponents()) {
            c.onUpdate();

            if (c instanceof Container) {
                Container container = (Container) c;
                for (Component component1 : container.getComponents()) {
                    component1.onUpdate();
                }
            }

            int yCount = getY() + this.getFrameBoxHeight();

            for (Component component1 : this.getComponents()) {
                if (this.getComponents().indexOf(component1) < this.getComponents().indexOf(c)) {
                    yCount += component1.getDimension().getHeight();
                }
            }

            c.setyBase(yCount);
            c.setyPos(c.getyBase() + this.scrollAmmount);
        }
        int height = (Wrapper.INSTANCE.mc().displayHeight / 3) + this.getComponents().size() + 1000;
        this.setDimension(new Dimension(this.getDimension().width, height));
    }

    public int getMaxScroll() {

        if (this.getComponents().size() == 0) {
            return 0;
        }

        Component last = this.getComponents().get(this.getComponents().size() - 1);
        int maxLast = (int) (last.getyBase() + last.getDimension().getHeight());
        return this.getMaxY() - maxLast;
    }

    public int getMaxY() {

        return (int) (this.getY() + this.getDimension().getHeight());
    }

    public int getFrameBoxHeight() {

        return ClickGui.getTheme().getFrameHeight();
    }


    public boolean isPinned() {

        return pinned;
    }

    public void setPinned(boolean pinned) {

        this.pinned = pinned;
    }

    public boolean isMaximized() {

        return maximized;
    }

    public void setMaximized(boolean maximized) {

        this.maximized = maximized;
    }

    public boolean isMaximizible() {

        return maximizible;
    }

    public void setMaximizible(boolean maximizible) {

        this.maximizible = maximizible;
    }

    public boolean isVisable() {

        return visable;
    }

    public void setVisable(boolean visable) {

        this.visable = visable;
    }

    public boolean isPinnable() {

        return pinnable;
    }

    public void setPinnable(boolean pinnable) {

        this.pinnable = pinnable;
    }

    public int getTicksSinceScroll() {

        return ticksSinceScroll;
    }

    public void setTicksSinceScroll(int ticksSinceScroll) {

        this.ticksSinceScroll = ticksSinceScroll;
    }

    public int getScrollAmmount() {

        return scrollAmmount;
    }

    public void setScrollAmmount(int scrollAmmount) {

        this.scrollAmmount = scrollAmmount;
    }
}
