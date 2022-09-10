package com.krazzzzymonkey.catalyst.gui.click;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class CustomGuiSlider extends GuiButton {

    public float sliderValue = 1.0F;
    public float sliderMaxValue = 1.0F;
    public float sliderMinValue = 1.0F;
    public boolean dragging = false;
    public String label;

    public CustomGuiSlider(int id, int x, int y, String label, float startingValue, float maxValue, float minValue) {
        super(id, x, y, 150, 20, label);
        this.label = label;
        this.sliderValue = startingValue;
        this.sliderMaxValue = maxValue;
        this.sliderMinValue = minValue;
    }


    protected int getHoverState(boolean par1) {
        return 0;
    }


    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);

            if (this.hovered && MouseUtils.isLeftClicked()) {

                this.sliderValue = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
                if (this.sliderValue < 0.0F) {
                    this.sliderValue = 0.0F;
                }
                if (this.sliderValue > 1.0F) {
                    this.sliderValue = 1.0F;
                }
                this.dragging = true;
            } else {
                dragging = false;
            }
            RenderUtils.drawBorderedRect(this.x, this.y, this.x + this.width, this.y + this.height, 1, ClickGui.getColor(), new Color(0, 0, 0, 150).getRGB());
            this.mouseDragged(mc, mouseX, mouseY);
            int l = 14737632;

            if (packedFGColour != 0) {
                l = packedFGColour;
            } else if (!this.enabled) {
                l = 10526880;
            } else if (this.hovered) {
                l = 16777120;
            }
            Main.fontRenderer.drawCenteredStringWithShadow(this.displayString, this.x + this.width / 2f, this.y + (this.height - 8) / 2f, l);

        }

    }


    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {

        if (this.visible && this.packedFGColour == 0) {
            if (this.dragging) {
                this.sliderValue = (float) (par2 - (this.x + 4)) / (float) (this.width - 8);
                if (this.sliderValue < 0.0F) {
                    this.sliderValue = 0.0F;
                }
                if (this.sliderValue > 1.0F) {
                    this.sliderValue = 1.0F;
                }
            }
            this.displayString = label + ": " + (int) (sliderValue * sliderMaxValue);
            RenderUtils.drawBorderedRect(this.x + (this.sliderValue * (float) (this.width - 8)), this.y, this.x + (this.sliderValue * (float) (this.width - 8)) + 8, this.y + 20, 1, ClickGui.getColor(), new Color(0, 0, 0, 150).getRGB());
        }
    }

}
