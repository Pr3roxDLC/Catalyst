package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoRespawn extends Modules {

    public AutoRespawn() {
        super("AutoRespawn", ModuleCategory.MISC, "Automatically respawns player");
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Minecraft.getMinecraft().player.isDead || Minecraft.getMinecraft().player.getHealth() < 0) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            Minecraft.getMinecraft().player.respawnPlayer();
        }
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGameOver) {
            Minecraft.getMinecraft().player.respawnPlayer();
        }
    });
}
