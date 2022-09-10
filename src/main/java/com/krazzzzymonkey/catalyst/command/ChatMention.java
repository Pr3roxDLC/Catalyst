package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.ChatMentionManager;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class ChatMention extends Command
{
    public ChatMention()
    {
        super("chatmention");
    }

    @Override
    public void runCommand(String s, String[] args)
    {
        try
        {
            if(args[0].equalsIgnoreCase("add")) {
                    ChatMentionManager.addMention(args[1]);
            }
            else
            if(args[0].equalsIgnoreCase("remove")) {
                ChatMentionManager.removeMention(args[1]);
            }
            else
            if(args[0].equalsIgnoreCase("clear")) {
                ChatMentionManager.clear();
            }
        }
        catch(Exception e)
        {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription()
    {
        return "Chat Mention Manager.";
    }

    @Override
    public String getSyntax()
    {
        return "chatmention <add/remove/clear> <word>";
    }
}