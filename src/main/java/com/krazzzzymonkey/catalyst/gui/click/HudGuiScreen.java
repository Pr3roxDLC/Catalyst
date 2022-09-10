package com.krazzzzymonkey.catalyst.gui.click;

import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.managers.ProfileManager;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class HudGuiScreen extends GuiScreen {

    public static HudEditor hudGui;
    public static int[] mouse = new int[2];


    public HudGuiScreen() {
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ClickGuiScreen.tooltip = null;
        hudGui.render();
        if (ClickGuiScreen.tooltip != null && i > 15) ClickGuiScreen.tooltip.render();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        super.initGui();
    }

    int i = 0;
    int previousMouseLocation = -1;

    @Override
    public void updateScreen() {
        HudEditor.onUpdate();

        if (previousMouseLocation == MouseUtils.getMouseX() + MouseUtils.getMouseY()) {
            i++;
        } else i = 0;
        previousMouseLocation = MouseUtils.getMouseX() + MouseUtils.getMouseY();
        if (ClickGuiScreen.tooltip != null && i > 10) {
            ClickGui.tooltip.render();
        }

        super.updateScreen();

    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        ModuleManager.getModule("HudEditor").setToggled(false);
        FileManager.saveModules(ProfileManager.currentProfile);
        super.onGuiClosed();
    }


    @Override
    public void handleInput() throws IOException {
        int scale = mc.gameSettings.guiScale;
        /* mc.gameSettings.guiScale = 2;*/


        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                int mouseX = Mouse.getEventX() * scaledResolution.getScaledWidth() / mc.displayWidth;
                int mouseY = scaledResolution.getScaledHeight() - Mouse.getEventY() * scaledResolution.getScaledHeight() / mc.displayHeight - 1;

                if (Mouse.getEventButton() == -1) {
                    if (Mouse.getEventDWheel() != 0) {
                        int x = mouseX;
                        int y = mouseY;
                        hudGui.onMouseScroll((Mouse.getEventDWheel() / 100) * 3);
                    }

                    hudGui.onMouseUpdate(mouseX, mouseY);
                    mouse[0] = mouseX;
                    mouse[1] = mouseY;
                } else if (Mouse.getEventButtonState()) {
                    hudGui.onMouseClick(mouseX, mouseY, Mouse.getEventButton());
                } else {
                    hudGui.onMouseRelease(mouseX, mouseY);
                }
            }
        }

        mc.gameSettings.guiScale = scale;

        super.handleInput();
    }
}
