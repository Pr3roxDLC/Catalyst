package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.CrystalClickCounter;
import com.krazzzzymonkey.catalyst.managers.TimerManager;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;


public class PlayerInfo extends Modules {

    public BooleanValue booleanBPS;
    public BooleanValue booleanTPS;
    public BooleanValue booleanFPS;
    public BooleanValue booleanPing;

    private Number xOffset;
    private Number yOffset;

    public ColorValue colorValue;
    public BooleanValue rainbow;

    public PlayerInfo() {
        super("PlayerInfo", ModuleCategory.HUD, "Displays various player info on hud", true);

        booleanBPS = new BooleanValue("BPS", true, "Counts speed in blocks per second");
        this.booleanFPS = new BooleanValue("FPS", true, "Counts frames per second");
        this.booleanPing = new BooleanValue("Ping", true, "Counts server ticks per second");
        this.booleanTPS = new BooleanValue("TPS", true, "Counts strength of connection to server");
        this.xOffset = new Number("X Offset", 100.0);
        this.yOffset = new Number("Y Offset", 0.0);
        this.colorValue = new ColorValue("Color", Color.CYAN, "The color of player info");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes the player info cycle through colors");
        this.addValue(booleanBPS, booleanFPS, booleanTPS, booleanPing, colorValue, rainbow, xOffset, yOffset);
    }

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;
    private final CrystalClickCounter fps = new CrystalClickCounter();

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;

        Color color = colorValue.getColor();

        if (rainbow.getValue())
            color = ColorUtils.rainbow();

        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());

        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();

        final DecimalFormat df = new DecimalFormat("0.0");
        final double deltaX = Minecraft.getMinecraft().player.posX - Minecraft.getMinecraft().player.prevPosX;
        final double deltaZ = Minecraft.getMinecraft().player.posZ - Minecraft.getMinecraft().player.prevPosZ;
        final double tickRate = (Minecraft.getMinecraft().timer.tickLength / 1000.0f);
        final String bps = df.format((MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) / tickRate));
        final String tps = df.format(TimerManager.INSTANCE.getTickRate());

        String FPS = "";
        String ping = "";
        String TPS = "";
        String BPS = "";

        if (booleanFPS.getValue()) {
            fps.onBreak();
            FPS = "\u00a7rFPS: \u00a7f" + fps.getCps() + " ";
        }
        if (booleanTPS.getValue()) {
            TPS = "\u00a7rTPS: \u00a7f" + tps + " ";
        }
        if (booleanBPS.getValue()) {
            BPS = "\u00a7rBPS: \u00a7f" + bps + " ";
        }

        try {
            if (booleanPing.getValue()) {
                String Ping = String.valueOf(Minecraft.getMinecraft().player.connection.getPlayerInfo(Minecraft.getMinecraft().player.getUniqueID()).getResponseTime());
                ping = "\u00a7rPING: \u00a7f" + Ping + " ";
            }
        } catch (NullPointerException ignored) {
        }

        GL11.glPushMatrix();

        if (ModuleManager.getModule("CustomFont").isToggled()) {
            Main.fontRenderer.drawStringWithShadow(FPS + ping + TPS + BPS, xPos, yPos, color.getRGB());
        } else {
            Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(FPS + ping + TPS + BPS, xPos, yPos, color.getRGB());
        }

        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
            if (ModuleManager.getModule("CustomFont").isToggled()) {
                RenderUtils.drawRect(xPos, yPos, xPos + Main.fontRenderer.getStringWidth(FPS + BPS + ping + TPS), yPos + 14,
                    ColorUtils.color(0, 0, 0, 100));
            } else
                RenderUtils.drawRect(xPos, yPos, xPos + Wrapper.INSTANCE.fontRenderer().getStringWidth(FPS + BPS + ping + TPS), yPos + 14,
                    ColorUtils.color(0, 0, 0, 100));
            if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(FPS + BPS + ping + TPS), yPos, yPos + 14))) {
                isAlreadyDragging = true;
            }

            if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                isAlreadyDragging = false;
            }

            if (!isAlreadyDragging || isDragging) {
                if (MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(FPS + BPS + ping + TPS), yPos, yPos + 14)) {
                    isDragging = true;
                }


                if (MouseUtils.isLeftClicked() && isDragging) {
                    finalMouseX = MouseUtils.getMouseX();
                    finalMouseY = MouseUtils.getMouseY();

                    xOffset.value = (double)finalMouseX - Main.fontRenderer.getStringWidth(FPS + BPS + ping + TPS) / 2;
                    yOffset.value = (double)finalMouseY;
                    MouseUtils.isDragging = true;
                } else isDragging = false;

            }
        }
        GL11.glPopMatrix();
    });
}


