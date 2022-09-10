package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

//TODO INVENTORY MODE

public class AutoTool extends Modules {

    public AutoTool() {
        super("AutoTool", ModuleCategory.PLAYER, "Finds the best tool in hotbar when breaking blocks");
    }

    public int slot = -1;
    public int currentSlot= -1;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        if(!Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown() && currentSlot != -1){
            Minecraft.getMinecraft().player.inventory.currentItem = currentSlot;
            currentSlot = -1;

        }
    });

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            Packet packet = e.getPacket();

            if (packet instanceof CPacketPlayerDigging) {
                CPacketPlayerDigging pck = (CPacketPlayerDigging) packet;
                if (Minecraft.getMinecraft().playerController.getCurrentGameType() != GameType.CREATIVE && pck.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                    if (currentSlot == -1) {
                        currentSlot = Minecraft.getMinecraft().player.inventory.currentItem;
                    }
                    int bestIndex = findTool(Minecraft.getMinecraft().world.getBlockState(pck.getPosition()).getBlock());
                    if (bestIndex != -1) {
                        if (slot == -1) {
                            slot = Minecraft.getMinecraft().player.inventory.currentItem;
                        }
                        Minecraft.getMinecraft().player.inventory.currentItem = bestIndex;
                    }

                }
            }
        }
    });

    public static int findTool(Block block) {
        float best = -1.0F;
        int index = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (itemStack != null) {
                float str = itemStack.getItem().getDestroySpeed(itemStack, block.getDefaultState());
                if (str > best) {
                    best = str;
                    index = i;
                }
            }
        }
        return index;
    }
}
