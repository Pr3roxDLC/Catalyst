package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


//TODO STOP THE CHAT FROM BEING VISIBLE
public class NoChat extends Modules {

    public NoChat() {
        super("NoChat", ModuleCategory.CHAT, "Stops rendering chat");
    }

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
            return;
        }
        Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages(true);
    });
    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
            return;
        }
        if(e.getPacket() instanceof SPacketChat){
            e.setCancelled(true);
        }
    });
    @Override
    public void onDisable() {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages(false);
        super.onDisable();
    }
}
