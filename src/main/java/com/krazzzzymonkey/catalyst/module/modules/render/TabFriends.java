package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

import static com.krazzzzymonkey.catalyst.managers.FriendManager.friendsList;

public class TabFriends extends Modules {
    public static BooleanValue prefix;
    private ModeValue friendColor;

    public static String color = "";
    public TabFriends() {
        super("TabFriends", ModuleCategory.RENDER, "Renders your friends differently in the tablist");
        prefix = new BooleanValue("Prefix", true, "Adds \"[F]\" before player name");
        this.friendColor = new ModeValue("FriendColor", new Mode("DarkRed", false), new Mode("Red", false), new Mode("Gold", false), new Mode("Yellow", false), new Mode("DarkGreen", false), new Mode("Green", false), new Mode("Aqua", true), new Mode("DarkAqua", false), new Mode("DarkBlue", false), new Mode("Blue", false), new Mode("LightPurple", false), new Mode("DarkPurple", false), new Mode("DarkGray", false), new Mode("Gray", false), new Mode("Black", false));
        this.addValue(prefix, friendColor);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null )
            return;

        if (friendColor.getMode("DarkRed").isToggled()) {
            color = "\u00A74";
        }else
        if (friendColor.getMode("Red").isToggled()) {
            color = "\u00A7c";
        }else
        if (friendColor.getMode("Gold").isToggled()) {
            color = "\u00A76";
        }else
        if (friendColor.getMode("Yellow").isToggled()) {
            color = "\u00A7e";
        }else
        if (friendColor.getMode("DarkGreen").isToggled()) {
            color = "\u00A72";
        }else
        if (friendColor.getMode("Green").isToggled()) {
            color = "\u00A7a";
        }else
        if (friendColor.getMode("Aqua").isToggled()) {
            color = "\u00A7b";
        }else
        if (friendColor.getMode("DarkAqua").isToggled()) {
            color = "\u00A73";
        }else
        if (friendColor.getMode("DarkBlue").isToggled()) {
            color = "\u00A71";
        }else
        if (friendColor.getMode("Blue").isToggled()) {
            color = "\u00A79";
        }else
        if (friendColor.getMode("LightPurple").isToggled()) {
            color = "\u00A7d";
        }else
        if (friendColor.getMode("DarkPurple").isToggled()) {
            color = "\u00A75";
        }else
        if (friendColor.getMode("Gray").isToggled()) {
            color = "\u00A77";
        }else
        if (friendColor.getMode("DarkGray").isToggled()) {
            color = "\u00A78";
        }else
        if (friendColor.getMode("Black").isToggled()) {
            color = "\u00A70";
        }

    });


}
