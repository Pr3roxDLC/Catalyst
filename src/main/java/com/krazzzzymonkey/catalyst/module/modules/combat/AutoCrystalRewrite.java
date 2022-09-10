package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.MotionEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.managers.RotationManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.chat.AutoGG;
import com.krazzzzymonkey.catalyst.utils.*;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import static com.krazzzzymonkey.catalyst.module.modules.render.HoleESP.AxisAligned;
import static com.krazzzzymonkey.catalyst.utils.Utils.nullCheck;

enum ThreadType {
    BLOCK,
    CRYSTAL
}

public class AutoCrystalRewrite extends Modules {
    public static AutoCrystalRewrite INSTANCE;

    final BooleanValue thirteen = new BooleanValue("1.13+Place", false, "Allows the client to use 1x1 blocks as valid crystal placements");
    final BooleanValue place = new BooleanValue("Place", true, "Makes the auto crystal place crystals");
    final BooleanValue hit = new BooleanValue("Hit", true, "Makes the auto crystal break crystals");
    final DoubleValue hitRange = new DoubleValue("HitRange", 5.0, 0.0, 6.0, "The max range at which the client will hit crystals");
    final DoubleValue hitWallRange = new DoubleValue("HitWallRange", 3.5, 0.0, 6.0, "The max range at which the client will hit crystals through walls");
    final DoubleValue placeRange = new DoubleValue("PlaceRange", 5.0, 0.0, 6.0, "The max range at which the client will hit crystals");
    final DoubleValue placeWallRange = new DoubleValue("PlaceWallRange", 3.5, 0.0, 6.0, "The max range at which the client will place crystals");
    final DoubleValue targetRange = new DoubleValue("TargetRange", 15.0, 0.0, 20.0, "The max range at which the client will target players");
    final IntegerValue placeDelay = new IntegerValue("PlaceDelay", 0, 0, 10, "The delay between placing crystals");
    final IntegerValue hitDelay = new IntegerValue("HitDelay", 0, 0, 10, "The delay between breaking crystals");
    final IntegerValue targetHealthPlace = new IntegerValue("TargetHealthPlace", 10, 0, 36, "Will only place if the minimum damage the crystal will do to the target is greater than this");
    final IntegerValue targetHealthBreak = new IntegerValue("TargetHealthBreak", 9, 0, 36, "Will only break if the minimum damage the crystal will do to the target is greater than this");
    final BooleanValue multiPlace = new BooleanValue("MultiPlace", false, "Places multiple crystals around the target");
    final BooleanValue packetHit = new BooleanValue("PacketHit", true, "Only sends attack packets rather than swinging your hand");
    final BooleanValue antiSuicide = new BooleanValue("AntiSuicide", true, "Stops placing and breaking crystals if the crystal will kill you");
    final IntegerValue maxSelfDmg = new IntegerValue("MaxSelfDmg", 5, 0, 36, "The maximum amount of self damage the crystal can do if broken");
    final ModeValue rotateMode = new ModeValue("Rotate", new Mode("RotateOff", true), new Mode("RotatePacket", false), new Mode("Rotate", false));
    final BooleanValue stopOnSword = new BooleanValue("StopOnSword", false, "Stops trying to place crystals when holding a sword");
    final BooleanValue spoofPlaceSwing = new BooleanValue("SpoofPlaceSwing", true, "Stops sending the swing animation when placing a crystal");
    final ModeValue swing = new ModeValue("BreakSwing", new Mode("Mainhand", true), new Mode("Offhand", false), new Mode("Cancel", false));
    final BooleanValue raytrace = new BooleanValue("Raytrace", false, "Checks if crystal can be seen by the player");
    final ModeValue autoSwitch = new ModeValue("AutoSwitch", new Mode("Nothing", false), new Mode("Everything", false), new Mode("NoGapSwitch", true), new Mode("SilentSwitch", false));
    final BooleanValue multiThreaded = new BooleanValue("MultiThreaded", false, "Handles every crystal on a different thread");
    final BooleanValue packetBlockPredict = new BooleanValue("PacketBlockPredict", true, "Predicts when crystal block doesn't have a crystal on it anymore, then places a new crystal");
    final BooleanValue packetCrystalPredict = new BooleanValue("PacketCrystalPredict", true, "Predicts when a crystal has been attacked, then places a new crystal");
    final BooleanValue packetEntityPredict = new BooleanValue("PacketEntityPredict", true, "Predicts the position of the target based on the targets motion");
    final IntegerValue maxPredictTime = new IntegerValue("MaxPredictTime", 3, 0, 5, "The max amount of ticks used to predict the targets position");
    final ModeValue crystalLogic = new ModeValue("CrystalLogic", new Mode("IgnoreCrystal", true), new Mode("RemoveCrystal", false), new Mode("CheckCrystal", false), new Mode("NoLogic", false));
    final BooleanValue facePlace = new BooleanValue("FacePlace", true, "Should the client face place crystals based on FacePlaceLogic");
    final ModeValue facePlaceLogic = new ModeValue("FacePlaceLogic", new Mode("Smart", true), new Mode("ArmorDurability", false), new Mode("None", false));
    final IntegerValue facePlaceHealth = new IntegerValue("FacePlaceHealth", 8, 0, 36, "The maximum heath needed by the target to get face placed");
    final IntegerValue facePlaceArmorDur = new IntegerValue("FacePlaceArmorDur", 25, 0, 100, "The maximum armor durability needed by the target to get face placed");
    final IntegerValue facePlaceDelay = new IntegerValue("FacePlaceDelay", 0, 0, 10, "The amount of ticks between face placing");
    final BooleanValue antiWeakness = new BooleanValue("AntiWeakness", true, "Uses a sword when attacking crystals if you have the weakness effect");
    final BooleanValue ignoreBlocks = new BooleanValue("IgnoreBlocks", true, "Ignores the blocks around the crystal when calculating damage");

