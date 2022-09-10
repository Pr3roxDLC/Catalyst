package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Burrow extends Modules {

    public static long timeSinceLastBurrow = 0L;
    public static BooleanValue onSneak;
    public static DoubleValue offset;
    public static IntegerValue delay;
    public static ModeValue modes;
    public static BooleanValue rotate;
    public static BooleanValue zeroBeeBypass;

    private int slot = 0;
    private BlockPos lastBurrowPos = null;
    private long lastBurrowTime = 0l;


    public Burrow() {
        super("Burrow", ModuleCategory.COMBAT, "Places a Block in your feet");
        onSneak = new BooleanValue("OnSneak", true, "Only activate on sneak");
        delay = new IntegerValue("Delay", 10, 0, 200, "Delay between burrows on sneak");
        modes = new ModeValue("Mode", new Mode("Obsidian", true), new Mode("EChest", false), new Mode("Both", false), new Mode("MainHand", false));
        offset = new DoubleValue("Offset", 3d, -5d, 5d, "The height for the packet that glitches you in the block");
        rotate = new BooleanValue("Rotate", true, "Send rotation packets to the server");
        zeroBeeBypass = new BooleanValue("0bBypass", false, "Prevents you from taking damage when burrowing");
        addValue(modes, onSneak, rotate, delay, offset, zeroBeeBypass);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {

        if (mc.player == null) return;

        timeSinceLastBurrow++;

        if (onSneak.getValue() && mc.player.isSneaking() && timeSinceLastBurrow > delay.getValue().longValue()) {

            BlockPos originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            lastBurrowPos = originalPos;
            lastBurrowTime = System.currentTimeMillis();

            slot = getSlot();
            if (slot < 0) return;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821, mc.player.posZ, true));
            BlockUtils.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(), mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            mc.player.setSneaking(false);
            timeSinceLastBurrow = 0;


        }

        if (!onSneak.getValue()) {

            BlockPos originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            lastBurrowPos = originalPos;
            lastBurrowTime = System.currentTimeMillis();

            slot = getSlot();
            if (slot < 0) return;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821, mc.player.posZ, true));
            BlockUtils.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(), mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            mc.player.setSneaking(false);
            timeSinceLastBurrow = 0;
            this.setToggled(false);

        }

    });


    @EventHandler
    private final EventListener<PacketEvent> onPacketSend = new EventListener<>(e -> {
        if (e.getSide() == PacketEvent.Side.OUT) {
            if(!zeroBeeBypass.getValue() || lastBurrowPos == null)return;
            if(System.currentTimeMillis() - lastBurrowTime > 500){
                if(e.getPacket() instanceof CPacketPlayer){
                    CPacketPlayer cPacketPlayer = (CPacketPlayer) e.getPacket();
                    if(cPacketPlayer.onGround && mc.world.collidesWithAnyBlock(mc.player.getEntityBoundingBox())){
                        e.setCancelled(true);
                    }
                }
            }
        }
    });

    @Override
    public void onEnable() {

        super.onEnable();
        timeSinceLastBurrow = 0L;

    }

    @Override
    public void onDisable() {
        super.onDisable();
        timeSinceLastBurrow = 0L;

    }


    public int getSlot() {
        if (modes.getMode("EChest").isToggled()) return getSlotWithBlock(BlockEnderChest.class);
        if (modes.getMode("Obsidian").isToggled()) return getSlotWithBlock(BlockObsidian.class);
        if (modes.getMode("Both").isToggled()) {
            int slot1 = getSlotWithBlock(BlockEnderChest.class);
            int slot2 = getSlotWithBlock(BlockObsidian.class);
            return slot1 == -1 ? slot2 : slot1;
        }
        if (modes.getMode("MainHand").isToggled()) return mc.player.inventory.currentItem;
        throw new UnsupportedOperationException("No mode active");
    }

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
