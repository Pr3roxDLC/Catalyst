package com.krazzzzymonkey.catalyst.gui.click;

import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.ProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class HudEditorMainMenu extends HudGuiScreen {

    protected GuiScreen prevScreen;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mc.player == null || mc.world == null) {
            this.drawDefaultBackground();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public HudEditorMainMenu(GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
    }


    @Override
    public void handleInput() throws IOException {
        int scale = mc.gameSettings.guiScale;

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == 1) {
                    mc.displayGuiScreen(prevScreen);
                    FileManager.saveModules(ProfileManager.currentProfile);
                    FileManager.saveClickGui();
                }
            }
        }


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
