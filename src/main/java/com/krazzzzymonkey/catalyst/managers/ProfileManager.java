package com.krazzzzymonkey.catalyst.managers;

public class ProfileManager {
    public static String currentProfile = "default";

    public ProfileManager(){
        FileManager.loadCurrentProfile();
    }
}
