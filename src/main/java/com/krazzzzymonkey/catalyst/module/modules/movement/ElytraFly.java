package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.managers.TimerManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.world.Timer;
import com.krazzzzymonkey.catalyst.utils.MathUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

public class ElytraFly extends Modules {

    boolean isHackFlying = false;
    int tickspassed = 0;
    int entryID = -1;
    public ModeValue Modes;
    public ModeValue deployMode;
    public DoubleValue velocitySpeed;
    public BooleanValue useTimer;
    public DoubleValue timer;
    public BooleanValue autoBoost;

    public ElytraFly() {
        super("ElytraFly", ModuleCategory.MOVEMENT, "Better Elytra Flying");
        Modes = new ModeValue("Mode", new Mode("CFly", true), new Mode("Packet", false), new Mode("Strict", false));
        autoBoost = new BooleanValue("AutoBoost", false, "Automatically use firework rockets");
        deployMode = new ModeValue("DeploymentMode", new Mode("NotOnGround", true), new Mode("VerticalVelocity", false), new Mode("ElytraFlying", false));
        velocitySpeed = new DoubleValue("Speed", 1.8f, 0f, 10f, "Fly speed");
        useTimer = new BooleanValue("TimerTakeoff", false, "Slows down your game when taking off");
        timer = new DoubleValue("TimerMultiplier", 0.5, 0.1, 1.5, "How much the game should be slowed down when trying to take off");

        this.addValue(Modes, deployMode, autoBoost, velocitySpeed, useTimer, timer);
    }

