package com.krazzzzymonkey.catalyst.module.modules.gui;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.HudEditorMainMenu;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.ProfileManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;

//TODO FIX STUFF BEING RENDERED OFFSCREEN

public class HudEditor extends Modules {

    public HudEditor() {
        super("HudEditor", ModuleCategory.GUI, "Allows you to set position of hud elements");
    }

    @Override
    public void onEnable() {
        FileManager.saveModules(ProfileManager.currentProfile);
        if (mc.player == null) {
            mc.displayGuiScreen(new HudEditorMainMenu(mc.currentScreen));
        } else {
            Wrapper.INSTANCE.mc().displayGuiScreen(Main.moduleManager.getHudGui());
        }

        super.onEnable();
        this.toggle();
    }

}
