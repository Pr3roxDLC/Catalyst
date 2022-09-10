package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.Value;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketSpawnObject;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PearlBait extends Modules {

    public static final BooleanValue guarantee = new BooleanValue("Forced", true, "Cancels all outgoing events while pearl is flying");

    private final Queue<CPacketPlayer> packets = new ConcurrentLinkedQueue<>();
    private int thrownPearlId = -1;
    public PearlBait() {
        super("PearlBait", ModuleCategory.PLAYER, "Allows you to throw an EnderPearl without getting teleported");
        addValue(guarantee);
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 65) {
                mc.world.playerEntities.stream()
                    .min(Comparator.comparingDouble((p) -> p.getDistance(packet.getX(), packet.getY(), packet.getZ())))
                    .ifPresent((player) -> {
                        if (player.equals(mc.player)) {
                            if (mc.player.onGround) {
                                // do not allow movement
                                mc.player.motionX = 0.0;
                                mc.player.motionY = 0.0;
                                mc.player.motionZ = 0.0;

                                mc.player.movementInput.moveForward = 0.0f;
                                mc.player.movementInput.moveStrafe = 0.0f;

                                // send rubberband packet
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ, false));

                                thrownPearlId = packet.getEntityID();
                            }
                        }
                    });
            }
        } else if (event.getPacket() instanceof CPacketPlayer && guarantee.getValue() && thrownPearlId != -1) {
            packets.add((CPacketPlayer) event.getPacket());
            event.setCancelled(true);
        }
    });

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(event -> {
        if (thrownPearlId != -1) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity.getEntityId() == thrownPearlId && entity instanceof EntityEnderPearl) {
                    EntityEnderPearl pearl = (EntityEnderPearl) entity;
                    if (pearl.isDead) {
                        thrownPearlId = -1;
                    }
                }
            }
        } else {
            if (!packets.isEmpty()) {
                do {
                    mc.player.connection.sendPacket(packets.poll());
                } while (!packets.isEmpty());
            }
        }
    });

}
