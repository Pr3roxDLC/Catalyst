package com.krazzzzymonkey.catalyst.utils.visual;


import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;

public class Notification {

    public static ArrayList<Notification> notifications = new ArrayList<>();
    private final String title;
    private final String text;
    private final Color color;
    private final Timer timer = new Timer();
    private final long delay;
    private int width;

    public Notification(String title, String text, long delay, Color color) {
        this.title = title;
        this.text = text;
        this.color = color;
        this.delay = delay;
        this.width = Main.fontRenderer.getStringWidth(text) + 10;
        if (title.length() > text.length()) {
            this.width = Main.fontRenderer.getStringWidth(title) + 10;
        }
        this.timer.reset();

        notifications.add(this);
    }

    ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    int alpha = 255;
    int fadeTime = 0;
    int transition = 0;
    int prevHeight = -1;

    public void render(int height) {
        if (prevHeight == -1) {
            prevHeight = height;
        }
        if (height > prevHeight) {
            prevHeight = prevHeight + 3;
        }
        if(height < prevHeight){
            prevHeight = height;
        }


        if (this.timer.passedMs(this.delay)) {
            notifications.remove(this);
            return;
        }


        if (transition < width) {
            transition = transition + 6;
        }
        if (transition > width) transition = width;

        if (timer.passedMs((delay / 2) + fadeTime)) {
            fadeTime = fadeTime + (int) (delay / 2) / 51;
            alpha = alpha - 5;
        }

        if (alpha <= 10) alpha = 10;
        RenderUtils.drawBorderedRect(scaledResolution.getScaledWidth() - transition, scaledResolution.getScaledHeight() - prevHeight, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight() - prevHeight + 30, 1, ColorUtils.rainbow().getRGB() + (alpha << 24), new Color(0, 0, 0, 255).getRGB() + (alpha << 24));
        Main.fontRenderer.drawString(title, scaledResolution.getScaledWidth() - transition + 5, scaledResolution.getScaledHeight() - prevHeight + 4, -1 + (alpha << 24));
        Main.smallFontRenderer.drawString(text, scaledResolution.getScaledWidth() - transition + 5, scaledResolution.getScaledHeight() - prevHeight + 18, -1 + (alpha << 24));

    }

}

