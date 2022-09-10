package com.krazzzzymonkey.catalyst.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseUtils {

    public static boolean isLeftClicked() {
        return Mouse.isButtonDown(0);
    }

    public static boolean isRightClicked() {
        return Mouse.isButtonDown(1);
    }

    public static boolean isDragging = false;

    public static boolean isMouseOver(int left, int right, int up, int down) {

        return MouseUtils.getMouseX() >= left && MouseUtils.getMouseX() <= right && MouseUtils.getMouseY() >= up && MouseUtils.getMouseY() <= down;
    }


    public static boolean isMiddleClicked() {
        return Mouse.isButtonDown(2);
    }

    public static int getMouseX() {
        if (Minecraft.getMinecraft().gameSettings.guiScale == 0) Minecraft.getMinecraft().gameSettings.guiScale = 3;
        return Mouse.getEventX() / Minecraft.getMinecraft().gameSettings.guiScale;
    }

    public static int getMouseY() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        if (Minecraft.getMinecraft().gameSettings.guiScale == 0) Minecraft.getMinecraft().gameSettings.guiScale = 3;
        return scaledResolution.getScaledHeight() - Mouse.getEventY() / Minecraft.getMinecraft().gameSettings.guiScale;

    }

}
