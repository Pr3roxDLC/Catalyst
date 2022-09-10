package com.krazzzzymonkey.catalyst.module.modules.world;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.managers.XRayManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.utils.TimerUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.xray.XRayBlock;
import com.krazzzzymonkey.catalyst.xray.XRayData;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;

//TODO ADD ACTUAL XRAY
//TODO ADD IT BACK

public class XRay extends Modules{

    public DoubleValue distance;
	public IntegerValue delay;

	public TimerUtils timer;

	public XRay() {
		super("XRay", ModuleCategory.WORLD, "Allows you to see specific blocks");
		distance = new DoubleValue("Distance", 50D, 4D, 100D, "");
		delay = new IntegerValue("UpdateDelay", 100, 0, 300, "");
		timer = new TimerUtils();
		this.addValue(distance, delay);
	}
	LinkedList<XRayBlock> blocks = new LinkedList<XRayBlock>();

	@Override
	public void onEnable() {
		blocks.clear();
		super.onEnable();
	}

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
		if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

		int distance = this.distance.getValue().intValue();
		if(!timer.isDelay(delay.getValue().intValue() * 10)) {
			return;
		}
		blocks.clear();
		for(XRayData data : XRayManager.xrayList) {
			for (BlockPos blockPos : BlockUtils.findBlocksNearEntity(Wrapper.INSTANCE.player(), data.getId(), data.getMeta(), distance)) {
				XRayBlock xRayBlock = new XRayBlock(blockPos, data);
				blocks.add(xRayBlock);
			}
		}
		timer.setLastMS();

	});

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
		RenderUtils.drawXRayBlocks(blocks, e.getPartialTicks());
	});
}
