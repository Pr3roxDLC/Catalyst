package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.module.modules.render.Breadcrumbs;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

import java.util.Locale;

public class BreadcrumbsCommand extends Command{
    public BreadcrumbsCommand() {
        super("breadcrumbs");
    }

    @Override
    public void runCommand(String s, String[] args) {
        if(args[0].equalsIgnoreCase("add")){
            Breadcrumbs.names.add(args[1].toLowerCase());
            ChatUtils.message("Added " + args[1] + " to the breadcrumb list");
        }
        if(args[0].equalsIgnoreCase("remove")){
            if(Breadcrumbs.names.contains(args[1].toLowerCase())){
                ChatUtils.message("Removed " + args[1] + " from the breadcrumb list");
                Breadcrumbs.names.remove(args[1].toLowerCase());
            }else{
                ChatUtils.message(args[1] + " is not in the list");
            }
        }
        if(args[0].equalsIgnoreCase("clear")){
            ChatUtils.message("Cleared the breadcrumb list" );
            Breadcrumbs.names.clear();
            Breadcrumbs.points.clear();
        }
    }

    @Override
    public String getDescription() {
        return "Add/Remove players you want to be traced by Breadcrumbs";
    }

    @Override
    public String getSyntax() {
        return "breadcrumbs <<add/remove> <player>>/<clear>";
    }
}
