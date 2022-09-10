package com.krazzzzymonkey.catalyst.command;

import net.minecraft.client.Minecraft;

public abstract class Command {

    private final String command;
    Minecraft mc = Minecraft.getMinecraft();

    public Command(String command) {
        this.command = command;
    }

    public abstract void runCommand(String s, String[] args);

    public abstract String getDescription();

    public abstract String getSyntax();

    public String getName() {
        // TODO: is there a reason this ever should be overwritten? otherwise make this final and never overwrite it.
        return command;
    }

    public String getCommand() {
        return command;
    }

}
