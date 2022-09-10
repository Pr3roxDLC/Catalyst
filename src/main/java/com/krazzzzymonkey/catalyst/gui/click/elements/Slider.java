package com.krazzzzymonkey.catalyst.gui.click.elements;

import com.krazzzzymonkey.catalyst.gui.click.ClickGui;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.HudEditor;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.listener.SliderChangeListener;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;

public class Slider extends Component {

    public boolean dragging = false;
    public double min, max, value;
    public double percent = 0;
    public String description;
    public Number type;

    private final SubMenu subMenu;
    private final ArrayList<SliderChangeListener> listeners = new ArrayList<SliderChangeListener>();


    public Slider(Number min, Number max, Number value, Component component, String text, String description, SubMenu subMenu) {

        super(0, 0, 99, 14, ComponentType.SLIDER, component, text, description);
        this.type = value;
        this.min = min.doubleValue();
        this.max = max.doubleValue();
        this.value = value.doubleValue();
        this.percent = (this.value - this.min) / (this.max - this.min);
        this.description = description;
        this.subMenu = subMenu;
    }

    public void addListener(SliderChangeListener listener) {

        listeners.add(listener);
    }

    @Override
    public void onMousePress(int x, int y, int buttonID) {
        int[] mouse = ClickGui.mouse;
        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
            mouse = HudEditor.mouse;
        }
        x -= this.getX();
        int x1 = (int) (getDimension().getWidth());
        int y1 = (int) (getDimension().getHeight());
        percent = (double) x / (double) x1;
        value = this.round(((max - min) * percent) + min, 2);

        if (Mouse.isButtonDown(0) && this.isMouseOver(mouse[0], mouse[1])) {
            this.dragging = true;
        }
        fireListeners();
    }

    private void fireListeners() {

        for (SliderChangeListener listener : listeners) {
            listener.onSliderChange(this);
        }
    }

    @Override
    public void onMouseRelease(int x, int y, int buttonID) {
        dragging = false;
    }

    @Override
    public void onUpdate() {

        if (Frame.isDragging) return;
        int[] mouse = ClickGui.mouse;
        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
            mouse = HudEditor.mouse;
        }

        if (!MouseUtils.isLeftClicked() && dragging) dragging = false;
        if (dragging) {
            if ((mouse[0]) <= this.getX()) {
                this.percent = 0;
                this.value = this.min;
                fireListeners();
            } else if ((mouse[0]) >= this.getX() + this.getDimension().getWidth()) {
                this.percent = 1;
                this.value = this.max;
                fireListeners();
            } else {

                int x1 = (int) (getDimension().getWidth());
                int x = Mouse.getEventX() / Minecraft.getMinecraft().gameSettings.guiScale;
                if (Minecraft.getMinecraft().currentScreen instanceof ClickGuiScreen) {
                    x = (int) (x / com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue());
                }

                x -= this.getX();
                percent = (double) x / (double) x1;
                value = this.round(((max - min) * percent) + min, 2);
                fireListeners();
            }
        }
    }

    public ArrayList<SliderChangeListener> getListeners() {

        return listeners;
    }


    public boolean isDragging() {

        return dragging;
    }

    public void setDragging(boolean dragging) {

        this.dragging = dragging;
    }

    public double getMin() {

        return min;
    }

    public void setMin(double min) {

        this.min = min;
    }

    public double getMax() {

        return max;
    }

    public void setMax(double max) {

        this.max = max;
    }

    public double getValue() {

        return value;
    }

    public String getRenderValue() {
        if (this.type instanceof Integer)
            return RenderUtils.DF(this.value, 0); // if(this.type instanceof Double)
        return RenderUtils.DF(this.value, 1);
    }

    public void setValue(double value) {

        this.value = value;
    }

    public double getPercent() {

        return percent;
    }


    public String getDescription() {

        return description;
    }

    public void setPercent(double percent) {

        this.percent = percent;
    }

    private double round(double valueToRound, int numberOfDecimalPlaces) {

        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = valueToRound * multipicationFactor;
        return Math.round(interestedInZeroDPs) / multipicationFactor;
    }

    public SubMenu getSubMenu() {

        return subMenu;
    }

    @Override
    public int getX(){
        return super.getX()+1;
    }

    @Override
    public Dimension getDimension(){
       return new Dimension(super.getDimension().width -2, super.getDimension().height);
    }
}
