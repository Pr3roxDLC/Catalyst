package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.CommandManager;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class Prefix extends Command {

    public Prefix() {
        super("prefix");
    }

    @Override
    public void runCommand(String s, String[] args) {
        if(args[0].length() != 1){
            ChatUtils.error("Invalid Prefix!");
            return;
        }

        CommandManager.prefix =  args[0];
        FileManager.savePrefix();

        ChatUtils.normalMessage("Set Catalyst prefix to: " + ChatColor.AQUA + CommandManager.prefix);

    }

    @Override
    public String getDescription() {
        return "Changes the Command Prefix";
    }

    @Override
    public String getSyntax() {
        return "prefix <prefix>";
    }
}
