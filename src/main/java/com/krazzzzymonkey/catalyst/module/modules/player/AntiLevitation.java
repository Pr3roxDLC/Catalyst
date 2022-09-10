package com.krazzzzymonkey.catalyst.module.modules.player;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.Notification;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class AntiLevitation extends Modules {
    public AntiLevitation() {
        super("AntiLevitation", ModuleCategory.PLAYER, "Prevents you from levitating");
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if(mc.player == null)return;
        if(mc.player.isPotionActive(Potion.getPotionById(25))){
            mc.player.removeActivePotionEffect(Potion.getPotionFromResourceLocation("levitation"));

        }
    });

}
