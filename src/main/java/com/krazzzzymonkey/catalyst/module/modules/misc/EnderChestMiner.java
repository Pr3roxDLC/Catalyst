package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//TODO FIX
public class EnderChestMiner extends Modules {

    public final ArrayDeque<Set<BlockPos>> prevBlocks = new ArrayDeque<Set<BlockPos>>();
    public BlockPos currentBlock;

    public float progress;
    public float prevProgress;
    public int id;

    public EnderChestMiner() {
        super("EnderChestMiner", ModuleCategory.MISC, "Automatically places and mines ender chests for you");
    }

    @Override
    public void onDisable() {
        if (currentBlock != null) {
            PlayerControllerUtils.setIsHittingBlock(true);
            Wrapper.INSTANCE.mc().playerController.resetBlockRemoving();
            currentBlock = null;
        }
        prevBlocks.clear();
        id = 0;
        super.onDisable();
    }
    private final List<Block> whiteList = Collections.singletonList(Blocks.ENDER_CHEST);

    @EventHandler
    private final dev.tigr.simpleevents.listener.EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        BlockPos l_ClosestPos = getSphere(GetLocalPlayerPosFloored(), 4, 4, false, true, 0).stream()
                .filter(p_Pos -> IsValidBlockPos(p_Pos))
               .min(Comparator.comparing(p_Pos -> GetDistanceOfEntityToBlock(Minecraft.getMinecraft().player, p_Pos)))
               .orElse(null);

        boolean hasPickaxe = Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE;
        if (!hasPickaxe) {
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() == Items.DIAMOND_PICKAXE) {
                    hasPickaxe = true;
                    Minecraft.getMinecraft().player.inventory.currentItem = i;
                    Minecraft.getMinecraft().playerController.updateController();
                    break;
                }
            }
        }

        currentBlock = null;

        Vec3d eyesPos = Utils.getEyesPos().subtract(0.5, 0.5, 0.5);
        BlockPos eyesBlock = new BlockPos(Utils.getEyesPos());

        double rangeSq = Math.pow(6, 2);
        int blockRange = (int) Math.ceil(6);

        Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(
                eyesBlock.add(blockRange, blockRange, blockRange),
                eyesBlock.add(-blockRange, -blockRange, -blockRange)).spliterator(), true);

        stream = stream.filter(pos -> eyesPos.squareDistanceTo(new Vec3d(pos)) <= rangeSq)
                .filter(pos -> BlockUtils.canBeClicked(pos))
                .sorted(Comparator.comparingDouble(pos -> eyesPos.squareDistanceTo(new Vec3d(pos))));


        stream = stream.filter(pos -> Block.getIdFromBlock(Wrapper.INSTANCE.world().getBlockState(pos).getBlock()) == Block.getIdFromBlock(Block.getBlockById(130)));
        List<BlockPos> blocks = stream.collect(Collectors.toList());

        if (Wrapper.INSTANCE.player().capabilities.isCreativeMode) {
            Stream<BlockPos> stream2 = blocks.parallelStream();

            for (Set<BlockPos> set : prevBlocks) {
                stream2 = stream2.filter(pos -> !set.contains(pos));
            }

            List<BlockPos> blocks2 = stream2.collect(Collectors.toList());
            prevBlocks.addLast(new HashSet<>(blocks2));

            while (prevBlocks.size() > 5) {
                prevBlocks.removeFirst();
            }

            if (!blocks2.isEmpty()) {
                currentBlock = blocks2.get(0);
            }

            Wrapper.INSTANCE.mc().playerController.resetBlockRemoving();
            progress = 1;
            prevProgress = 1;
            BlockUtils.breakBlocksPacketSpam(blocks2);
            return;
        }

        for (BlockPos pos : blocks)
            if (BlockUtils.breakBlockSimple(pos)) {
                currentBlock = pos;
                break;
            }

        if (currentBlock == null) {
            Wrapper.INSTANCE.mc().playerController.resetBlockRemoving();
        }

        if (currentBlock != null && BlockUtils.getHardness(currentBlock) < 1) {
            prevProgress = progress;
        }

        progress = PlayerControllerUtils.getCurBlockDamageMP();

        if (progress < prevProgress) {
            prevProgress = progress;
        } else {
            progress = 1;
            prevProgress = 1;
        }
        int EchestSlot = -1;
        for(int i = 0; i < 9; i++)
        {
            ItemStack stack =
                    Minecraft.getMinecraft().player.inventory.getStackInSlot(i);

            if(stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (!whiteList.contains(block)) {
                continue;
            }

            EchestSlot = i;
            break;
        }
        if(EchestSlot == -1)
            return;
        Minecraft.getMinecraft().player.inventory.currentItem = EchestSlot;
        if(currentBlock ==null){
            if(l_ClosestPos != null) BlockUtils.placeBlockScaffold(l_ClosestPos, true);
        }

    });


    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (currentBlock == null) {
            return;
        }
        RenderUtils.drawBlockESP(currentBlock, 1.0f, 0.0f, 0.0f, 1);


    });


    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    private boolean IsValidBlockPos(final BlockPos p_Pos) {
        IBlockState l_State = Minecraft.getMinecraft().world.getBlockState(p_Pos);
        return true;
    }
    public static double GetDistance(double p_X, double p_Y, double p_Z, double x, double y, double z)
    {
        double d0 = p_X - x;
        double d1 = p_Y - y;
        double d2 = p_Z - z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static double GetDistanceOfEntityToBlock(Entity p_Entity, BlockPos p_Pos)
    {
        return GetDistance(p_Entity.posX, p_Entity.posY, p_Entity.posZ, p_Pos.getX(), p_Pos.getY(), p_Pos.getZ());
    }
    public static BlockPos GetLocalPlayerPosFloored()
    {
        return new BlockPos(Math.floor(Minecraft.getMinecraft().player.posX), Math.floor(Minecraft.getMinecraft().player.posY), Math.floor(Minecraft.getMinecraft().player.posZ));
    }
}
