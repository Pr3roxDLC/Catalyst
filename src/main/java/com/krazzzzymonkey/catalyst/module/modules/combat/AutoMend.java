package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class AutoMend extends Modules {

    private final BooleanValue activeOnSneak;
    private final BooleanValue itemSpoof;
    private IntegerValue maxDurability;
    private IntegerValue mendWhen;

    public AutoMend() {
        super("AutoMend", ModuleCategory.COMBAT, "Mend your armor automatically");

        this.activeOnSneak = new BooleanValue("ActiveOnSneak", true, "Only mend when sneaking");
        this.itemSpoof = new BooleanValue("ItemSpoof", true, "Spoof held items when auto mending");
        this.maxDurability = new IntegerValue("MendTo", 80, 1, 100, "The amount to which you want to mend your armor durability");
        this.mendWhen = new IntegerValue("MendWhen", 20, 1, 100, "The amount needed to start auto mending");

        this.addValue(activeOnSneak, itemSpoof, maxDurability, mendWhen);
    }

    int toMend = 0;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {

        if (mc.player == null || mc.world == null || mc.player.ticksExisted < 10) {
            return;
        }

        List<ItemStack> armour = mc.player.inventory.armorInventory;
        for (int i = 0; i < armour.size(); i++) {
            ItemStack itemStack = armour.get(i);
            if (itemStack.isEmpty) {
                continue;
            }

            float damageOnArmor = (float) (itemStack.getMaxDamage() - itemStack.getItemDamage());
            float damagePercent = 100 - (100 * (1 - damageOnArmor / itemStack.getMaxDamage()));

            if (damagePercent <= maxDurability.getValue()) {
                if (damagePercent <= mendWhen.getValue()) {
                    toMend |= 1 << i;
                }
            } else {
                toMend &= ~(1 << i);
            }
        }

        if (toMend > 0) {
            mendArmor(mc.player.inventory.currentItem);
        }
    });

    private int findXPSlot() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }

        return slot;
    }


    private void mendArmor(int oldSlot) {
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.getDistance(mc.player) < 1 && entityPlayer != mc.player) {
                return;
            }
        }


        if (activeOnSneak.getValue() && !mc.player.isSneaking()) {
            return;
        }

        int newSlot = findXPSlot();

        if (newSlot == -1) {
            return;
        }

        if (oldSlot != newSlot) {
            if (itemSpoof.getValue()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(newSlot));
            } else {
                mc.player.inventory.currentItem = newSlot;
            }
            mc.playerController.syncCurrentPlayItem();
        }

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, 90, true));
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (itemSpoof.getValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        } else {
            mc.player.inventory.currentItem = oldSlot;
        }
        mc.playerController.syncCurrentPlayItem();
    }


}
