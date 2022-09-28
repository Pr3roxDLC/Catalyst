package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

//TODO DISPLAY USERNAME FROM THE WEBSERVER
public class Watermark extends Modules {

    private static int color;

    private BooleanValue version;
    private BooleanValue rainbow;
    private ColorValue colorValue;
    private Number xOffset;
    private Number yOffset;
    private DoubleValue scale;


    File file = FileManager.getAssetFile("gui/watermark.png");

    ResourceLocation resource;


    public Watermark() {
        super("Watermark", ModuleCategory.HUD, "Displays client name on hud", true);
        this.version = new BooleanValue("Mod Version", true, "");
        this.colorValue = new ColorValue("Color", Color.CYAN, "");
        this.rainbow = new BooleanValue("Rainbow", false, "");
        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("Y Offset", 0.0);
        this.scale = new DoubleValue("Size", 0.5, 0.01f, 1, "");
        this.addValue(xOffset, yOffset, scale);
        {

        }
    }

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;
        if (resource == null) {
            try {
                resource = Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());

        if (!rainbow.getValue()) {
            color = colorValue.getColor().getRGB();
        } else {
            color = ColorUtils.rainbow().getRGB();
        }

        GL11.glPushMatrix();
        String ModName = Main.NAME;
        String ModVer = "";

        if (version.getValue()) {
            ModVer = " " + Main.VERSION;
        }

        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();

        float SCALE = scale.getValue().floatValue();

        GlStateManager.enableAlpha();
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.scale(SCALE, SCALE, SCALE);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(xPos / SCALE, yPos / SCALE, 0, 0, 256, 75);
        GlStateManager.disableAlpha();
        GlStateManager.scale(1 / SCALE, 1 / SCALE, 1 / SCALE);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);


        GlStateManager.scale(1, 1, 1);
        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
            RenderUtils.drawRect(xPos, yPos, (xPos + (256 * SCALE)), (yPos + (75 * SCALE)), ColorUtils.color(0, 0, 0, 100));

            if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, (int) (xPos + (256* SCALE)), yPos, (int) (yPos + (75*SCALE))))) {
                isAlreadyDragging = true;
            }

            if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                isAlreadyDragging = false;
            }

            if (!isAlreadyDragging || isDragging) {
                if (MouseUtils.isMouseOver(xPos, xPos + 256, yPos, yPos + 75)) {
                    isDragging = true;
                }


                if (MouseUtils.isLeftClicked() && isDragging) {
                    finalMouseX = MouseUtils.getMouseX();
                    finalMouseY = MouseUtils.getMouseY();
                    xOffset.setValue((double) finalMouseX - (256* SCALE)/2);
                    yOffset.setValue((double) finalMouseY);
                } else isDragging = false;

            }
        }
        GL11.glPopMatrix();

    });


}
