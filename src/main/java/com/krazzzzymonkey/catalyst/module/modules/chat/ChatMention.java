package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.managers.CommandManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;

import static com.krazzzzymonkey.catalyst.managers.ChatMentionManager.mentionList;


//TODO FIX PREVENT COLOR STRIPPING
public class ChatMention extends Modules {

    public static BooleanValue booleanName;
    public static ModeValue nameTextColor;

    public ChatMention() {

        super("ChatMention", ModuleCategory.CHAT, "Highlights your name and other words in chat");
        booleanName = new BooleanValue("Name", true, "Highlights your player name in chat");
        nameTextColor = new ModeValue("MentionColor", new Mode("Dark Red", true), new Mode("Red", false), new Mode("Gold", false), new Mode("Yellow", false), new Mode("Dark Green", false), new Mode("Green", false), new Mode("Aqua", false), new Mode("Dark Aqua", false), new Mode("Dark Blue", false), new Mode("Blue", false), new Mode("Light Purple", false), new Mode("Dark Purple", false), new Mode("Dark Gray", false), new Mode("Gray", false), new Mode("Black", false));

        this.addValue(booleanName, nameTextColor);

    }


    public void onEnable() {
        super.onEnable();
        ChatUtils.message("To add words to mention list, use command \"" + CommandManager.prefix + "chatmention add <word>\"");
    }

    public static String formatting = "";

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {


        if (nameTextColor.getMode("Dark Red").isToggled()) {
            formatting = "\u00A74";
        }
        if (nameTextColor.getMode("Red").isToggled()) {
            formatting = "\u00A7c";
        }
        if (nameTextColor.getMode("Gold").isToggled()) {
            formatting = "\u00A76";
        }
        if (nameTextColor.getMode("Yellow").isToggled()) {
            formatting = "\u00A7e";
        }
        if (nameTextColor.getMode("Dark Green").isToggled()) {
            formatting = "\u00A72";
        }
        if (nameTextColor.getMode("Green").isToggled()) {
            formatting = "\u00A7a";
        }
        if (nameTextColor.getMode("Aqua").isToggled()) {
            formatting = "\u00A7b";
        }
        if (nameTextColor.getMode("Dark Aqua").isToggled()) {
            formatting = "\u00A73";
        }
        if (nameTextColor.getMode("Dark Blue").isToggled()) {
            formatting = "\u00A71";
        }
        if (nameTextColor.getMode("Blue").isToggled()) {
            formatting = "\u00A79";
        }
        if (nameTextColor.getMode("Light Purple").isToggled()) {
            formatting = "\u00A7d";
        }
        if (nameTextColor.getMode("Dark Purple").isToggled()) {
            formatting = "\u00A75";
        }
        if (nameTextColor.getMode("Gray").isToggled()) {
            formatting = "\u00A77";
        }
        if (nameTextColor.getMode("Dark Gray").isToggled()) {
            formatting = "\u00A78";
        }
        if (nameTextColor.getMode("Black").isToggled()) {
            formatting = "\u00A70";
        }
    }
    public static ArrayList<String> getMentionList(){
        return mentionList;
    }

}
