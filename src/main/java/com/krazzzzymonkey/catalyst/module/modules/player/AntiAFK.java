package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AntiAFK extends Modules {

    public IntegerValue seconds;
    public BooleanValue shouldJump;
    public BooleanValue shouldRotate;
    public BooleanValue shouldMove;
    public BooleanValue autoReply;
    public DoubleValue delay;

    public AntiAFK() {
        super("AntiAFK", ModuleCategory.COMBAT, "Prevents you from getting kicked from a server for being AFK");
    }
}
