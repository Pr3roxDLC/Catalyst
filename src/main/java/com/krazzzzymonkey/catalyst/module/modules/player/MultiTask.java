package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MultiTask extends Modules {

    public MultiTask() {
        super("MultiTask", ModuleCategory.PLAYER, "Allows you to use items while mining");
    }

}
