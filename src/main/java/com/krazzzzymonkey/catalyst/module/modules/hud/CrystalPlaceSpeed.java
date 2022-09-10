package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.CrystalClickCounter;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import dev.tigr.simpleevents.listener.Priority;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

//TODO REFACTER TO CRYSTALS PER SEC

public class CrystalPlaceSpeed extends Modules {
    private final CrystalClickCounter placeSpeed = new CrystalClickCounter();
    private final CrystalClickCounter breakSpeed = new CrystalClickCounter();

    private static int color;

    private BooleanValue rainbow;
    private ColorValue colorValue;

    private Number xOffset;
    private Number yOffset;

    public CrystalPlaceSpeed() {
        super("CrystalsPerSecond", ModuleCategory.HUD, "Shows you how many crystals you explode every second", true);

        this.colorValue = new ColorValue("Color", Color.CYAN, "Changes the color of the text");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes the text cycle through colors");
        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("Y Offset", 0.0);
        this.addValue(colorValue, rainbow, xOffset, yOffset);

    }

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;


    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(Priority.HIGHEST, event -> {
        if(event.getSide() == PacketEvent.Side.OUT) {

            Packet packet = event.getPacket();
            if (packet instanceof CPacketUseEntity) {
                CPacketUseEntity cPacketUseEntity = (CPacketUseEntity) packet;
                Entity entity = cPacketUseEntity.getEntityFromWorld(mc.player.world);
                if (entity instanceof EntityEnderCrystal && cPacketUseEntity.getAction() == CPacketUseEntity.Action.ATTACK) {
                    breakSpeed.onBreak();
                }
            }
        }
    });

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {

        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());

        if (!rainbow.getValue()) {
            color = colorValue.getColor().getRGB();
        } else {
            color = ColorUtils.rainbow().getRGB();
        }

        GL11.glPushMatrix();
        String CrystalsPerSecond = "Crystals Per Second:\u00A7f " + breakSpeed.getCps();


        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();

        if (ModuleManager.getModule("CustomFont").isToggled()) {
            Main.fontRenderer.drawStringWithShadow(CrystalsPerSecond, (float) xPos, (float) yPos, color);
            //Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(ModName + "\u00A7f" +  ModVer, xPos, yPos, color);
        } else {
            Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(CrystalsPerSecond, xPos, yPos, color);
        }

        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
            try {
                if (ModuleManager.getModule("CustomFont").isToggled()) {
                    RenderUtils.drawRect(xPos, yPos, xPos + Main.fontRenderer.getStringWidth(CrystalsPerSecond), yPos + 14,
                            ColorUtils.color(0, 0, 0, 100));
                } else
                    RenderUtils.drawRect(xPos, yPos, xPos + Wrapper.INSTANCE.fontRenderer().getStringWidth(CrystalsPerSecond), yPos + 14,
                            ColorUtils.color(0, 0, 0, 100));
                if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(CrystalsPerSecond), yPos, yPos + 14))) {
                    isAlreadyDragging = true;
                }

                if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                    isAlreadyDragging = false;
                }

                if (!isAlreadyDragging || isDragging) {
                    if (MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(CrystalsPerSecond), yPos, yPos + 14)) {
                        isDragging = true;
                    }


                    if (MouseUtils.isLeftClicked() && isDragging) {
                        finalMouseX = MouseUtils.getMouseX();
                        finalMouseY = MouseUtils.getMouseY();
                        xOffset.value = (double)finalMouseX - 30;
                        yOffset.value = (double)finalMouseY;
                    } else isDragging = false;

                }
            } catch (NullPointerException ignored) {
            }
        }
        GL11.glPopMatrix();

    });


}
