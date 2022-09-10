package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

//TODO MERGE WITH CHAMS, ADD SETTINGS
public class ItemChams extends Modules {

    public ItemChams() {
        super("ItemChams", ModuleCategory.RENDER, "See items through walls");
    }

}
