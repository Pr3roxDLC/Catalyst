package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class MiddleClickPearl extends Modules {
    public MiddleClickPearl() {
        super("MiddleClickPearl", ModuleCategory.PLAYER, "Throws an ender pearl when the middle click button is down");
    }

    private boolean clicked = false;



    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Mouse.isButtonDown(2)) {
            if (!this.clicked) {
                this.throwPearl();
            }
            this.clicked = true;
        } else {
            this.clicked = false;
        }
    });


    private void throwPearl() {
        int pearlSlot = getSlotWithBlock(ItemEnderPearl.class);
        boolean offhand = mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        if (pearlSlot != -1 || offhand) {
            int oldSlot = mc.player.inventory.currentItem;
            if (!offhand) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(pearlSlot));
            }
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            if (!offhand) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            }
        }
    }


    public static int getSlotWithBlock(final Class clazz) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock) stack.getItem()).getBlock();
                    if (clazz.isInstance(block)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

}
