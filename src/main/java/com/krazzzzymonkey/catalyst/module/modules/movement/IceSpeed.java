package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


//TODO MAKE THE SLIPPERINESS NUMBER SETTING MORE CONSISTENT
public class IceSpeed extends Modules {

    private DoubleValue slipperiness;
    public IceSpeed() {
        super("IceSpeed", ModuleCategory.MOVEMENT, "Set the speed you travel on ice");
        this.slipperiness = new DoubleValue("Slipperiness", 0.4D, 0D, 2D, "Sets the slipperiness of the ice block");
        this.addValue(slipperiness);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        Blocks.ICE.slipperiness = slipperiness.getValue().floatValue();
        Blocks.PACKED_ICE.slipperiness = slipperiness.getValue().floatValue();
        Blocks.FROSTED_ICE.slipperiness = slipperiness.getValue().floatValue();
    });

    public void onDisable() {
        super.onDisable();
        Blocks.ICE.slipperiness = 0.98f;
        Blocks.PACKED_ICE.slipperiness = 0.98f;
        Blocks.FROSTED_ICE.slipperiness = 0.98f;
    }
}
