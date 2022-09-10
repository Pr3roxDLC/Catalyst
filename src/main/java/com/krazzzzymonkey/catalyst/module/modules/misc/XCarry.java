package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCloseWindow;

public class XCarry extends Modules {

    public XCarry() {
        super("XCarry", ModuleCategory.MISC, "Allows you to use you crafting slots as inventory space");
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {

        Packet packet = e.getPacket();

        if (packet instanceof CPacketCloseWindow) {

            final CPacketCloseWindow cPacketCloseWindow = (CPacketCloseWindow) packet;
            if (cPacketCloseWindow.windowId == Minecraft.getMinecraft().player.inventoryContainer.windowId) {
                e.setCancelled(true);
            }

        }

    });

    public void onDisable() {
        super.onDisable();
        if (Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCloseWindow(Minecraft.getMinecraft().player.inventoryContainer.windowId));
        }
    }
}
