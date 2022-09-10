package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class PotionEffects extends Modules {

    public BooleanValue rainbow;
    private ColorValue colorValue;
    private DoubleValue hueOffset;
    private IntegerValue rainbowSpeed;

    private Number xOffset;
    private Number yOffset;

    public PotionEffects() {
        super("PotionEffects", ModuleCategory.HUD, "Displays active potion effects on hud", true);
        this.colorValue = new ColorValue("Color", Color.CYAN, "Changes the text color of the effect name");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes the text color cycle through colors");
        this.rainbowSpeed = new IntegerValue("RainbowSpeed", 100, 0, 100, "The speed at which the colors cycle");
        this.hueOffset = new DoubleValue("HueOffset", 0.999, 0.1D, 1D, "The difference of color between each effect when rainbow is active");

        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("y Offset", 15.0);

        this.addValue(hueOffset, rainbowSpeed, colorValue, rainbow, xOffset, yOffset);
    }


    int color;
    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;
    ArrayList<PotionEffect> effects = new ArrayList<>();

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;
        float offset = 0;

        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());

        color = colorValue.getColor().getRGB();


        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();

        effects = new ArrayList<>(mc.player.getActivePotionEffects());


      //  effects.sort(Comparator.comparing(PotionEffect::getEffectName));

        effects.sort((h1, h2) -> {
                String s1 = h1.getPotion().getName() + " " + h1.getAmplifier() + 1 + " " + Potion.getPotionDurationString(h1, 1.0f);
                String s2 = h2.getPotion().getName() + " " + h2.getAmplifier() + 1 + " " + Potion.getPotionDurationString(h2, 1.0f);
                final int cmp;
                if (ModuleManager.getModule("CustomFont").isToggled()) {
                    cmp = Main.fontRenderer.getStringWidth(s2) - Main.fontRenderer.getStringWidth(s1);
                } else {
                    cmp = Wrapper.INSTANCE.fontRenderer().getStringWidth(s2) - Wrapper.INSTANCE.fontRenderer().getStringWidth(s1);
                }
                return (cmp != 0) ? cmp : s2.compareTo(s1);
            });











        GL11.glPushMatrix();


        for (PotionEffect potionEffect : effects ){
            String name = I18n.format(potionEffect.getPotion().getName());
            int amplifier = potionEffect.getAmplifier() + 1;
            String effect = name + " " + amplifier + ChatColor.GRAY + " " + Potion.getPotionDurationString(potionEffect, 1.0f);

            if (ModuleManager.getModule("CustomFont").isToggled()) {
                if (xPos > sr.getScaledWidth() / 2)
                    xPos = xOffset.getValue().intValue() - Main.fontRenderer.getStringWidth(effect);
                Main.fontRenderer.drawStringWithShadow(effect, xPos, yPos, rainbow.getValue() ? ColorUtils.rainbow(offset, rainbowSpeed.getValue()).getRGB() : color);
            } else {
                if (xPos > sr.getScaledWidth() / 2)
                    xPos = xOffset.getValue().intValue() - Wrapper.INSTANCE.fontRenderer().getStringWidth(effect);
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(effect, xPos, yPos, rainbow.getValue() ? ColorUtils.HUDRainbow(offset, rainbowSpeed.getValue()).getRGB() : color);
            }
            offset += hueOffset.getValue() / 10;

            if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
                if (ModuleManager.getModule("CustomFont").isToggled()) {
                    RenderUtils.drawRect(xPos, yPos, xPos + Main.fontRenderer.getStringWidth(effect), yPos + 12,
                        ColorUtils.color(0, 0, 0, 100));
                } else
                    RenderUtils.drawRect(xPos, yPos, xPos + Wrapper.INSTANCE.fontRenderer().getStringWidth(effect), yPos + 12,
                        ColorUtils.color(0, 0, 0, 100));
                if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(effect), yPos, yPos + 12))) {
                    isAlreadyDragging = true;
                }

                if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                    isAlreadyDragging = false;
                }

                if (!isAlreadyDragging || isDragging) {
                    if (MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(effect), yPos, yPos + 12)) {
                        isDragging = true;
                    }


                    if (MouseUtils.isLeftClicked() && isDragging) {
                        finalMouseX = MouseUtils.getMouseX();
                        finalMouseY = MouseUtils.getMouseY();


                        xOffset.value = (double)finalMouseX;

                        yOffset.value = (double)finalMouseY;
                        MouseUtils.isDragging = true;
                    } else isDragging = false;

                }
            }

            if (yOffset.getValue() > sr.getScaledHeight() / 2) {
                yPos -= 12;
            } else {
                yPos += 12;
            }

        }
        GL11.glPopMatrix();

    });
}
