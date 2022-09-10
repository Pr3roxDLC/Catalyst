package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.CatalystRPC;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


//TODO MAKE CUSTOMIZABLE
public class RPC extends Modules {

    public RPC() {
        super("CatalystRPC", ModuleCategory.MISC, "Discord Rich Presence");
    }

    boolean firstRun = false;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (!firstRun) {
            CatalystRPC.init();
            firstRun = true;
        }
    });

    public void onDisable() {
        firstRun = false;
        super.onDisable();
    }
}
