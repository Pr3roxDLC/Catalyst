package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//TODO ADD MORE BYPASSES
public class Flight extends Modules{

	public ModeValue mode;

	int ticks = 0;
	public Flight() {
		super("Flight", ModuleCategory.MOVEMENT, "Allows you to fly");
		this.mode = new ModeValue("Mode", new Mode("Simple", true), new Mode("Dynamic", false), new Mode("Hypixel", false));
	}

	@Override
	public void onEnable() {
		ticks = 0;
		super.onEnable();
	}

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
		if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

		EntityPlayerSP player = Wrapper.INSTANCE.player();
		if(mode.getMode("Hypixel").isToggled()) {
			player.motionY = 0.0;
			player.setSprinting(true);
			player.onGround = true;
		    ticks++;
		    if(ticks == 2 || ticks == 4 || ticks == 6 || ticks == 8 || ticks == 10 || ticks == 12 || ticks == 14 || ticks == 16 || ticks == 18 || ticks == 20) {
		    player.setPosition(player.posX, player.posY + 0.00000000128, player.posZ);
		    } if(ticks == 20) {
		    	ticks = 0;
		    }
		}
		else if(mode.getMode("Simple").isToggled())
		{
			player.capabilities.isFlying = true;
		}
		else if(mode.getMode("Dynamic").isToggled())
		{
			float flyspeed = 1.0f;
			player.jumpMovementFactor = 0.4f;
			player.motionX = 0.0;
			player.motionY = 0.0;
			player.motionZ = 0.0;
			player.jumpMovementFactor *= flyspeed * 3f;
	        if (Wrapper.INSTANCE.mcSettings().keyBindJump.isKeyDown()) {
	        	player.motionY += flyspeed;
	        }
	        if (Wrapper.INSTANCE.mcSettings().keyBindSneak.isKeyDown()) {
	        	player.motionY -= flyspeed;
	        }
		}

	});

	@Override
	public void onDisable() {
		if(mode.getMode("Simple").isToggled()) {
            if(mc.player == null)return;
			Wrapper.INSTANCE.player().capabilities.isFlying = false;
		}
		super.onDisable();
	}
}
