package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.AutoGGManager;

import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class AutoGG extends Command {
    public AutoGG() {
        super("autogg");
    }

    @Override
    public void runCommand(String s, String[] args) {

        try {
            if (args[0].equalsIgnoreCase("add")) {
                String message = "";
                for (int i = 1; i < args.length; i++) {
                    message = message.concat(args[i] + " ");
                }
                message = message.substring(0, message.length() - 1);

                AutoGGManager.addMessage(message);
            } else if (args[0].equalsIgnoreCase("remove")) {
                String message = "";
                for (int i = 1; i < args.length; i++) {
                    message = message.concat(args[i] + " ");
                }
                message = message.substring(0, message.length() - 1);

                AutoGGManager.removeMessage(message);
            } else if (args[0].equalsIgnoreCase("clear")) {
                AutoGGManager.clear();
            }else if(args[0].equalsIgnoreCase("list")){
                ChatUtils.normalMessage("AutoGG messages:");
                for(String message : AutoGGManager.messages){
                    ChatUtils.normalMessage(message);
                }
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Add messages to AutoGG module";
    }

    @Override
    public String getSyntax() {
        return "autogg <add/remove/clear/list> <message>";
    }
}
