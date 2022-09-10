package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class Toggle extends Command{
    public Toggle() {
        super("toggle");
    }


    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (args.length > 1 || args[0].equals("")) throw new Exception();
            try {
                ModuleManager.getModule(args[0]).setToggled(!ModuleManager.getModule(args[0]).isToggled());
                ChatUtils.message(ChatColor.AQUA + ModuleManager.getModule(args[0]).getModuleName()  + ChatColor.GRAY     + " is now " + (ModuleManager.getModule(args[0]).isToggled() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.GRAY + ".");
            } catch (Exception e) {
                ChatUtils.error("Unknown Module: " + args[0]);
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Toggles a specified module on or off";
    }

    @Override
    public String getSyntax() {
        return "toggle <module>";
    }
}
