package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.managers.AutoGGManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//TODO MAKE THE MESSAGE CUSTOMIZABLE, HANDLE CRYSTAL KILLS

public class AutoGG extends Modules {


    public BooleanValue greenText;

    public AutoGG() {
        super("AutoGG", ModuleCategory.CHAT, "Sends a chat message after killing someone");

        greenText = new BooleanValue("GreenText", false, "Adds \">\" to the beginning of your message, making it green on some severs");

        this.addValue(greenText);

    }

    private static ConcurrentHashMap targetedPlayers = null;
    int index = -1;

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        Packet packet = e.getPacket();

        if (Minecraft.getMinecraft().player != null) {
            if (targetedPlayers == null) {
                targetedPlayers = new ConcurrentHashMap();
            }

            if (packet instanceof CPacketUseEntity) {
                CPacketUseEntity cPacketUseEntity = (CPacketUseEntity) packet;
                if (cPacketUseEntity.getAction().equals(CPacketUseEntity.Action.ATTACK)) {
                    Entity targetEntity = cPacketUseEntity.getEntityFromWorld(Minecraft.getMinecraft().world);
                    if (targetEntity instanceof EntityPlayer) {
                        addTargetedPlayer(targetEntity.getName());
                    }
                }
            }
        }
    });

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (Minecraft.getMinecraft().player != null) {
            if (targetedPlayers == null) {
                targetedPlayers = new ConcurrentHashMap();
            }

            EntityLivingBase entity = event.getEntityLiving();
            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    if (player.getHealth() <= 0.0F) {
                        String name = player.getName();
                        if (this.shouldAnnounce(name)) {
                            this.doAnnounce(name);
                        }

                    }
                }
            }
        }
    }

    public void onEnable() {
        targetedPlayers = new ConcurrentHashMap();
        super.onEnable();
    }

    public void onDisable() {
        targetedPlayers = null;
        super.onDisable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (targetedPlayers == null) {
            targetedPlayers = new ConcurrentHashMap();
        }

        for (Entity entity : Minecraft.getMinecraft().world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.getHealth() <= 0.0F) {
                    String name = player.getName();
                    if (this.shouldAnnounce(name)) {
                        this.doAnnounce(name);
                        break;
                    }
                }
            }
        }

        targetedPlayers.forEach((namex, timeout) -> {
            if ((int) timeout <= 0) {
                targetedPlayers.remove(namex);
            } else {
                targetedPlayers.put(namex, (int) timeout - 1);
            }

        });
    });

    private boolean shouldAnnounce(String name) {
        return targetedPlayers.containsKey(name);
    }


    private void doAnnounce(String name) {
        targetedPlayers.remove(name);
        if (index >= (AutoGGManager.messages.size() - 1)) index = -1;
        index++;
        String message;
        if (AutoGGManager.messages.size() > 0)
            message = AutoGGManager.messages.get(index);
        else
            message = "GG {name}, Catalyst is on top!";

        String messageSanitized = message.replace("{name}", name);
        if (messageSanitized.length() > 255) {
            messageSanitized = messageSanitized.substring(0, 255);
        }

        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketChatMessage(messageSanitized));
    }

    public static void addTargetedPlayer(String name) {
        if (!Objects.equals(name, Minecraft.getMinecraft().player.getName())) {
            if (targetedPlayers == null) {
                targetedPlayers = new ConcurrentHashMap();
            }

            targetedPlayers.put(name, 20);
        }
    }

}
