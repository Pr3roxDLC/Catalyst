package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemExpBottle;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class FastXP extends Modules {

    public FastXP() {
        super("FastXP", ModuleCategory.COMBAT, "Allows you to throw XP faster");
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)return;

        if (Minecraft.getMinecraft().player.inventory.getCurrentItem().getItem() instanceof ItemExpBottle) {
            Minecraft.getMinecraft().rightClickDelayTimer = 0;
        }

    });

    @Override
    public void onDisable(){
        super.onDisable();
        mc.rightClickDelayTimer = 4;
    }
}
