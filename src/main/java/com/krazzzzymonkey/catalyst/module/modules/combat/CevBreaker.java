package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.world.FastBreak;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.utils.EntityUtils;
import com.krazzzzymonkey.catalyst.utils.PlayerControllerUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class CevBreaker extends Modules {


    DoubleValue range = new DoubleValue("Range", 5.0f, 1.0f, 6.0f, "");
    ModeValue blockPlaceMode = new ModeValue("Placement", new Mode("Basic", false), new Mode("Legit", true), new Mode("NoRotate", false));
    ModeValue towerPlacement = new ModeValue("TowerPlacement", new Mode("Near", false), new Mode("Far", true));
    IntegerValue crystalTries = new IntegerValue("CrystalTries", 10, 1, 40, "");
    ColorValue targetColor = new ColorValue("TargetColor", Color.CYAN, "");
//    ModeValue target = new ModeValue("Target", new Mode("Distance", false), new Mode("Rotation", true));
//    ModeValue breakCrystal = new ModeValue("Break Crystal", new Mode("Attack", false), new Mode("Packet", false), new Mode("None", false));
//    BooleanValue breakBlock = new BooleanValue("PacketBreak", false);
//    DoubleValue enemyRange = new DoubleValue("Range", 4.9, 0, 6);
//    IntegerValue preRotationDelay = new IntegerValue("Pre Rotation Delay", 0, 0, 20);
//    IntegerValue afterRotationDelay = new IntegerValue("After Rotation Delay", 0, 0, 20);
//    IntegerValue supDelay = new IntegerValue("Support Delay", 1, 0, 4);
//    IntegerValue crystalDelay = new IntegerValue("Crystal Delay", 2, 0, 20);
//    IntegerValue blocksPerTick = new IntegerValue("Blocks Per Tick", 4, 2, 6);
//    IntegerValue hitDelay = new IntegerValue("Hit Delay", 2, 0, 20);
//    IntegerValue midHitDelay = new IntegerValue("Mid Hit Delay", 1, 0, 20);
//    IntegerValue endDelay = new IntegerValue("End Delay", 1, 0, 20);
//    IntegerValue pickSwitchTick = new IntegerValue("Pick Switch Tick", 100, 0, 500);
//    BooleanValue rotate = new BooleanValue("Rotate", false);
//    BooleanValue confirmBreak = new BooleanValue("No Glitch Break", true);
//    BooleanValue confirmPlace = new BooleanValue("No Glitch Place", true);
//    BooleanValue antiWeakness = new BooleanValue("Anti Weakness", false);
//    BooleanValue switchSword = new BooleanValue("Switch Sword", false);
//    BooleanValue fastPlace = new BooleanValue("Fast Place", false);
//    BooleanValue fastBreak = new BooleanValue("Fast Break", true);
//    BooleanValue trapPlayer = new BooleanValue("Trap Player", false);
//    BooleanValue antiStep = new BooleanValue("Anti Step", false);
//    BooleanValue placeCrystal = new BooleanValue("Place Crystal", true);
//    BooleanValue forceRotation = new BooleanValue("Force Rotation", false);
//    BooleanValue forceBreaker = new BooleanValue("Force Breaker", false);

    public CevBreaker() {
        super("CevBreaker", ModuleCategory.COMBAT, "Allows you to Break your enemies armor faster");
        addValue(range, blockPlaceMode,towerPlacement, crystalTries, targetColor);
    }

    public enum Phase{START,PLACE, DIG, ATTACK}
    public Phase currentPhase = Phase.PLACE;
    public static EnumFacing[] facings = {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST};
    public static ArrayList<Entity> targets = new ArrayList<>();
    private Entity globalTarget = null;
    private final Entity crystal = null;
    boolean sentPlacePacket = false;
    boolean startedDigging = false;
    int counter = 0;


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (mc.player == null || mc.world == null) return;
        targets = (ArrayList<Entity>) mc.world.loadedEntityList.stream().filter(n -> n instanceof EntityPlayer).filter(n -> n != mc.player).filter(n -> n.getDistance(mc.player) < range.getValue().doubleValue()).collect(Collectors.toList());
        if (!targets.isEmpty()) {
            Optional<Entity> closest = Optional.of(targets.stream().min(Comparator.comparingDouble(entity -> entity.getDistance(mc.player))).get());

            int slot = -1;
            //Tower/Obsidian Placement LOGIC
            if (!isTowerPresent(new BlockPos(closest.get().posX, closest.get().posY, closest.get().posZ))) {
                slot = PlayerControllerUtils.findObiInHotbar();
                if (slot != -1) {
                    mc.player.inventory.currentItem = slot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                }else{
                    return;
                }
                closest.ifPresent(this::placeTower);
            } else if(currentPhase == Phase.PLACE){
                slot = PlayerControllerUtils.findObiInHotbar();
                if (slot != -1) {
                    mc.player.inventory.currentItem = slot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                }else{
                    return;
                }
                tryPlaceBlock(new BlockPos(closest.get().posX, closest.get().posY, closest.get().posZ).up(2));
            }

            globalTarget = closest.get();

            //Place Crystal (Highly WIP)
            if (currentPhase == Phase.PLACE) {
            if(counter < crystalTries.getValue()) {
                slot = getSlotWithBlock(ItemEndCrystal.class);
                if (slot != -1) {

                    mc.player.inventory.currentItem = slot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    //BlockUtils.faceBlockPacket(new BlockPos(closest.get().posX, closest.get().posY, closest.get().posZ).up(2));

                    counter++;
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(new BlockPos(closest.get().posX, closest.get().posY, closest.get().posZ).up(2), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                    sentPlacePacket = true;
                    return;
                }

            }else{
                counter = 0;
                currentPhase = Phase.DIG;
            }
            }

            if(currentPhase == Phase.DIG){


                slot = getSlotWithBlock(ItemPickaxe.class);
                if(slot != -1){
                    mc.player.inventory.currentItem = slot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                }
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK,new BlockPos(closest.get().posX, closest.get().posY, closest.get().posZ).up(2), EnumFacing.DOWN ));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,new BlockPos(closest.get().posX, closest.get().posY, closest.get().posZ).up(2), EnumFacing.DOWN ));
                currentPhase = Phase.ATTACK;
                return;
            }

            if(currentPhase == Phase.ATTACK && mc.world.getBlockState(new BlockPos(closest.get().posX, closest.get().posY, closest.get().posZ).up(2)).getBlock() == Blocks.AIR){


                mc.world.loadedEntityList.stream().filter(n -> n instanceof EntityEnderCrystal).min(Comparator.comparingDouble(entity -> entity.getDistance(mc.player))).ifPresent(n -> EntityUtils.attackEntity(n, true, false));
                currentPhase = Phase.PLACE;
            }

        }


    });


    public static int getSlotWithBlock(final Class clazz) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock) stack.getItem()).getBlock();
                    if (clazz.isInstance(block)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (globalTarget != null) {
            RenderUtils.drawBlockESP(new BlockPos(globalTarget.posX, globalTarget.posY, globalTarget.posZ).up(2), targetColor.getColor().getRed()/255f, targetColor.getColor().getGreen()/255f, targetColor.getColor().getBlue()/255f, 1);
        }
    });

    public Optional<EnumFacing> getClosestOffsetForTower(BlockPos pos) {
        if (towerPlacement.getMode("Near").isToggled())
            return Arrays.stream(facings).min(Comparator.comparingDouble(n -> Math.sqrt(pos.offset(n).distanceSq(mc.player.posX, mc.player.posY, mc.player.posZ))));
        else
            return Arrays.stream(facings).max(Comparator.comparingDouble(n -> Math.sqrt(pos.offset(n).distanceSq(mc.player.posX, mc.player.posY, mc.player.posZ))));

    }

    public void placeTower(Entity target) {
        Optional<EnumFacing> closestFacing = getClosestOffsetForTower(target.getPosition());
        closestFacing.ifPresent(facing -> {
            BlockPos targetPos = new BlockPos(target.posX, target.posY, target.posZ);
            tryPlaceBlock(targetPos.offset(facing));
            tryPlaceBlock(targetPos.offset(facing).up());
            tryPlaceBlock(targetPos.offset(facing).up(2));
            tryPlaceBlock(targetPos.up(2));
        });
    }

    public boolean isTowerPresent(BlockPos pos) {
        return Arrays.stream(facings).anyMatch(n -> mc.world.getBlockState(pos.up(2).offset(n)).getBlock() == Blocks.OBSIDIAN);
    }

    public void tryPlaceBlock(BlockPos pos) {
        if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {

            if (blockPlaceMode.getMode("Legit").isToggled()) {
                BlockUtils.placeBlockLegit(pos);
                return;
            }
            if (blockPlaceMode.getMode("Basic").isToggled()) {
                BlockUtils.placeBlock(pos, EnumHand.MAIN_HAND, true, false, false);
                return;
            }
            if (blockPlaceMode.getMode("NoRotate").isToggled()) {
                BlockUtils.placeBlock(pos, EnumHand.MAIN_HAND, false, false, false);
                return;
            }
        }
    }

}


