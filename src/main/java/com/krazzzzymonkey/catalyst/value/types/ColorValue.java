package com.krazzzzymonkey.catalyst.value.types;

import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.value.Value;

import java.awt.*;

public class ColorValue extends Value<Integer> {

    protected Double min, max;
    protected boolean rainbow;
    private final String description;
    private int colorInt = ColorUtils.ColorToInt(Color.CYAN);
    private Color color;
    private Color lineColor;
    private final int[] triPos = {-10000, -10000};
    private int selColorY;
    private int selOpacityY;


    public ColorValue(String name, int defaultValue, String description) {
        super(name, defaultValue, description);
        this.colorInt = defaultValue;
        color = new Color(defaultValue, true);
        this.lineColor = color;
        this.description = description;
        this.selColorY = -10000;
        this.selOpacityY = 54;

    }

    public ColorValue(String name, Color defaultColor, String description) {
        super(name, ColorUtils.ColorToInt(defaultColor), description);
        this.colorInt = defaultColor.getRGB();
        this.lineColor = defaultColor;
        color = defaultColor;
        this.description = description;
        this.triPos[0] = -10000;
        this.triPos[1] = -10000;
        this.selColorY = -10000;
        this.selOpacityY = 54;
    }


    public Double getMin() {
        return min;
    }

    public String getDescription() {

        return description;
    }

    public Double getMax() {

        return max;
    }

    public boolean getRainbow() {
        return rainbow;
    }

    public int getColorInt(){return  colorInt;}
    public void setColorInt(int rgb){
        colorInt = rgb;
        color = new Color(rgb, true);
    }

    public Color getColor() { return color; }
    public void setColor(Color color){this.color = color;
    this.colorInt = color.getRGB();}

    public void setRainbow(boolean setRainbow) {
        rainbow = setRainbow;
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
    public Color getLineColor() {
        return this.lineColor;
    }

    public void setTriPos(int x, int y){
        this.triPos[0] = x;
        this.triPos[1] = y;
    }

    public void setSelColorY(int y){
        this.selColorY = y;
    }

    public void setSelOpacityY(int y){
        this.selOpacityY = y;
    }

    public void setLineColor(int color){
        this.lineColor = new Color(color);
    }

    public void setLineColor(Color color){
        this.lineColor = color;
    }



}
