package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

//TODO FACING DIR, BIOME

public class Coordinates extends Modules {
    private BooleanValue nether;

    private Number xOffset;
    private Number yOffset;
    private ModeValue alignment;

    public Coordinates() {
        super("Coordinates", ModuleCategory.HUD, "Displays coordinates on hud", true);
        this.nether = new BooleanValue("Nether", true, "Shows corresponding coordinates in nether");
        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("Y Offset", 275.0);
        this.alignment = new ModeValue("Align", new Mode("OneLine", true), new Mode("MultipleLines", false));
        this.addValue(nether, alignment, xOffset, yOffset);
    }

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());
        final DecimalFormat df = new DecimalFormat("0.0");
        String x = df.format(Wrapper.INSTANCE.player().posX);
        String y = df.format(Wrapper.INSTANCE.player().posY);
        String z = df.format(Wrapper.INSTANCE.player().posZ);
        if (Minecraft.getMinecraft().world.getBiome(Minecraft.getMinecraft().player.getPosition()).getBiomeName().equals("Hell")) {
            x = df.format(Wrapper.INSTANCE.player().posX * 8);
            y = df.format(Wrapper.INSTANCE.player().posY);
            z = df.format(Wrapper.INSTANCE.player().posZ * 8);
        }
        String X = "\u00a77X: \u00a7f" + x;
        String Y = "\u00a77Y: \u00a7f" + y;
        String Z = "\u00a77Z: \u00a7f" + z;

        String nX;
        String nY;
        String nZ;


        if (!Minecraft.getMinecraft().world.getBiome(Minecraft.getMinecraft().player.getPosition()).getBiomeName().equals("Hell")) {
            nX = df.format((Wrapper.INSTANCE.player().posX) / 8);
            nY = df.format((Wrapper.INSTANCE.player().posY));
            nZ = df.format((Wrapper.INSTANCE.player().posZ) / 8);
        } else {
            nX = df.format((Wrapper.INSTANCE.player().posX));
            nY = df.format((Wrapper.INSTANCE.player().posY));
            nZ = df.format((Wrapper.INSTANCE.player().posZ));
        }
        String NX = "\u00a7cX: \u00a7f" + nX;
        String NY = "\u00a7cY: \u00a7f" + nY;
        String NZ = "\u00a7cZ: \u00a7f" + nZ;

        String coords = X + " " + Y + " " + Z + "";
        String netherCoords = "\u00A7c[ " + NX + " " + NY + " " + NZ + " \u00A7c] ";

        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();

        GL11.glPushMatrix();
        if (ModuleManager.getModule("CustomFont").isToggled()) {
            if (alignment.getMode("MultipleLines").isToggled()) {

                Main.fontRenderer.drawStringWithShadow(coords, xPos, yPos, ClickGui.getColor());
                if (nether.getValue())
                    Main.fontRenderer.drawStringWithShadow(netherCoords, xPos, yPos + 14, ClickGui.getColor());
            } else {
                if(nether.getValue()) {
                    coords = coords + " " + netherCoords;
                }
                Main.fontRenderer.drawStringWithShadow(coords, xPos, yPos, ClickGui.getColor());
            }
        } else {
            if (alignment.getMode("MultipleLines").isToggled()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(coords, xPos, yPos, ClickGui.getColor());
                if (nether.getValue())
                    Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(netherCoords, xPos, yPos + 14, ClickGui.getColor());
            }else {
                if(nether.getValue()) {
                    coords = coords + " " + netherCoords;
                }

                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(coords, xPos, yPos, ClickGui.getColor());
            }
        }

        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
            if (ModuleManager.getModule("CustomFont").isToggled()) {
                RenderUtils.drawRect(xPos, yPos, xPos + Main.fontRenderer.getStringWidth(coords), yPos + 14,
                        ColorUtils.color(0, 0, 0, 100));
            } else
                RenderUtils.drawRect(xPos, yPos, xPos + Wrapper.INSTANCE.fontRenderer().getStringWidth(coords), yPos + 14,
                        ColorUtils.color(0, 0, 0, 100));
            if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(coords), yPos, yPos + 14))) {
                isAlreadyDragging = true;
            }

            if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                isAlreadyDragging = false;
            }

            if (!isAlreadyDragging || isDragging) {
                if (MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(coords), yPos, yPos + 14)) {
                    isDragging = true;
                }


                if (MouseUtils.isLeftClicked() && isDragging) {
                    finalMouseX = MouseUtils.getMouseX();
                    finalMouseY = MouseUtils.getMouseY();

                    xOffset.value = (double)finalMouseX - Main.fontRenderer.getStringWidth(coords) / 2;
                    yOffset.value = (double)finalMouseY;
                    MouseUtils.isDragging = true;
                } else isDragging = false;

            }
        }
        GL11.glPopMatrix();
    });

}

























