package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.Timer;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PingSpoof extends Modules {

    CPacketKeepAlive cPacketKeepAlive = null;
    Timer timer = new Timer();

    public static IntegerValue ping = new IntegerValue("Ping", 200, 0, 5000, "The amount of latency you want to spoof");

    public PingSpoof() {
        super("PingSpoof", ModuleCategory.MISC, "Spoofs your ping to a specified amount");
        addValue(ping);
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if(event.getPacket() instanceof CPacketKeepAlive && cPacketKeepAlive != event.getPacket() && ping.getValue() != 0) {
            cPacketKeepAlive = (CPacketKeepAlive) event.getPacket();
            event.setCancelled(true);
            timer = timer.reset();
        }
    });


    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent event) {
        if (timer.passedDs(ping.getValue()) && cPacketKeepAlive != null) {
            mc.player.connection.sendPacket(cPacketKeepAlive);
            cPacketKeepAlive = null;
        }
    }
}
