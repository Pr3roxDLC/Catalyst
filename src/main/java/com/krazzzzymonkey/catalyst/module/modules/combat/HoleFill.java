package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.krazzzzymonkey.catalyst.module.modules.combat.Surround.getEyesPos;


//TODO MORE RENDER MODES
public class HoleFill extends Modules {

    private static BooleanValue noGhostBlock;
    private DoubleValue range;
    private DoubleValue yRange;
    private IntegerValue delay;
    private final BooleanValue rotate;

    private ArrayList<BlockPos> holes = new ArrayList();

    private final List<Block> whiteList = Collections.singletonList(Blocks.OBSIDIAN);

    public HoleFill() {
        super("HoleFill", ModuleCategory.COMBAT, "Fills holes preventing an enemy from jumping in when crystal pvping");
        this.range = new DoubleValue("Range", 5D, 0D, 10D, "The range to fill holes");
        this.yRange = new DoubleValue("YRange", 2D, 0D, 10D, "");
        this.delay = new IntegerValue("Delay", 1, 0, 20, "The delay between hole fills");
        this.rotate = new BooleanValue("Rotate", true, "Send rotation packets to the server");
        noGhostBlock = new BooleanValue("NoGhostBlock", false, "Makes it less likely to get ghost blocks");
        this.addValue(range, yRange, delay, rotate, noGhostBlock);
    }

    BlockPos pos;
    private int waitCounter;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        holes = new ArrayList<>();

        Iterable<BlockPos> blocks = BlockPos.getAllInBox(Minecraft.getMinecraft().player.getPosition().add(-range.getValue(), -yRange.getValue(), -range.getValue()), Minecraft.getMinecraft().player.getPosition().add(range.getValue(), yRange.getValue(), range.getValue()));
        for (BlockPos pos : blocks) {
            if (!Minecraft.getMinecraft().world.getBlockState(pos).getMaterial().blocksMovement() && !Minecraft.getMinecraft().world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) {
                boolean solidNeighbours = (
                        Minecraft.getMinecraft().world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | Minecraft.getMinecraft().world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN
                                && Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN
                                && Minecraft.getMinecraft().world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | Minecraft.getMinecraft().world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN
                                && Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN
                                && Minecraft.getMinecraft().world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR
                                && Minecraft.getMinecraft().world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR
                                && Minecraft.getMinecraft().world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR);
                if (solidNeighbours) {
                    this.holes.add(pos);
                }
            }
        }

        int newSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack =
                    Minecraft.getMinecraft().player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (!whiteList.contains(block)) {
                continue;
            }

            newSlot = i;
            break;
        }

        if (newSlot == -1)
            return;

        int oldSlot = Minecraft.getMinecraft().player.inventory.currentItem;
        if (delay.getValue() > 0) {
            if (waitCounter < delay.getValue()) {
                Minecraft.getMinecraft().player.inventory.currentItem = newSlot;
                holes.forEach(this::place);
                Minecraft.getMinecraft().player.inventory.currentItem = oldSlot;
                return;
            } else {
                waitCounter = 0;
            }
        }

    });

    private void place(BlockPos blockPos) {
        for (Entity entity : Minecraft.getMinecraft().world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos))) {
            if (entity instanceof EntityLivingBase) {
                return;
            }
        }
        placeBlockScaffold(blockPos, rotate.getValue());
        waitCounter++;
    }

    public static void placeBlockScaffold(BlockPos pos, boolean rotate) {
        Vec3d eyesPos = new Vec3d(mc.player.posX,
                mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ);

        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();


            if (!canBeClicked(neighbor))
                continue;

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
                    .add(new Vec3d(side2.getDirectionVec()).scale(0.5));


            if (rotate)
                faceVectorPacketInstant(hitVec);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            processRightClickBlock(neighbor, side2, hitVec);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.rightClickDelayTimer = 0;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

            return;
        }
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getNeededRotations2(vec);

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
        getPlayerController().processRightClickBlock(mc.player, mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
        if (noGhostBlock.getValue() && !mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
        }
    }
    private static PlayerControllerMP getPlayerController() {
        return mc.playerController;
    }
}
