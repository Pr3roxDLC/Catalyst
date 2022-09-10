package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class MsgAll extends Command {
    public MsgAll() {
        super("msgall");
    }


    @Override
    public void runCommand(String s, String[] args) {
        try {
            String[] players;
            final NetHandlerPlayClient netHandlerPlayClient = Minecraft.getMinecraft().getConnection();
            if (netHandlerPlayClient != null) {
                final Collection<NetworkPlayerInfo> playerInfoMap = netHandlerPlayClient.getPlayerInfoMap();
                final GuiPlayerTabOverlay tabOverlay = Minecraft.getMinecraft().ingameGUI.getTabList();

                final String outputText = playerInfoMap.stream().map(tabOverlay::getPlayerName).collect(Collectors.joining(", "));
                players = outputText.split(",");
                StringBuilder message = new StringBuilder();
                for (String arg : args) {
                    message.append(" ").append(arg);
                }
                runThread(message.toString(), players);
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    private void runThread(String message, String[] players) {
        (new Thread(() -> {
            try {
                for (String player : players) {
                    Minecraft.getMinecraft().player.sendChatMessage("/msg " + player + " " + message + " " + getAlphaNumericString(5));
                    ChatUtils.normalMessage(" Sent: /msg " + player + " " + message+ " " + getAlphaNumericString(5));
                    Thread.sleep(2000);
                }
            } catch (Exception ignored) {
            }
        })
        ).start();
    }
    static String getAlphaNumericString(int n) {

        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString
                = new String(array, StandardCharsets.UTF_8);

        StringBuffer r = new StringBuffer();

        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9'))
                    && (n > 0)) {

                r.append(ch);
                n--;
            }
        }
        return r.toString();
    }
    @Override
    public String getDescription() {
        return "Messages all players on the server";
    }

    @Override
    public String getSyntax() {
        return "msgall <message>";
    }
}

