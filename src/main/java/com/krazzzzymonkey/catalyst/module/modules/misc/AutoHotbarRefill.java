package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
//TODO REDO THIS, DOESNT REALLY WORK
public class AutoHotbarRefill extends Modules {

    private IntegerValue delay;
    private final IntegerValue stackPercentage;
    private final BooleanValue offHand;


    public AutoHotbarRefill() {
        super("AutoHotbarRefill", ModuleCategory.MISC, "Automatically refills items in your hotbar");
        this.delay = new IntegerValue("DelayTime", 500, 20, 1000, "The time between each check");
        this.stackPercentage = new IntegerValue("RefillPercent", 50, 0, 99, "What percent should the stack be refilled");
        this.offHand = new BooleanValue("RefillOffhand", false, "Should it refill offhand");

        this.addValue(offHand, delay, stackPercentage);
    }


    int Timer = 0;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        Timer++;

        if (Timer > delay.getValue().intValue()) {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.currentScreen instanceof GuiInventory) {
                return;
            }

            int toRefill = getRefillable(mc.player);
            if (toRefill != -1) {
                refillHotbarSlot(mc, toRefill);
            }


            Timer = 0;
        }

    });


    private int getRefillable(EntityPlayerSP player) {
        if (offHand.getValue()) {
            if (player.getHeldItemOffhand().getItem() != Items.AIR
                    && player.getHeldItemOffhand().getCount() < player.getHeldItemOffhand().getMaxStackSize()
                    && (double) player.getHeldItemOffhand().getCount() / player.getHeldItemOffhand().getMaxStackSize() <= (stackPercentage.getValue().intValue() / 100.0)) {
                return 45;
            }
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.getItem() != Items.AIR && stack.getCount() < stack.getMaxStackSize()
                    && (double) stack.getCount() / stack.getMaxStackSize() <= (stackPercentage.getValue().intValue() / 100.0)) {
                return i;
            }
        }

        return -1;
    }


    private int getSmallestStack(EntityPlayerSP player, ItemStack itemStack) {
        if (itemStack == null) {
            return -1;
        }
        int minCount = itemStack.getMaxStackSize() + 1;
        int minIndex = -1;

        // i starts at 9 so that the hotbar is not checked
        for (int i = 9; i < player.inventory.mainInventory.size(); i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);

            if (stack.getItem() != Items.AIR
                    && stack.getItem() == itemStack.getItem()
                    && stack.getCount() < minCount) {

                minCount = stack.getCount();
                minIndex = i;
            }
        }

        return minIndex;
    }

    public void refillHotbarSlot(Minecraft mc, int slot) {
        ItemStack stack;
        if (slot == 45) { // Special case for offhand
            stack = mc.player.getHeldItemOffhand();
        } else {
            stack = mc.player.inventory.mainInventory.get(slot);
        }

        // If the slot is air it cant be refilled
        if (stack.getItem() == Items.AIR) {
            return;
        }

        // The slot can't be refilled if there is nothing to refill it with
        int biggestStack = getSmallestStack(mc.player, stack);
        if (biggestStack == -1) {
            return;
        }

        // Special case for offhand (can't use QUICK_CLICK)
        if (slot == 45) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.PICKUP, mc.player);
            return;
        }

        int overflow = -1; // The slot a shift click will overflow to
        for (int i = 0; i < 9 && overflow == -1; i++) {
            if (mc.player.inventory.mainInventory.get(i).getItem() == Items.AIR) {
                overflow = i;
            }
        }

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.QUICK_MOVE, mc.player);

        // If the two stacks don't overflow when combined we don't have to move overflow
        if (overflow != -1 && mc.player.inventory.mainInventory.get(overflow).getItem() != Items.AIR) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, overflow, ClickType.SWAP, mc.player);
        }
    }


}
