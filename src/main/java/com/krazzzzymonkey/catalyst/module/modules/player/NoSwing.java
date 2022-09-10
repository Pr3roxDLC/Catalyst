package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;


public class NoSwing extends Modules {

    public NoSwing() {
        super("NoSwing", ModuleCategory.PLAYER, "Prevents sending CPacketAnimation to server");
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            Packet packet = e.getPacket();

            if (packet instanceof CPacketAnimation) {
                e.setCancelled(true);
            }
        }
    });
}
