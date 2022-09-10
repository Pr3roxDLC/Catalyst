package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


//TODO MIN FALL SETTING
public class FastFall extends Modules {

    private DoubleValue Speed;
    private IntegerValue MinFallDistance;

    public FastFall() {
        super("FastFall", ModuleCategory.MOVEMENT, "Makes player fall with more speed");
        this.Speed = new DoubleValue("Speed", 1D, 1D, 5D, "The speed at which you fall");
        MinFallDistance = new IntegerValue("MinFallDistance", 0, 0, 255, "The minimum amount of fall distance to activate");
        this.addValue(Speed, MinFallDistance);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Wrapper.INSTANCE.player().fallDistance > MinFallDistance.getValue())
            Wrapper.INSTANCE.player().motionY = -Speed.getValue();

    });

}
