package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.krazzzzymonkey.catalyst.utils.BlockUtils.getState;

//TODO AUTO CENTER, STEAL FROM AUTOOBI

public class SelfTrap extends Modules {

    private static BooleanValue noGhostBlock;
    private BooleanValue rotate;

    private IntegerValue blocksPerTick;

    static Minecraft mc = Minecraft.getMinecraft();

    public SelfTrap() {
        super("SelfTrap", ModuleCategory.COMBAT, "Automatically incases you in obsidian, safely allowing you to mend your armor");
        this.rotate = new BooleanValue("Rotate", true, "Send rotation packets to the server");
        this.blocksPerTick = new IntegerValue("BlocksPerTick", 8, 1, 15, "The amount of blocks placed in one tick");
        noGhostBlock = new BooleanValue("NoGhostBlock", false, "Makes it less likely to get ghost blocks");
        this.addValue(rotate, blocksPerTick, noGhostBlock);
    }


    int blocksPlaced;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(event -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityPlayer)
                .filter(e -> e == mc.player)
                .sorted(Comparator.comparing(e -> mc.player.getDistance(e)))
                .forEach(e -> {
                    Vec3d vec = getInterpolatedPos(e, mc.getRenderPartialTicks());
                    BlockPos playerPos = new BlockPos(vec);
                    BlockPos x = playerPos.add(1, 0, 0);
                    BlockPos xMinus = playerPos.add(-1, 0, 0);
                    BlockPos z = playerPos.add(0, 0, 1);
                    BlockPos zMinus = playerPos.add(0, 0, -1);
                    BlockPos up = playerPos.add(0, 2, 0);
                    BlockPos xUp = x.up();
                    BlockPos xMinusUp = xMinus.up();
                    BlockPos zUp = z.up();
                    BlockPos zMinusUp = zMinus.up();
                    BlockPos xUp2 = xUp.up();
                    BlockPos xMinusUp2 = xMinusUp.up();
                    BlockPos zUp2 = zUp.up();
                    BlockPos zMinusUp2 = zMinusUp.up();

                    // search blocks in hotbar
                    int newSlot = -1;
                    for (int i = 0; i < 9; i++) {
                        // filter out non-block items
                        ItemStack stack =
                                mc.player.inventory.getStackInSlot(i);

                        if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                            continue;
                        }
                        // only use whitelisted blocks
                        Block block = ((ItemBlock) stack.getItem()).getBlock();

                        if (!(block instanceof BlockObsidian)) {
                            continue;
                        }

                        newSlot = i;
                        break;
                    }

                    // check if any blocks were found
                    if (newSlot == -1)
                        return;

                    // set slot
                    int oldSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = newSlot;

                    blocksPlaced = 0;
                    if (blocksPlaced > blocksPerTick.getValue()) {
                        blocksPlaced = 0;
                        return;
                    }

                    // x
                    if (shouldPlace(x)) {
                        placeBlockScaffold(x, rotate.getValue());
                        blocksPlaced++;
                    }
                    // xMinus
                    if (shouldPlace(xMinus)) {
                        placeBlockScaffold(xMinus, rotate.getValue());
                        blocksPlaced++;
                    }
                    // z
                    if (shouldPlace(z)) {
                        placeBlockScaffold(z, rotate.getValue());
                        blocksPlaced++;
                    }
                    // zMinus
                    if (shouldPlace(zMinus)) {
                        placeBlockScaffold(zMinus, rotate.getValue());
                        blocksPlaced++;
                    }
                    // xUp
                    if (shouldPlace(xUp)) {
                        placeBlockScaffold(xUp, rotate.getValue());
                        blocksPlaced++;
                    }
                    // xMinusUp
                    if (shouldPlace(xMinusUp)) {
                        placeBlockScaffold(xMinusUp, rotate.getValue());
                        blocksPlaced++;
                    }
                    // zUp
                    if (shouldPlace(zUp)) {
                        placeBlockScaffold(zUp, rotate.getValue());
                        blocksPlaced++;
                    }
                    // zMinusUp
                    if (shouldPlace(zMinusUp)) {
                        placeBlockScaffold(zMinusUp, rotate.getValue());
                        blocksPlaced++;
                    }
                    // xUp2
                    if (shouldPlace(xUp2)) {
                        placeBlockScaffold(xUp2, rotate.getValue());
                        blocksPlaced++;
                    }
                    // xMinusUp2
                    if (shouldPlace(xMinusUp2)) {
                        placeBlockScaffold(xMinusUp2, rotate.getValue());
                        blocksPlaced++;
                    }
                    // zUp2
                    if (shouldPlace(zUp2)) {
                        placeBlockScaffold(zUp2, rotate.getValue());
                        blocksPlaced++;
                    }
                    // zMinusUp2
                    if (shouldPlace(zMinusUp2)) {
                        placeBlockScaffold(zMinusUp2, rotate.getValue());
                        blocksPlaced++;
                    }
                    // up
                    if (shouldPlace(up)) {
                        placeBlockScaffold(up, rotate.getValue());
                        blocksPlaced++;
                    }
                    mc.player.inventory.currentItem = oldSlot;
                });

    });

    private boolean shouldPlace(BlockPos pos) {
        List<Entity> entities = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream()
                .filter(e -> !(e instanceof EntityItem))
                .filter(e -> !(e instanceof EntityXPOrb))
                .collect(Collectors.toList());
        boolean a = entities.isEmpty();
        boolean b = mc.world.getBlockState(pos).getMaterial().isReplaceable();
        boolean c = blocksPlaced < blocksPerTick.getValue();
        return a && b && c;
    }

    public static boolean placeBlockScaffold(BlockPos pos, boolean rotate) {
        Vec3d eyesPos = new Vec3d(mc.player.posX,
                mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ);

        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            // check if side is visible (facing away from player)
            //if(eyesPos.squareDistanceTo(
            //        new Vec3d(pos).add(0.5, 0.5, 0.5)) >= eyesPos
            //        .squareDistanceTo(
            //                new Vec3d(neighbor).add(0.5, 0.5, 0.5)))
            //    continue;

            // check if neighbor can be right clicked
            if (!canBeClicked(neighbor))
                continue;

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
                    .add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            // check if hitVec is within range (4.25 blocks)
            //if(eyesPos.squareDistanceTo(hitVec) > 18.0625)
            //continue;

            // place block
            if (rotate)
                faceVectorPacketInstant(hitVec);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            processRightClickBlock(neighbor, side2, hitVec);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.rightClickDelayTimer = 0;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

            return true;
        }

        return false;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(mc.world.getBlockState(pos), false);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getNeededRotations2(vec);

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
                rotations[1], mc.player.onGround));
    }

    public static Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
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
                mc.player.rotationYaw
                        + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper
                        .wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX,
                mc.player.posY + mc.player.getEyeHeight(),
                mc.player.posZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z
        );
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
