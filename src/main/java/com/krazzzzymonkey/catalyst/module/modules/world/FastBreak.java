package com.krazzzzymonkey.catalyst.module.modules.world;

import com.krazzzzymonkey.catalyst.events.*;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.utils.PlayerControllerUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class FastBreak extends Modules {

    public static ModeValue modes = new ModeValue("Mode", new Mode("Instant", true), new Mode("Basic", false), new Mode("Packet", false));

    private BlockPos renderBlock;
    private BlockPos lastBlock;
    private boolean packetCancel = false;
    private long systime = System.currentTimeMillis();
    private EnumFacing direction;

    //InstantMine Values
    private final BooleanValue autoBreak = new BooleanValue("AutoBreak", true, "Auto breaks block if placed in previously mined location");
    private final IntegerValue delay = new IntegerValue("Delay", 20, 0, 500, "Delays instant mode");
    private final BooleanValue pickaxeOnly = new BooleanValue("Pickaxe Only", true, "Breaks blocks only when pickaxe is held");

    public FastBreak() {
        super("FastBreak", ModuleCategory.WORLD, "Allows you to mine blocks faster");
        this.addValue(modes, autoBreak, delay, pickaxeOnly);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (modes.getMode("Basic").isToggled()) {
            Minecraft.getMinecraft().playerController.blockHitDelay = 0;
        }
        if (modes.getMode("Instant").isToggled()) {
            if (renderBlock != null) {
                if (autoBreak.getValue().booleanValue() && systime + delay.getValue().intValue() < System.currentTimeMillis()) {
                    if (pickaxeOnly.getValue().booleanValue() && !(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.DIAMOND_PICKAXE))
                        return;
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        renderBlock, direction));
                    systime = System.currentTimeMillis();
                }

            }

            try {
                mc.playerController.blockHitDelay = 0;

            } catch (Exception ex) {
            }
        }
    });


    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (modes.getMode("Instant").isToggled()) {
            if (renderBlock != null) {
                RenderUtils.drawBlockESP(renderBlock, 255, 0, 255, 1);
            }
        }
    });

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if (event.getSide() == PacketEvent.Side.OUT) {

            if (modes.getMode("Instant").isToggled()) {
                Packet packet = event.getPacket();
                if (packet instanceof CPacketPlayerDigging) {
                    if (((CPacketPlayerDigging) packet).getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK && packetCancel)
                        event.setCancelled(true);
                }
            }
        }
    });


    @EventHandler
    private final EventListener<DamageBlockEvent> onBlockDamage = new EventListener<>(e -> {
        if (modes.getMode("Instant").isToggled()) {
            if (canBreak(e.getPos())) {
                if (lastBlock == null || e.getPos().getX() != lastBlock.getX() || e.getPos().getY() != lastBlock.getY() || e.getPos().getZ() != lastBlock.getZ()) {
                    packetCancel = false;
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, e.getPos(), e.getFacing()));
                    packetCancel = true;
                } else {
                    packetCancel = true;
                }
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, e.getPos(), e.getFacing()));

                renderBlock = e.getPos();
                lastBlock = e.getPos();
                direction = e.getFacing();

                e.setCancelled(true);

            }
        }
    });

    @EventHandler
    private final EventListener<LeftClickBlockEvent> onLeftClickBlock = new EventListener<>(e -> {
        if (modes.getMode("Packet").isToggled()) {
            float progress = PlayerControllerUtils.getCurBlockDamageMP() + BlockUtils.getHardness(e.getPos());

            if (progress >= 1) {
                return;
            }

            Wrapper.INSTANCE.sendPacket(new CPacketPlayerDigging(
                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, e.getPos(),
                Wrapper.INSTANCE.mc().objectMouseOver.sideHit));

        }
    });

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();

        return block.getBlockHardness(blockState, mc.world, pos) != -1;
    }

    public BlockPos getTarget() {
        return renderBlock;
    }

    public void setTarget(BlockPos pos) {
        renderBlock = pos;
        packetCancel = false;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
            pos, EnumFacing.DOWN));
        packetCancel = true;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
            pos, EnumFacing.DOWN));
        direction = EnumFacing.DOWN;
        lastBlock = pos;
    }

}
