package com.krazzzzymonkey.catalyst.utils.visual;

import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.Sys;

import javax.vecmath.Color4f;
import java.awt.*;

public class ColorUtils {

    public static float[] ColorToGLFloatColor(Color color){
        return new float[]{color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f};
    }

    public static Color rainbow() {
        long offset = 999999999999L;
        float fade = 1.0f;
        float hue = (float) (System.nanoTime() + offset) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int) color);

        return new Color((float) c.getRed() / 255.0f * fade, (float) c.getGreen() / 255.0f * fade, (float) c.getBlue() / 255.0f * fade, (float) c.getAlpha() / 255.0f);
    }

    static double rainbowSpeed = 0;
    static double rainbowSpeedTicks = 0;


    public static Color getColorFromDurability(ItemStack itemStack){

        Color firstCol = Color.GREEN;
        Color secondCol = Color.RED;

        if(!itemStack.isItemDamaged()) return Color.GREEN;
        if(!itemStack.isItemEnchanted()) return new Color(0.38f*255f, 0.19f*255f, 0.608f*255f, 255f);


        float dura = itemStack.getMaxDamage() - itemStack.getItemDamage();
        float p = dura/itemStack.getMaxDamage();


        int r = (int)( firstCol.getRed() * p + secondCol.getRed() * (1 - p));
        int g = (int)( firstCol.getGreen() * p + secondCol.getGreen() * (1 - p));
        int b = (int) (firstCol.getBlue() * p + secondCol.getBlue() * (1 - p));
        try {
            return new Color(r, g, b);
        }catch (IllegalArgumentException e){
            return Color.green;
        }
    }


    public static Color rainbow(float offset, double rainbowSpeedTicks) {
        ColorUtils.rainbowSpeedTicks = ClickGui.rainbowSpeed.getValue();
        float fade = 1.0f;
        float hue = (float) (rainbowSpeed * 1000000 + 999999999999L) / 1.0E10f % 1.0f;
        hue = hue + offset;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int) color);
        return new Color((float) c.getRed() / 255.0f * fade, (float) c.getGreen() / 255.0f * fade, (float) c.getBlue() / 255.0f * fade, (float) c.getAlpha() / 255.0f);

    }

    @SubscribeEvent
    public void onUpdateRainbow(TickEvent.ClientTickEvent event) {
        rainbowSpeed += rainbowSpeedTicks;
    }

    static double HUDRainbowSpeed = 0;
    static double HUDRainbowSpeedTicks = 0;

    public static Color HUDRainbow(float offset, double rainbowSpeedTicks) {
        ColorUtils.HUDRainbowSpeedTicks = rainbowSpeedTicks;
        float fade = 1.0f;
        float hue = (float) (HUDRainbowSpeed * 1000000 + 999999999999L) / 1.0E10f % 1.0f;
        hue = hue + offset;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int) color);
        return new Color((float) c.getRed() / 255.0f * fade, (float) c.getGreen() / 255.0f * fade, (float) c.getBlue() / 255.0f * fade, (float) c.getAlpha() / 255.0f);

    }

    @SubscribeEvent
    public void onUpdateHUDRainbow(TickEvent.ClientTickEvent event) {
        HUDRainbowSpeed += HUDRainbowSpeedTicks;
    }

    public static int ColorSlider(float hue, float saturation) {
        final float[] hueColor = {(hue % (360 * 32)) / (360f * 32)};

        int rgb = Color.HSBtoRGB(hueColor[0], saturation, 1);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        return new Color(red, green, blue, 255).getRGB();
    }

    public static int color(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int color(float r, float g, float b, float a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int getColor(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int getColor(int r, int g, int b) {
        return 255 << 24 | r << 16 | g << 8 | b;
    }

    public static int ColorToInt(Color color){
        int rgb = color.getRed();
        rgb = (rgb << 8) + color.getGreen();
        rgb = (rgb << 8) + color.getBlue();
       // rgb = (rgb << 8) + color.getAlpha();
        return rgb;
    }

    public static Color IntToColor(int rgb){

        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        return new Color(red,green,blue);

    }
}
