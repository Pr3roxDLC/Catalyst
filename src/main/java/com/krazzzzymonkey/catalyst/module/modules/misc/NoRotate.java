package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Modules {

    public NoRotate() {
        super("NoRotate", ModuleCategory.MISC, "Stops server side rotation packets");
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {

        Packet packet = e.getPacket();

        if (packet instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) packet;
            if (Minecraft.getMinecraft().player != null) {
                sPacketPlayerPosLook.yaw = Minecraft.getMinecraft().player.rotationYaw;
                sPacketPlayerPosLook.pitch = Minecraft.getMinecraft().player.rotationPitch;
            }
        }

    });

}
