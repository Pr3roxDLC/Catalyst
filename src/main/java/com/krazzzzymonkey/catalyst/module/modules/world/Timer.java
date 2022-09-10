package com.krazzzzymonkey.catalyst.module.modules.world;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.managers.TimerManager;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;

//TODO FIX TPS SYNC, ADD MULTIPLIER RELATIVE TO CURRENT TPS
public class Timer extends Modules {

    public static DoubleValue multiplier;
    public BooleanValue tpsSync;
    public static Timer INSTANCE;
    private static int entryID = -1;

    public Timer() {
        super("Timer", ModuleCategory.WORLD, "Speeds up game ticks");

        tpsSync = new BooleanValue("TpsSync", false, "Synchronizes client sided tps with server tps");
        multiplier = new DoubleValue("Multiplier", 2D, 0.1D, 10D, "Multiplies speed of client sided game");
        INSTANCE = this;
        this.addValue(multiplier, tpsSync);
    }

    @EventHandler
    public EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (mc.world == null || mc.player == null) return;

        if (tpsSync.getValue()) {
            setExtraInfo(Math.round(50.0f / mc.timer.tickLength * 100.0f) / 100.0f + "");
            TimerManager.getMultiplier(entryID).setMultiplier(20 / TimerManager.INSTANCE.getTickRate());
        } else {
            setExtraInfo(multiplier.getValue() + "");
            TimerManager.getMultiplier(entryID).setMultiplier(multiplier.getValue());
        }
    });

    @Override
    public void onEnable(){
        entryID = TimerManager.addTimerMultiplier(multiplier.getValue(), 1);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        TimerManager.removeTimerMultiplier(entryID);
        super.onDisable();
    }



}
