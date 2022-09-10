package com.krazzzzymonkey.catalyst.managers;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class TimerManager {

    private static final HashMap<Integer, Multiplier> multiplierHashMap = new HashMap<>();
    private static int id = 0;

    public static TimerManager INSTANCE;

    public TimerManager() {
        ModuleManager.EVENT_MANAGER.register(this);
        reset();
        //Add a default mutliplier of 1 with the lowest priority to the Map, so we get a multiplier of 1 if no module has any active multipliers
        addTimerMultiplier(1d, 0);

    }

    private final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate;

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if (e.getPacket() instanceof SPacketTimeUpdate) {
            INSTANCE.onTimeUpdate();
        }
    });
    //            Minecraft.getMinecraft().timer.tickLength = Math.min(500, 50F * (20F / getTickRate()));

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
            Minecraft.getMinecraft().timer.tickLength = 50.0f;
            return;
        }
        multiplierHashMap.values().stream().filter(Multiplier::isEnabled).max(Comparator.comparingInt(Multiplier::getPrio)).ifPresent(n -> {
            Minecraft.getMinecraft().timer.tickLength = (float)(50/n.getMultiplier());
        });

    });


    public static int addTimerMultiplier(double multiplier, int priority){
        id++;
        multiplierHashMap.put(id, new Multiplier(priority, multiplier));
        return id;
    }

    public static int addTimerMultiplier(double multiplier, int priority, boolean enabled) {
         int entryID = addTimerMultiplier(multiplier, priority);
         getMultiplier(entryID).setEnabled(enabled);
         return entryID;
    }
    public static void removeTimerMultiplier(int id){
        multiplierHashMap.remove(id);
    }

    public static Multiplier getMultiplier(int id){
        return multiplierHashMap.get(id);
    }

    public void reset() {
        this.nextIndex = 0;
        this.timeLastTimeUpdate = -1L;
        Arrays.fill(this.tickRates, 20F);
    }

    public float getTickRate() {
        float numTicks = 0.0F;
        float sumTickRates = 0.0F;
        for (float tickRate : this.tickRates) {
            if (tickRate > 0.0F) {
                sumTickRates += tickRate;
                numTicks += 1.0F;
            }
        }
        return MathHelper.clamp(sumTickRates / numTicks, 0.01F, 20.0F);
    }

    public void onTimeUpdate() {
        if (this.timeLastTimeUpdate != -1L) {
            float timeElapsed = (float) (System.currentTimeMillis() - this.timeLastTimeUpdate) / 1000.0F;
            this.tickRates[(this.nextIndex % this.tickRates.length)] = MathHelper.clamp(20.0F / timeElapsed, 0.01f, 20.0F);
            this.nextIndex += 1;
        }
        this.timeLastTimeUpdate = System.currentTimeMillis();
    }

    public static class Multiplier{
        private int prio = -1;
        private double multiplier = 1d;
        private boolean enabled = true;

        public Multiplier(int prio, double multiplier){
            this.multiplier = multiplier;
            this.prio = prio;
        }

        public int getPrio() {
            return prio;
        }

        public void setPrio(int prio) {
            this.prio = prio;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

}