    final ModeValue renderMode = new ModeValue("RenderMode", new Mode("Full", false), new Mode("Outline", true));
    final ColorValue espColor = new ColorValue("EspColor", Color.CYAN.getRGB(), "The color of the box showing you where a crystal is going to be placed");
    final BooleanValue rainbow = new BooleanValue("EspRainbow", false, "Makes the esp box cycle through colors");
    final IntegerValue espHeight = new IntegerValue("EspHeight", 1, 0, 2, "The height of the esp box");
    private final List<EntityEnderCrystal> attemptedCrystals = new ArrayList<>();
    public EntityPlayer ezTarget = null;
    public BlockPos renderBlock = null;
    public BlockPos staticPos;
    public EntityEnderCrystal staticEnderCrystal;
    private float yaw;
    private float pitch;
    private boolean alreadyAttacking;
    private boolean placeTimeoutFlag;
    private boolean hasPacketBroke;
    private boolean isRotating;
    private boolean didAnything;
    private boolean facePlacing;

    private int breakDelayCounter;
    private int placeDelayCounter;
    private int facePlaceDelayCounter;

    public AutoCrystalRewrite() {
        super("AutoCrystalRewrite", ModuleCategory.COMBAT, "Automatically places and breaks end crystals");
        this.addValue(thirteen, place, hit, hitRange, hitWallRange, placeRange, placeWallRange, targetRange, placeDelay, hitDelay, targetHealthPlace, targetHealthBreak, multiPlace, packetHit, antiSuicide, maxSelfDmg, rotateMode, stopOnSword, spoofPlaceSwing, swing, raytrace,
            autoSwitch, multiThreaded, packetBlockPredict, packetCrystalPredict, packetEntityPredict, maxPredictTime, crystalLogic, facePlace, facePlaceLogic, facePlaceHealth, facePlaceArmorDur, facePlaceDelay, antiWeakness, ignoreBlocks, renderMode, espColor, rainbow, espHeight);
        INSTANCE = this;
    }


    @EventHandler
    private final EventListener<MotionEvent.PREWALK> onUpdateWalkingPlayerEvent = new EventListener<>(event -> {
        if (this.rotateMode.getMode("Rotate").isToggled()) {
            if (this.isRotating) {
                setPlayerRotations(yaw, pitch);
            }
            this.doCrystalAura();
        }
    });

