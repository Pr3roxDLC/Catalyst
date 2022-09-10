package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


//TODO ADD POTION SETTING
public class FullBright extends Modules{

	public FullBright() {
		super("FullBright", ModuleCategory.RENDER, "Allows you to see in the dark");
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}
	@Override
	public void onDisable() {
		Wrapper.INSTANCE.mcSettings().gammaSetting = 1;
		super.onDisable();
	}

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
		if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

		Wrapper.INSTANCE.mcSettings().gammaSetting = 20;

	});
}
