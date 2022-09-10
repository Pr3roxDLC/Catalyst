package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class MiddleClickFriends extends Modules {

    public MiddleClickFriends() {
        super("MiddleClickFriends", ModuleCategory.MISC, "Allows you to add or remove a player from friend list");
    }

    private boolean Clicked = false;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {

        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Minecraft.getMinecraft().currentScreen != null)
            return;

        if (!Mouse.isButtonDown(2)) {
            Clicked = false;
            return;
        }

        if (!Clicked) {
            Clicked = true;

            final RayTraceResult entity = Minecraft.getMinecraft().objectMouseOver;

            if (entity == null || entity.typeOfHit != RayTraceResult.Type.ENTITY)
                return;

            Entity player = entity.entityHit;

            if (player == null || !(player instanceof EntityPlayer))
                return;

            if (FriendManager.friendsList.contains(player.getName())) {
                FriendManager.removeFriend(player.getName());

            } else {
                FriendManager.addFriend(player.getName());

            }
        }

    });

}
