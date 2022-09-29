package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.ProfileManager;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

import java.io.File;
import java.util.Objects;

public class Profile extends Command {
    public Profile() {
        super("profile");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("save")) {
                String profileName = args[1];
                FileManager.saveModules(profileName);
                ProfileManager.currentProfile = args[1];
                FileManager.saveCurrentProfile();
                ChatUtils.normalMessage("Successfully saved: " + ChatColor.AQUA + profileName + ChatColor.GRAY + ".");

            } else if (args[0].equalsIgnoreCase("load")) {
                String profileName = args[1];
                try {
                    FileManager.loadModules(profileName);
                    FileManager.saveCurrentProfile();
                } catch (Exception ignored) {
                }

                if (!ProfileManager.currentProfile.equalsIgnoreCase(profileName)) {
                    ChatUtils.error("Could find profile named: " + ChatColor.AQUA + profileName + ChatColor.RED + ". Loaded default config.");
                } else {
                    ChatUtils.normalMessage("Successfully loaded: " + ChatColor.AQUA + profileName + ChatColor.GRAY + ".");
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                ChatUtils.normalMessage("Profile List:");
                File[] files = FileManager.PROFILES_DIR.toFile().listFiles();
                Objects.requireNonNull(files);
                for (File file : files) {
                    if (file.isFile()) {
                        ChatUtils.normalMessage(file.getName().replace(".json", ""));
                    }
                }
            }
        } catch (Exception e) {

            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Allows you to switch between or create profiles";
    }

    @Override
    public String getSyntax() {
        return "profile <save/load/list> <Name>";
    }
}
