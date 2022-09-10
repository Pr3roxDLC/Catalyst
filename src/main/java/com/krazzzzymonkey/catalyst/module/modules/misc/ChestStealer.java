package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;


//TODO ENDER CHEST SETTING
public class ChestStealer extends Modules{

	public IntegerValue delay;
	public SPacketWindowItems packet;
	public int ticks;

	public ChestStealer() {
		super("ChestStealer", ModuleCategory.MISC, "Automatically steals items from a chest");

		delay = new IntegerValue("Delay", 4, 0, 20, "The delay between taking each item stack");
		this.addValue(delay);
		this.ticks = 0;
	}

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.IN) {

            Packet packet = e.getPacket();
            if (packet instanceof SPacketWindowItems) {
                this.packet = (SPacketWindowItems) packet;
            }
        }
	});

	boolean isContainerEmpty(Container container) {
		boolean temp = true;
	    int i = 0;
	    for(int slotAmount = container.inventorySlots.size() == 90 ? 54 : 35; i < slotAmount; i++) {
	    	if (container.getSlot(i).getHasStack()) {
	    		temp = false;
	    	}
	    }
	    return temp;
	}

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
		if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

		if(e.getPhase() != Phase.START) {
			return;
		}
		EntityPlayerSP player = Wrapper.INSTANCE.player();
		if ((!Wrapper.INSTANCE.mc().inGameHasFocus)
        		&& (this.packet != null)
        		&& (player.openContainer.windowId == this.packet.getWindowId())
        		&& ((Wrapper.INSTANCE.mc().currentScreen instanceof GuiChest))) {
			if (!isContainerEmpty(player.openContainer)) {
				for (int i = 0; i < player.openContainer.inventorySlots.size() - 36; ++i) {
                    Slot slot = player.openContainer.getSlot(i);
                    if (slot.getHasStack() && slot.getStack() != null) {
                    	if (this.ticks >= this.delay.getValue().intValue()) {
        	            	Wrapper.INSTANCE.mc().playerController.windowClick(player.openContainer.windowId, i, 1, ClickType.QUICK_MOVE, player);
        	            	this.ticks = 0;
        	            }
                    }
                }
				this.ticks += 1;
			}
			else
			{
            	player.closeScreen();
            	this.packet = null;
            }
		}
	});
}
