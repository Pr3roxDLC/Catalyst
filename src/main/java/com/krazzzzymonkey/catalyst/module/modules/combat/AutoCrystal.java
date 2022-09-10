package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;

import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

import static com.krazzzzymonkey.catalyst.module.modules.render.HoleESP.AxisAligned;

//Osiris Skid - Made By finz0 (I take no credit)
//TODO MERGE THIS WITH REWRITE
public class AutoCrystal extends Modules {

    public IntegerValue waitTick;
    public DoubleValue range;
    public DoubleValue walls;
    public DoubleValue placeRange;
    public DoubleValue minDmg;
    public DoubleValue facePlace;
    public DoubleValue maxSelfDmg;
    public DoubleValue minHitDmg;


    public ColorValue espColor;
    public IntegerValue espHeight;

    public BooleanValue explode;
    public BooleanValue antiWeakness;
    public BooleanValue place;
    public BooleanValue nodesync;
    public BooleanValue rotate;
    public BooleanValue spoofRotations;


    public BooleanValue noGappleSwitch;
    public BooleanValue rainbow;
    public BooleanValue raytrace;
    public BooleanValue autoSwitch;

    public BooleanValue onePointThirteen;

    public ModeValue renderMode;

    public AutoCrystal() {
        super("AutoCrystal", ModuleCategory.COMBAT, "Automatically places and breaks end crystals");
        onePointThirteen = new BooleanValue("1.13+ Place", false, "");
        explode = new BooleanValue("Hit", true, "");
        waitTick = new IntegerValue("TickDelay", 1, 0, 20, "");
        range = new DoubleValue("HitRange", 5D, 0D, 10D, "");
        walls = new DoubleValue("WallsRange", 3.5D, 0D, 10D, "");
        antiWeakness = new BooleanValue("AntiWeakness", true, "");
        nodesync = new BooleanValue("AntiDesync", true, "");
        noGappleSwitch = new BooleanValue("NoGapSwitch", true, "");
        place = new BooleanValue("Place", true, "");
        autoSwitch = new BooleanValue("AutoSwitch", true, "");
        placeRange = new DoubleValue("PlaceRange", 5D, 0D, 10D, "");
        minDmg = new DoubleValue("MinDmg", 5D, 0D, 40D, "");
        minHitDmg = new DoubleValue("MinHitDmg", 6D, 0D, 40D, "");
        facePlace = new DoubleValue("FacePlaceHP", 6D, 0D, 40D, "");
        raytrace = new BooleanValue("Raytrace", false, "");
        rotate = new BooleanValue("Rotate", true, "");
        spoofRotations = new BooleanValue("SpoofAngles", true, "");
        maxSelfDmg = new DoubleValue("MaxSelfDmg", 10D, 0D, 36D, "");

        rainbow = new BooleanValue("EspRainbow", false, "");
        espColor = new ColorValue("EspColor", Color.CYAN.getRGB(), "");
        espHeight = new IntegerValue("EspHeight", 1, 0, 2, "");

        this.renderMode = new ModeValue("RenderMode", new Mode("Full", false), new Mode("Outline", true));
        this.addValue(onePointThirteen, explode, waitTick, range, walls, antiWeakness, nodesync, noGappleSwitch, place, autoSwitch, minDmg, minHitDmg, facePlace, raytrace, rotate, spoofRotations, maxSelfDmg, rainbow, espColor, espHeight, renderMode);
    }

