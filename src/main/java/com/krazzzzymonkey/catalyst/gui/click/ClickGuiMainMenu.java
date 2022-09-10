package com.krazzzzymonkey.catalyst.gui.click;

import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.ProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class ClickGuiMainMenu extends ClickGuiScreen {

    protected GuiScreen prevScreen;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mc.player == null || mc.world == null) {
            this.drawDefaultBackground();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public ClickGuiMainMenu(GuiScreen prevScreen) {
        this.prevScreen = prevScreen;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        searchTickPos = 0;
        searchField.setCanLoseFocus(false);
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }

    }

    @Override
    public void handleInput() throws IOException {
        if (Keyboard.isCreated()) {
            Keyboard.enableRepeatEvents(true);

            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                    if (searchField.isFocused()) {
                        searchField.textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
                    }
                    if (Keyboard.getEventKey() == 1) {
                        mc.displayGuiScreen(prevScreen);
                        FileManager.saveModules(ProfileManager.currentProfile);
                        FileManager.saveClickGui();
                    } else {
                        clickGui.onkeyPressed(Keyboard.getEventKey(), Keyboard.getEventCharacter());
                    }
                } else {
                    clickGui.onKeyRelease(Keyboard.getEventKey(), Keyboard.getEventCharacter());
                }
            }
        }

        if (Mouse.isCreated()) {
            while (Mouse.next()) {

                ScaledResolution scaledResolution = new ScaledResolution(mc);

                if (Minecraft.getMinecraft().gameSettings.guiScale == 0) {
                    Minecraft.getMinecraft().gameSettings.guiScale = 2;
                }

                int mouseX = (int) ((Mouse.getEventX() / Minecraft.getMinecraft().gameSettings.guiScale) / (com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()));
                int mouseY = (int) ((scaledResolution.getScaledHeight() - Mouse.getEventY() / Minecraft.getMinecraft().gameSettings.guiScale) / (com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()));

                if (Mouse.getEventButton() == -1) {
                    if (Mouse.getEventDWheel() != 0) {
                        clickGui.onMouseScroll((Mouse.getEventDWheel() / 100) * 3);
                    }

                    clickGui.onMouseUpdate(mouseX, mouseY);
                    mouse[0] = mouseX;
                    mouse[1] = mouseY;
                } else if (Mouse.getEventButtonState()) {
                    clickGui.onMouseClick(mouseX, mouseY, Mouse.getEventButton());
                } else {
                    clickGui.onMouseRelease(mouseX, mouseY);
                }
            }
        }
    }

}
