package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

//TODO ALLOW THE USER TO ADD MORE INSULTS

public class Insulter extends Modules {

    public IntegerValue settingDelay;
    public BooleanValue greenText;

    public Insulter() {
        super("Insulter", ModuleCategory.CHAT, "Insults random players in chat");
        settingDelay = new IntegerValue("Delay", 50, 1, 500, "");
        greenText = new BooleanValue("GreenText", true, "");

        this.addValue(settingDelay, greenText);
    }

    int delay = 0;

    String prefix = "";

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        int Delay = 1 + settingDelay.getValue();
        delay++;
        if (greenText.getValue()) {
            prefix = "> ";
        }
        if (!greenText.getValue()) {
            prefix = "";
        }
        final NetHandlerPlayClient netHandlerPlayClient = Minecraft.getMinecraft().getConnection();
        if (netHandlerPlayClient != null) {
            final Collection<NetworkPlayerInfo> playerInfoMap = netHandlerPlayClient.getPlayerInfoMap();
            final GuiPlayerTabOverlay tabOverlay = Minecraft.getMinecraft().ingameGUI.getTabList();

            final String outputText = playerInfoMap.stream().map(tabOverlay::getPlayerName).collect(Collectors.joining(", "));
            String[] players = outputText.split(",");
            if (delay > Delay) {
                Random r = new Random();
                int randomNumber = r.nextInt(players.length);
                String randomPlayer = players[randomNumber];
                if (randomPlayer.contains(Minecraft.getMinecraft().player.getName())) return;
                String insult = this.getRandomStringFromArray(insults).replace("{name}", randomPlayer);
                Minecraft.getMinecraft().player.sendChatMessage(prefix + insult);
                delay = 0;
            }


        }

    });

    private String getRandomStringFromArray(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    public static final String[] insults = new String[]{
        "{name} you're a fat virgin.",
        "{name} you're a retarded newfag.",
        "{name} you're such a bad pvper.",
        "Stop being british {name}.",
        "Why do you eat beans and toast for breakfast {name}?",
        "You're really fucking ez {name}.",
        "Imagine being like {name} and not having Catalyst xD",
        "You pvp like a quadriplegic {name}",
        "Your totems pop like bubblerap {name}",
        "What client do you use? rusherhack? {name}",
        "People like {name} are the reason we have middle fingers."
    };
}
