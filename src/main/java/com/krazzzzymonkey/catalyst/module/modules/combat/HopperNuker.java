package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.utils.PlayerControllerUtils;
import com.krazzzzymonkey.catalyst.utils.Utils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.krazzzzymonkey.catalyst.module.modules.player.AutoTool.findTool;

public class HopperNuker extends Modules{


    public final ArrayDeque<Set<BlockPos>> prevBlocks = new ArrayDeque<Set<BlockPos>>();
    public BlockPos currentBlock;

    public float progress;
    public float prevProgress;
    public int id;
    int slot = -1;
    int currentSlot = -1;

    public HopperNuker() {
        super("HopperNuker", ModuleCategory.COMBAT, "Automatically breaks hoppers within players reach");
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
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        currentBlock = null;

        Vec3d eyesPos = Utils.getEyesPos().subtract(0.5, 0.5, 0.5);
        BlockPos eyesBlock = new BlockPos(Utils.getEyesPos());

        double rangeSq = Math.pow(6, 2);
        int blockRange = (int)Math.ceil(6);

        Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(
                eyesBlock.add(blockRange, blockRange, blockRange),
                eyesBlock.add(-blockRange, -blockRange, -blockRange)).spliterator(), true);

        stream = stream.filter(pos -> eyesPos.squareDistanceTo(new Vec3d(pos)) <= rangeSq)
                .filter(pos -> BlockUtils.canBeClicked(pos))
                .sorted(Comparator.comparingDouble(pos -> eyesPos.squareDistanceTo(new Vec3d(pos))));


        stream = stream.filter(pos -> Block.getIdFromBlock(Wrapper.INSTANCE.world().getBlockState(pos).getBlock()) == Block.getIdFromBlock(Block.getBlockById(154)));
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
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            Packet packet = e.getPacket();
            if (packet instanceof CPacketPlayerDigging) {
                CPacketPlayerDigging pck = (CPacketPlayerDigging) packet;
                if ((Minecraft.getMinecraft().world.getBlockState(pck.getPosition()).getBlock() instanceof BlockHopper)) {
                    if (Minecraft.getMinecraft().playerController.getCurrentGameType() != GameType.CREATIVE && pck.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                        if (currentSlot == -1) {
                            currentSlot = Minecraft.getMinecraft().player.inventory.currentItem;
                        }
                        int bestIndex = findTool(Minecraft.getMinecraft().world.getBlockState(pck.getPosition()).getBlock());
                        if (bestIndex != -1) {
                            if (slot == -1) {
                                slot = Minecraft.getMinecraft().player.inventory.currentItem;
                            }
                            Minecraft.getMinecraft().player.inventory.currentItem = bestIndex;
                        }

                    }
                }
            }
        }
    });

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if(currentBlock == null) {
            return;
        }
        if(currentSlot != -1 && currentBlock == null){
            Minecraft.getMinecraft().player.inventory.currentItem = currentSlot;
            currentSlot = -1;
        }
        RenderUtils.drawBlockESP(currentBlock, 1.0f, 0.0f, 0.0f, 1);


    });


}
