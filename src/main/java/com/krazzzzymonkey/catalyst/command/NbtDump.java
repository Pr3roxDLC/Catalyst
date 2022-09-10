/*
package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.paste.PasteBuilder;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.concurrent.CompletableFuture;

public class NbtDump extends Command {

    public NbtDump() {
        super("nbtdump");
    }

    @Override
    public void runCommand(String s, String[] args) {

        final PasteBuilder paste = new PasteBuilder().setName("Catalyst NBT Dump");
        final ItemStack stack = Minecraft.getMinecraft().player.inventory.getCurrentItem();
        final String nbt = stack.serializeNBT().toString();

        if (args[0].equalsIgnoreCase("paste")) {
            ResourceLocation registryName = stack.getItem().getRegistryName();
            String name = stack.getDisplayName() + (registryName == null ? "" : " (" + registryName + ")");
            CompletableFuture.runAsync(() -> {
                paste.addContent(name, nbt);
                paste.post();
            });
        } else {
            ChatUtils.normalMessage(nbt);
            Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(nbt), null);
            ChatUtils.normalMessage("NBT copied to clipboard.");
        }

    }

    @Override
    public String getDescription() {
        return "Dump nbt to clipboard or upload as paste.";
    }

    @Override
    public String getSyntax() {
        return "nbtdump [paste]";
    }

}
*/
