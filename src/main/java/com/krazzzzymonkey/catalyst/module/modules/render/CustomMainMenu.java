package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

//TODO USER CUSTOM BACKGROUND
public class CustomMainMenu extends Modules {

    public static ModeValue mode = new ModeValue("Mode", new Mode("Image", false), new Mode("Shader", true));
    public static BooleanValue logo = new BooleanValue("CustomLogo", true, "Replaces the Minecraft Logo with a custom Logo");
    public static ModeValue shader;

    public static final File SHADER_DIR = new File(String.format("%s%s%s%s%s", Minecraft.getMinecraft().gameDir, File.separator, Main.NAME, File.separator, "Shader"));

    public CustomMainMenu() {
        super("CustomMainMenu", ModuleCategory.RENDER, "Toggles Catalyst Main Menu");

        if(!SHADER_DIR.exists()){
            SHADER_DIR.mkdirs();
        }
        File defaultShader = new File(SHADER_DIR, "default.fsh");
        if(!defaultShader.exists()){
            //Grab default shader from the userdir in case it doesnt exist (as a fallback)
            File fragShaderFile = FileManager.getAssetFile("shader" + File.separator + "fragment.fsh");
            try {
                FileUtils.copyFile(fragShaderFile, defaultShader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Yesss dynamic mode values generated from whatever is in a folder, i love these, like unironically
        shader = new ModeValue("Shader", Arrays.stream(SHADER_DIR.listFiles()).filter(file -> file.getName().endsWith(".fsh")).map(file -> new Mode(file.getName(), false)).toArray(Mode[]::new));
        addValue(logo, mode, shader);
        if(Arrays.stream(shader.getModes()).noneMatch(Mode::isToggled)){
            shader.getModes()[0].setToggled(true);
        }

    }


}
