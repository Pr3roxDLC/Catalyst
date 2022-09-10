package com.krazzzzymonkey.catalyst.command;


import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class Drawn extends Command {
    public Drawn() {
        super("drawn");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (args.length > 1 || args[0].equals("")) throw new Exception();
            try {
                ModuleManager.getModule(args[0]).setDrawn(!ModuleManager.getModule(args[0]).isDrawn());
                ChatUtils.message("Drawn is now " + (ModuleManager.getModule(args[0]).isDrawn() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.GRAY + " for " + ChatColor.AQUA + ModuleManager.getModule(args[0]).getModuleName() + ChatColor.GRAY + ".");
            } catch (Exception e) {
                ChatUtils.error("Unknown Module: " + args[0]);
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Toggles the drawn setting in a specified module";
    }

    @Override
    public String getSyntax() {
        return "drawn <module>";
    }
}
