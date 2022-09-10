package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.CommandManager;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class Help extends Command {
    public Help() {
        super("help");
    }

    @Override
    public void runCommand(String s, String[] args) {
        ChatUtils.normalChat(" ");
        ChatUtils.normalChat(ChatColor.GREEN + "==============" + ChatColor.WHITE + "CATALYST" + ChatColor.GREEN + "==============");
        for (Command cmd : CommandManager.getInstance().getCommands()) {
            ChatUtils.normalChat(cmd.getSyntax().replace("<", ChatColor.GRAY + "<" + ChatColor.GREEN).replace(">", "\2477>")
                .replace("[", ChatColor.GRAY + "[" + ChatColor.GREEN).replace("]", "\2477]"));
            ChatUtils.normalChat(ChatColor.GRAY + " ➥ " + cmd.getDescription());
        }
        ChatUtils.normalChat(ChatColor.GREEN + "====================================");
        ChatUtils.normalChat(" ");
    }

    @Override
    public String getDescription() {
        return "Lists all commands.";
    }

    @Override
    public String getSyntax() {
        return "help";
    }
}
