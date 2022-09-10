package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class PVPInfo extends Modules {

    public Number xOffset;
    public Number yOffset;
    private ColorValue colorValue;
    public BooleanValue rainbow;

    public PVPInfo() {
        super("PVPInfo", ModuleCategory.HUD, "Displays amount of PvP items in your inventory", true);

        this.xOffset = new Number("OffsetX", 0.0);
        this.yOffset = new Number("OffsetY", 105.0);
        this.rainbow = new BooleanValue("Rainbow", false, "Makes the text cycle through colors");
        this.colorValue = new ColorValue("Color", Color.CYAN, "Changes the color of the text");
        this.addValue(xOffset, yOffset, colorValue, rainbow, xOffset, yOffset);
    }


    Minecraft mc = Minecraft.getMinecraft();
    int colorRect = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F);
    int colorRect2 = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.5F);

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        int color = colorValue.getColor().getRGB();

        if (rainbow.getValue()) {
            color = ColorUtils.rainbow().getRGB();
        }
        int xPos = xOffset.getValue().intValue();
        int yPos = yOffset.getValue().intValue();
        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());
        int Totems = 0;
        int Crystals = 0;
        int XP = 0;
        int Gaps = 0;
        int Obi = 0;

        Totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum() + ((mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) ? mc.player.getHeldItemOffhand().getCount() : 0);
        Crystals = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum() + ((mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? mc.player.getHeldItemOffhand().getCount() : 0);
        XP = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.EXPERIENCE_BOTTLE).mapToInt(ItemStack::getCount).sum() + ((mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) ? mc.player.getHeldItemOffhand().getCount() : 0);
        Gaps = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum() + ((mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) ? mc.player.getHeldItemOffhand().getCount() : 0);

        String str = "Totems: " + ChatColor.WHITE + Totems + ChatColor.RESET + " XP: " + ChatColor.WHITE + XP + ChatColor.RESET + " Crystals: " + ChatColor.WHITE + Crystals + ChatColor.RESET + " Gapples: " + ChatColor.WHITE + Gaps;
        GL11.glPushMatrix();
        if (ModuleManager.getModule("CustomFont").isToggled()) {
            Main.fontRenderer.drawStringWithShadow(str, xPos, yPos, color);
        } else {
            Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(str, xPos, yPos, color);
        }
        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
            if (ModuleManager.getModule("CustomFont").isToggled()) {
                RenderUtils.drawRect(xPos, yPos, xPos + Main.fontRenderer.getStringWidth(str), yPos + 14,
                        ColorUtils.color(0, 0, 0, 100));
            } else
                RenderUtils.drawRect(xPos, yPos, xPos + Wrapper.INSTANCE.fontRenderer().getStringWidth(str), yPos + 14,
                        ColorUtils.color(0, 0, 0, 100));
            if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(str), yPos, yPos + 14))) {
                isAlreadyDragging = true;
            }

            if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                isAlreadyDragging = false;
            }

            if (!isAlreadyDragging || isDragging) {
                if (MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(str), yPos, yPos + 14)) {
                    isDragging = true;
                }


                if (MouseUtils.isLeftClicked() && isDragging) {
                    finalMouseX = MouseUtils.getMouseX();
                    finalMouseY = MouseUtils.getMouseY();

                    xOffset.value = (double) finalMouseX - Main.fontRenderer.getStringWidth(str) / 2;
                    yOffset.value = (double) finalMouseY;
                    MouseUtils.isDragging = true;
                } else isDragging = false;

            }
        }
        GL11.glPopMatrix();


    });
}
