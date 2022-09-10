package com.krazzzzymonkey.catalyst.module.modules.misc;


import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;


public class DonkeyDrop extends Modules {

    public DonkeyDrop() {
        super("DonkeyDrop", ModuleCategory.MISC, "Automatically drops all items in a donkeys inventory");
    }

    @Override
    public void onEnable() {
        runThread();
        super.onEnable();
    }

    private void runThread() {
        (new Thread(() -> {
            try {
                if (mc.player.getRidingEntity() instanceof AbstractHorse) {
                    mc.getConnection().sendPacket(new CPacketEntityAction(mc.player.getRidingEntity(), CPacketEntityAction.Action.OPEN_INVENTORY));
                    Thread.sleep(175);
                    for (int i = 1; i < 17; ++i) {
                        Thread.sleep(75);
                        final ItemStack itemStack = mc.player.openContainer.getInventory().get(i);
                        if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR) {
                            mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(mc.player.openContainer.windowId, -999, 0, ClickType.PICKUP, mc.player);
                        }
                    }
                }
                Minecraft.getMinecraft().displayGuiScreen(null);
                this.toggle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        ).start();
    }


}
