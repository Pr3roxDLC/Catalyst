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

public class LowOffHand extends Modules {

    private DoubleValue Height;

    public LowOffHand() {
        super("LowOffhand", ModuleCategory.RENDER, "Lowers your offhand");
        this.Height = new DoubleValue("Height", 0.5D, 0D, 1D, "The height of your offhand");
        this.addValue(Height);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        Double height = Height.getValue();
        Minecraft.getMinecraft().entityRenderer.itemRenderer.equippedProgressOffHand = height.floatValue();
    });

}