    @EventHandler
    private final EventListener<PacketEvent> onPacketSend = new EventListener<>(event -> {
        if (event.getSide() == PacketEvent.Side.OUT) {

            Packet packet = event.getPacket();
            if (packet instanceof CPacketPlayer && isRotating && rotateMode.getMode("RotatePacket").isToggled()) {
                final CPacketPlayer p = (CPacketPlayer) packet;
                p.yaw = yaw;
                p.pitch = pitch;
            }
            CPacketUseEntity cPacketUseEntity;
            if (packet instanceof CPacketUseEntity && (cPacketUseEntity = (CPacketUseEntity) packet).getAction() == CPacketUseEntity.Action.ATTACK
                && cPacketUseEntity.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                if (this.crystalLogic.getMode("RemoveCrystal").isToggled()) {
                    Objects.requireNonNull(cPacketUseEntity.getEntityFromWorld(mc.world)).setDead();
                    mc.world.removeEntityFromWorld(cPacketUseEntity.entityId);
                }
            }
        }
    });

    @EventHandler
    private final EventListener<PacketEvent> onPacketRecieve = new EventListener<>(event -> {
        if (event.getSide() == PacketEvent.Side.IN) {

            Packet packet = event.getPacket();
            SPacketSpawnObject sPacketSpawnObject;
            if (packet instanceof SPacketSpawnObject && (sPacketSpawnObject = (SPacketSpawnObject) packet).getType() == 51) {
                for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
                    if (this.isCrystalGood(new EntityEnderCrystal(mc.world, sPacketSpawnObject.getX(), sPacketSpawnObject.getY(), sPacketSpawnObject.getZ()), target) != 0) {
                        if (this.packetCrystalPredict.getValue() && this.breakDelayCounter > this.hitDelay.getValue()) {
                            CPacketUseEntity predict = new CPacketUseEntity();
                            predict.entityId = sPacketSpawnObject.getEntityID();
                            predict.action = CPacketUseEntity.Action.ATTACK;
                            mc.player.connection.sendPacket(predict);
                            if (!this.swing.getMode("Cancel").isToggled()) {
                                BlockUtils.swingArm(swing);
                            }
                            hasPacketBroke = true;
                            didAnything = true;
                        }
                        break;
                    }
                }
            }
            if (packet instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities packet_ = (SPacketDestroyEntities) packet;
                for (int id : packet_.getEntityIDs()) {
                    try {
                        Entity entity = mc.world.getEntityByID(id);
                        if (!(entity instanceof EntityEnderCrystal)) continue;
                        this.attemptedCrystals.remove(entity);
                    } catch (Exception ignored) {
                    }
                }
            }
            if (packet instanceof SPacketSoundEffect) {
                if (((SPacketSoundEffect) packet).getCategory() == SoundCategory.BLOCKS && ((SPacketSoundEffect) packet).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity crystal : new ArrayList<>(mc.world.loadedEntityList)) {
                        if (crystal instanceof EntityEnderCrystal)
                            if (crystal.getDistance(((SPacketSoundEffect) packet).getX(), ((SPacketSoundEffect) packet).getY(), ((SPacketSoundEffect) packet).getZ()) <= hitRange.getValue()) {
                                if (crystalLogic.getMode("CheckCrystal").isToggled()) {
                                    crystal.setDead();
                                }
                            }
                    }
                }
            }
            if (packet instanceof SPacketExplosion) {
                SPacketExplosion sPacketExplosion = (SPacketExplosion) packet;
                BlockPos pos = new BlockPos(Math.floor(sPacketExplosion.getX()), Math.floor(sPacketExplosion.getY()), Math.floor(sPacketExplosion.getZ())).down();
                if (this.packetBlockPredict.getValue() && this.breakDelayCounter > this.hitDelay.getValue()) {
                    for (EntityPlayer player : new ArrayList<>(mc.world.playerEntities)) {
                        if (this.isBlockGood(pos, player) > 0) {
                            if (mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal)) {
                                BlockUtils.placeCrystalOnBlock(pos, EnumHand.OFF_HAND, spoofPlaceSwing.getValue());
                            } else {
                                BlockUtils.placeCrystalOnBlock(pos, EnumHand.MAIN_HAND, spoofPlaceSwing.getValue());
                            }

                        }
                    }
                }
            }
        }
    });

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (!this.rotateMode.getMode("Rotate").isToggled()) {
            this.doCrystalAura();
        }
    });

    private void doCrystalAura() {
        if (mc.player == null || mc.world == null) {
            this.toggle();
            return;
        }
        didAnything = false;

        if (this.place.getValue() && placeDelayCounter > this.placeDelay.getValue() && (facePlaceDelayCounter >= facePlaceDelay.getValue() || !facePlacing)) {
            this.placeCrystal();
        }
        if (this.hit.getValue() && breakDelayCounter > this.hitDelay.getValue() && (!hasPacketBroke)) {
            this.breakCrystal();
        }

        if (!didAnything) {
            hasPacketBroke = false;
            ezTarget = null;
            isRotating = false;
        }

        breakDelayCounter++;
        placeDelayCounter++;
        facePlaceDelayCounter++;
    }

    private void placeCrystal() {
        BlockPos targetBlock;
        if (multiThreaded.getValue() && mc.fpsCounter > 60) {
            Threads threads = new Threads(ThreadType.BLOCK);
            threads.start();
            targetBlock = staticPos;
        } else {
            targetBlock = this.getBestBlock();
        }
        if (targetBlock == null) {
            renderBlock = null;
            return;
        }
        renderBlock = targetBlock;

        placeDelayCounter = 0;
        facePlaceDelayCounter = 0;
        alreadyAttacking = false;
        boolean offhandCheck = false;
        if (CrystalUtils.calculateDamage(targetBlock, mc.player, false) > maxSelfDmg.getValue() || (antiSuicide.getValue() && CrystalUtils.calculateDamage(targetBlock, mc.player, false) >= PlayerUtils.getHealth())) {
            return;
        }
        boolean hasSilentSwitched = false;
        int silentCurrentItem = mc.player.inventory.currentItem;
        if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && (autoSwitch.getMode("Everything").isToggled() || autoSwitch.getMode("NoGapSwitch").isToggled() || autoSwitch.getMode("SilentSwitch").isToggled())) {
                if (autoSwitch.getMode("NoGapSwitch").isToggled()) {
                    if (mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE) {
                        return;
                    }
                }
                if (this.findCrystalsHotbar() == -1) return;
                if (autoSwitch.getMode("SilentSwitch").isToggled()) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(this.findCrystalsHotbar()));
                    hasSilentSwitched = true;
                } else {
                    mc.player.inventory.currentItem = this.findCrystalsHotbar();
                    mc.playerController.syncCurrentPlayItem();
                }

            }
        } else {
            offhandCheck = true;
        }

        didAnything = true;
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal || hasSilentSwitched) {
            setYawPitch(targetBlock);
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetBlock, EnumFacing.UP, offhandCheck ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            if (spoofPlaceSwing.getValue()) {
                mc.player.connection.sendPacket(new CPacketAnimation(offhandCheck ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            }
            if (autoSwitch.getMode("SilentSwitch").isToggled()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(silentCurrentItem));
            }

            //  BlockUtils.placeCrystalOnBlock(targetBlock, offhandCheck ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, !spoofPlaceSwing.getValue());
         //
        }
    }

    private void breakCrystal() {
        EntityEnderCrystal crystal;
        if (multiThreaded.getValue() && mc.fpsCounter > 60) {
            Threads threads = new Threads(ThreadType.CRYSTAL);
            threads.start();
            crystal = staticEnderCrystal;
        } else {
            crystal = this.getBestCrystal();
        }
        if (crystal == null) {
            return;
        }
        if (CrystalUtils.calculateDamage(crystal, mc.player, false) > maxSelfDmg.getValue()) {
            staticEnderCrystal = null;
            return;
        }
        if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            boolean shouldWeakness = true;
            if (mc.player.isPotionActive(MobEffects.STRENGTH)) {
                if (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() == 2) {
                    shouldWeakness = false;
                }
            }
            if (shouldWeakness) {
                if (!alreadyAttacking) {
                    this.alreadyAttacking = true;
                }
                int newSlot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) {
                        newSlot = i;
                        mc.playerController.updateController();
                        break;
                    }
                }
                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                }
            }
        }
        didAnything = true;
        setYawPitch(crystal);
        EntityUtils.attackEntity(crystal, this.packetHit.getValue(), false);
        attemptedCrystals.add(crystal);
        if (!this.swing.getMode("Cancel").isToggled()) {
            BlockUtils.swingArm(swing);
        }

        breakDelayCounter = 0;
    }

    public final EntityEnderCrystal getBestCrystal() {
        double bestDamage = 0;
        EntityEnderCrystal bestCrystal = null;
        for (Entity e : mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderCrystal)) continue;
            EntityEnderCrystal crystal = (EntityEnderCrystal) e;
            for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
                if (mc.player.getDistanceSq(target) > MathUtils.square(targetRange.getValue().floatValue())) continue;
                if (packetEntityPredict.getValue() && target != mc.player && this.breakDelayCounter > this.hitDelay.getValue()) {
                    float f = target.width / 2.0F, f1 = target.height;
                    target.setEntityBoundingBox(new AxisAlignedBB(target.posX - (double) f, target.posY, target.posZ - (double) f, target.posX + (double) f, target.posY + (double) f1, target.posZ + (double) f));
                    Entity y = CrystalUtils.getPredictedPosition(target, maxPredictTime.getValue());
                    target.setEntityBoundingBox(y.getEntityBoundingBox());
                }
                double targetDamage = this.isCrystalGood(crystal, target);
                if (targetDamage == 0) continue;
                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    this.ezTarget = target;
                    bestCrystal = crystal;
                }
            }
        }
        if (this.ezTarget != null) {
            AutoGG.addTargetedPlayer(this.ezTarget.getName());
        }
        return bestCrystal;
    }


    public final BlockPos getBestBlock() {
        if (getBestCrystal() != null && crystalLogic.getMode("NoLogic").isToggled()) {
            placeTimeoutFlag = true;
            return null;
        }

        if (placeTimeoutFlag) {
            placeTimeoutFlag = false;
            return null;
        }
        double bestDamage = 0;
        BlockPos bestPos = null;

        ArrayList<CrystalPos> validPos = new ArrayList<>();

        for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
            if (mc.player.getDistanceSq(target) > MathUtils.square(targetRange.getValue().floatValue())) continue;
            if (packetEntityPredict.getValue() && target != mc.player && this.breakDelayCounter > this.hitDelay.getValue()) {
                float f = target.width / 2.0F, f1 = target.height;
                target.setEntityBoundingBox(new AxisAlignedBB(target.posX - (double) f, target.posY, target.posZ - (double) f, target.posX + (double) f, target.posY + (double) f1, target.posZ + (double) f));
                Entity y = CrystalUtils.getPredictedPosition(target, maxPredictTime.getValue());
                target.setEntityBoundingBox(y.getEntityBoundingBox());
            }
            for (BlockPos blockPos : CrystalUtils.possiblePlacePositions(this.placeRange.getValue().floatValue(), !multiPlace.getValue(), this.thirteen.getValue())) {
                double targetDamage = isBlockGood(blockPos, target);
                if (targetDamage == 0) continue;
                if (CrystalUtils.calculateDamage(blockPos, mc.player, false) > maxSelfDmg.getValue()) {
                    bestPos = null;
                    continue;
                }

                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    bestPos = blockPos;
                    ezTarget = target;

                }

            }
        }

        if (this.ezTarget != null) {
            AutoGG.addTargetedPlayer(this.ezTarget.getName());
        }
        if (bestPos == null) {
            bestPos = searchPosition();
        }

        return bestPos;
    }

    public BlockPos searchPosition() {
        if (place.getValue()) {
            // map of viable positions
            TreeMap<Float, AutoCrystalRewrite.CrystalPosition> positionMap = new TreeMap<>();

            for (BlockPos calculatedPosition : BlockUtils.getSurroundingBlocks(mc.player, placeRange.getValue(), false)) {
                // make sure it's actually a viable position
                if (!canPlaceCrystal(calculatedPosition, thirteen.getValue()))
                    continue;

                // make sure it doesn't do too much dmg to us or kill us
                float localDamage = mc.player.capabilities.isCreativeMode ? 0 : ExplosionUtils.getDamageFromExplosion(calculatedPosition.getX() + 0.5, calculatedPosition.getY() + 1, calculatedPosition.getZ() + 0.5, mc.player, ignoreBlocks.getValue(), false);
                if (localDamage > maxSelfDmg.getValue() || (localDamage + 1 > PlayerUtils.getHealth()/* && pauseSafety.getValue()*/))
                    continue;

                // if the block above the one we can't see through is air, then NCP won't flag us for placing at normal ranges
                boolean wallPlacement = raytrace.getValue();

                // if it is a wall placement, use our wall ranges
                double distance = mc.player.getDistance(calculatedPosition.getX() + 0.5, calculatedPosition.getY() + 1, calculatedPosition.getZ() + 0.5);
                if (distance > placeWallRange.getValue() && wallPlacement)
                    continue;

                for (EntityPlayer calculatedTarget : mc.world.playerEntities) {
                    // make sure the target is not dead, a friend, or the local player
                    if (calculatedTarget.equals(mc.player) || EntityUtils.isDead(calculatedTarget) || FriendManager.friendsList.contains(calculatedTarget.getName()))
                        continue;

                    // make sure target's within our specified target range
                    float targetDistance = mc.player.getDistance(calculatedTarget);
                    if (targetDistance > targetRange.getValue())
                        continue;

                    // calculate the damage this position will do to each target, we can verify if it meets our requirements later
                    float targetDamage = calculateLogic(ExplosionUtils.getDamageFromExplosion(calculatedPosition.getX() + 0.5, calculatedPosition.getY() + 1, calculatedPosition.getZ() + 0.5, calculatedTarget, ignoreBlocks.getValue(), true), localDamage, distance);

                    // add it to our list of viable positions
                    positionMap.put(targetDamage, new AutoCrystalRewrite.CrystalPosition(calculatedPosition, calculatedTarget, targetDamage, localDamage));
                }
            }

            if (!positionMap.isEmpty()) {
                // in the map, the best position will be the last entry
                AutoCrystalRewrite.CrystalPosition idealPosition = positionMap.lastEntry().getValue();
                // required damage for it the placement to be continued
                double requiredDamage = targetHealthPlace.getValue();

                // find out if we need to override our min dmg, 2 sounds like a good number for face placing but might need to be lower
         /*       if (override.getValue()) {
                    if (idealPosition.getTargetDamage() * overrideThreshold.getValue() >= EnemyUtil.getHealth(idealPosition.getPlaceTarget()))
                        requiredDamage = 0.5;

                    if (HoleUtil.isInHole(idealPosition.getPlaceTarget())) {
                        if (EnemyUtil.getHealth(idealPosition.getPlaceTarget()) < overrideHealth.getValue())
                            requiredDamage = 0.5;

                        if (EnemyUtil.getArmor(idealPosition.getPlaceTarget(), overrideArmor.getValue()))
                            requiredDamage = 0.5;
                    }
                }*/

                // verify if the ideal position meets our requirements, if it doesn't it automatically rules out all other placements
                if (idealPosition.getTargetDamage() > requiredDamage) {
                    return idealPosition.getPosition();
                }
            }
        }

        return null;
    }

    public float calculateLogic(float targetDamage, float selfDamage, double distance) {
        return targetDamage;
        /*switch (logic.getValue()) {
            case DAMAGE:
            default:
                return targetDamage;
            case MINIMAX:
                return targetDamage - selfDamage;
            case ATOMIC:
                return targetDamage - selfDamage - (float) distance;
            case VOLATILE:
                return targetDamage - (float) distance;
        }*/
    }

    public boolean canPlaceCrystal(BlockPos blockPos, boolean oneThirteen) {
        try {
            if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK) && !mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN))
                return false;

            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0)))) {
                if (entity.isDead || (entity instanceof EntityEnderCrystal && entity.getPosition().equals(blockPos.add(0, 1, 0))))
                    continue;

                return false;
            }

            if (!oneThirteen)
                return mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR);
            else
                return mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR);
        } catch (Exception ignored) {
            return false;
        }
    }

    private double isCrystalGood(EntityEnderCrystal crystal, EntityPlayer target) {
        if (CrystalUtils.calculateDamage(crystal, mc.player, false) > maxSelfDmg.getValue() || (antiSuicide.getValue() && CrystalUtils.calculateDamage(crystal, mc.player, false) >= PlayerUtils.getHealth())) {
            return 0;
        }
        if (this.isPlayerValid(target)) {
            if (mc.player.canEntityBeSeen(crystal)) {
                if (mc.player.getDistanceSq(crystal) > MathUtils.square(this.hitRange.getValue().floatValue())) {
                    return 0;
                }
            } else {
                if (mc.player.getDistanceSq(crystal) > MathUtils.square(this.hitWallRange.getValue().floatValue())) {
                    return 0;
                }
            }
            if (crystal.isDead) return 0;
            if (attemptedCrystals.contains(crystal)) return 0;

            double minimumDamage;
            if (CrystalUtils.calculateDamage(crystal, target, ignoreBlocks.getValue()) >= targetHealthPlace.getValue()) {
                facePlacing = false;
                minimumDamage = this.targetHealthBreak.getValue();
            } else if (((EntityUtils.getHealth(target) <= facePlaceHealth.getValue() && facePlace.getValue())
                || (CrystalUtils.getArmourFucker(target, facePlaceArmorDur.getValue()) && !facePlaceLogic.getMode("None").isToggled()))
                && !stopOnSword.getValue()) {
                minimumDamage = EntityUtils.isInHole(target) ? 1 : 2;
                facePlacing = true;
            } else {
                facePlacing = false;
                minimumDamage = this.targetHealthBreak.getValue();
            }

            double targetDamage = CrystalUtils.calculateDamage(crystal, target, ignoreBlocks.getValue());
            if (targetDamage < minimumDamage && EntityUtils.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = 0;

            if (selfDamage > maxSelfDmg.getValue()) return 0;
            if (EntityUtils.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private double isBlockGood(BlockPos blockPos, EntityPlayer target) {
        if (this.isPlayerValid(target)) {
            // if raytracing and cannot see block
            if (!CrystalUtils.canSeePos(blockPos) && raytrace.getValue()) return 0;
            // if cannot see pos use wall range, else use normal
            if (!CrystalUtils.canSeePos(blockPos)) {
                if (mc.player.getDistanceSq(blockPos) > MathUtils.square(this.placeWallRange.getValue().floatValue())) {
                    return 0;
                }
            } else {
                if (mc.player.getDistanceSq(blockPos) > MathUtils.square(this.placeRange.getValue().floatValue())) {
                    return 0;
                }
            }

            double miniumDamage;
            if (CrystalUtils.calculateDamage(blockPos, target, ignoreBlocks.getValue()) >= targetHealthPlace.getValue()) {
                facePlacing = false;
                miniumDamage = this.targetHealthBreak.getValue();
            } else if (((EntityUtils.getHealth(target) <= facePlaceHealth.getValue() && facePlace.getValue())
                || (CrystalUtils.getArmourFucker(target, facePlaceArmorDur.getValue()) && !facePlaceLogic.getMode("None").isToggled()))
                && !stopOnSword.getValue()) {
                miniumDamage = EntityUtils.isInHole(target) ? 1 : 2;
                facePlacing = true;
            } else {
                miniumDamage = this.targetHealthPlace.getValue();
                facePlacing = false;
            }

            double targetDamage = CrystalUtils.calculateDamage(blockPos, target, ignoreBlocks.getValue());
            if (targetDamage < miniumDamage && EntityUtils.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = 0;
            if (selfDamage > maxSelfDmg.getValue()) return 0;
            if (EntityUtils.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private boolean isPlayerValid(EntityPlayer player) {
        if (player.getHealth() + player.getAbsorptionAmount() <= 0 || player == mc.player) return false;
        if (FriendManager.friendsList.contains(player.getName())) return false;
        if (player.getName().equals(mc.player.getName())) return false;
        return !(player.getDistanceSq(mc.player) > 13 * 13);
    }


    private int findCrystalsHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    private void setYawPitch(EntityEnderCrystal crystal) {
        float[] angle = MathUtils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), crystal.getPositionEyes(mc.getRenderPartialTicks()));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.isRotating = true;
    }

    private void setYawPitch(BlockPos pos) {
        float[] angle = MathUtils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.isRotating = true;
    }


    public void setPlayerRotations(float yaw, float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }


    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        Color color = ColorUtils.rainbow();
        Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        if (renderBlock != null && mc.player != null) {

            if (rainbow.getValue()) {
                drawCurrentBlock(renderBlock, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            } else {

                drawCurrentBlock(renderBlock, espColor.getColor().getRed(), espColor.getColor().getGreen(), espColor.getColor().getBlue(), 255);
            }
            if(this.ezTarget != null){
                this.setExtraInfo(this.ezTarget.getName());
            }
        } else {
            this.setExtraInfo("");
        }
    });

    private void drawCurrentBlock(BlockPos render, int r, int g, int b, int a) {
        if (renderMode.getMode("Full").isToggled())
            RenderUtils.drawBlockESP(render, r / 255f, g / 255f, b / 255f, espHeight.getValue());
        else if (renderMode.getMode("Outline").isToggled())
            RenderUtils.drawOutlinedBox(AxisAligned(render), r / 255f, g / 255f, b / 255f, 1f, espHeight.getValue());
    }

    @Override
    public void onEnable() {

        placeTimeoutFlag = false;
        isRotating = false;
        ezTarget = null;
        facePlacing = false;
        attemptedCrystals.clear();
        hasPacketBroke = false;
        placeTimeoutFlag = false;
        alreadyAttacking = false;
        staticEnderCrystal = null;
        staticPos = null;
        super.onEnable();
    }


    public static class Rotation {
        Minecraft mc = Minecraft.getMinecraft();
        private float yaw;
        private float pitch;
        private String rotate;

        public Rotation(float yaw, float pitch, ModeValue rotate) {
            this.yaw = yaw;
            this.pitch = pitch;
            if (rotate.getMode("Packet").isToggled()) {
                this.rotate = "Packet";
            } else if (rotate.getMode("Client").isToggled()) {
                this.rotate = "Client";
            } else if (rotate.getMode("None").isToggled()) {
                this.rotate = "None";
            }
        }

        public Rotation(float yaw, float pitch, String rotate) {
            this.yaw = yaw;
            this.pitch = pitch;
            switch (rotate) {
                case "Packet":
                    this.rotate = "Packet";
                    break;
                case "Client":
                    this.rotate = "Client";
                    break;
                case "None":
                    this.rotate = "None";
                    break;
            }
        }

        public void updateModelRotations() {
            if (nullCheck()) {
                switch (rotate) {
                    case "Packet":
                        mc.player.renderYawOffset = yaw;
                        mc.player.rotationYawHead = yaw;
                        RotationManager.setHeadPitch(pitch);
                        break;
                    case "Client":
                        mc.player.rotationYaw = yaw;
                        mc.player.rotationPitch = pitch;
                        break;
                    case "None":
                        break;
                }
            }
        }

        public void restoreRotations() {
            if (nullCheck()) {
                yaw = mc.player.rotationYaw;
                pitch = mc.player.rotationPitch;
            }
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }

    }

    public static class CrystalPosition {

        private final BlockPos blockPos;
        private final EntityPlayer placeTarget;
        private final double targetDamage;
        private final double selfDamage;

        public CrystalPosition(BlockPos blockPos, EntityPlayer placeTarget, double targetDamage, double selfDamage) {
            this.blockPos = blockPos;
            this.placeTarget = placeTarget;
            this.targetDamage = targetDamage;
            this.selfDamage = selfDamage;
        }

        public BlockPos getPosition() {
            return blockPos;
        }

        public EntityPlayer getPlaceTarget() {
            return placeTarget;
        }

        public double getTargetDamage() {
            return targetDamage;
        }

        public double getSelfDamage() {
            return selfDamage;
        }
    }

}

class CrystalPos {

    private final BlockPos pos;
    private final Double damage;

    public CrystalPos(BlockPos pos, Double damage) {
        this.pos = pos;
        this.damage = damage;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Double getDamage() {
        return damage;
    }
}

final class Threads extends Thread {
    ThreadType type;
    BlockPos bestBlock;
    EntityEnderCrystal bestCrystal;

    public Threads(ThreadType type) {
        this.type = type;
    }

    @Override
    public void run() {

        if (this.type == ThreadType.BLOCK) {
            bestBlock = AutoCrystalRewrite.INSTANCE.getBestBlock();
            AutoCrystalRewrite.INSTANCE.staticPos = bestBlock;
        } else if (this.type == ThreadType.CRYSTAL) {
            bestCrystal = AutoCrystalRewrite.INSTANCE.getBestCrystal();
            AutoCrystalRewrite.INSTANCE.staticEnderCrystal = bestCrystal;
        }
    }

}
