package com.krazzzzymonkey.catalyst.utils;

import java.util.*;
import java.util.stream.Collectors;

import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.GameType;

public final class BlockUtils
{
	private static final Minecraft mc = Wrapper.INSTANCE.mc();



	public static HashSet<Block> blackList = new HashSet<>(Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE));
	public static HashSet<Block> shulkerList = new HashSet<>(Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX));
	public static List<Block> emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE);
	public static List<Block> rightclickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE);


	public static IBlockState getState(BlockPos pos)
	{
		return mc.world.getBlockState(pos);
	}

	public static Block getBlock(BlockPos pos)
	{
		return getState(pos).getBlock();
	}

	public static Material getMaterial(BlockPos pos)
	{
		return getState(pos).getMaterial();
	}

	public static boolean canBeClicked(BlockPos pos)
	{
		return getBlock(pos).canCollideCheck(getState(pos), false);
	}

	public static float getHardness(BlockPos pos) {
		return getState(pos).getPlayerRelativeBlockHardness(Wrapper.INSTANCE.player(), Wrapper.INSTANCE.world(), pos);
	}

    public static List<BlockPos> getSurroundingBlocks(EntityPlayer player, double blockRange, boolean motion) {
        List<BlockPos> nearbyBlocks = new ArrayList<>();
        int range = (int) MathUtils.round(blockRange, 0);

        if (motion)
            player.getPosition().add(new Vec3i(player.motionX, player.motionY, player.motionZ));

        for (int x = -range; x <= range; x++)
            for (int y = -range; y <= range; y++)
                for (int z = -range; z <= range; z++)
                    nearbyBlocks.add(player.getPosition().add(x, y, z));
        if(nearbyBlocks.isEmpty()){
            return nearbyBlocks;
        }

        return nearbyBlocks.stream().filter(blockPos -> mc.player.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5) <= blockRange).sorted(Comparator.comparing(blockPos -> mc.player.getDistanceSq(blockPos))).collect(Collectors.toList());
    }

	public static boolean placeBlockLegit(BlockPos pos)
	{
		Vec3d eyesPos = new Vec3d(mc.player.posX,
			mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);

		for(EnumFacing side : EnumFacing.values())
		{
			BlockPos neighbor = pos.offset(side);
			EnumFacing side2 = side.getOpposite();

			// check if side is visible (facing away from player)
			// TODO: actual line-of-sight check
			if(eyesPos.squareDistanceTo(
				new Vec3d(pos).add(0.5, 0.5, 0.5)) >= eyesPos
					.squareDistanceTo(
						new Vec3d(neighbor).add(0.5, 0.5, 0.5)))
				continue;

			// check if neighbor can be right clicked
			if(!getBlock(neighbor)
				.canCollideCheck(mc.world.getBlockState(neighbor), false))
				continue;

			Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
				.add(new Vec3d(side2.getDirectionVec()).scale(0.5));

			// check if hitVec is within range (4.25 blocks)
			if(eyesPos.squareDistanceTo(hitVec) > 18.0625)
				continue;

			// place block
			faceVectorPacket(hitVec);
			mc.playerController.processRightClickBlock(mc.player, mc.world,
				neighbor, side2, hitVec, EnumHand.MAIN_HAND);
			mc.player.swingArm(EnumHand.MAIN_HAND);
			mc.rightClickDelayTimer = 4;

		}
		Wrapper.INSTANCE.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
		mc.player.swingArm(EnumHand.MAIN_HAND);

		return true;
	}

	public static void swingArm(ModeValue setting) {
		if (setting.getMode("Mainhand").isToggled()) {
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
		if (setting.getMode("Offhand").isToggled()) {
			mc.player.swingArm(EnumHand.OFF_HAND);
		}
	}

	public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing) {
		RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
		EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
		mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
		if (swing) {
			mc.player.connection.sendPacket(new CPacketAnimation(hand));
		}
	}

	public static EnumFacing getFirstFacing(final BlockPos pos) {
		final Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
		if (iterator.hasNext()) {
			final EnumFacing facing = iterator.next();
			return facing;
		}
		return null;
	}

	public static boolean placeBlock(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean isSneaking) {
		boolean sneaking = false;
		final EnumFacing side = getFirstFacing(pos);
		if (side == null) {
			return isSneaking;
		}
		final BlockPos neighbour = pos.offset(side);
		final EnumFacing opposite = side.getOpposite();
		final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
		final Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
		if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			mc.player.setSneaking(true);
			sneaking = true;
		}
		if (rotate) {
			faceVector(hitVec, true);
		}
		rightClickBlock(neighbour, hitVec, hand, opposite, packet);
		mc.player.swingArm(EnumHand.MAIN_HAND);
		mc.rightClickDelayTimer = 4;
		return sneaking || isSneaking;
	}


	public static boolean isBlockEmpty(BlockPos pos) {
		try {
			if (emptyBlocks.contains(mc.world.getBlockState(pos).getBlock())) {
				AxisAlignedBB box = new AxisAlignedBB(pos);
				Iterator<Entity> entityIter = mc.world.loadedEntityList.iterator();

				Entity e;

				do {
					if (!entityIter.hasNext()) {
						return true;
					}

					e = entityIter.next();
				} while (!(e instanceof EntityLivingBase) || !box.intersects(e.getEntityBoundingBox()));

			}
		} catch (Exception ignored) { }
		return false;
	}

	public static void rotatePacket(double x, double y, double z) {
		double diffX = x - mc.player.posX;
		double diffY = y - (mc.player.posY + (double) mc.player.getEyeHeight());
		double diffZ = z - mc.player.posZ;
		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
		float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
	}

	public static boolean placeBlock(BlockPos pos, int slot, boolean rotate, boolean rotateBack, ModeValue setting) {
		if (isBlockEmpty(pos)) {
			int old_slot = -1;
			if (slot != mc.player.inventory.currentItem) {
				old_slot = mc.player.inventory.currentItem;
				mc.player.inventory.currentItem = slot;
			}

			EnumFacing[] facings = EnumFacing.values();

			for (EnumFacing f : facings) {
				Block neighborBlock = mc.world.getBlockState(pos.offset(f)).getBlock();
				Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) f.getXOffset() * 0.5D, pos.getY() + 0.5D + (double) f.getYOffset() * 0.5D, pos.getZ() + 0.5D + (double) f.getZOffset() * 0.5D);

				if (!emptyBlocks.contains(neighborBlock) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25D) {
					float[] rot = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};

					if (rotate) {
						rotatePacket(vec.x, vec.y, vec.z);
					}

					if (rightclickableBlocks.contains(neighborBlock)) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
					}

					mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
					if (rightclickableBlocks.contains(neighborBlock)) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
					}

					if (rotateBack) {
						mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
					}

					swingArm(setting);

					if (old_slot != -1) {
						mc.player.inventory.currentItem = old_slot;
					}

					return true;
				}
			}

			if (old_slot != -1) {
				mc.player.inventory.currentItem = old_slot;
			}

		}

		return false;
	}

	public static boolean placeBlock(BlockPos pos, int slot, boolean rotate, boolean rotateBack, ModeValue setting, boolean noGhostBlock) {
		if (isBlockEmpty(pos)) {
			int old_slot = -1;
			if (slot != mc.player.inventory.currentItem) {
				old_slot = mc.player.inventory.currentItem;
				mc.player.inventory.currentItem = slot;
			}

			EnumFacing[] facings = EnumFacing.values();

			for (EnumFacing f : facings) {
				Block neighborBlock = mc.world.getBlockState(pos.offset(f)).getBlock();
				Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) f.getXOffset() * 0.5D, pos.getY() + 0.5D + (double) f.getYOffset() * 0.5D, pos.getZ() + 0.5D + (double) f.getZOffset() * 0.5D);

				if (!emptyBlocks.contains(neighborBlock) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25D) {
					float[] rot = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};

					if (rotate) {
						rotatePacket(vec.x, vec.y, vec.z);
					}

					if (rightclickableBlocks.contains(neighborBlock)) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
					}

					mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
					if (rightclickableBlocks.contains(neighborBlock)) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
					}

					if (rotateBack) {
						mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
					}

					swingArm(setting);

					if(noGhostBlock && !mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)){
						mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, f));
					}
					if (old_slot != -1) {
						mc.player.inventory.currentItem = old_slot;
					}

					return true;
				}
			}

			if (old_slot != -1) {
				mc.player.inventory.currentItem = old_slot;
			}

		}

		return false;
	}

	public static void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction, final boolean packet) {
		if (packet) {
			final float f = (float)(vec.x - pos.getX());
			final float f2 = (float)(vec.y - pos.getY());
			final float f3 = (float)(vec.z - pos.getZ());
			mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
		}
		else {
			mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
		}
		mc.player.swingArm(EnumHand.MAIN_HAND);
		mc.rightClickDelayTimer = 4;
	}


	public static float[] getLegitRotations(final Vec3d vec) {
		final Vec3d eyesPos = getEyesPos();
		final double diffX = vec.x - eyesPos.x;
		final double diffY = vec.y - eyesPos.y;
		final double diffZ = vec.z - eyesPos.z;
		final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
		final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
		return new float[] { mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch) };
	}

	public static void faceVector(final Vec3d vec, final boolean normalizeAngle) {
		final float[] rotations = getLegitRotations(vec);
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? ((float)MathHelper.normalizeAngle((int)rotations[1], 360)) : rotations[1], mc.player.onGround));
	}

	public static List<EnumFacing> getPossibleSides(final BlockPos pos) {
		final ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
		for (final EnumFacing side : EnumFacing.values()) {
			final BlockPos neighbour = pos.offset(side);
			if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
				final IBlockState blockState;
				if (!(blockState = mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) {
					facings.add(side);
				}
			}
		}
		return facings;
	}


	public static boolean placeBlockSimple(BlockPos pos)
	{
		Vec3d eyesPos = new Vec3d(mc.player.posX,
			mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);

		for(EnumFacing side : EnumFacing.values())
		{
			BlockPos neighbor = pos.offset(side);
			EnumFacing side2 = side.getOpposite();

			// check if neighbor can be right clicked
			if(!getBlock(neighbor)
				.canCollideCheck(mc.world.getBlockState(neighbor), false))
				continue;

			Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
				.add(new Vec3d(side2.getDirectionVec()).scale(0.5));

			// check if hitVec is within range (6 blocks)
			if(eyesPos.squareDistanceTo(hitVec) > 36)
				continue;

			// place block
			mc.playerController.processRightClickBlock(mc.player, mc.world,
				neighbor, side2, hitVec, EnumHand.MAIN_HAND);

			return true;
		}

		return false;
	}
	public static boolean isEntitiesEmpty(BlockPos pos){
		List<Entity> entities =  mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream()
				.filter(e -> !(e instanceof EntityItem))
				.filter(e -> !(e instanceof EntityXPOrb))
				.collect(Collectors.toList());
		return entities.isEmpty();
	}
	public static boolean placeBlockScaffold(BlockPos pos, boolean rotate) {
		for(EnumFacing side : EnumFacing.values())
		{
			BlockPos neighbor = pos.offset(side);
			EnumFacing side2 = side.getOpposite();

			// check if side is visible (facing away from player)
			//if(eyesPos.squareDistanceTo(
			//        new Vec3d(pos).add(0.5, 0.5, 0.5)) >= eyesPos
			//        .squareDistanceTo(
			//                new Vec3d(neighbor).add(0.5, 0.5, 0.5)))
			//    continue;

			// check if neighbor can be right clicked
			if(!canBeClicked(neighbor))
				continue;

			Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
					.add(new Vec3d(side2.getDirectionVec()).scale(0.5));

			// check if hitVec is within range (4.25 blocks)
			//if(eyesPos.squareDistanceTo(hitVec) > 18.0625)
			//continue;

			// place block
			if(rotate)
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

	public static EnumFacing getPlaceableSide(BlockPos pos) {
		final Minecraft mc = Minecraft.getMinecraft();
		for (EnumFacing side : EnumFacing.values()) {
			BlockPos neighbour = pos.offset(side);
			if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
				return side;
			}
		}
		return null;
	}
	private static PlayerControllerMP getPlayerController() {
		return mc.playerController;
	}


	public static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
		getPlayerController().processRightClickBlock(mc.player,
				mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
	}


	public static void faceVectorPacketInstant(Vec3d vec) {
		float[] rotations = getNeededRotations2(vec);

		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
				rotations[1], mc.player.onGround));
	}


	private static float[] getNeededRotations2(Vec3d vec) {
		Vec3d eyesPos = getEyesPos();

		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

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

	// TODO: RotationUtils class for all the faceSomething() methods

	public static void faceVectorPacket(Vec3d vec)
	{
		double diffX = vec.x - mc.player.posX;
		double diffY = vec.y - (mc.player.posY + mc.player.getEyeHeight());
		double diffZ = vec.z - mc.player.posZ;

		double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, dist));

		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(
			mc.player.rotationYaw
				+ MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
			mc.player.rotationPitch
				+ MathHelper.wrapDegrees(pitch - mc.player.rotationPitch),
			mc.player.onGround));
	}

	public static void faceBlockClient(BlockPos blockPos)
	{
		double diffX = blockPos.getX() + 0.5 - mc.player.posX;
		double diffY =      //0.5
			blockPos.getY() + 0.0 - (mc.player.posY + mc.player.getEyeHeight());
		double diffZ = blockPos.getZ() + 0.5 - mc.player.posZ;
		double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		mc.player.rotationYaw = mc.player.rotationYaw
			+ MathHelper.wrapDegrees(yaw - mc.player.rotationYaw);
		mc.player.rotationPitch = mc.player.rotationPitch
			+ MathHelper.wrapDegrees(pitch - mc.player.rotationPitch);
	}

	public static void faceBlockPacket(BlockPos blockPos)
	{
		double diffX = blockPos.getX() + 0.5 - mc.player.posX;
		double diffY =      //0.5
			blockPos.getY() + 0.0 - (mc.player.posY + mc.player.getEyeHeight());
		double diffZ = blockPos.getZ() + 0.5 - mc.player.posZ;
		double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(
			mc.player.rotationYaw
				+ MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
			mc.player.rotationPitch
				+ MathHelper.wrapDegrees(pitch - mc.player.rotationPitch),
			mc.player.onGround));
	}

	public static void faceBlockClientHorizontally(BlockPos blockPos)
	{
		double diffX = blockPos.getX() + 0.5 - mc.player.posX;
		double diffZ = blockPos.getZ() + 0.5 - mc.player.posZ;
		float yaw =
			(float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		mc.player.rotationYaw = mc.player.rotationYaw
			+ MathHelper.wrapDegrees(yaw - mc.player.rotationYaw);
	}

	public static float getPlayerBlockDistance(BlockPos blockPos)
	{
		return getPlayerBlockDistance(blockPos.getX(), blockPos.getY(),
			blockPos.getZ());
	}

	public static float getPlayerBlockDistance(double posX, double posY,
		double posZ)
	{
		float xDiff = (float)(mc.player.posX - posX);
		float yDiff = (float)(mc.player.posY - posY);
		float zDiff = (float)(mc.player.posZ - posZ);
		return getBlockDistance(xDiff, yDiff, zDiff);
	}

	public static float getBlockDistance(float xDiff, float yDiff, float zDiff)
	{
		return MathHelper.sqrt(
			(xDiff - 0.5F) * (xDiff - 0.5F) + (yDiff - 0.5F) * (yDiff - 0.5F)
				+ (zDiff - 0.5F) * (zDiff - 0.5F));
	}

	public static float getHorizontalPlayerBlockDistance(BlockPos blockPos)
	{
		float xDiff = (float)(mc.player.posX - blockPos.getX());
		float zDiff = (float)(mc.player.posZ - blockPos.getZ());
		return MathHelper.sqrt(
			(xDiff - 0.5F) * (xDiff - 0.5F) + (zDiff - 0.5F) * (zDiff - 0.5F));
	}

	public static boolean breakBlockSimple(BlockPos pos)
	{
		EnumFacing side = null;
		EnumFacing[] sides = EnumFacing.values();

		Vec3d eyesPos = Utils.getEyesPos();
		Vec3d relCenter = getState(pos).getBoundingBox(Wrapper.INSTANCE.world(), pos).getCenter();
		Vec3d center = new Vec3d(pos).add(relCenter);

		Vec3d[] hitVecs = new Vec3d[sides.length];
		for(int i = 0; i < sides.length; i++)
		{
			Vec3i dirVec = sides[i].getDirectionVec();
			Vec3d relHitVec = new Vec3d(relCenter.x * dirVec.getX(),
					relCenter.y * dirVec.getY(),
					relCenter.z * dirVec.getZ());
			hitVecs[i] = center.add(relHitVec);
		}

		for(int i = 0; i < sides.length; i++)
		{
			if(Wrapper.INSTANCE.world().rayTraceBlocks(eyesPos, hitVecs[i], false,
				true, false) != null)
				continue;

			side = sides[i];
			break;
		}

		if(side == null)
		{
			double distanceSqToCenter = eyesPos.squareDistanceTo(center);
			for(int i = 0; i < sides.length; i++)
			{
				if(eyesPos.squareDistanceTo(hitVecs[i]) >= distanceSqToCenter)
					continue;

				side = sides[i];
				break;
			}
		}

		if(side == null)
			side = sides[0];

		Utils.faceVectorPacket(hitVecs[side.ordinal()]);

		if(!mc.playerController.onPlayerDamageBlock(pos, side))
			return false;
		Wrapper.INSTANCE.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

		return true;
	}

	public static void breakBlocksPacketSpam(Iterable<BlockPos> blocks)
	{
		Vec3d eyesPos = Utils.getEyesPos();
		NetHandlerPlayClient connection = Wrapper.INSTANCE.player().connection;

		for(BlockPos pos : blocks)
		{
			Vec3d posVec = new Vec3d(pos).add(0.5, 0.5, 0.5);
			double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);

			for(EnumFacing side : EnumFacing.values())
			{
				Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));

				if(eyesPos.squareDistanceTo(hitVec) >= distanceSqPosVec)
					continue;

				connection.sendPacket(new CPacketPlayerDigging(
					Action.START_DESTROY_BLOCK, pos, side));
				connection.sendPacket(new CPacketPlayerDigging(
					Action.STOP_DESTROY_BLOCK, pos, side));

				break;
			}
		}
	}

	public static LinkedList<BlockPos> findBlocksNearEntity(EntityLivingBase entity, int blockId, int blockMeta, int distance) {
		LinkedList<BlockPos> blocks = new LinkedList<BlockPos>();

		for (int x = (int) Wrapper.INSTANCE.player().posX - distance; x <= (int) Wrapper.INSTANCE.player().posX + distance; ++x) {
            for (int z = (int) Wrapper.INSTANCE.player().posZ - distance; z <= (int) Wrapper.INSTANCE.player().posZ + distance; ++z) {

                int height = Wrapper.INSTANCE.world().getHeight(x, z);
                block: for (int y = 0; y <= height; ++y) {

                	BlockPos blockPos = new BlockPos(x, y, z);
                	IBlockState blockState = Wrapper.INSTANCE.world().getBlockState(blockPos);

                	if(blockId == -1 || blockMeta == -1) {
                		blocks.add(blockPos);
            			continue block;
                	}

                		int id = Block.getIdFromBlock(blockState.getBlock());
                		int meta =  blockState.getBlock().getMetaFromState(blockState);

                		if(id == blockId && meta == blockMeta) {

                			blocks.add(blockPos);
                			continue block;
                		}

                	}
                }
            }
		return blocks;
	}

}
