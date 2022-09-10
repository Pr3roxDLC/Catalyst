package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.theme.dark.DarkFrame;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.FontManager;
import com.krazzzzymonkey.catalyst.module.modules.chat.CustomChat;
import com.krazzzzymonkey.catalyst.module.modules.hud.Graphs;
import com.krazzzzymonkey.catalyst.module.modules.hud.TargetHUD;
import com.krazzzzymonkey.catalyst.module.modules.render.Nametags;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.Notification;

import java.awt.*;

public class Font extends Command {
    public Font() {
        super("font");
    }

    @Override
    public void runCommand(String s, String[] args) {
        if (args[0] == null) {
            ChatUtils.error("Usage: " + getSyntax());
            return;
        }
        try {
            if (args[0].equalsIgnoreCase("set")) {
                if (args[1] == null) {
                    ChatUtils.error("Usage: " + getSyntax());
                    return;
                }
                String font = "";
                for (int i = 1; i < args.length; i++) {
                    font = font.concat(args[i] + " ");
                }
                font = font.substring(0, font.length() - 1);

                String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

                for (String f : fonts) {
                    if (f.equalsIgnoreCase(font)) {
                        FontManager.font = args[1];
                        FileManager.saveFont();
                        Main.fontRenderer = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.PLAIN, 20), true, true);
                        Main.smallFontRenderer = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.PLAIN, 15), true, true);
                        Graphs.graphFontRenderer = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.PLAIN, 12), true, true);
                        DarkFrame.fontRenderer = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.PLAIN, 20), true, false);
                        ComponentRenderer.fontRenderer = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.PLAIN, 16), true, true);
                        TargetHUD.fontRenderer = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.PLAIN, 15), true, true);
                        Nametags.fontRendererIn = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.BOLD, 20), true, true);
                        Nametags.fontRendererSmall = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.PLAIN, 20), true, false);
                        CustomChat.fontRenderer = new CFontRenderer(new java.awt.Font(FontManager.font, java.awt.Font.PLAIN, 18), true, true);
                        ChatUtils.normalMessage("Successfully loaded and applied font: " + ChatColor.AQUA + font + ChatColor.GRAY + ".");
                        new Notification("Font Manager", "Successfully loaded font: " + ChatColor.AQUA + font + ChatColor.GRAY + ".", 5000, Color.BLACK);
                        return;
                    }
                }
                ChatUtils.error("Could not find system font: " + ChatColor.AQUA + font + ChatColor.GRAY + ".");

            } else if (args[0].equalsIgnoreCase("reset")) {
                FontManager.font = "Arial";
                FileManager.saveFont();
                new Notification("Font Manager", "Successfully loaded font: " + ChatColor.AQUA + "Arial" + ChatColor.GRAY + ".", 5000, Color.BLACK);
                ChatUtils.normalMessage("Successfully loaded font: " + ChatColor.AQUA + "Arial" + ChatColor.GRAY + ".");
            } else if (args[0].equalsIgnoreCase("list")) {
                String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
                String font = "";
                for (String f : fonts) {
                   font = font.concat(f+ ", ");
                }
                ChatUtils.normalMessage(font);
            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Change the font of the client.";
    }

    @Override
    public String getSyntax() {
        return "font <set/reset/list> <font>";
    }

}
