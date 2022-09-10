package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.Number;

import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PlayerRadar extends Modules {

    private IntegerValue amount;
    private Number xOffset;
    private Number yOffset;

    public PlayerRadar() {
        super("PlayerRadar", ModuleCategory.HUD, "Shows players in render distance on hud", true);
        this.xOffset = new Number("X Offset", 100.0);
        this.yOffset = new Number("Y Offset", 30.0);
        this.amount = new IntegerValue("Players", 30, 1, 100, "Shows max amount of how many players will be rendered at once");
        this.addValue(amount, xOffset, yOffset);
    }

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;


    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {

        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();
        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());
        int i = 0;
        for (Object o : Wrapper.INSTANCE.world().loadedEntityList) {
            if (o instanceof EntityPlayer && o != Minecraft.getMinecraft().player) {
                i++;
                if (i > amount.getValue()) return;
                EntityPlayer entity = (EntityPlayer) o;
                float range = Wrapper.INSTANCE.player().getDistance(entity);
                float health = entity.getHealth() + entity.getAbsorptionAmount();

                String heal = " \u00a72[" + RenderUtils.DF(health, 0) + "] ";
                if (health >= 12.0) {
                    heal = " \u00a72[" + RenderUtils.DF(health, 0) + "] ";
                } else if (health >= 4.0) {
                    heal = " \u00a76[" + RenderUtils.DF(health, 0) + "] ";
                } else {
                    heal = " \u00a74[" + RenderUtils.DF(health, 0) + "] ";
                }

                String name = entity.getGameProfile().getName();
                String str = name + heal + "\u00a77" + "[" + RenderUtils.DF(range, 0) + "]";

                int color;
                if (entity.isInvisible()) {
                    color = ColorUtils.color(155, 155, 155, 255);
                } else {
                    color = ColorUtils.color(255, 255, 255, 255);
                }

                GL11.glPushMatrix();

                if (ModuleManager.getModule("CustomFont").isToggled()) {
                    if (xPos > sr.getScaledWidth() / 2)
                        xPos = xOffset.getValue().intValue() - Main.fontRenderer.getStringWidth(str);
                    Main.fontRenderer.drawStringWithShadow(str, xPos, yPos, color);
                } else {
                    if (xPos > sr.getScaledWidth() / 2)
                        xPos = xOffset.getValue().intValue() - Wrapper.INSTANCE.fontRenderer().getStringWidth(str);
                    Wrapper.INSTANCE.fontRenderer().drawString(str, xPos, yPos, color);
                }

                if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
                    if (ModuleManager.getModule("CustomFont").isToggled()) {
                        RenderUtils.drawRect(xPos, yPos, xPos + Main.fontRenderer.getStringWidth(str), yPos + 12,
                                ColorUtils.color(0, 0, 0, 100));
                    } else
                        RenderUtils.drawRect(xPos, yPos, xPos + Wrapper.INSTANCE.fontRenderer().getStringWidth(str), yPos + 12,
                                ColorUtils.color(0, 0, 0, 100));

                    if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(str), yPos, yPos + 12))) {
                        isAlreadyDragging = true;
                    }

                    if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                        isAlreadyDragging = false;
                    }

                    if (!isAlreadyDragging || isDragging) {
                        if (MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(str), yPos, yPos + 12)) {
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
                GL11.glPopMatrix();
            }
        }


    });

}
