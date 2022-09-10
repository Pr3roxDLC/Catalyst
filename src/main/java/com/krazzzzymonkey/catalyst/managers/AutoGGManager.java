package com.krazzzzymonkey.catalyst.managers;

import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

import java.util.ArrayList;

public class AutoGGManager {

    public static ArrayList<String> messages = new ArrayList<String>();

    public static void addMessage(String message) {
        if(!messages.contains(message)) {
            messages.add(message);
           // FileManager.saveChatMention();
            ChatUtils.message("\"" +ChatColor.AQUA + message + ChatColor.GRAY + "\" has been " + ChatColor.GREEN + "added" + ChatColor.GRAY + " to message list.");
        }
    }

    public static void removeMessage(String message) {
        if(messages.contains(message)) {
            messages.remove(message + " ");
            //FileManager.saveChatMention();
            ChatUtils.message("\"" +ChatColor.AQUA + message + ChatColor.GRAY + "\" has been " + ChatColor.RED + "removed" + ChatColor.GRAY + " from the message list.");
            return;
        }
        ChatUtils.error("\"" +ChatColor.AQUA + message + ChatColor.GRAY + "\" is not in the message list.");
    }

    public static void clear() {
        if(!messages.isEmpty()) {
            messages.clear();
            FileManager.saveChatMention();
            ChatUtils.message(ChatColor.AQUA +"Message list" + ChatColor.GRAY + " has been cleared.");
        }
    }
}
