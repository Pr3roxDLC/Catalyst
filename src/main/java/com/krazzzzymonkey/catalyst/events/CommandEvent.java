package com.krazzzzymonkey.catalyst.events;

import com.krazzzzymonkey.catalyst.command.EntityDesync;
import com.krazzzzymonkey.catalyst.managers.CommandManager;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommandEvent {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(CommandManager.prefix)) {

            event.setCanceled(true);
            try {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                CommandManager.getInstance().runCommands(event.getMessage().substring(1));
            } catch (Exception e) {
                e.printStackTrace();
                ChatUtils.message(ChatFormatting.DARK_RED + "Error: " + e.getMessage());
            }
        }
    }

    public static String inputField;

    @EventHandler
    private final EventListener<PacketEvent> onTabComplete = new EventListener<>(event -> {
        Packet packet = event.getPacket();
        if ((packet instanceof CPacketTabComplete) && inputField != null && inputField.startsWith(CommandManager.prefix) && !inputField.contains(" ")) {
            event.setCancelled(true);
        }
    });


    //todo allow events in Commands
    // entity desync

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(Minecraft.getMinecraft().player == null)return;
        if (EntityDesync.getRidingEntity == null || Minecraft.getMinecraft().player.isRiding())
            return;
        Minecraft.getMinecraft().player.onGround = true;

        EntityDesync.getRidingEntity.setPosition(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ);

        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketVehicleMove(EntityDesync.getRidingEntity));
    }


    @EventHandler
    private final EventListener<PacketEvent> packetEventListener = new EventListener<>(event -> {
        Packet packet = event.getPacket();
        if (packet instanceof SPacketSetPassengers) {
            if (EntityDesync.getRidingEntity == null)
                return;

            SPacketSetPassengers sPacketSetPassengers = (SPacketSetPassengers) packet;

            Entity en = Minecraft.getMinecraft().world.getEntityByID(sPacketSetPassengers.getEntityId());

            if (en == EntityDesync.getRidingEntity) {
                for (int i : sPacketSetPassengers.getPassengerIds()) {
                    Entity ent = Minecraft.getMinecraft().world.getEntityByID(i);

                    if (ent == Minecraft.getMinecraft().player)
                        return;
                }

                ChatUtils.warning("Server sent dismount packet! Remounting client side.");
                if (EntityDesync.getRidingEntity != null) {
                    EntityDesync.getRidingEntity.isDead = false;
                    if (!Minecraft.getMinecraft().player.isRiding()) {
                        Minecraft.getMinecraft().world.spawnEntity(EntityDesync.getRidingEntity);
                        Minecraft.getMinecraft().player.startRiding(EntityDesync.getRidingEntity, true);
                    }
                    EntityDesync.getRidingEntity = null;
                }
            }
        } else if (packet instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities sPacketDestroyEntities = (SPacketDestroyEntities) packet;
            if(EntityDesync.getRidingEntity == null)return;
            for (int entityID : sPacketDestroyEntities.getEntityIDs()) {
                if (entityID == EntityDesync.getRidingEntity.getEntityId()) {
                    ChatUtils.normalChat(EntityDesync.getRidingEntity.getName() + " is now out of render distance!");
                    return;
                }
            }
        }
    });

}
