package com.krazzzzymonkey.catalyst.gui.click;

import com.krazzzzymonkey.catalyst.gui.GuiTextField;
import com.krazzzzymonkey.catalyst.gui.Tooltip;
import com.krazzzzymonkey.catalyst.gui.click.theme.dark.DarkFrame;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.managers.ProfileManager;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.TimerUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class ClickGuiScreen extends GuiScreen {

    public static ClickGui clickGui;
    public static int[] mouse = new int[2];
    public static GuiTextField searchField;
    public CustomGuiSlider scale;
    public static Tooltip tooltip;
    protected GuiScreen prevScreen;
    public static TimerUtils splashTimer = new TimerUtils();


    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        searchField.mouseClicked(x, y, button);
    }

    int searchTickPos = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tooltip = null;
        clickGui.render();
        if (tooltip != null && i > 10) tooltip.render();

        if ((mouseX >= this.width / 2 - 100 && mouseX <= this.width / 2 + 100) && mouseY <= 12) {
            if (MouseUtils.isLeftClicked() && !searchField.isFocused()) {
                searchField.setFocused(true);
            }
            if (splashTimer.isDelay(5)) {
                splashTimer.setLastMS();
                if (searchTickPos <= 3) {
                    searchField.y = searchTickPos++;
                }

            }
        } else {
            if (MouseUtils.isLeftClicked() && searchField.isFocused()) {
                searchField.setFocused(false);
            }
            if (!searchField.isFocused()) {
                if (splashTimer.isDelay(5)) {
                    splashTimer.setLastMS();
                    if (searchTickPos >= -11) {
                        searchField.y = searchTickPos--;
                    }

                }
            }
        }


        searchField.drawTextBox(ColorUtils.color(0, 0, 0, 140), com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.getColor());
        searchField.setTextColor(Color.WHITE.getRGB());
        scale.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
        if (scale.dragging) {
            com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.setValue((scale.sliderValue + 0.00001) * 2);
        } else {
            scale.sliderValue = (com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue().floatValue() / 2);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        searchTickPos = 0;
        if (OpenGlHelper.shadersSupported && com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.blur.getValue()) {
            if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null) {
                Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            try {
                Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        searchField = new GuiTextField(0, DarkFrame.fontRenderer, this.width / 2 - 100, -10, 200, 12);
        searchField.setMaxStringLength(100);
        searchField.setText("");
        searchField.setFocused(false);
        scale = new CustomGuiSlider(1, this.width - 150, this.height - 21, "GuiScale", 100f, 200f, 10f);
        scale.sliderValue = (com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue().floatValue() / 2);
        super.initGui();
    }

    int i = 0;
    int previousMouseLocation = -1;

    @Override
    public void updateScreen() {
        searchField.updateCursorCounter();
        ClickGui.onUpdate();

        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null && !com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.blur.getValue()) {
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() == null && com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.blur.getValue()) {
            try {
                Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            } catch (Exception e) {
                //ignored catch block
            }
        }

        if (previousMouseLocation == MouseUtils.getMouseX() + MouseUtils.getMouseY()) {
            i++;
        } else i = 0;
        previousMouseLocation = MouseUtils.getMouseX() + MouseUtils.getMouseY();
        if (tooltip != null && i > 10) tooltip.render();
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        searchTickPos = 0;
        searchField.setCanLoseFocus(false);
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        ModuleManager.getModule("ClickGui").toggle();

        super.onGuiClosed();
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
                        mc.displayGuiScreen(null);
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

        super.handleInput();
    }

}
