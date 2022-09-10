package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class ChorusControl extends Modules {

    public BooleanValue forceStop = new BooleanValue("ForceStop", false, "Force the player to remain in the same position while selecting tp locations");
    public ColorValue targetColor = new ColorValue("TargetColor", Color.CYAN, "The color of the Teleportation Target");

    public ChorusControl() {
        super("ChorusControl", ModuleCategory.MISC, "Allows you to keep eating chorus fruits until a desired position is reached");
        addValue(forceStop, targetColor);
    }


    BlockPos target = null;
    BlockPos origin = null;
    SPacketPlayerPosLook posLook = null;
    boolean flag = false;
    int ticksSinceTP = 0;
    int id = -999;


    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if (mc.player == null) return;
        if (event.getPacket() instanceof CPacketPlayerTryUseItem && mc.player.isHandActive() && mc.player.activeItemStack.getItem() == Items.CHORUS_FRUIT) {
            for (int i = 0; i != 37; ++i) {
                mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
            }
            mc.player.stopActiveHand();
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && mc.player.isHandActive() && mc.player.activeItemStack.getItem() == Items.CHORUS_FRUIT) {
            if (!mc.player.isSneaking()) return;
            SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
            origin = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            target = new BlockPos(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY(), sPacketPlayerPosLook.getZ());
            posLook = sPacketPlayerPosLook;
            flag = true;
            id = sPacketPlayerPosLook.getTeleportId();
            event.setCancelled(true);
        }
        if (event.getSide() == PacketEvent.Side.OUT && flag) {
            if (event.getPacket() instanceof CPacketKeepAlive) {
                return;
            }
            if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
                return;
            }
            if (event.getPacket() instanceof CPacketEntityAction) {
                return;
            }
            event.setCancelled(true);
        }

    });


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (mc.player == null) return;

        if(!mc.player.isSneaking() && target != null && posLook != null){
            mc.player.setPositionAndRotation(posLook.getX(), posLook.getY(), posLook.getZ(), posLook.getYaw(), posLook.getPitch());
            target = null;
            posLook = null;
        }

        if (!flag) ticksSinceTP++;
        if (!mc.player.isSneaking() && flag) {
            flag = false;
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(id));
            ticksSinceTP = 0;
        }
        if (forceStop.getValue() && flag && mc.player.isSneaking()) {
            mc.player.setPosition(origin.getX(), origin.getY(), origin.getZ());
        }
    });

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (target == null) return;
        RenderUtils.drawBlockESP(target, targetColor.getColor().getRed() / 255f, targetColor.getColor().getGreen() / 255f, targetColor.getColor().getBlue() / 255f, 1);
    });


    @Override
    public void onDisable() {
        super.onDisable();
        target = null;
        origin = null;
        flag = false;
    }
}
