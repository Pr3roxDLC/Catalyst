package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

//TODO BARITONE SUPPORT
public class AutoWalk extends Modules {

    public AutoWalk() {
        super("AutoWalk", ModuleCategory.MOVEMENT, "Automatically walks for you");
    }

}
