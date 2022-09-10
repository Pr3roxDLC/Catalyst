package com.krazzzzymonkey.catalyst.managers.accountManager.msauth;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.chest.CustomGuiButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MSAuthScreen extends GuiScreen {
    public static final String[] symbols = new String[]{"█ █ █ █ _ _ _ _ _ _ _", "_ █ █ █ █ _ _ _ _ _ _", "_ _ █ █ █ █ _ _ _ _ _", "_ _ _ █ █ █ █ _ _ _ _", "_ _ _ _ █ █ █ █ _ _ _", "_ _ _ _ _ █ █ █ █ _ _", "_ _ _ _ _ _ █ █ █ █ _", "_ _ _ _ _ _ _ █ █ █ █", "_ _ _ _ _ _ █ █ █ █ _", "_ _ _ _ _ █ █ █ █ _ _", "_ _ _ _ █ █ █ █ _ _ _", "_ _ _ █ █ █ █ _ _ _ _", "_ _ █ █ █ █ _ _ _ _ _", "_ █ █ █ █ _ _ _ _ _ _"};
    public GuiScreen prev;
    public List<String> text = new ArrayList<>();
    public boolean endTask = false;
    public int tick;



    public MSAuthScreen(GuiScreen prev) {
        this.prev = prev;
        AuthSys.start(this);
    }


    @Override
    public void initGui() {
        addButton(new CustomGuiButton(0, width / 2 - 50, (height / 8 * 2) + 20, 100, 20, I18n.format("gui.cancel"), -1, new Color(0,0,0,100).getRGB()));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) mc.displayGuiScreen(prev);
    }

    boolean halfTick = false;

    @Override
    public void updateScreen() {
        if (halfTick) {
            tick++;
            halfTick = false;
        } else halfTick = true;

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        drawDefaultBackground();
        Main.fontRenderer.drawCenteredString("Waiting For Browser Authentication", width / 2f, 10, -1);
        for (int i = 0; i < text.size(); i++) {
            Main.fontRenderer.drawCenteredString( text.get(i), width / 2f, (height / 8f) + i * 10, -1);
        }
        if (!endTask) drawCenteredString(fontRenderer, symbols[tick % symbols.length], width / 2, height / 8 * 2, -1);
        super.drawScreen(mouseX, mouseY, delta);
    }

    @Override
    public void onGuiClosed() {
        AuthSys.stop();
        super.onGuiClosed();
    }

    public void setState(String s) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> this.text = mc.fontRenderer.listFormattedStringToWidth(s, width));
    }

    public void error(String error) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> {
            this.text = mc.fontRenderer.listFormattedStringToWidth(TextFormatting.RED + "Error: " + error, width);
            endTask = true;
        });
    }
}
