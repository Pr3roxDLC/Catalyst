package com.krazzzzymonkey.catalyst.module.modules.chat;


import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class ChatSuffix extends Modules {

    private BooleanValue addChars;
    private BooleanValue blueText;

    public ChatSuffix() {
        super("ChatSuffix", ModuleCategory.CHAT, "Adds client name at the end of a chat message");
        this.addChars = new BooleanValue("AddChars", true, "Adds random unicode chars to the suffix");
        this.blueText = new BooleanValue("BlueName", false, "Adds \"`\" to the beginning of the suffix making it blue on some severs");
        this.addValue(addChars, blueText);
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {
            Packet packet = e.getPacket();
            if ((packet instanceof CPacketChatMessage)) {
                String decoChar = this.getRandomStringFromArray(deco);
                final CPacketChatMessage cPacketChatMessage = (CPacketChatMessage) packet;
                if (!cPacketChatMessage.getMessage().startsWith("/")) {
                    String concat;
                    if (addChars.getValue()) {
                        concat = cPacketChatMessage.getMessage().concat("   " + decoChar + " \u1d04\u1d00\u1d1b\u1d00\u029f\u028f\ua731\u1d1b " + decoChar);
                    } else {
                        concat = cPacketChatMessage.getMessage().concat(" \u1d04\u1d00\u1d1b\u1d00\u029f\u028f\ua731\u1d1b ");
                    }
                    if (blueText.getValue()) {
                        if (addChars.getValue()) {
                            concat = cPacketChatMessage.getMessage().concat("   `" + decoChar + " \u1d04\u1d00\u1d1b\u1d00\u029f\u028f\ua731\u1d1b " + decoChar);
                        } else {
                            concat = cPacketChatMessage.getMessage().concat("` \u1d04\u1d00\u1d1b\u1d00\u029f\u028f\ua731\u1d1b ");
                        }
                    }
                    cPacketChatMessage.message = concat;
                }

            }
        }
    });

    private String getRandomStringFromArray(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    private static final String[] deco = new String[]{"\u2622", "\u2623", "\u2620", "\u26A0", "\u2624", "\u269A", "\u2020", "\u262F", "\u262E", "\u2698", "\u271E", "\u271F", "\u2727", "\u2661", "\u2665", "\u2764", "\u266B", "\u266A", "\u2655", "\u265B", "\u2656", "\u265C", "\u2601", "\u2708"};
}
