package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoEat extends Modules {

    private static BooleanValue noInteract;
    private final DoubleValue minHealth;

    public AutoEat() {
        super("AutoEat", ModuleCategory.MISC, "Automatically eats for you");
        this.minHealth = new DoubleValue("Heath", 15D, 1D, 19D, "The minimum health to start eating");
        noInteract = new BooleanValue("NoInteract", true, "Stops interactions with intractable blocks");
        this.addValue(minHealth, noInteract);
    }

    private int lastSlot = -1;
    private boolean eating = false;

    private boolean isValid(ItemStack stack, int food) {
        if (mc.player.getHealth() <= minHealth.getValue()) return true;
        else
            return stack.getItem() instanceof ItemFood && (20 - food) >= ((ItemFood) stack.getItem()).getHealAmount(stack);
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock e) {

        if (noInteract.getValue() && e.getItemStack().getItemUseAction() == EnumAction.EAT && eating) {
            e.setCanceled(true);
        }

    }


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (eating && !Minecraft.getMinecraft().player.isHandActive()) {
            if (lastSlot != -1) {
                Minecraft.getMinecraft().player.inventory.currentItem = lastSlot;
                lastSlot = -1;
            }
            eating = false;
            KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), false);
            return;
        }
        if (eating) return;

        FoodStats stats = Minecraft.getMinecraft().player.getFoodStats();
        if (isValid(Minecraft.getMinecraft().player.getHeldItemOffhand(), stats.getFoodLevel())) {
            Minecraft.getMinecraft().player.setActiveHand(EnumHand.OFF_HAND);
            eating = true;
            KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), true);
            Minecraft.getMinecraft().rightClickMouse();
        } else {
            for (int i = 0; i < 9; i++) {
                if (isValid(Minecraft.getMinecraft().player.inventory.getStackInSlot(i), stats.getFoodLevel())) {
                    lastSlot = Minecraft.getMinecraft().player.inventory.currentItem;
                    Minecraft.getMinecraft().player.inventory.currentItem = i;
                    eating = true;
                    KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), true);
                    Minecraft.getMinecraft().rightClickMouse();
                    return;
                }
            }
        }

    });
}