    @Override
    public void onEnable() {
        if(mc.player==null)return;
        if (Modes.getMode("CFly").isToggled()) {
            mc.player.capabilities.setFlySpeed(velocitySpeed.getValue().floatValue()/35);
        }
        entryID = TimerManager.addTimerMultiplier(1, 2);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.player == null) return;
        isHackFlying = false;
        mc.player.capabilities.isFlying = false;
        ElytraFly.mc.player.capabilities.setFlySpeed(0.05f);
        if (Modes.getMode("Packet").isToggled()) {
            ElytraFly.mc.player.capabilities.isFlying = false;
            ElytraFly.mc.player.capabilities.setFlySpeed(0.05f);
            if (!ElytraFly.mc.player.capabilities.isCreativeMode) {
                ElytraFly.mc.player.capabilities.allowFlying = false;
            }
        }
        TimerManager.removeTimerMultiplier(entryID);
        super.onDisable();
    }

    int i = 0;
    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {

        if (mc.player == null) return;

        final ItemStack chestplateSlot = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!(chestplateSlot.getItem() == Items.ELYTRA)) return;


        if (Modes.getMode("CFly").isToggled()) {
            mc.player.capabilities.setFlySpeed(velocitySpeed.getValue().floatValue()/35);
        }


        if (mc.player.onGround) isHackFlying = false;
        if(!mc.player.onGround && useTimer.getValue()) {
            TimerManager.getMultiplier(entryID).setMultiplier(timer.getValue());
            TimerManager.getMultiplier(entryID).setEnabled(true);
        }
        if((isHackFlying || mc.player.onGround) && useTimer.getValue()){
            TimerManager.getMultiplier(entryID).setEnabled(false);
        }


        //CHECK IF THE CLIENT SHOULD START HACK FLYING
        if (deployMode.getMode("NotOnGround").isToggled() && !isHackFlying) {
            if (!mc.player.onGround) {
                isHackFlying = true;
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                mc.player.capabilities.isFlying = true;
            }
        } else if (mc.player.motionY < -0.15 && deployMode.getMode("VerticalVelocity").isToggled() && !isHackFlying && !mc.player.isElytraFlying()) {
            isHackFlying = true;
            mc.player.setVelocity(mc.player.motionX, 0, mc.player.motionZ);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            mc.player.capabilities.isFlying = true;

        } else if (mc.player.isElytraFlying() && !isHackFlying) {
            isHackFlying = true;
            mc.player.capabilities.isFlying = true;

        }


        //SPAWN ROCKETS IF NON ARE PRESENT
        if (autoBoost.getValue().booleanValue() && isHackFlying) {
            if (mc.world.loadedEntityList.stream().filter(n -> n instanceof EntityFireworkRocket).noneMatch(n -> ((EntityFireworkRocket) n).boostedEntity == mc.player)) {
                tickspassed++;
                if (tickspassed > 5) {
                    tickspassed = 0;
                    int oldslot = mc.player.inventory.currentItem;
                    int newslot = getSlotWithRockets();
                    if (newslot != -1) {
                        mc.player.inventory.currentItem = newslot;
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(newslot));
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    } else {
                        ChatUtils.normalMessage("No rockets found in hotbar, disabling AutoBoost");
                        autoBoost.setValue(false);
                    }
                    mc.player.inventory.currentItem = oldslot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
                }
            }
        }


        if (Modes.getMode("Packet").isToggled() && isHackFlying) {
            if (ElytraFly.mc.player.capabilities.isFlying || ElytraFly.mc.player.isElytraFlying()) {
                ElytraFly.mc.player.setSprinting(false);
            }

            if (ElytraFly.mc.player.capabilities.isFlying) {
                ElytraFly.mc.player.setVelocity(0.0, 0.0, 0.0);
                ElytraFly.mc.player.setPosition(ElytraFly.mc.player.posX, ElytraFly.mc.player.posY - 5.0000002374872565E-5, ElytraFly.mc.player.posZ);
                ElytraFly.mc.player.capabilities.setFlySpeed(this.velocitySpeed.getValue().floatValue());
                ElytraFly.mc.player.setSprinting(false);
            }
            if (ElytraFly.mc.player.onGround) {
                ElytraFly.mc.player.capabilities.allowFlying = false;
            }
            if (ElytraFly.mc.player.isElytraFlying()) {
                ElytraFly.mc.player.capabilities.setFlySpeed(0.915f);
                ElytraFly.mc.player.capabilities.isFlying = true;
                if (!ElytraFly.mc.player.capabilities.isCreativeMode) {
                    ElytraFly.mc.player.capabilities.allowFlying = true;
                }
            }
            if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
                ElytraFly.mc.player.capabilities.setFlySpeed(0.05f);
            }

        }

        if (Modes.getMode("Strict").isToggled() && isHackFlying) {
            final double deltaX = Minecraft.getMinecraft().player.posX - Minecraft.getMinecraft().player.prevPosX;
            final double deltaZ = Minecraft.getMinecraft().player.posZ - Minecraft.getMinecraft().player.prevPosZ;
            final double tickRate = (Minecraft.getMinecraft().timer.tickLength / 1000.0f);
            final double bps = (MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) / tickRate);
            mc.player.capabilities.isFlying = false;
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                if (i > 50) {
                    i = 0;
                }
                if (bps > 12) {
                    i--;
                    if (i < -30) {
                        i = -30;
                    }
                    mc.player.rotationPitch = (float) i;

                } else {
                    i++;
                    if (i > 20) {
                        i = 20;
                    }
                    mc.player.rotationPitch = (float) i;
                }
                mc.player.movementInput.forwardKeyDown = true;

            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                i = i + 2;
                if (i > 80) {
                    i = 80;
                }
                mc.player.rotationPitch = (float) i;

                /*mc.player.rotationPitch = (float) -1;
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.getPitchYaw().y, -1, false));*/
            }
            if (mc.player.rotationPitch > 0) {
                mc.player.motionX = 0.0;
                mc.player.motionZ = 0.0;
                mc.player.motionY = 0.0f;

                if (mc.player.movementInput.sneak) {
                    mc.player.motionY = -0.8f;
                    mc.player.movementInput.forwardKeyDown = true;
                }

                if (mc.player.movementInput.forwardKeyDown && !mc.gameSettings.keyBindForward.isKeyDown()) {
                    mc.player.movementInput.forwardKeyDown = false;
                }

                final double[] directionSpeedPacket = MathUtils.directionSpeed(velocitySpeed.getValue().floatValue());
                if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
                    mc.player.motionX = directionSpeedPacket[0];
                    mc.player.motionZ = directionSpeedPacket[1];
                }
            }

        }
    });

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if (e.getSide() == PacketEvent.Side.OUT) {

            if (mc.player == null) return;
            if (Modes.getMode("Packet").isToggled() && isHackFlying)
                if (e.getPacket() instanceof CPacketPlayer.PositionRotation) {
                    CPacketPlayer.PositionRotation packet = (CPacketPlayer.PositionRotation) e.getPacket();
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(packet.x, packet.y, packet.z, packet.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(packet.yaw, 0, packet.onGround));
                    e.setCancelled(true);

                }
        }
    });

    private void runNoKick() {
        if (!mc.player.isElytraFlying() && mc.player.ticksExisted % 4 == 0) {
            mc.player.motionY = -0.03999999910593033;
        }
    }

    private int getSlotWithRockets() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.FIREWORKS) {
                return i;
            }
        }
        return -1;
    }

}
