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


//TODO ADD MORE MODES

public class FancyChat extends Modules {

    ModeValue mode;

    public FancyChat() {
        super("FancyChat", ModuleCategory.CHAT, "Converts chat messages into unicode fonts");
        mode = new ModeValue("Mode", new Mode("Smooth", true), new Mode("Circle", false));
        this.addValue(mode);
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT){
            if ((e.getPacket() instanceof CPacketChatMessage)) {
                final CPacketChatMessage cPacketChatMessage = (CPacketChatMessage) e.getPacket();
                if (!cPacketChatMessage.getMessage().startsWith("/")) {
                    final String msg = cPacketChatMessage.getMessage();
                    if (mode.getMode("Smooth").isToggled()) {
                        cPacketChatMessage.message = toSmoothUnicode(msg);
                    }
                    if (mode.getMode("Circle").isToggled()) {
                        cPacketChatMessage.message = toCircle(msg);
                    }
                }
            }
        }
    });

    public String toCircle(String msg) {
        return msg
            .replace("a", "\u24D0")
            .replace("b", "\u24D1")
            .replace("c", "\u24D2")
            .replace("d", "\u24D3")
            .replace("e", "\u24D4")
            .replace("f", "\u24D5")
            .replace("g", "\u24D6")
            .replace("h", "\u24D7")
            .replace("i", "\u24D8")
            .replace("j", "\u24D9")
            .replace("k", "\u24DA")
            .replace("l", "\u24DB")
            .replace("m", "\u24DC")
            .replace("n", "\u24DD")
            .replace("o", "\u24DE")
            .replace("p", "\u24DF")
            .replace("q", "\u24E0")
            .replace("r", "\u24E1")
            .replace("s", "\u24E2")
            .replace("t", "\u24E3")
            .replace("u", "\u24E4")
            .replace("v", "\u24E5")
            .replace("w", "\u24E6")
            .replace("x", "\u24E7")
            .replace("y", "\u24E8")
            .replace("z", "\u24E9")

            .replace("A", "\u24B6")
            .replace("B", "\u24B7")
            .replace("C", "\u24B8")
            .replace("D", "\u24B9")
            .replace("E", "\u24BA")
            .replace("F", "\u24BB")
            .replace("G", "\u24BC")
            .replace("H", "\u24BD")
            .replace("I", "\u24BE")
            .replace("J", "\u24BF")
            .replace("K", "\u24DA")
            .replace("L", "\u24DB")
            .replace("M", "\u24DC")
            .replace("N", "\u24DD")
            .replace("O", "\u24DE")
            .replace("P", "\u24DF")
            .replace("Q", "\u24C6")
            .replace("R", "\u24C7")
            .replace("S", "\u24C8")
            .replace("T", "\u24C9")
            .replace("U", "\u24CA")
            .replace("V", "\u24CB")
            .replace("W", "\u24CC")
            .replace("X", "\u24CD")
            .replace("Y", "\u24CE")
            .replace("Z", "\u24CF");
    }

    public String toSmoothUnicode(String msg) {
        return msg
            .replace("a", "\uFF41")
            .replace("b", "\uFF42")
            .replace("c", "\uFF43")
            .replace("d", "\uFF44")
            .replace("e", "\uFF45")
            .replace("f", "\uFF46")
            .replace("g", "\uFF47")
            .replace("h", "\uFF48")
            .replace("i", "\uFF49")
            .replace("j", "\uFF4A")
            .replace("k", "\uFF4B")
            .replace("l", "\uFF4C")
            .replace("m", "\uFF4D")
            .replace("n", "\uFF4E")
            .replace("o", "\uFF4F")
            .replace("p", "\uFF50")
            .replace("q", "\uFF51")
            .replace("r", "\uFF52")
            .replace("s", "\uFF53")
            .replace("t", "\uFF54")
            .replace("u", "\uFF55")
            .replace("v", "\uFF56")
            .replace("w", "\uFF57")
            .replace("x", "\uFF58")
            .replace("y", "\uFF59")
            .replace("z", "\uFF5A")

            .replace("A", "\uFF21")
            .replace("B", "\uFF22")
            .replace("C", "\uFF23")
            .replace("D", "\uFF24")
            .replace("E", "\uFF25")
            .replace("F", "\uFF26")
            .replace("G", "\uFF27")
            .replace("H", "\uFF28")
            .replace("I", "\uFF29")
            .replace("J", "\uFF2A")
            .replace("K", "\uFF2B")
            .replace("L", "\uFF2C")
            .replace("M", "\uFF2D")
            .replace("N", "\uFF2E")
            .replace("O", "\uFF2F")
            .replace("P", "\uFF30")
            .replace("Q", "\uFF31")
            .replace("R", "\uFF32")
            .replace("S", "\uFF33")
            .replace("T", "\uFF34")
            .replace("U", "\uFF35")
            .replace("V", "\uFF36")
            .replace("W", "\uFF37")
            .replace("X", "\uFF38")
            .replace("Y", "\uFF39")
            .replace("Z", "\uFF3A")

            .replace("1", "\uFF11")
            .replace("2", "\uFF12")
            .replace("3", "\uFF13")
            .replace("4", "\uFF14")
            .replace("5", "\uFF15")
            .replace("6", "\uFF16")
            .replace("7", "\uFF17")
            .replace("8", "\uFF18")
            .replace("9", "\uFF19")
            .replace("0", "\uFF10");


    }

}
