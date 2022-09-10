package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class VClip extends Command {
    public VClip() {
        super("vclip");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            Wrapper.INSTANCE.player().setPosition(Wrapper.INSTANCE.player().posX,
                Wrapper.INSTANCE.player().posY + Double.parseDouble(args[0]), Wrapper.INSTANCE.player().posZ);
            ChatUtils.message("Height teleported to " + Double.parseDouble(args[0]));
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Teleports you up/down.";
    }

    @Override
    public String getSyntax() {
        return "vclip <height>";
    }
}
