package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.DamageBlockEvent;
import com.krazzzzymonkey.catalyst.events.ReachEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Reach extends Modules {

    public final DoubleValue distance = new DoubleValue("distance", 8, 1, 64, "Arm length");

    public Reach() {
        super("Reach", ModuleCategory.PLAYER, "Make arm very long");
        this.addValue(distance);
    }


    @EventHandler
    private final EventListener<ReachEvent> onReachEvent = new EventListener<>(e -> {
        if (this.isToggled()) e.distance = distance.getValue().floatValue();
    });

}
