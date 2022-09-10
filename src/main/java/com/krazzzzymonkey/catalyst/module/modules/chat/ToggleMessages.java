package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


//TODO STOP THE CHAT FROM BEING VISIBLE
public class ToggleMessages extends Modules {

    public ToggleMessages() {
        super("ToggleMessages", ModuleCategory.CHAT, "Sends a clientside message when you toggle a module");
    }

}
