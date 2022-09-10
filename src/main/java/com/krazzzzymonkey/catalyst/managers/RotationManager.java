package com.krazzzzymonkey.catalyst.managers;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderModelEntityLivingEvent;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.locks.ReentrantLock;

public class RotationManager {

    private static final ReentrantLock mutex = new ReentrantLock();

    private static float spoofPitch = 0, spoofYaw = 0;
    private static int currentPrio = Integer.MIN_VALUE;
    private static float headPitch = -1;
    /**
     * @param prio  Priority of rotation spoof
     * @param pitch Player pitch
     * @param yaw   Player yaw
     * @return True if set
     */
    public static boolean set(final int prio, final float pitch, final float yaw) {

        try {
            mutex.lock();

            if (currentPrio >= prio) return false;
            currentPrio = prio;
            spoofPitch = pitch;
            spoofYaw = yaw;
            return true;

        } finally {
            mutex.unlock();
        }

    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            if (currentPrio == Integer.MIN_VALUE) return;

            if ((e.getPacket() instanceof CPacketPlayer.Rotation) || (e.getPacket() instanceof CPacketPlayer.PositionRotation)) {
                ((CPacketPlayer) e.getPacket()).pitch = spoofPitch;
                ((CPacketPlayer) e.getPacket()).yaw = spoofYaw;
                currentPrio = Integer.MIN_VALUE;
            }
        }
    });

    @EventHandler
    private final EventListener<RenderModelEntityLivingEvent> onRenderModel = new EventListener<>(event -> {
        if (event.getEntityLivingBase().equals(Minecraft.getMinecraft().player)) {
            event.setCancelled(true);
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), headPitch == -1 ? event.getHeadPitch() : headPitch, event.getScaleFactor());
        }
    });

    public static void setHeadPitch(float in) {
        headPitch = in;
    }
}
