package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


//TODO REWRITE THIS, REMOVE BRITISHNESS
public class ArmorHUD extends Modules {

    public BooleanValue damage;

    private int armorCompress;
    private int armorSpacing;

    public ArmorHUD() {
        super("ArmorHUD", ModuleCategory.HUD, "Shows armor and durability on HUD", true);
        damage = new BooleanValue("Damage", true, "Shows current durability of the armor");
        this.addValue(damage);
    }

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        ScaledResolution resolution = new ScaledResolution(Wrapper.INSTANCE.mc());
        RenderItem itemRender = Wrapper.INSTANCE.mc().getRenderItem();

        GlStateManager.enableTexture2D();
        int i = resolution.getScaledWidth() / 2;
        int iteration = 0;
        int y = resolution.getScaledHeight() - 55 - (Wrapper.INSTANCE.player().isInWater() ? 10 : 0);

        for (ItemStack is : Wrapper.INSTANCE.inventory().armorInventory) {

            iteration++;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * armorSpacing + armorCompress;
            GlStateManager.enableDepth();

            itemRender.zLevel = 200F;
            itemRender.renderItemAndEffectIntoGUI(is, x, y);
            itemRender.renderItemOverlayIntoGUI(Wrapper.INSTANCE.fontRenderer(), is, x, y, "");
            itemRender.zLevel = 0F;

            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            if (ModuleManager.getModule("CustomFont").isToggled()) {
                Main.fontRenderer.drawString(s, x + 19 - 2 - Main.fontRenderer.getStringWidth(s), y + 9, 0xffffff);
            } else {
                Wrapper.INSTANCE.fontRenderer().drawString(s, x + 19 - 2 - Wrapper.INSTANCE.fontRenderer().getStringWidth(s), y + 9, 0xffffff);
            }
            if (damage.getValue()) {
                try {
                    float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                    float red = 1 - green;
                    int dmg = 100 - (int) (red * 100);

                    if (red > 255 || red < 0) red = 255;
                    if (green > 255 || green < 0) green = 255;
                    int color = ColorUtils.color(red, green, 0, 1f);

                    if (ModuleManager.getModule("CustomFont").isToggled()) {
                        Main.fontRenderer.drawString(dmg + "", x + 8 - (float) Main.fontRenderer.getStringWidth(dmg + "") / 2, y - 11, color);
                    } else {
                        Wrapper.INSTANCE.fontRenderer().drawString(dmg + "", x + 8 - Wrapper.INSTANCE.fontRenderer().getStringWidth(dmg + "") / 2, y - 11, color);
                    }
                }catch (Exception ignored){
                }
            }

            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }

        GlStateManager.enableDepth();
        GlStateManager.disableLighting();

        armorCompress = 2;
        armorSpacing = 20;

        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    });

}
