package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.Timer;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class AutoArmor extends Modules {

    public BooleanValue elytraPriority;
    public BooleanValue whileMoving;
    public IntegerValue delay;

    private int timer;

    public AutoArmor() {
        super("AutoArmor", ModuleCategory.COMBAT, "Automatically puts on armor or an elytra on for you");



        elytraPriority = new BooleanValue("ElytraPriority", false, "Prioritizes elytra wings over chestplates");
        whileMoving = new BooleanValue("SwapWhileMoving", false, "Dont put armor on while moving");
        delay = new IntegerValue("Delay", 2, 1, 20, "The delay between putting armor on");
        this.addValue(elytraPriority, whileMoving, delay);

    }

    private final Timer rightClickTimer = new Timer();

    private boolean sleep;


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(event -> {
        if (event.getPhase() == TickEvent.Phase.END) return;

        if (mc.world == null || mc.player == null) return;

        if (mc.player.ticksExisted % delay.getValue() != 0) {
            return;
        }

        if (whileMoving.getValue()) {
            if (mc.player.motionX != 0D || mc.player.motionZ != 0D) return;
        }


        if (mc.currentScreen instanceof GuiContainer) return;

        AtomicBoolean hasSwapped = new AtomicBoolean(false);

        if (sleep) {
            sleep = false;
            return;
        }

        boolean ep = elytraPriority.getValue();

        final Set<InvStack> replacements = new HashSet<>();

        for (int slot = 0; slot < 36; slot++) {

            InvStack invStack = new InvStack(slot, mc.player.inventory.getStackInSlot(slot));
            if (invStack.stack.getItem() instanceof ItemArmor || invStack.stack.getItem() instanceof ItemElytra) {
                replacements.add(invStack);
            }

        }

        List<InvStack> armors = replacements.stream()
            .filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
            .sorted(Comparator.comparingInt(invStack -> invStack.slot))
            .sorted(Comparator.comparingInt(invStack -> ((ItemArmor) invStack.stack.getItem()).damageReduceAmount))
            .collect(Collectors.toList());

        boolean wasEmpty = armors.isEmpty();

        if (wasEmpty) {
            armors = replacements.stream()
                .filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                .sorted(Comparator.comparingInt(invStack -> invStack.slot))
                .sorted(Comparator.comparingInt(invStack -> ((ItemArmor) invStack.stack.getItem()).damageReduceAmount))
                .collect(Collectors.toList());
        }

        List<InvStack> elytras = replacements.stream()
            .filter(invStack -> invStack.stack.getItem() instanceof ItemElytra)
            .sorted(Comparator.comparingInt(invStack -> invStack.slot))
            .collect(Collectors.toList());


        Item currentHeadItem = mc.player.inventory.getStackInSlot(39).getItem();
        Item currentChestItem = mc.player.inventory.getStackInSlot(38).getItem();
        Item currentLegsItem = mc.player.inventory.getStackInSlot(37).getItem();
        Item currentFeetItem = mc.player.inventory.getStackInSlot(36).getItem();

        boolean replaceHead = currentHeadItem.equals(Items.AIR);
        boolean replaceChest = currentChestItem.equals(Items.AIR);
        boolean replaceLegs = currentLegsItem.equals(Items.AIR);
        boolean replaceFeet = currentFeetItem.equals(Items.AIR);


        if (replaceHead && !hasSwapped.get()) {
            armors.stream().filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                .filter(invStack -> ((ItemArmor) invStack.stack.getItem()).armorType.equals(EntityEquipmentSlot.HEAD)
                ).findFirst().ifPresent(invStack -> {
                    swapSlot(invStack.slot, 5);
                    hasSwapped.set(true);
                });
        }

        if (ep && !(currentChestItem instanceof ItemElytra) && elytras.size() > 0 && !hasSwapped.get()) {
            elytras.stream().findFirst().ifPresent(invStack -> {
                swapSlot(invStack.slot, 6);
                hasSwapped.set(true);
            });
        }

        if (replaceChest || (!ep && currentChestItem.equals(Items.ELYTRA)) && !hasSwapped.get()) {
            armors.stream().filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                .filter(invStack -> ((ItemArmor) invStack.stack.getItem()).armorType.equals(EntityEquipmentSlot.CHEST)
                ).findFirst().ifPresent(invStack -> {
                    swapSlot(invStack.slot, 6);
                    hasSwapped.set(true);
                });
        }

        if (replaceLegs && !hasSwapped.get()) {
            armors.stream().filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                .filter(invStack -> ((ItemArmor) invStack.stack.getItem()).armorType.equals(EntityEquipmentSlot.LEGS)
                ).findFirst().ifPresent(invStack -> {
                    swapSlot(invStack.slot, 7);
                    hasSwapped.set(true);
                });
        }

        if (replaceFeet && !hasSwapped.get()) {
            armors.stream().filter(invStack -> invStack.stack.getItem() instanceof ItemArmor)
                .filter(invStack -> ((ItemArmor) invStack.stack.getItem()).armorType.equals(EntityEquipmentSlot.FEET)
                ).findFirst().ifPresent(invStack -> {
                    swapSlot(invStack.slot, 8);
                    hasSwapped.set(true);
                });
        }

    });


    @SubscribeEvent //todo refactor to new event handler
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {

        if (event.getEntityPlayer() != mc.player) return;

        if (event.getItemStack().getItem() != Items.EXPERIENCE_BOTTLE) return;

        rightClickTimer.reset();
    }

    private void swapSlot(int source, int target) {

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, source < 9 ? source + 36 : source, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, target, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, source < 9 ? source + 36 : source, 0, ClickType.PICKUP, mc.player);

        sleep = true;

    }


    private static class InvStack {

        public final int slot;
        public final ItemStack stack;

        public InvStack(int slot, ItemStack stack) {
            this.slot = slot;
            this.stack = stack;
        }

    }
}
