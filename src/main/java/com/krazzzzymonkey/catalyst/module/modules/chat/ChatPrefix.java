package com.krazzzzymonkey.catalyst.module.modules.chat;


import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


//TODO CHECK WHAT SERVER IS ON => CHANGE SETTINGS ACCORDING TO SERVER
public class ChatPrefix extends Modules {

    ModeValue mode;

    public ChatPrefix() {
        super("ChatPrefix", ModuleCategory.CHAT, "Adds prefixes before chat message, Designed for 0b0t.org");
        mode = new ModeValue("Mode", new Mode("Green", true), new Mode("Red", false),
            new Mode("Orange", false), new Mode("Black", false), new Mode("Gray", false),
            new Mode("Blue", false), new Mode("Light Blue", false), new Mode("Yellow", false));
        this.addValue(mode);
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {
            Packet packet = e.getPacket();
            if ((packet instanceof CPacketChatMessage)) {
                final CPacketChatMessage cPacketChatMessage = (CPacketChatMessage) packet;
                if (!cPacketChatMessage.getMessage().startsWith("/")) {
                    if (mode.getMode("Green").isToggled()) {
                        cPacketChatMessage.message = ">" + cPacketChatMessage.getMessage();

                    }
                    if (mode.getMode("Red").isToggled()) {
                        cPacketChatMessage.message = "<" + cPacketChatMessage.getMessage();

                    }
                    if (mode.getMode("Orange").isToggled()) {
                        cPacketChatMessage.message = "," + cPacketChatMessage.getMessage();

                    }
                    if (mode.getMode("Black").isToggled()) {
                        cPacketChatMessage.message = "]" + cPacketChatMessage.getMessage();

                    }
                    if (mode.getMode("Gray").isToggled()) {
                        cPacketChatMessage.message = "[" + cPacketChatMessage.getMessage();

                    }
                    if (mode.getMode("Blue").isToggled()) {
                        cPacketChatMessage.message = ";" + cPacketChatMessage.getMessage();

                    }
                    if (mode.getMode("Light Blue").isToggled()) {
                        cPacketChatMessage.message = ":" + cPacketChatMessage.getMessage();

                    }
                    if (mode.getMode("Yellow").isToggled()) {
                        cPacketChatMessage.message = "!" + cPacketChatMessage.getMessage();

                    }
                }
            }
        }
    });
}
