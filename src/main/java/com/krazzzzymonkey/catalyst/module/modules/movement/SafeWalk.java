package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;

//TODO ALLOW WALKING DOWN STAIRS, MIN HEIGHT FOR IT TO STOP YOU
public class SafeWalk extends Modules {

    public SafeWalk() {
        super("SafeWalk", ModuleCategory.MOVEMENT, "Stops you from walking off edges");
    }

}
