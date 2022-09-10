package com.krazzzzymonkey.catalyst.module.modules.hud;


import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

//TODO E CHEST PREVIEW
//TODO XCARY SLOTS IF XCARY IS ON
public class InvPreview extends Modules {

    private BooleanValue boarderColorRainbow;
    private ColorValue boarderColorValue;
    private final BooleanValue mainColorRainbow;
    private final ColorValue mainColorValue;
    private final IntegerValue mainOpacity;
    private final BooleanValue drawBoarder;
    public ModeValue inventory;
    public Number xOffset;
    public Number yOffset;

    ResourceLocation resource;

    public InvPreview() {
        super("InvPreview", ModuleCategory.HUD, "Renders inventory on hud", true);


        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("Y Offset", 300.0);

        this.mainColorValue = new ColorValue("MainColor", Color.BLACK, "Background color of inventory preview");
        this.mainColorRainbow = new BooleanValue("MainRainbow", false, "Makes the background cycle through colors");
        this.mainOpacity = new IntegerValue("Opacity", 140, 0, 255, "Changes the opacity of background");

        this.drawBoarder = new BooleanValue("Border", false, "Renders color border around inventory preview");
        this.boarderColorValue = new ColorValue("BorderColor", Color.CYAN, "Selects color of border around inventory preview");
        this.boarderColorRainbow = new BooleanValue("BorderRainbow", false, "Makes the background cycle through colors");

        this.boarderColorValue = new ColorValue("BorderColorValue", Color.CYAN, "Border color of inventory preview");
        this.boarderColorRainbow = new BooleanValue("BorderRainbow", false, "Makes the border color cycle through colors");
        this.addValue(mainColorValue, mainColorRainbow, mainOpacity, drawBoarder, boarderColorValue, boarderColorRainbow, xOffset, yOffset);
    }

    int color;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
            return;
        } else {
            Minecraft.getMinecraft().player.getUniqueID();
        }

        color = boarderColorValue.getColor().getRGB();

        if (boarderColorRainbow.getValue()) {
            color = ColorUtils.rainbow().getRGB();
        }
    });

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();
        GL11.glPushMatrix();
        if (this.isToggled()) {
            drawInventory(xPos, yPos);
        }
        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {


            if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + 180, yPos, yPos + 90))) {
                isAlreadyDragging = true;
            }

            if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                isAlreadyDragging = false;
            }

            if (!isAlreadyDragging || isDragging) {
                if (MouseUtils.isMouseOver(xPos, xPos + 180, yPos, yPos + 90)) {
                    isDragging = true;
                }


                if (MouseUtils.isLeftClicked() && isDragging) {
                    finalMouseX = MouseUtils.getMouseX();
                    finalMouseY = MouseUtils.getMouseY();
                    xOffset.value = (double)finalMouseX - 90;
                    yOffset.value = (double)finalMouseY - 10;
                } else isDragging = false;

            }
        }
        GL11.glPopMatrix();

    });


    public void drawInventory(int x, int y) {

        NonNullList<ItemStack> items = Minecraft.getMinecraft().player.inventory.mainInventory;
        GlStateManager.enableRescaleNormal();
        if(drawBoarder.getValue()){
            if(mainColorRainbow.getValue()){
                RenderUtils.drawBorderedRect(x, y, x + 160, y + 54, 1f, color, new Color(ColorUtils.rainbow().getRed(), ColorUtils.rainbow().getGreen(), ColorUtils.rainbow().getBlue(), mainOpacity.getValue()).getRGB());
            }else {
                RenderUtils.drawBorderedRect(x, y, x + 160, y + 54, 1f, color, new Color(mainColorValue.getColor().getRed(), mainColorValue.getColor().getGreen(), mainColorValue.getColor().getBlue(), mainOpacity.getValue()).getRGB());
            }

        }else {
            if(mainColorRainbow.getValue()){
                RenderUtils.drawRect(x, y, x + 160, y + 54, new Color(ColorUtils.rainbow().getRed(), ColorUtils.rainbow().getGreen(), ColorUtils.rainbow().getBlue(), mainOpacity.getValue()).getRGB());
            }else {
                RenderUtils.drawRect(x, y, x + 160, y + 54, new Color(mainColorValue.getColor().getRed(), mainColorValue.getColor().getGreen(), mainColorValue.getColor().getBlue(), mainOpacity.getValue()).getRGB());
            }

        }

        for (int size = items.size(), item = 9; item < size; ++item) {
            final int slotX = x + item % 9 * 18;
            final int slotY = y + (item / 9 - 1) * 18;
            RenderHelper.enableGUIStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(items.get(item), slotX, slotY);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, items.get(item), slotX, slotY);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
        }
    }
}
