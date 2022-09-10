package com.krazzzzymonkey.catalyst.gui.click.elements;

import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.listener.ColorChangeListener;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;

import java.awt.*;
import java.util.ArrayList;

//TODO FIX rainbow not updating, FIX color remaining as default value until the player opens up the ClickGUI
public class ColorPicker extends Component {

    private boolean enabled = false;
    public int color;
    private int lineColor;
    public boolean isRainbow;
    public String description;
    private final SubMenu subMenu;
    private final ArrayList<ColorChangeListener> listeners = new ArrayList<ColorChangeListener>();
    private final int[] triPos;
    private int selColorY;
    private int selOpacityY;


    public ColorPicker(int xPos, int yPos, int width, int height, int color, int lineColor, int[] tryPos, int selColorY, int selOpacityY, boolean rainbow, Component component, String text, String description, ColorValue colorValue, SubMenu subMenu) {

        super(xPos, yPos, width, height, ComponentType.COLOR_PICKER, component, text, description);
        this.lineColor = lineColor;
        this.color = color;
        this.isRainbow = rainbow;
        this.description = description;
        this.subMenu = subMenu;
        this.triPos = tryPos;
        this.selColorY = selColorY;
        this.selOpacityY = selOpacityY;
    }

    public void addListener(ColorChangeListener listener) {

        listeners.add(listener);
    }

    @Override
    public void onMousePress(int x, int y, int buttonID) {

        this.enabled = !this.enabled;

        fireListeners();
    }

    private void fireListeners() {

        for (ColorChangeListener listener : listeners) {
            listener.onColorChangeClick(this);
        }
    }

    @Override
    public void onMouseRelease(int x, int y, int buttonID) {
    }

    @Override
    public void onUpdate() {
        fireListeners();
    }

    @Override
    public void onMouseDrag(int x, int y) {
    }

    public ArrayList<ColorChangeListener> getListeners() {

        return listeners;
    }


    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    public Color getColor() {
        int opacity = (int)((this.getSelOpacityY() -15) *6.54);
        if(opacity > 255)opacity=255;
        if(opacity < 0)opacity= 0;
        Color c = new Color(color);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity);
    }

    public int[] getTriPos() {

        return this.triPos;
    }

    public int getSelColorY() {

        return this.selColorY;
    }

    public int getSelOpacityY() {

        return this.selOpacityY;
    }

    public void setTriPos(int x, int y) {
        this.triPos[0] = x;
        this.triPos[1] = y;

    }

    public void setSelColorY(int y){
        this.selColorY = y;
    }

    public void setSelOpacityY(int y){
        this.selOpacityY = y;
    }

    public Color getLineColor(){
        return new Color(this.lineColor);
    }


    public void setLineColor(Color color){
        this.lineColor = color.getRGB();
    }
    public void setLineColor(int color){
        this.lineColor = color;
    }

    public boolean isRainbow() {

        return isRainbow;
    }

    public String getDescription() {

        return description;
    }

    public void setRainbow(boolean rainbow) {
        this.isRainbow = rainbow;
    }

    public void setColor(Color color) {
        this.color = color.getRGB();
    }

    public SubMenu getSubMenu() {

        return subMenu;
    }
}
