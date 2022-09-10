package com.krazzzzymonkey.catalyst.module;


import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.Value;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.awt.*;
import java.util.ArrayList;

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;


//TODO ADD SETTING TOOLTIP
public class Modules extends Thread {

    private final BooleanValue drawn = new BooleanValue("Drawn", true, "Renders the module in active modules");
    private final boolean ignoreDrawn;

    public static Minecraft mc = Minecraft.getMinecraft();
    private String name;
    private ModuleCategory category;
    private String description;
    private String extraInfo = "";
    private boolean toggled;
    private int key;
    private boolean hold;
    private final ArrayList<Value> values = new ArrayList<>();

    public Modules(String name, ModuleCategory category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.toggled = false;
        this.key = -1;
        this.ignoreDrawn = false;
        this.hold = false;
        this.addValue(drawn);
    }

    public Modules(String name, ModuleCategory category, String description, boolean ignoreDrawn) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.toggled = false;
        this.key = -1;
        this.hold = false;
        this.ignoreDrawn = ignoreDrawn;
    }


    public void addValue(Value... values) {
        for (Value value : values) {
            this.getValues().add(value);
        }
    }

    public void setExtraInfo(String info) {
        this.extraInfo = info;
    }

    public String getExtraInfo() {
        return this.extraInfo;
    }

    public ArrayList<Value> getValues() {
        return values;
    }


    public Mode getToggledMode(String mode){
        for (Value value : this.values) {
            if (value instanceof ModeValue) {
                ModeValue modeValue = (ModeValue) value;
                if (modeValue.getName().equalsIgnoreCase(mode)) {
                    for (Mode m : modeValue.getModes()) {
                        if(m.isToggled()){
                            return m;
                        }
                    }
                }
            } else if (value instanceof SubMenu) {
                SubMenu subMenu = (SubMenu) value;
                for (Value value1 : subMenu.getValues()) {
                    if (value1 instanceof ModeValue) {
                        ModeValue modeValue = (ModeValue) value1;
                        if (modeValue.getName().equalsIgnoreCase(mode)) {
                            for (Mode m : modeValue.getModes()) {
                                if(m.isToggled()){
                                    return m;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isToggledMode(String mode, String modeName) {
        for (Value value : this.values) {
            if (value instanceof ModeValue) {
                ModeValue modeValue = (ModeValue) value;
                if (modeValue.getName().equalsIgnoreCase(mode)) {
                    for (Mode m : modeValue.getModes()) {
                        if (m.getName().equalsIgnoreCase(modeName)) {
                            if (m.isToggled()) {
                                return true;
                            }
                        }
                    }
                }
            } else if (value instanceof SubMenu) {
                SubMenu subMenu = (SubMenu) value;
                for (Value value1 : subMenu.getValues()) {
                    if (value1 instanceof ModeValue) {
                        ModeValue modeValue = (ModeValue) value1;
                        if (modeValue.getName().equalsIgnoreCase(mode)) {
                            for (Mode m : modeValue.getModes()) {
                                if (m.getName().equalsIgnoreCase(modeName)) {
                                    if (m.isToggled()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    public boolean isToggledValue(String valueName) {
        for (Value value : this.values) {
            if (value instanceof BooleanValue) {
                BooleanValue booleanValue = (BooleanValue) value;
                if (booleanValue.getName().equalsIgnoreCase(valueName)) {
                    if (booleanValue.getValue()) {
                        return true;
                    }
                }
            } else if (value instanceof SubMenu) {
                SubMenu subMenu = (SubMenu) value;

                //Main.logger.info(subMenu.getSubMenuName());
                for (Value value1 : subMenu.getValues()) {
                    if (subMenu.getSubMenuName().equals("NoArmor")) {
                 //       Main.logger.info("\t" + value1.getName() + " = " + value1.getClass().getSimpleName() + " (" + value1.getValue().toString() + ")");
                    }

                    if (value1 instanceof BooleanValue) {
                        BooleanValue booleanValue = (BooleanValue) value1;
                        if (booleanValue.getName().equalsIgnoreCase(valueName)) {
                            if (booleanValue.getValue()) {
                                return true;
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    public int getIntegerValue(String valueName) {
        for (Value value : this.values) {
            if (value instanceof IntegerValue) {
                IntegerValue integerValue = (IntegerValue) value;
                if (integerValue.getName().equalsIgnoreCase(valueName)) {
                    return integerValue.getValue();
                }
            } else if (value instanceof SubMenu) {
                SubMenu subMenu = (SubMenu) value;
                for (Value value1 : subMenu.getValues()) {
                    if (value1 instanceof IntegerValue) {
                        IntegerValue integerValue = (IntegerValue) value1;
                        if (integerValue.getName().equalsIgnoreCase(valueName)) {
                            return integerValue.getValue();
                        }
                    }
                }
            }
        }
        Main.logger.error(valueName + " is not an integerValue!");
        return -1;
    }

    public double getDoubleValue(String valueName) {
        for (Value value : this.values) {
            if (value instanceof DoubleValue) {
                DoubleValue doubleValue = (DoubleValue) value;
                if (doubleValue.getName().equalsIgnoreCase(valueName)) {
                    return doubleValue.getValue();
                }
            } else if (value instanceof SubMenu) {
                SubMenu subMenu = (SubMenu) value;
                for (Value value1 : subMenu.getValues()) {
                    if (value1 instanceof DoubleValue) {
                        DoubleValue doubleValue = (DoubleValue) value1;
                        if (doubleValue.getName().equalsIgnoreCase(valueName)) {
                            return doubleValue.getValue();
                        }
                    }
                }
            }
        }
        Main.logger.error(valueName + " is not an doubleValue!");
        return -1;
    }

    public boolean getBooleanValue(String valueName) {
        for (Value value : this.values) {
            if (value instanceof BooleanValue) {
                BooleanValue booleanValue = (BooleanValue) value;
                if (booleanValue.getName().equalsIgnoreCase(valueName)) {
                    return booleanValue.getValue();
                }
            } else if (value instanceof SubMenu) {
                SubMenu subMenu = (SubMenu) value;
                for (Value value1 : subMenu.getValues()) {
                    if (value1 instanceof BooleanValue) {
                        BooleanValue booleanValue = (BooleanValue) value1;
                        if (booleanValue.getName().equalsIgnoreCase(valueName)) {
                            return booleanValue.getValue();
                        }
                    }
                }
            }
        }
        Main.logger.error(valueName + " is not an booleanValue!");
        return false;
    }

    public Color getColorValue(String valueName) {
        for (Value value : this.values) {
            if (value instanceof ColorValue) {
                ColorValue colorValue = (ColorValue) value;
                if (colorValue.getName().equalsIgnoreCase(valueName)) {
                    return colorValue.getColor();
                }
            } else if (value instanceof SubMenu) {
                SubMenu subMenu = (SubMenu) value;
                for (Value value1 : subMenu.getValues()) {
                    if (value1 instanceof ColorValue) {
                        ColorValue colorValue = (ColorValue) value1;
                        if (colorValue.getName().equalsIgnoreCase(valueName)) {
                            return colorValue.getColor();
                        }
                    }
                }
            }
        }
        Main.logger.error(valueName + " is not a colorValue!");
        return new Color(-1);
    }


    public void setValues(ArrayList<Value> values) {
        for (Value value : values) {
            for (Value value1 : this.values) {
                if (value.getName().equalsIgnoreCase(value1.getName())) {
                    value1.setValue(value.getValue());
                }
            }
        }
    }


    public void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            this.onEnable();
            if (ModuleManager.getModule("ToggleMessages").isToggled() && mc.player != null)
                ChatUtils.message(ChatColor.GRAY + this.getModuleName() + ChatColor.DARK_GREEN + " ON");
        } else {
            this.onDisable();
            if (ModuleManager.getModule("ToggleMessages").isToggled() && mc.player != null)
                ChatUtils.message(ChatColor.GRAY + this.getModuleName() + ChatColor.DARK_RED + " OFF");
        }

        RenderUtils.splashTickPos = 0;
        if (!RenderUtils.isSplash && !(Wrapper.INSTANCE.mc().currentScreen instanceof ClickGuiScreen)) {
            RenderUtils.isSplash = true;
        }
    }


    public void onEnable() {
        EVENT_MANAGER.register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }


    public void onDisable() {
        EVENT_MANAGER.unregister(this);
        MinecraftForge.EVENT_BUS.unregister(this);
    }


    public String getModuleName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setModuleName(String name) {
        this.name = name;
    }


    public ModuleCategory getCategory() {
        return category;
    }


    public void setCategory(ModuleCategory category) {
        this.category = category;
    }


    public int getKey() {
        return key;
    }


    public void setKey(int key) {
        this.key = key;
    }

    public boolean isBindHold() {
        return hold;
    }

    public void setBindHold(boolean hold) {
        this.hold = hold;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        if(this.toggled == toggled)return;
        this.toggled = toggled;
        if (toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public void eventToggle(boolean toggled) {
        this.toggled = toggled;
        if (toggled) {
            try{
                onEnable();
            }catch (Exception ignored){
                System.out.println("Unable to enabled Module " + name);
                try{
                    onDisable();
                }catch (Exception ignored2){
                    this.toggled = false;
                    EVENT_MANAGER.unregister(this);
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        } else {
            EVENT_MANAGER.unregister(this);
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    public boolean isDrawn() {
        if (ignoreDrawn) return false;
        return drawn.getValue();
    }

    public void setDrawn(boolean drawn) {
        if (ignoreDrawn) return;
        this.drawn.setValue(drawn);
    }

}
