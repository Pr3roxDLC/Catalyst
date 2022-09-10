package com.krazzzzymonkey.catalyst.managers;

import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

import java.util.ArrayList;

public class ChatMentionManager {

    public static ArrayList<String> mentionList = new ArrayList<String>();

    public static void addMention(String word) {
        if(!mentionList.contains(word)) {
            mentionList.add(word);
            FileManager.saveChatMention();
            ChatUtils.message(("\u00A7b" +word + "\u00a7 Added to mention \u00a77list."));
        }
    }

    public static void removeMention(String word) {
        if(mentionList.contains(word)) {
            mentionList.remove(word + " ");
            FileManager.saveChatMention();
            ChatUtils.message("\u00A7b" + word + "\u00a7 Removed from mention \u00a77list.");
        }
    }

    public static void clear() {
        if(!mentionList.isEmpty()) {
            mentionList.clear();
            FileManager.saveChatMention();
            ChatUtils.message("\u00a7bMention \u00a77list cleared.");
        }
    }

    public static ArrayList<String> getMentionList(){
        return mentionList;
    }
}
