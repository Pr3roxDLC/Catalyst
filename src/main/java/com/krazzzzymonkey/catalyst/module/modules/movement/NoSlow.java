package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//TODO GUI WALK
public class NoSlow extends Modules {

    private final BooleanValue items;
    public static BooleanValue soulSand;
    public static BooleanValue slimeBlocks;
    public static BooleanValue web;

    public NoSlow() {
        super("NoSlow", ModuleCategory.MOVEMENT, "Makes you take no item slow down");

        this.items = new BooleanValue("Items", true, "Allows you to use items without slowing down");
        soulSand = new BooleanValue("SoulSand", true, "Allows you to walk on soul sand without slowing down");
        slimeBlocks = new BooleanValue("SlimeBlocks", true, "Allows you to walk on slime blocks without slowing down");
        web = new BooleanValue("Webs", true, "Allows you to walk through webs without slowing down");
        this.addValue(items, soulSand, slimeBlocks, web);
    }


    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (Minecraft.getMinecraft().player == null) return;
        if (items.getValue()) {
            if (Minecraft.getMinecraft().player.isHandActive() && !Minecraft.getMinecraft().player.isRiding()) {
                Minecraft.getMinecraft().player.movementInput.moveForward *= 5;
                Minecraft.getMinecraft().player.movementInput.moveStrafe *= 5;
            }
        }
    }

}
