package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class Panic extends Command {
    public Panic() {
        super("panic");
    }

    @Override
    public void runCommand(String s, String[] args) {
        System.out.println(args.length);
        if (args.length == 1 && args[0].equals("")) {
            int toggledModules = 0;
            for (Modules module : ModuleManager.getModules()) {
                if (module.isToggled()) {
                    toggledModules++;
                    module.toggle();
                }
            }
            ChatUtils.message("Disabled " + ChatColor.AQUA + toggledModules + ChatColor.GRAY + " modules!");
        } else if (args.length == 1) {
            String category = args[0];
            for (ModuleCategory moduleCategory : ModuleCategory.values()) {
                if (category.equalsIgnoreCase(moduleCategory.toString())) {
                    int toggledModules = 0;
                    for (Modules module : ModuleManager.getModules()) {
                        if (module.getCategory() == moduleCategory) {
                            if (module.isToggled()) {
                                toggledModules++;
                                module.toggle();
                            }
                        }
                    }
                    ChatUtils.message("Disabled " + ChatColor.AQUA + toggledModules + ChatColor.GRAY + " modules in " + ChatColor.AQUA + category + ChatColor.GRAY + "!");
                    return;
                }
            }
            ChatUtils.error("No category named: " + category);



        } else {
            ChatUtils.error("Usage: " + getSyntax());
        }

    }

    @Override
    public String getDescription() {
        return "Disables all modules in Catalyst";
    }

    @Override
    public String getSyntax() {
        return "panic [category]";
    }
}
