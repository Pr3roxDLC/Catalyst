package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;


public class PortalGodMode extends Modules {

    public PortalGodMode() {
        super("PortalGodMode", ModuleCategory.PLAYER, "Allows you to be invincible when in a nether portal");
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            Packet packet = e.getPacket();

            if (packet instanceof CPacketConfirmTeleport) {
                e.setCancelled(true);
            }
        }
    });
}
