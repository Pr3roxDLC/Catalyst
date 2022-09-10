package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;


//TODO PERCENTAGE SETTING
public class Velocity extends Modules {

    private BooleanValue fishingBob;
    private BooleanValue explosions;
    public static BooleanValue flowingWater;
    public static BooleanValue push;

    public Velocity() {
        super("Velocity", ModuleCategory.PLAYER, "Cancels various knockback packets");

        this.explosions = new BooleanValue("Explosions", true, "Cancel explosion knockback");
        this.fishingBob = new BooleanValue("FishingBob", true, "Cancel fishing rod bob knockback");
        push = new BooleanValue("NoPush", true, "Cancel player and block push events");
        flowingWater = new BooleanValue("FlowingWater", true, "");

        this.addValue(explosions, push, fishingBob, flowingWater);
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.IN) {

            Packet packet = e.getPacket();

            if (packet instanceof SPacketEntityVelocity) {
                SPacketEntityVelocity p = (SPacketEntityVelocity) packet;

                if (mc.player == null) return;

                if (p.getEntityID() == Minecraft.getMinecraft().player.getEntityId()) {
                    e.setCancelled(true);
                }
            }
            if (packet instanceof SPacketExplosion && explosions.getValue()) {
                SPacketExplosion p = (SPacketExplosion) packet;
                p.motionX = 0;
                p.motionY = 0;
                p.motionZ = 0;
            }
            if (packet instanceof SPacketEntityStatus && fishingBob.getValue()) {
                final SPacketEntityStatus p = (SPacketEntityStatus) packet;
                if (p.getOpCode() == 31) {
                    final Entity entity = p.getEntity(Minecraft.getMinecraft().world);
                    if (entity != null && entity instanceof EntityFishHook) {
                        final EntityFishHook fishHook = (EntityFishHook) entity;
                        if (fishHook.caughtEntity == Minecraft.getMinecraft().player) {
                            e.setCancelled(true);
                        }
                    }
                }

            }
        }
    });
}
