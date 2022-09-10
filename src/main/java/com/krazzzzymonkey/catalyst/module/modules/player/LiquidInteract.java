package com.krazzzzymonkey.catalyst.module.modules.player;


import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LiquidInteract extends Modules {

    public LiquidInteract(){
        super("LiquidInteract", ModuleCategory.PLAYER, "Allows you to place blocks on liquids");
    }

}
