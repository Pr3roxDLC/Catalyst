package com.krazzzzymonkey.catalyst.utils.visual;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatUtils {

    public static void component(ITextComponent component) {
        if (Wrapper.INSTANCE.player() == null || Wrapper.INSTANCE.mc().ingameGUI.getChatGUI() == null)
            return;
        Wrapper.INSTANCE.mc().ingameGUI.getChatGUI()
                .printChatMessage(new TextComponentTranslation("")
                        .appendSibling(component));
    }

    public static void message(String message) {
        if(Minecraft.getMinecraft().player == null)return;
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentTranslation("\u00a78[" + ChatColor.RESET + Main.NAME + "\u00a78]\u00a77 " + message), 582956);

    }
    public static void normalMessage(String message) {
        component(new TextComponentTranslation("\u00a78[" + ChatColor.RESET + Main.NAME + "\u00a78]\u00a77 " +message));
    }

    public static void normalChat(String message) {
        component(new TextComponentTranslation(message));
    }

    public static void timeStampedChat(String message) {
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
        String strDate = dateFormatter.format(date);
        component(new TextComponentTranslation("\u00A7d<" + strDate + ">\u00A7r" + message));
    }

    public static void warning(String message) {
        message("\u00a78[\u00a7eWARNING\u00a78]\u00a7e " + message);
    }

    public static void error(String message) {
        message("\u00a78[\u00a74ERROR\u00a78]\u00a7c " + message);
    }
}
