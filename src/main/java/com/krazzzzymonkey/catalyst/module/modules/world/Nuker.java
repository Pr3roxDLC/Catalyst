package com.krazzzzymonkey.catalyst.module.modules.world;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.LeftClickBlockEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.utils.PlayerControllerUtils;
import com.krazzzzymonkey.catalyst.utils.Utils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

//TODO MAKE IN BREAK LIQUIDS IN GMC
public class Nuker extends Modules{

	public ModeValue mode;
    public DoubleValue distance;

    public final ArrayDeque<Set<BlockPos>> prevBlocks = new ArrayDeque<Set<BlockPos>>();
    public BlockPos currentBlock;
    public float progress;
    public float prevProgress;
    public int id;

	public Nuker() {
		super("Nuker", ModuleCategory.WORLD, "Automatically breaks specified blocks in players reach");

		this.mode = new ModeValue("Mode", new Mode("ID", true), new Mode("All", false));
		distance = new DoubleValue("Distance", 6.0D, 0.1D, 6.0D, "Breaking distance of nuker");

		this.addValue(mode, distance);
	}

	@Override
	public void onDisable() {
		if(currentBlock != null) {
			PlayerControllerUtils.setIsHittingBlock(true);
			Wrapper.INSTANCE.mc().playerController.resetBlockRemoving();
			currentBlock = null;
		}
		prevBlocks.clear();
		id = 0;
		super.onDisable();
	}

    @EventHandler
    private final dev.tigr.simpleevents.listener.EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
		if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

		currentBlock = null;

		Vec3d eyesPos = Utils.getEyesPos().subtract(0.5, 0.5, 0.5);
		BlockPos eyesBlock = new BlockPos(Utils.getEyesPos());

		double rangeSq = Math.pow(distance.getValue().doubleValue(), 2);
		int blockRange = (int)Math.ceil(distance.getValue().doubleValue());

		Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(
						eyesBlock.add(blockRange, blockRange, blockRange),
						eyesBlock.add(-blockRange, -blockRange, -blockRange)).spliterator(), true);

		stream = stream.filter(pos -> eyesPos.squareDistanceTo(new Vec3d(pos)) <= rangeSq)
				.filter(pos -> BlockUtils.canBeClicked(pos))
				.sorted(Comparator.comparingDouble(pos -> eyesPos.squareDistanceTo(new Vec3d(pos))));

		if(mode.getMode("ID").isToggled()) {
			stream = stream.filter(pos -> Block.getIdFromBlock(Wrapper.INSTANCE.world().getBlockState(pos).getBlock()) == id);
		}
		else if(mode.getMode("All").isToggled()) {
			//stream = stream.filter(pos -> BlockUtils.getHardness(pos) >= 1);
		}

		List<BlockPos> blocks = stream.collect(Collectors.toList());

		if(Wrapper.INSTANCE.player().capabilities.isCreativeMode){
			Stream<BlockPos> stream2 = blocks.parallelStream();

			for(Set<BlockPos> set : prevBlocks) {
				stream2 = stream2.filter(pos -> !set.contains(pos));
			}

			List<BlockPos> blocks2 = stream2.collect(Collectors.toList());
			prevBlocks.addLast(new HashSet<>(blocks2));

			while(prevBlocks.size() > 5) {
				prevBlocks.removeFirst();
			}

			if(!blocks2.isEmpty()) {
				currentBlock = blocks2.get(0);
			}

			Wrapper.INSTANCE.mc().playerController.resetBlockRemoving();
			progress = 1;
			prevProgress = 1;
			BlockUtils.breakBlocksPacketSpam(blocks2);
			return;
		}

		for(BlockPos pos : blocks)
			if(BlockUtils.breakBlockSimple(pos)){
				currentBlock = pos;
				break;
			}

		if(currentBlock == null) {
			Wrapper.INSTANCE.mc().playerController.resetBlockRemoving();
		}

		if(currentBlock != null && BlockUtils.getHardness(currentBlock) < 1) {
			prevProgress = progress;
		}

		progress = PlayerControllerUtils.getCurBlockDamageMP();

		if(progress < prevProgress) {
			prevProgress = progress;
		} else {
			progress = 1;
			prevProgress = 1;
		}

	});


    @EventHandler
    private final EventListener<LeftClickBlockEvent> onLeftClickBlock = new EventListener<>(e -> {
		if(mode.getMode("ID").isToggled() && Wrapper.INSTANCE.world().isRemote) {
			IBlockState blockState = Wrapper.INSTANCE.world().getBlockState(e.getPos());
			id = Block.getIdFromBlock(blockState.getBlock());
		}

	});

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
		if(currentBlock == null) {
			return;
		}
		if(mode.getMode("All").isToggled()) {
			RenderUtils.drawBlockESP(currentBlock, 1.0f, 0.0f, 0.0f,1);
		}
		else if(mode.getMode("ID").isToggled()) {
			RenderUtils.drawBlockESP(currentBlock, 0.0f, 0.0f, 1.0f,1);
		}

	});


}
