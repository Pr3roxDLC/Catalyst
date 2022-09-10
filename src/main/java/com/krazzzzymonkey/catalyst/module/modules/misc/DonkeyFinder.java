package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.google.common.collect.Lists;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.UUID;

public class DonkeyFinder extends Modules {

    public DonkeyFinder() {
        super("DonkeyFinder", ModuleCategory.MISC, "Tells you when a donkey is in render distance");
    }
    public static List<UUID> knownEntities = Lists.newArrayList();

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        for (Entity entity : Minecraft.getMinecraft().world.getLoadedEntityList()) {
            if (entity instanceof EntityDonkey) {
                if(knownEntities.contains(entity.getUniqueID())) return;
                knownEntities.add(entity.getUniqueID());
                ChatUtils.message("There is a donkey at: " + Math.round(entity.posX) + " " + Math.round(entity.posY) + " " + Math.round(entity.posZ));
            }
        }
        for (Entity entity : Minecraft.getMinecraft().world.getLoadedEntityList()) {
            if (entity instanceof EntityLlama) {
                if(knownEntities.contains(entity.getUniqueID())) return;
                knownEntities.add(entity.getUniqueID());
                ChatUtils.message("There is a llama at: " + Math.round(entity.posX) + " " + Math.round(entity.posY) + " " + Math.round(entity.posZ));
            }
        }
    });

    @Override
    public void onEnable() {
        knownEntities = Lists.newArrayList();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        knownEntities = Lists.newArrayList();
        super.onDisable();
    }
}

