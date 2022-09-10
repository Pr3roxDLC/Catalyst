package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class FaceTowards extends Command {
    public FaceTowards() {
        super("lookat");
    }

    @Override
    public void runCommand(String s, String[] args) {
        if (args.length == 2) {
            float rotOld = Minecraft.getMinecraft().player.rotationPitch;
            BlockUtils.faceBlockClient(new BlockPos(Double.parseDouble(args[0]), Minecraft.getMinecraft().player.posY, Double.parseDouble(args[1])));
            Minecraft.getMinecraft().player.rotationPitch = rotOld;
        }
        if (args.length == 3) {
            BlockUtils.faceBlockClient(new BlockPos(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])));
        }
    }

    @Override
    public String getDescription() {
        return "Makes the player look at a given coordinate";
    }

    @Override
    public String getSyntax() {
        return "lookat <x><z>/<x><y><z>";
    }
}
