package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


//TODO MERGE WITH LowOffHand
//TODO FIX: FREEZES YOUR RENDERED ITEM SAME FOR OFFHAND
public class LowMainHand extends Modules {

    private DoubleValue Height;
    public LowMainHand() {
        super("LowMainHand", ModuleCategory.RENDER, "Lowers your Main Hand");
        this.Height = new DoubleValue("Height", 0.5D, 0D, 1D, "The height of your main hand");
        this.addValue(Height);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        Double height = Height.getValue();
        Minecraft.getMinecraft().entityRenderer.itemRenderer.equippedProgressMainHand = height.floatValue();

    });
}


