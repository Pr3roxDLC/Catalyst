package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class EntityDesync extends Command {
    public EntityDesync() {
        super("entitydesync");
    }

    public static Entity getRidingEntity = null;


    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (args.length > 1 || args[0].equals("")) throw new Exception();
            if (args[0].equalsIgnoreCase("dismount")) {
                if (!Minecraft.getMinecraft().player.isRiding()) {
                    ChatUtils.error("You are not currently riding an entity!");
                    getRidingEntity = null;
                    return;
                }
                getRidingEntity = Minecraft.getMinecraft().player.getRidingEntity();

                Minecraft.getMinecraft().player.dismountRidingEntity();
                Minecraft.getMinecraft().world.removeEntity(getRidingEntity);
                ChatUtils.message("Successfully dismounted from the " + getRidingEntity.getName());
            } else if (args[0].equalsIgnoreCase("remount")) {
                if (getRidingEntity != null) {
                    getRidingEntity.isDead = false;
                    if (!Minecraft.getMinecraft().player.isRiding()) {
                        Minecraft.getMinecraft().world.spawnEntity(getRidingEntity);
                        Minecraft.getMinecraft().player.startRiding(getRidingEntity, true);
                    }
                    ChatUtils.message("Force remounted you to the " + getRidingEntity.getName());
                    getRidingEntity = null;
                }
            } else throw new Exception();

        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Dismounts from a rideable entity clientside";
    }

    @Override
    public String getSyntax() {
        return "entitydesync <dismount/remount>";
    }
}
