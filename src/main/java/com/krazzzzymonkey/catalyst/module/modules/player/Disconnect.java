package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//TODO FIX GUI MESSING UP IF PLAYER GETS DISCONNECTED WHILE GUI IS OPEN (MIGHT BE SP ONLY)

public class Disconnect extends Modules {

    public DoubleValue leaveHealth;
    public BooleanValue onPlayerVisible;

    public Disconnect() {
        super("AutoDisconnect", ModuleCategory.COMBAT, "Automatically disconnects from server on specific health");

        //TODO ADD ALLOW FRIENDS
        onPlayerVisible = new BooleanValue("OnPlayerVisible", false, "Automatically disconnects when a player enters your render distance");
        leaveHealth = new DoubleValue("LeaveHealth", 4.0D, 0D, 19D, "The amount of health needed to disconnect");
        this.addValue(leaveHealth);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Wrapper.INSTANCE.world().loadedEntityList.stream().filter(n -> n.getName() != mc.player.getName()).anyMatch(n -> n instanceof EntityPlayer)
			|| Wrapper.INSTANCE.player().getHealth() <= leaveHealth.getValue().floatValue()) {

            boolean flag = Wrapper.INSTANCE.mc().isIntegratedServerRunning();
            Wrapper.INSTANCE.world().sendQuittingDisconnectingPacket();
            Wrapper.INSTANCE.mc().loadWorld(null);

            if (flag) {
                Wrapper.INSTANCE.mc().displayGuiScreen(new GuiMainMenu());
            } else {
                Wrapper.INSTANCE.mc().displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            }

            this.setToggled(false);
        }

    });
}
