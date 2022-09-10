package com.krazzzzymonkey.catalyst.gui.chest;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class CustomGuiButton extends GuiButton {
    private final int borderColor;
    private final int color;

    public CustomGuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public CustomGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this(buttonId, x, y, widthIn, heightIn, buttonText, new Color(0, 0, 0, 100).getRGB(), new Color(0, 0, 0, 0).getRGB());
    }


    public CustomGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int borderColor, int color) {
        super(buttonId, x, y, buttonText);
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
        this.borderColor = borderColor;
        this.color = color;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            RenderUtils.drawBorderedRect(this.x, this.y, this.x + this.width, this.y + this.height, 1, this.borderColor, this.color);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;
            if (this.packedFGColour != 0) {
                j = this.packedFGColour;
            } else if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            Main.fontRenderer.drawCenteredString(this.displayString, this.x + this.width / 2f, this.y + (this.height - 8) / 2f, j);
        }

    }
}
