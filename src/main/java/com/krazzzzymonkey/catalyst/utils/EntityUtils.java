package com.krazzzzymonkey.catalyst.utils;

import com.krazzzzymonkey.catalyst.managers.EnemyManager;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntityUtils {
    public static boolean isPassive(final Entity e) {
        return (!(e instanceof EntityWolf) || !((EntityWolf) e).isAngry()) && (e instanceof EntityAnimal || e instanceof EntityAgeable || e instanceof EntityTameable || e instanceof EntityAmbientCreature || e instanceof EntitySquid || (e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null));
    }


    public static boolean isFakeLocalPlayer(final Entity entity) {
        return entity != null && entity.getEntityId() == -100 && Minecraft.getMinecraft().player != entity;
    }


    public static Vec3d getInterpolatedAmount(final Entity entity, final Vec3d vec) {
        return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
    }

    public static Vec3d getInterpolatedAmount(final Entity entity, final double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static boolean isMobAggressive(final Entity entity) {
        if (entity instanceof EntityPigZombie) {
            if (((EntityPigZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry()) {
                return true;
            }
        } else {
            if (entity instanceof EntityWolf) {
                return ((EntityWolf) entity).isAngry() && !Minecraft.getMinecraft().player.equals(((EntityWolf) entity).getOwner());
            }
            if (entity instanceof EntityEnderman) {
                return ((EntityEnderman) entity).isScreaming();
            }
        }
        return isHostileMob(entity);
    }

    public static boolean isNeutralMob(final Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }

    public static boolean isFriendlyMob(final Entity entity) {
        return (isCreatureType(entity, EnumCreatureType.CREATURE, false) && !isNeutralMob(entity)) || isCreatureType(entity, EnumCreatureType.AMBIENT, false) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || (isNeutralMob(entity) && !isMobAggressive(entity));
    }

    public static boolean isHostileMob(final Entity entity) {
        return isCreatureType(entity, EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
    }

    private static boolean isCreatureType(Entity entity, EnumCreatureType type, boolean forSpawnCount) {
        return (!forSpawnCount || !(entity instanceof EntityLiving) || !((EntityLiving) entity).isNoDespawnRequired()) && type.getCreatureClass().isAssignableFrom(Entity.class);
    }

    public static boolean isDrivenByPlayer(final Entity entityIn) {
        return Minecraft.getMinecraft().player != null && entityIn != null && entityIn.equals(Minecraft.getMinecraft().player.getRidingEntity());
    }

    public static boolean isInWater(final Entity entity) {
        if (entity == null) {
            return false;
        }
        final double y = entity.posY + 0.01;
        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, (int) y, z);
                if (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDead(Entity entity) {
        return getHealth(entity) <= 0 || entity.isDead;
    }

    public static boolean getArmor(Entity target, double durability) {
        if (!(target instanceof EntityPlayer))
            return false;

        for (ItemStack stack : target.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR)
                return true;

            if (durability >= ((float) (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage()) * 100.0f)
                return true;
        }

        return false;
    }


    public static float getArmor(Entity target) {
        if (!(target instanceof EntityPlayer))
            return 0;

        float armorDurability = 0;
        for (ItemStack stack : target.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR)
                continue;

            armorDurability += ((float) (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage()) * 100.0f;
        }

        return armorDurability;
    }

    public static boolean isPlayer(final Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static double getRelativeX(final float yaw) {
        return MathHelper.sin(-yaw * 0.017453292f);
    }

    public static boolean isAboveWater(final Entity entity, final boolean packet) {
        if (entity == null) {
            return false;
        }
        final double y = entity.posY - (packet ? 0.03 : (isPlayer(entity) ? 0.2 : 0.5));
        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static double[] calculateLookAt(final double px, final double py, final double pz, final EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0 / 3.141592653589793;
        yaw = yaw * 180.0 / 3.141592653589793;
        yaw += 90.0;
        return new double[]{yaw, pitch};
    }

    public static double getRelativeZ(final float yaw) {
        return MathHelper.cos(yaw * 0.017453292f);
    }

    public static boolean canEntityFeetBeSeen(final Entity entityIn) {
        return Minecraft.getMinecraft().world.rayTraceBlocks(new Vec3d(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posX + Minecraft.getMinecraft().player.getEyeHeight(), Minecraft.getMinecraft().player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static boolean isAboveBlock(final Entity entity, final BlockPos blockPos) {
        return entity.posY >= blockPos.getY();
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = Minecraft.getMinecraft().world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow))
                continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static boolean isAboveLiquid(final Entity entity) {
        if (entity == null) {
            return false;
        }
        final double n = entity.posY + 0.01;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (Minecraft.getMinecraft().world.getBlockState(new BlockPos(i, (int) n, j)).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkForLiquid(final Entity entity, final boolean b) {
        if (entity == null) {
            return false;
        }
        final double posY = entity.posY;
        double n;
        if (b) {
            n = 0.03;
        } else if (entity instanceof EntityPlayer) {
            n = 0.2;
        } else {
            n = 0.5;
        }
        final double n2 = posY - n;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (Minecraft.getMinecraft().world.getBlockState(new BlockPos(i, MathHelper.floor(n2), j)).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkCollide() {
        return !Minecraft.getMinecraft().player.isSneaking() && (Minecraft.getMinecraft().player.getRidingEntity() == null || Minecraft.getMinecraft().player.getRidingEntity().fallDistance < 3.0f) && Minecraft.getMinecraft().player.fallDistance < 3.0f;
    }

    public static boolean isOnLiquid(final double offset) {
        if (Minecraft.getMinecraft().player.fallDistance >= 3.0f) {
            return false;
        }
        final AxisAlignedBB bb = (Minecraft.getMinecraft().player.getRidingEntity() != null) ? Minecraft.getMinecraft().player.getRidingEntity().getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0, -offset, 0.0) : Minecraft.getMinecraft().player.getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0, -offset, 0.0);
        boolean onLiquid = false;
        final int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0); ++x) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0); ++z) {
                final Block block = Minecraft.getMinecraft().world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(MathUtils.roundVec(entity.getPositionVector(), 0));
    }

    public static boolean stopSneaking(boolean isSneaking) {
        if (isSneaking && Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return false;
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(Minecraft.getMinecraft().player.posX), Math.floor(Minecraft.getMinecraft().player.posY), Math.floor(Minecraft.getMinecraft().player.posZ));
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
        List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static void attackEntity(final Entity entity, final boolean packet, final boolean swingArm, boolean strict) {
        if (packet) {
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().player, entity);
        }
        if (swingArm) {
            Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public static void attackEntity(final Entity entity, final boolean packet, boolean strict) {

        if (!strict) {
            if (packet) {
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketUseEntity(entity));
            } else {
                Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().player, entity);
            }
        } else {
            Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().player, entity);
            Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
        }
    }


    public static BlockPos getFlooredPos(Entity e) {
        return new BlockPos(Math.floor(e.posX), Math.floor(e.posY), Math.floor(e.posZ));
    }

    public static boolean isInHole(Entity entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        for (BlockPos pos : new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = Minecraft.getMinecraft().world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.OBSIDIAN) continue;
            return false;
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        for (BlockPos pos : new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = Minecraft.getMinecraft().world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.BEDROCK) continue;
            return false;
        }
        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        for (BlockPos pos : new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = Minecraft.getMinecraft().world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && (touchingState.getBlock() == Blocks.BEDROCK || touchingState.getBlock() == Blocks.OBSIDIAN))
                continue;
            return false;
        }
        return true;
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return getInterpolatedPos(entity, partialTicks).subtract(Minecraft.getMinecraft().getRenderManager().renderPosX, Minecraft.getMinecraft().getRenderManager().renderPosY, Minecraft.getMinecraft().getRenderManager().renderPosZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, partialTicks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static boolean isLiving(final Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static BlockPos getPlayerPosWithEntity() {
        return new BlockPos((Minecraft.getMinecraft().player.getRidingEntity() != null) ? Minecraft.getMinecraft().player.getRidingEntity().posX : Minecraft.getMinecraft().player.posX, (Minecraft.getMinecraft().player.getRidingEntity() != null) ? Minecraft.getMinecraft().player.getRidingEntity().posY : Minecraft.getMinecraft().player.posY, (Minecraft.getMinecraft().player.getRidingEntity() != null) ? Minecraft.getMinecraft().player.getRidingEntity().posZ : Minecraft.getMinecraft().player.posZ);
    }

    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static java.util.List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plusY) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plusY, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }

    public static boolean holding32k(final EntityPlayer player) {
        return is32k(player.getHeldItemMainhand());
    }

    public static boolean is32k(final ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getTagCompound() == null) {
            return false;
        }
        final NBTTagList enchants = (NBTTagList) stack.getTagCompound().getTag("ench");
        if (enchants == null) {
            return false;
        }
        int i = 0;
        while (i < enchants.tagCount()) {
            final NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                final int lvl = enchant.getInteger("lvl");
                if (lvl >= 42) {
                    return true;
                }
                break;
            } else {
                ++i;
            }
        }
        return false;
    }

    public static float getHealth(final Entity entity) {
        if (entity.isEntityAlive()) {
            final EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static boolean isntValid(final Entity entity, final double range) {
        return entity == null || !entity.isEntityAlive() || entity.equals(Minecraft.getMinecraft().player) || (entity instanceof EntityPlayer && FriendManager.friendsList.contains(entity.getName())) || Minecraft.getMinecraft().player.getDistanceSq(entity) > (range * range);
    }

    public static Color getColor(final Entity entity, final int red, final int green, final int blue, final int alpha, final boolean colorFriends) {
        Color color = new Color(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
        if (entity instanceof EntityPlayer) {
            if (colorFriends && FriendManager.friendsList.contains(entity.getName())) {
                color = new Color(0.33f, 1.0f, 1.0f, alpha / 255.0f);
            }
            if (colorFriends && EnemyManager.enemysList.contains(entity.getName())) {
                color = new Color(1, 0.33f, 1.0f, alpha / 255.0f);
            }
        }
        return color;
    }

    public static Block isColliding(double posX, double posY, double posZ) {
        Block block = null;
        if (Minecraft.getMinecraft().player != null) {
            final AxisAlignedBB bb = Minecraft.getMinecraft().player.getRidingEntity() != null ? Minecraft.getMinecraft().player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : Minecraft.getMinecraft().player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ);
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    block = Minecraft.getMinecraft().world.getBlockState(new BlockPos(x, y, z)).getBlock();
                }
            }
        }
        return block;
    }

    public static boolean isInLiquid() {
        if (Minecraft.getMinecraft().player != null) {
            if (Minecraft.getMinecraft().player.fallDistance >= 3.0f) {
                return false;
            }
            boolean inLiquid = false;
            final AxisAlignedBB bb = Minecraft.getMinecraft().player.getRidingEntity() != null ? Minecraft.getMinecraft().player.getRidingEntity().getEntityBoundingBox() : Minecraft.getMinecraft().player.getEntityBoundingBox();
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    final Block block = Minecraft.getMinecraft().world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }
                        inLiquid = true;
                    }
                }
            }
            return inLiquid;
        }
        return false;
    }

    public static double sigmoid(final double x) {
        return (1 / (1 + Math.pow(Math.E, (-1 * x))));
    }

    public static double predictPos(final double motion, final double threshold) {
        double m = Math.abs(motion);
        m = m - threshold;
        m = sigmoid(m);
        return m;
    }

    public static Vec3d getInterpolatedLinearVec(Entity entity, float ticks) {
        return new Vec3d(
            lerp(entity.lastTickPosX, entity.posX, ticks),
            lerp(entity.lastTickPosY, entity.posY, ticks),
            lerp(entity.lastTickPosZ, entity.posZ, ticks)
        );
    }

    public static double lerp(double a, double b, float ticks) {
        return (a + (b - a) * ticks);
    }
}
