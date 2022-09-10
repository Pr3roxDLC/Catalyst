package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.managers.ProfileManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Profile extends Modules {

    private static int color;

    private BooleanValue rainbow;
    private ColorValue colorValue;

    private Number xOffset;
    private Number yOffset;

    public Profile() {
        super("Profile", ModuleCategory.HUD, "Displays what Catalyst Profile is currently active", true);

        this.colorValue = new ColorValue("Color", Color.CYAN, "Changes the color of the text");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes the text cycle through colors");
        this.xOffset = new Number("X Offset", 500.0);
        this.yOffset = new Number("Y Offset", 300.0);
        this.addValue(colorValue, rainbow, xOffset, yOffset);

    }

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;


    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {

        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());

        if (!rainbow.getValue()) {
            color = colorValue.getColor().getRGB();
        } else {
            color = ColorUtils.rainbow().getRGB();
        }

        GL11.glPushMatrix();
        String profile = "Profile:\u00A7f " + ProfileManager.currentProfile;


        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();

        if (ModuleManager.getModule("CustomFont").isToggled()) {
            Main.fontRenderer.drawStringWithShadow(profile, (float) xPos, (float) yPos, color);
        } else {
            Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(profile, xPos, yPos, color);
        }

        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
            try {
                if (ModuleManager.getModule("CustomFont").isToggled()) {
                    RenderUtils.drawRect(xPos, yPos, xPos + Main.fontRenderer.getStringWidth(profile), yPos + 14,
                        ColorUtils.color(0, 0, 0, 100));
                } else
                    RenderUtils.drawRect(xPos, yPos, xPos + Wrapper.INSTANCE.fontRenderer().getStringWidth(profile), yPos + 14,
                        ColorUtils.color(0, 0, 0, 100));
                if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(profile), yPos, yPos + 14))) {
                    isAlreadyDragging = true;
                }

                if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                    isAlreadyDragging = false;
                }

                if (!isAlreadyDragging || isDragging) {
                    if (MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(profile), yPos, yPos + 14)) {
                        isDragging = true;
                    }


                    if (MouseUtils.isLeftClicked() && isDragging) {
                        finalMouseX = MouseUtils.getMouseX();
                        finalMouseY = MouseUtils.getMouseY();
                        xOffset.value = (double) finalMouseX - 30;
                        yOffset.value = (double) finalMouseY;
                    } else isDragging = false;

                }
            } catch (NullPointerException ignored) {
            }
        }
        GL11.glPopMatrix();

    });


}
