package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;

//TODO ADD PER SOUND SETTING
public class NoGlobalSounds extends Modules {

    public NoGlobalSounds() {
        super("NoGlobalSounds", ModuleCategory.MISC, "Disables lightning and wither spawn sounds");
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        Packet packet = e.getPacket();
        if (packet instanceof SPacketSoundEffect) {
            final SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect) packet;
            if (sPacketSoundEffect.getCategory() == SoundCategory.WEATHER && sPacketSoundEffect.getSound() == SoundEvents.ENTITY_LIGHTNING_THUNDER) {
                e.setCancelled(true);
            }
        }
        if (packet instanceof SPacketEffect) {
            final SPacketEffect sPacketEffect = (SPacketEffect) packet;

            if (sPacketEffect.getSoundType() == 1038 || sPacketEffect.getSoundType() == 1023 || sPacketEffect.getSoundType() == 1028) {
                e.setCancelled(true);
            }
        }
    });
}
