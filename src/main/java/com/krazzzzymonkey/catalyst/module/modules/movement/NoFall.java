package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.PlayerMoveEvent;
import com.krazzzzymonkey.catalyst.events.PlayerUpdateEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.managers.TimerManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.world.Timer;
import com.krazzzzymonkey.catalyst.utils.InventoryUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class NoFall extends Modules {

    public static ModeValue mode = new ModeValue("Mode", new Mode("Elytra", true), new Mode("Packet", false));
    public static IntegerValue minFallDistance = new IntegerValue("FallDistance", 3, 1, 255, "How far the player must fall before any NoFall options will be triggered");
    //Elytra Mode Settings, put these into their own submenu
    public static ModeValue eDetectionMode = new ModeValue("ElytraTriggerMode", new Mode("CollisionSim", false), new Mode("RayTrace", true));
    public static IntegerValue eTriggerHeight = new IntegerValue("TriggerHeight", 10, 1, 60, "Specifies the distance to the ground at which your elytra will be deployed");
    public static BooleanValue eEquipElytra = new BooleanValue("EquipElytra", false, "Allows the client to replace your chestplate with an elytra while falling");
    public static BooleanValue eUseTimer = new BooleanValue("UseTimer", false, "Uses timer to slow down your game before hitting the ground");

    SubMenu eSubMenu = new SubMenu("Elytra", eDetectionMode, eTriggerHeight, eEquipElytra, eUseTimer);

    public NoFall() {
        super("NoFall", ModuleCategory.MOVEMENT, "Prevents you from taking fall damage");
        addValue(mode, minFallDistance, eSubMenu);
    }

    boolean sentPacket = false;
    private int teleportId;
    private ArrayList<CPacketPlayer> packets = new ArrayList<>();
    private double oldTimer = 0f;
    private static int entryID = -1;

    @Override
    public void onEnable(){
        entryID = TimerManager.addTimerMultiplier(1, 3, false);
        super.onEnable();
    }

    @Override
    public void onDisable(){
        TimerManager.removeTimerMultiplier(entryID);
        super.onDisable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(event -> {
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.onGround && mc.player.fallDistance > minFallDistance.getValue()) {
            if (mode.getMode("Elytra").isToggled()) {
                if (eDetectionMode.getMode("RayTrace").isToggled()) {
                    //ChatUtils.normalMessage("Using RayTrace");
                    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ), new Vec3d(mc.player.posX, -64, mc.player.posZ));
                    assert result != null;
                    if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                        int landingHeight = result.getBlockPos().getY();
                        //ChatUtils.normalMessage(mc.player.posY - landingHeight + "");
                        if (mc.player.posY - landingHeight < eTriggerHeight.getValue() && !sentPacket) {
                            deploy();
                        }
                    }
                }
                if (eDetectionMode.getMode("CollisionSim").isToggled()) {
                    for (int i = 0; i < eTriggerHeight.getValue(); i++) {
                        boolean collided = mc.world.checkBlockCollision(mc.player.getEntityBoundingBox().offset(0, -i, 0));
                        if (collided && !sentPacket) {
                            deploy();
                            break;
                        }
                    }
                }
            }
        }
        if (mc.player.onGround && sentPacket) {
            if (eUseTimer.getValue()) {
                TimerManager.getMultiplier(entryID).setEnabled(false);
            }
            sentPacket = false;
        }
    });

    @EventHandler
    private final EventListener<PlayerUpdateEvent> onPlayerUpdate = new EventListener<>(event -> {
        if (!mode.getMode("Packet").isToggled()) return;
        if (mc.player.fallDistance > minFallDistance.getValue()) {
            if (teleportId <= 0) {
                // sending this without any other packets will probs cause server to send SPacketPlayerPosLook to fix our pos
                CPacketPlayer boundsPos = new CPacketPlayer.Position(NewPacketFly.randomHorizontal(), 1, NewPacketFly.randomHorizontal(), mc.player.onGround);
                packets.add(boundsPos);
                mc.player.connection.sendPacket(boundsPos);
            } else {
                CPacketPlayer nextPos = new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.062, mc.player.posZ, mc.player.onGround);
                packets.add(nextPos);
                mc.player.connection.sendPacket(nextPos);

                CPacketPlayer downPacket = new CPacketPlayer.Position(mc.player.posX, 1, mc.player.posZ, mc.player.onGround);
                packets.add(downPacket);
                mc.player.connection.sendPacket(downPacket);

                teleportId++;

                mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportId - 1));
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportId));
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportId + 1));
            }
        }
    });


    @EventHandler
    private final EventListener<PacketEvent> onPacketRecieve = new EventListener<>(event -> {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (mode.getMode("Packet").isToggled()
            && mc.player.fallDistance > minFallDistance.getValue()
            && event.getPacket() instanceof SPacketPlayerPosLook) {
            if (!(mc.currentScreen instanceof GuiDownloadTerrain)) {
                if (mc.player.isEntityAlive()) {
                    if (this.teleportId <= 0) {
                        this.teleportId = ((SPacketPlayerPosLook) event.getPacket()).getTeleportId();
                    } else {
                        SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                        packet.yaw = mc.player.rotationYaw;
                        packet.pitch = mc.player.rotationPitch;
                    }
                }
            } else {
                teleportId = 0;
            }
        }
    });


    @EventHandler
    private final EventListener<PlayerMoveEvent> onPlayerMove = new EventListener<>(event -> {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (mode.getMode("Packet").isToggled()
            && mc.player.fallDistance > minFallDistance.getValue() && !mc.player.onGround) {
            event.x = 0;
            event.y = (float) -0.062;
            event.z = 0;
        }
    });


    public void deploy() {
        if (eUseTimer.getValue()) {
            TimerManager.getMultiplier(entryID).setMultiplier(0.5d);
            TimerManager.getMultiplier(entryID).setEnabled(true);
        }
        //mc.playerController.windowClick(0, 30, 0, ClickType.THROW, mc.player);
        if (eEquipElytra.getValue()) {
            if (mc.player.inventory.getStackInSlot(38).getItem() != Items.ELYTRA && !mc.player.inventory.getStackInSlot(38).isEmpty) {
                mc.playerController.windowClick(0, 6, 0, ClickType.QUICK_MOVE, mc.player);
                return;
            } else if (mc.player.inventory.getStackInSlot(38).isEmpty) {
                int newslot = InventoryUtils.getItemSlot(Items.ELYTRA, InventoryUtils.Inventory.INVENTORY, true);
                mc.playerController.windowClick(0, newslot, 0, ClickType.QUICK_MOVE, mc.player);
                sentPacket = true;
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                return;
            }
        }
        sentPacket = true;
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    }

}
