package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatTimeStamps extends Modules {

    private final ModeValue bracketColor;
    private final ModeValue timeColor;

    public ChatTimeStamps() {
        super("ChatTimeStamps", ModuleCategory.CHAT, "Adds timestamps to messages that were sent in chat");
        this.bracketColor = new ModeValue("BracketColor", new Mode("Dark Red", true), new Mode("Red", false), new Mode("Gold", false), new Mode("Yellow", false), new Mode("Dark Green", false), new Mode("Green", false), new Mode("Aqua", false), new Mode("Dark Aqua", false), new Mode("Dark Blue", false), new Mode("Blue", false), new Mode("Light Purple", false), new Mode("Dark Purple", false), new Mode("Dark Gray", false), new Mode("Gray", false), new Mode("Black", false));
        this.timeColor = new ModeValue("TimeColor", new Mode("Dark Red", true), new Mode("Red", false), new Mode("Gold", false), new Mode("Yellow", false), new Mode("Dark Green", false), new Mode("Green", false), new Mode("Aqua", false), new Mode("Dark Aqua", false), new Mode("Dark Blue", false), new Mode("Blue", false), new Mode("Light Purple", false), new Mode("Dark Purple", false), new Mode("Dark Gray", false), new Mode("Gray", false), new Mode("Black", false));
        this.addValue(bracketColor, timeColor);
    }

    String bracketFormatting;
    String timeFormatting;

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {

        if (bracketColor.getMode("Dark Red").isToggled()) {
            bracketFormatting = "\u00A74";
        }
        if (bracketColor.getMode("Red").isToggled()) {
            bracketFormatting = "\u00A7c";
        }
        if (bracketColor.getMode("Gold").isToggled()) {
            bracketFormatting = "\u00A76";
        }
        if (bracketColor.getMode("Yellow").isToggled()) {
            bracketFormatting = "\u00A7e";
        }
        if (bracketColor.getMode("Dark Green").isToggled()) {
            bracketFormatting = "\u00A72";
        }
        if (bracketColor.getMode("Green").isToggled()) {
            bracketFormatting = "\u00A7a";
        }
        if (bracketColor.getMode("Aqua").isToggled()) {
            bracketFormatting = "\u00A7b";
        }
        if (bracketColor.getMode("Dark Aqua").isToggled()) {
            bracketFormatting = "\u00A73";
        }
        if (bracketColor.getMode("Dark Blue").isToggled()) {
            bracketFormatting = "\u00A71";
        }
        if (bracketColor.getMode("Blue").isToggled()) {
            bracketFormatting = "\u00A79";
        }
        if (bracketColor.getMode("Light Purple").isToggled()) {
            bracketFormatting = "\u00A7d";
        }
        if (bracketColor.getMode("Dark Purple").isToggled()) {
            bracketFormatting = "\u00A75";
        }
        if (bracketColor.getMode("Gray").isToggled()) {
            bracketFormatting = "\u00A77";
        }
        if (bracketColor.getMode("Dark Gray").isToggled()) {
            bracketFormatting = "\u00A78";
        }
        if (bracketColor.getMode("Black").isToggled()) {
            bracketFormatting = "\u00A70";
        }


        if (timeColor.getMode("Dark Red").isToggled()) {
            timeFormatting = "\u00A74";
        }
        if (timeColor.getMode("Red").isToggled()) {
            timeFormatting = "\u00A7c";
        }
        if (timeColor.getMode("Gold").isToggled()) {
            timeFormatting = "\u00A76";
        }
        if (timeColor.getMode("Yellow").isToggled()) {
            timeFormatting = "\u00A7e";
        }
        if (timeColor.getMode("Dark Green").isToggled()) {
            timeFormatting = "\u00A72";
        }
        if (timeColor.getMode("Green").isToggled()) {
            timeFormatting = "\u00A7a";
        }
        if (timeColor.getMode("Aqua").isToggled()) {
            timeFormatting = "\u00A7b";
        }
        if (timeColor.getMode("Dark Aqua").isToggled()) {
            timeFormatting = "\u00A73";
        }
        if (timeColor.getMode("Dark Blue").isToggled()) {
            timeFormatting = "\u00A71";
        }
        if (timeColor.getMode("Blue").isToggled()) {
            timeFormatting = "\u00A79";
        }
        if (timeColor.getMode("Light Purple").isToggled()) {
            timeFormatting = "\u00A7d";
        }
        if (timeColor.getMode("Dark Purple").isToggled()) {
            timeFormatting = "\u00A75";
        }
        if (timeColor.getMode("Gray").isToggled()) {
            timeFormatting = "\u00A77";
        }
        if (timeColor.getMode("Dark Gray").isToggled()) {
            timeFormatting = "\u00A78";
        }
        if (timeColor.getMode("Black").isToggled()) {
            timeFormatting = "\u00A70";
        }

        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
        String strDate = dateFormatter.format(date);
        TextComponentString time = new TextComponentString(bracketFormatting + "<" + timeFormatting + strDate + bracketFormatting + ">" + ChatFormatting.RESET);
        event.setMessage(time.appendSibling(event.getMessage()));
    }
}
