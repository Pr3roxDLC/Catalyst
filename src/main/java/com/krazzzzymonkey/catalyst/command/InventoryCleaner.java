package com.krazzzzymonkey.catalyst.command;


import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class InventoryCleaner extends Command {
    public InventoryCleaner() {
        super("inventorycleaner");
    }

    @Override
    public void runCommand(String s, String[] args) {
        if (args[0].equals("list")) {
            StringBuilder builder = new StringBuilder();
            com.krazzzzymonkey.catalyst.module.modules.misc.InventoryCleaner.listItems.forEach(n -> {
                if (n != null) {
                    builder.append(n.getItemStackDisplayName(n.getDefaultInstance()));
                    builder.append(", ");
                }
            });
            ChatUtils.normalMessage(builder.substring(0, builder.length() - 2));
        }
        if (args.length == 2) {
            if (args[0].equals("add")) {
                try {
                    com.krazzzzymonkey.catalyst.module.modules.misc.InventoryCleaner.listItems.add(Item.getByNameOrId(args[1]));
                    ChatUtils.normalMessage("Added " + Item.getByNameOrId(args[1]).getItemStackDisplayName(Item.getByNameOrId(args[1]).getDefaultInstance()) + " to the list.");
                } catch (NullPointerException e) {
                    ChatUtils.normalMessage("Unable to add item that does not exist.");
                }
            }
            if (args[0].equals("remove")) {
                try {
                    if (com.krazzzzymonkey.catalyst.module.modules.misc.InventoryCleaner.listItems.remove(Item.getByNameOrId(args[1]))) {
                        ChatUtils.normalMessage("Removed " + Item.getByNameOrId(args[1]).getItemStackDisplayName(Item.getByNameOrId(args[1]).getDefaultInstance()) + " from the list.");
                    } else {
                        ChatUtils.normalMessage("Unable to remove " + Item.getByNameOrId(args[1]).getItemStackDisplayName(Item.getByNameOrId(args[1]).getDefaultInstance()) + " from the list.");
                    }
                } catch (NullPointerException e) {
                    ChatUtils.normalMessage("Unable to remove item that does not exist.");
                }
            }
            try {
                FileManager.saveInventoryCleaner();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getDescription() {
        return "Edits the list of items to remove/keep by the inventory cleaner";
    }

    @Override
    public String getSyntax() {
        return "inventorycleaner <add/remove/list> <block>";
    }
}
