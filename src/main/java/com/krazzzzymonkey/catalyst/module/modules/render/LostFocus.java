package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

public class LostFocus extends Modules {

    private static final int maxFpsInactive = 1;
    private int maxFpsActive;

    public LostFocus() {
        super("LostFocus", ModuleCategory.RENDER, "Slow rendering while window focus is lost");
    }

    @Override
    public void onEnable() {
        maxFpsActive = mc.gameSettings.limitFramerate;
    }
    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (mc.world == null) return;
        if (e.getPhase() == TickEvent.Phase.START) return;
        if (!Display.isActive() && mc.gameSettings.limitFramerate != maxFpsInactive) {
            maxFpsActive = mc.gameSettings.limitFramerate;
            mc.gameSettings.limitFramerate = maxFpsInactive;
        } else if (mc.gameSettings.limitFramerate == maxFpsInactive) {
            mc.gameSettings.limitFramerate = maxFpsActive;
        }
    });

}
