package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author Reap
 */

//TODO FIX NPEs AND CRASHES
public class Offhand extends Modules {
    private final Minecraft mc = Minecraft.getMinecraft();

    public DoubleValue minItemHealth;
    public DoubleValue minFallHeight;
    public BooleanValue soft;
    //public BooleanValue nerfBypass; // currently unused, will be used in the future.
    public BooleanValue swordGap;
    public ModeValue mode;
    public ModeValue fallback;

    public Offhand() {
        super("Offhand", ModuleCategory.COMBAT, "Automatically places certain items in your offhand");

        minItemHealth = new DoubleValue("Health", 16.0, 0.0, 36.0, "Minimum health to trigger a totem swap");
        minFallHeight = new DoubleValue("FallHeight", 16.0, 1D, 100D, "Maximum fall height to trigger a totem swap");
        soft = new BooleanValue("Soft", false, "Should swap if item is already in offhand");
        //nerfBypass = new BooleanValue("OffhandNerfBypass", false),
        swordGap = new BooleanValue("SwordGap", false, "Switch offhand to gapples when holding a sword");
        mode = new ModeValue("Mode", new Mode("Crystal", true), new Mode("Gapple", false), new Mode("Totem", false));
        fallback = new ModeValue("FallbackMode", new Mode("Crystal", false), new Mode("Gapple", true), new Mode("Totem", false));
        addValue(minItemHealth, minFallHeight, soft, swordGap, mode, fallback);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        // null safety
        if (mc.player == null || mc.world == null)
            return;


        if(!(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory))return;

            int itemSlot = getItemSlot();

        // return if there's no valid item so we don't crash or we already have the right item so we don't spam packets to the server.
        if (itemSlot == -1)
            return;
        if (soft.getValue() && (mc.player.getHealth() + mc.player.getAbsorptionAmount() > minItemHealth.getValue()) && !(mc.player.getHeldItemOffhand().getItem() instanceof ItemAir))
            return;

        mc.playerController.windowClick(
            mc.player.inventoryContainer.windowId,
            itemSlot,
            0,
            ClickType.PICKUP,
            mc.player
        );
        mc.playerController.windowClick(
            mc.player.inventoryContainer.windowId,
            45,
            0,
            ClickType.PICKUP,
            mc.player
        );
        mc.playerController.windowClick(
            mc.player.inventoryContainer.windowId,
            itemSlot,
            0,
            ClickType.PICKUP,
            mc.player
        );
    });

    public int getItemSlot() {
        Item itemToSearch = Items.TOTEM_OF_UNDYING;
        Item fallbackItem = Items.TOTEM_OF_UNDYING;
        if (mc.player.fallDistance >= minFallHeight.getValue()) {
            for (int i = 0; i < 45; i++) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == itemToSearch) {
                    if (mc.player.getHeldItemOffhand().getItem() == fallbackItem) {
                        return -1;
                    }
                    return i < 9 ? i + 36 : i;
                }
            }
        }
        // should we look for an item instead of totem?
        if ((mc.player.getHealth() + mc.player.getAbsorptionAmount()) > minItemHealth.getValue()) {
            // should we not override if there's the wrong item in the offhand slot?
            if (mode.getMode("Crystal").isToggled())
                itemToSearch = Items.END_CRYSTAL; // if mode is Crystal we want to search for a crystal.
            else if (mode.getMode("Gapple").isToggled())
                itemToSearch = Items.GOLDEN_APPLE; // if mode is Gapple we want to search for a gapple.

            if (swordGap.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
                itemToSearch = Items.GOLDEN_APPLE;
            }

        }

        if (fallback.getMode("Crystal").isToggled())
            fallbackItem = Items.END_CRYSTAL; // if mode is Crystal we want to search for a crystal.
        else if (fallback.getMode("Gapple").isToggled())
            fallbackItem = Items.GOLDEN_APPLE; // if mode is Gapple we want to search for a gapple.

        // return if we already have the right item so we don't packet spam.

        if (mc.player.getHeldItemOffhand().getItem() == itemToSearch)
            return -1;

        // look for the item
        for (int i = 0; i < 45; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == itemToSearch)
                return i < 9 ? i + 36 : i;
        }

        // the rest of the code will not be executed if an item was found.

        // return if we already have the right item for fallback mode so we don't packet spam.
        if (mc.player.getHeldItemOffhand().getItem() == fallbackItem)
            return -1;

        // if we can't find the wanted item, look for a fallback item.
        for (int i = 0; i < 45; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == fallbackItem)
                return i < 9 ? i + 36 : i;
        }

        return -1;
    }
}
