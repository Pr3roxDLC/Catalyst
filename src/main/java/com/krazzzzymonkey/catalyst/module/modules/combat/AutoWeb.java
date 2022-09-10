package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoWeb extends Modules {

    public ModeValue mode = new ModeValue("Mode", new Mode("Always", true), new Mode("InHole", false));
    public BooleanValue rotate = new BooleanValue("Rotate", true, "Sends rotation packets to the server");
    public BooleanValue Double = new BooleanValue("DoubleHeight", true, "Places two webs on the player");
    public BooleanValue multi = new BooleanValue("Multi", true, "Allows for multiple targets");
    private final DoubleValue range = new DoubleValue("Range", 5D, 0D, 10D, "The maximum range for the target to get auto webbed");
    private final BooleanValue antiSelf = new BooleanValue("AntiSelf", true, "Stops auto web from webbing someone close to you");
    private final DoubleValue antiSelfDistance = new DoubleValue("AntiSelfDistance", 1.5, 1d, 5d, "The distance for for anti self");
    private final BooleanValue selfWeb = new BooleanValue("SelfWeb", false, "Webs yourself");
    private final BooleanValue doubleSelf = new BooleanValue("DoubleSelfWeb", false, "Places two webs on yourself");

    public AutoWeb() {
        super("AutoWeb", ModuleCategory.COMBAT, "Places webs in your enemies");
        addValue(mode, rotate, range, Double, antiSelf, antiSelfDistance, selfWeb, doubleSelf);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {

        if (mc.world == null) return;

        if (selfWeb.getValue()) {
            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR) return;
            if (getSlotWithBlock(BlockWeb.class) == -1) return;
            int oldslot = mc.player.inventory.currentItem;
            int slot = getSlotWithBlock(BlockWeb.class);
            if (slot < 0) return;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            BlockUtils.placeBlockScaffold(pos, rotate.getValue());
            if (doubleSelf.getValue()) {
                BlockUtils.placeBlockScaffold(pos.up(), rotate.getValue());
            }
            mc.player.inventory.currentItem = oldslot;
        }

        mc.world.loadedEntityList.stream()
            .filter(n -> n instanceof EntityPlayer)
            .filter(n -> mc.player.getDistance(n) <= range.getValue())
            .filter(n -> !FriendManager.friendsList.contains(n.getName()))
            .filter(n -> n != mc.player).map(n -> new BlockPos(n.posX, n.posY, n.posZ))
            .filter(n -> mc.world.getBlockState(n).getBlock() == Blocks.AIR)
            .forEach(n -> {
                if (mode.getMode("Always").isToggled()) {
                    if (n.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= antiSelfDistance.getValue())
                        return;
                    if (getSlotWithBlock(BlockWeb.class) == -1) return;
                    int oldslot = mc.player.inventory.currentItem;
                    int slot = getSlotWithBlock(BlockWeb.class);
                    if (slot < 0) return;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    mc.player.inventory.currentItem = slot;
                    BlockUtils.placeBlockScaffold(n, rotate.getValue());
                    if (Double.getValue()) {
                        BlockUtils.placeBlockScaffold(n.up(), rotate.getValue());
                    }
                    mc.player.inventory.currentItem = oldslot;
                    if (!multi.getValue()) {
                        return;
                    }
                } else if (mode.getMode("InHole").isToggled() &&
                    (mc.world.getBlockState(n.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(n.west()).getBlock() == Blocks.BEDROCK) &&
                    (mc.world.getBlockState(n.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(n.east()).getBlock() == Blocks.BEDROCK) &&
                    (mc.world.getBlockState(n.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(n.north()).getBlock() == Blocks.BEDROCK) &&
                    (mc.world.getBlockState(n.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(n.west()).getBlock() == Blocks.BEDROCK)) {
                    if (n.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= antiSelfDistance.getValue())
                        return;
                    if (getSlotWithBlock(BlockWeb.class) == -1) return;
                    int oldslot = mc.player.inventory.currentItem;
                    int slot = getSlotWithBlock(BlockWeb.class);
                    if (slot < 0) return;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    mc.player.inventory.currentItem = slot;
                    BlockUtils.placeBlockScaffold(n, rotate.getValue());
                    if (Double.getValue()) {
                        BlockUtils.placeBlockScaffold(n.up(), rotate.getValue());
                    }
                    mc.player.inventory.currentItem = oldslot;
                    if (!multi.getValue()) {
                        return;
                    }

                }
            });


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

}
