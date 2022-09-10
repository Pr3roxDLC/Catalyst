package com.krazzzzymonkey.catalyst.managers;

import com.google.common.io.Files;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.custom.CustomModule;
import com.krazzzzymonkey.catalyst.utils.Mapper;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.krazzzzymonkey.catalyst.managers.FileManager.CATALYST_DIR;

public class LuaManager {

    public static Mapper MAPPER;

    public static boolean isInDevEnv = false;

    public LuaManager() {
        isInDevEnv = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        MAPPER = new Mapper();
        registerScriptedModules();
    }

    public void registerScriptedModules() {
        File moduleDir = new File(CATALYST_DIR + "/Modules/");
        if (!moduleDir.exists()) {
            moduleDir.mkdirs();
            File hotbarRandomizerScriptInAssets = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator+ "assets" + File.separator + "lua" + File.separator + "HotbarRandomizer.lua");
            File hotbarRandomizerScriptInCatalystDir = new File(moduleDir, "HotbarRandomizer.lua");
            try {
                Files.copy(hotbarRandomizerScriptInAssets, hotbarRandomizerScriptInCatalystDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        Arrays.stream(moduleDir.listFiles()).filter(n -> n.getName().endsWith(".lua")).forEach(n -> {
            CustomModule customModule = new CustomModule(n.getName(), ModuleCategory.CUSTOM, "", n);
            ModuleManager.addModule(customModule);
        });
    }

}
