package com.krazzzzymonkey.catalyst.utils;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;

import java.awt.*;
import java.util.Arrays;

//this is used as the "proxy" for communication between the custom module lua scripts and the parts of the client, this class has to either remain unobfuscated or
//a list of mappings must be provided for this class and passed to the Mapper class
@SuppressWarnings("unused")
public class CustomModuleUtils {
    public static void toggleModule(String name){
        ModuleManager.getModule(name).toggle();
    }
    public static void setModuleToggled(String name, boolean enabled){
        ModuleManager.getModule(name).setToggled(enabled);
    }

    public static boolean isModuleToggled(String name){
        return ModuleManager.getModule(name).isToggled();
    }

    public static boolean isModuleModeToggled(String moduleName, String modeValueName, String modeName){
        return ModuleManager.getModule(moduleName).isToggledMode(modeValueName, modeName);
    }

    public static void toggleModuleMode(String moduleName, String modeValueName, String modeName){
        ModuleManager.getModule(moduleName).getValues().stream()
            .filter(value -> value instanceof ModeValue)
            .filter(value -> value.getName().equals(modeValueName)).findFirst()
            .ifPresent(value -> {
                Arrays.stream(((ModeValue) value).getModes()).forEach(mode -> mode.setToggled(false));
                ((ModeValue)value).getMode(modeName).setToggled(true);
            });
    }

    public static int getIntegerValueFromModule(String moduleName, String valueName){
       return ModuleManager.getModule(moduleName).getIntegerValue(valueName);
    }
    public static double getDoubleValueFromModule(String moduleName, String valueName){
        return ModuleManager.getModule(moduleName).getDoubleValue(valueName);
    }

    public static boolean getBooleanValueFromModule(String moduleName, String valueName){
        return ModuleManager.getModule(moduleName).getBooleanValue(valueName);
    }

    public static Color getColorValueFromModule(String moduleName, String valueName){
        return ModuleManager.getModule(moduleName).getColorValue(valueName);
    }



}
