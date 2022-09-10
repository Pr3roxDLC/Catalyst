package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//TODO MAKE TEXT COLORS CUSTOMIZABLE

public class VisualRange extends Modules {

    private BooleanValue clientSide;

    public VisualRange() {
        super("VisualRange", ModuleCategory.CHAT, "Tells you when someone enters your render distance");
        this.clientSide = new BooleanValue("ClientSide", true, "Only shows the message clientside");
        this.addValue(clientSide);
    }

    List<Entity> knownPlayers = new ArrayList<>();
    List<Entity> players;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(event -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Minecraft.getMinecraft().player == null) return;
        players = Minecraft.getMinecraft().world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList());
        try {
            for (Entity e : players) {
                if (e instanceof EntityPlayer && !e.getName().equalsIgnoreCase(Minecraft.getMinecraft().player.getName())) {
                    if (!knownPlayers.contains(e)) {
                        knownPlayers.add(e);
                        if (clientSide.getValue()) {
                            ChatUtils.message(e.getName() + " Entered Render Distance.");
                        } else {
                            Minecraft.getMinecraft().player.sendChatMessage(e.getName() + " Entered My Render Distance.");
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            for (Entity e : knownPlayers) {
                if (e instanceof EntityPlayer && !e.getName().equalsIgnoreCase(Minecraft.getMinecraft().player.getName())) {
                    if (!players.contains(e)) {
                        knownPlayers.remove(e);
                    }
                }
            }
        } catch (Exception e) {
        }
    });

    public void onDisable() {
        knownPlayers.clear();
        super.onDisable();
    }
}