    private BlockPos render;
    private Entity renderEnt;
    // we need this cooldown to not place from old hotbar slot, before we have switched to crystals
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private int oldSlot = -1;
    private int newSlot;
    private int waitCounter;
    EnumFacing f;
    public boolean isActive = false;


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(event -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        isActive = false;
        if (mc.player == null || mc.player.isDead) return; // bruh
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
            .filter(entity -> entity instanceof EntityEnderCrystal)
            .filter(e -> mc.player.getDistance(e) <= range.getValue())
            .map(entity -> (EntityEnderCrystal) entity)
            .min(Comparator.comparing(c -> mc.player.getDistance(c)))
            .orElse(null);
        if (explode.getValue() && crystal != null) {
            if (!mc.player.canEntityBeSeen(crystal) && mc.player.getDistance(crystal) > walls.getValue()) return;

            if (waitTick.getValue() > 0) {
                if (waitCounter < waitTick.getValue()) {
                    waitCounter++;
                    return;
                } else {
                    waitCounter = 0;
                }
            }
            if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!isAttacking) {
                    // save initial player hand
                    oldSlot = mc.player.inventory.currentItem;
                    isAttacking = true;
                }
                // search for sword and tools in hotbar
                newSlot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) {
                        continue;
                    }
                    if ((stack.getItem() instanceof ItemSword)) {
                        newSlot = i;
                        break;
                    }
                    if ((stack.getItem() instanceof ItemTool)) {
                        newSlot = i;
                        break;
                    }
                }
                // check if any swords or tools were found
                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                    switchCooldown = true;
                }
            }
            boolean hitcrystal = minHitDmg.getValue() <= 0;

            if (minHitDmg.getValue() >= 0) {
                for (Entity entity : mc.world.playerEntities.stream()
                    .filter(e -> mc.player != e)
                    .filter(e -> mc.player.getDistance(e) <= 11)
                    .filter(e -> e.getHealth() > 0).filter(e -> !e.isDead)
                    .filter(e -> !FriendManager.friendsList.contains(e.getName()))
                    .collect(Collectors.toList())) {
                    if (crystal != null) {
                        hitcrystal = calculateDamage(crystal, entity) >= minHitDmg.getValue();
                    }
                }
            }


            isActive = true;
            if (hitcrystal) {
                if (rotate.getValue()) {
                    lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
                }
                mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            isActive = false;
            return;
        } else {
            resetRotation();
            if (oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;
            isActive = false;
        }

        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        List<BlockPos> blocks = findCrystalBlocks();
        List<Entity> entities = new ArrayList<>();
        entities.addAll(mc.world.playerEntities.stream().filter(entityPlayer -> !FriendManager.friendsList.contains(entityPlayer.getName()) && !mc.player.getName().equals(entityPlayer.getName())).sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList()));

        BlockPos q = null;
        double damage = .5;
        for (Entity entity : entities) {
            if (entity == mc.player) continue;
            if (((EntityLivingBase) entity).getHealth() <= 0 || entity.isDead || mc.player == null) {
                continue;
            }
            for (BlockPos blockPos : blocks) {
                double b = entity.getDistanceSq(blockPos);
                if (b >= 169) {
                    continue; // If this block if further than 13 (3.6^2, less calc) blocks, ignore it. It'll take no or very little damage
                }
                double d = calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, entity);
                if (d < minDmg.getValue() && ((EntityLivingBase) entity).getHealth() + ((EntityLivingBase) entity).getAbsorptionAmount() > facePlace.getValue()) {
                    continue;
                }
                if (d > damage) {
                    double self = calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, mc.player);
                    // If this deals more damage to ourselves than it does to our target, continue. This is only ignored if the crystal is sure to kill our target but not us.
                    // Also continue if our crystal is going to hurt us.. alot
                    if ((self > d && !(d < ((EntityLivingBase) entity).getHealth())) || self - .5 > mc.player.getHealth()) {
                        continue;
                    }
                    if (self > maxSelfDmg.getValue())
                        continue;
                    damage = d;
                    q = blockPos;
                    renderEnt = entity;
                }
            }
        }
        if (damage == .5) {
            render = null;
            renderEnt = null;
            resetRotation();
            return;
        }
        render = q;


        if (place.getValue()) {
            if (mc.player == null) return;
            isActive = true;
            if (rotate.getValue()) {
                lookAtPacket(q.getX() + .5, q.getY() - .5, q.getZ() + .5, mc.player);
            }
            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(q.getX() + .5, q.getY() - .5d, q.getZ() + .5));
            if (raytrace.getValue()) {
                if (result == null || result.sideHit == null) {
                    q = null;
                    f = null;
                    render = null;
                    resetRotation();
                    isActive = false;
                    return;
                } else {
                    f = result.sideHit;
                }
            }

            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                if (autoSwitch.getValue()) {
                    if (noGappleSwitch.getValue() && isEatingGap()) {
                        isActive = false;
                        resetRotation();
                        return;
                    } else {
                        isActive = true;
                        mc.player.inventory.currentItem = crystalSlot;
                        resetRotation();
                        switchCooldown = true;
                    }
                }
                return;
            }
            // return after we did an autoswitch
            if (switchCooldown) {
                switchCooldown = false;
                return;
            }
            //mc.playerController.processRightClickBlock(mc.player, mc.world, q, f, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
            if (q != null && mc.player != null) {
                isActive = true;
                if (raytrace.getValue() && f != null) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                }
            }
            isActive = false;
        }
    });

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        Color color = ColorUtils.rainbow();
        Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        if (render != null && mc.player != null) {

            if (rainbow.getValue()) {
                drawCurrentBlock(render, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            } else {

                drawCurrentBlock(render, espColor.getColor().getRed(), espColor.getColor().getGreen(), espColor.getColor().getBlue(), 255);
            }
        }
    });

    private boolean isEatingGap() {
        return mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold && mc.player.isHandActive();
    }

    private void drawCurrentBlock(BlockPos render, int r, int g, int b, int a) {
        if (renderMode.getMode("Full").isToggled())
            RenderUtils.drawBlockESP(render, r / 255f, g / 255f, b / 255f, espHeight.getValue());
        else if (renderMode.getMode("Outline").isToggled())
            RenderUtils.drawOutlinedBox(AxisAligned(render), r / 255f, g / 255f, b / 255f, 1f, espHeight.getValue());
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);

        BlockPos boost2 = blockPos.add(0, 2, 0);
        if (onePointThirteen.getValue()) {
            boost2 = blockPos.add(0, 1, 0);
        }
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
            || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
            && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
            && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
            && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
            && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(Minecraft.getMinecraft().player.posX), Math.floor(Minecraft.getMinecraft().player.posY), Math.floor(Minecraft.getMinecraft().player.posZ));
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange.getValue().floatValue(), (int) placeRange.getValue().floatValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

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

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;
        /*if (entity instanceof EntityLivingBase)
            finald = getBlastReduction((EntityLivingBase) entity,getDamageMultiplied(damage));*/
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(Minecraft.getMinecraft().world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }
            //   damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    private static float getDamageMultiplied(float damage) {
        int diff = Minecraft.getMinecraft().world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }
    //Better Rotation Spoofing System:

    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;

    //this modifies packets being sent so no extra ones are made. NCP used to flag with "too many packets"
    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = Minecraft.getMinecraft().player.rotationYaw;
            pitch = Minecraft.getMinecraft().player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        //to degree
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw, pitch};
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if(event.getSide() == PacketEvent.Side.IN) {

            Packet packet = event.getPacket();
            if ((packet instanceof SPacketSoundEffect) && nodesync.getValue()) {
                final SPacketSoundEffect Packet = (SPacketSoundEffect) packet;
                if (Packet.getCategory() == SoundCategory.BLOCKS && Packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    try {
                        for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
                            if (e instanceof EntityEnderCrystal) {
                                if (e.getDistance(Packet.getX(), Packet.getY(), Packet.getZ()) <= 6.0f) {
                                    e.setDead();
                                }
                            }
                        }
                    } catch (ConcurrentModificationException e) {
                        //empty catch block
                    }
                }
            }
        }
    });

    public void onEnable() {
        super.onEnable();
        isActive = false;
    }

    public void onDisable() {
        super.onDisable();
        render = null;
        renderEnt = null;
        resetRotation();
        isActive = false;
    }
}
