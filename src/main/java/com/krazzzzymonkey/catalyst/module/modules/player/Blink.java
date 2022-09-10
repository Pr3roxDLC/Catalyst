package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.LinkedList;
import java.util.Queue;

public class Blink extends Modules {


    public Queue<CPacketPlayer> packets = new LinkedList<>();
    public static Blink INSTANCE;

    public Blink() {
        super("Blink", ModuleCategory.PLAYER, "Cancels movement packets and allows you to teleport short distances");
        INSTANCE = this;
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if (event.getSide() == PacketEvent.Side.OUT) {

            if (event.getPacket() instanceof CPacketPlayer) {
                event.setCancelled(true);
                packets.add((CPacketPlayer) event.getPacket());
                this.setExtraInfo(packets.size() + "");
            }
        }
    });


    private EntityOtherPlayerMP clonedPlayer;

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            clonedPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            clonedPlayer.copyLocationAndAnglesFrom(mc.player);
            clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(-100, clonedPlayer);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        while (!packets.isEmpty()) {
            mc.player.connection.sendPacket(packets.poll());
        }

        EntityPlayer localPlayer = mc.player;
        if (localPlayer != null) {
            mc.world.removeEntityFromWorld(-100);
            clonedPlayer = null;
        }
    }


}
