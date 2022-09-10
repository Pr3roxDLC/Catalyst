package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

//TODO ADD JUMP HEIGHT

public class EntityControl extends Modules {

    public EntityControl() {
        super("EntityControl", ModuleCategory.MOVEMENT, "Allows you to ride entities without saddles or taming them.");
    }

}
