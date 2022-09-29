package com.krazzzzymonkey.catalyst.managers;

import com.google.common.io.Files;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.custom.CustomModule;
import com.krazzzzymonkey.catalyst.utils.Mapper;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
        moduleDir.getParentFile().mkdirs();
        try {
            FileUtils.copyFile(FileManager.getAssetFile("lua/HotbarRandomizer.lua"),
                               new File(moduleDir, "HotbarRandomizer.lua"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Arrays.stream(moduleDir.listFiles()).filter(n -> n.getName().endsWith(".lua")).forEach(n -> {
            CustomModule customModule = new CustomModule(n.getName(), ModuleCategory.CUSTOM, "", n);
            ModuleManager.addModule(customModule);
        });
    }

}
