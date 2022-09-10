package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.AddCollisionBoxToListEvent;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.EntityUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.krazzzzymonkey.catalyst.utils.EntityUtils.isAboveBlock;
import static com.krazzzzymonkey.catalyst.utils.Utils.isPlayer;

//TODO JUST FIX

public class Jesus extends Modules {

    public static ModeValue mode;

    public Jesus() {
        super("Jesus", ModuleCategory.MOVEMENT, "Automatically swims for you");

        mode = new ModeValue("Mode", new Mode("Solid", true), new Mode("Jump", false), new Mode("Dolphin", false), new Mode("Fish", false));

        this.addValue(mode);
    }

    private static final AxisAlignedBB WATER_WALK_AA = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.99, 1.0);

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {

        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (!Wrapper.INSTANCE.player().isInWater() && !Wrapper.INSTANCE.player().isInLava()) {
            return;
        }
        if (Wrapper.INSTANCE.player().isSneaking() || Wrapper.INSTANCE.mcSettings().keyBindJump.isKeyDown()) {
            return;
        }
        if (mode.getMode("Jump").isToggled()) {
            Wrapper.INSTANCE.player().jump();
        } else if (mode.getMode("Dolphin").isToggled()) {
            Wrapper.INSTANCE.player().motionY += 0.04f;
        } else if (mode.getMode("Fish").isToggled()) {
            Wrapper.INSTANCE.player().motionY += 0.02f;
        } else if (mode.getMode("Solid").isToggled()) {
            if (!ModuleManager.getModule("Freecam").isToggled() && isInLiquid() && !Jesus.mc.player.isSneaking()) {
                Jesus.mc.player.motionY = 0.1;
                if (Jesus.mc.player.getRidingEntity() != null && !(Jesus.mc.player.getRidingEntity() instanceof EntityBoat)) {
                    Jesus.mc.player.getRidingEntity().motionY = 0.3;
                }
            }

        }

    });


    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if(event.getSide() == PacketEvent.Side.OUT) {

            if (mode.getMode("Solid").isToggled()) {
                Packet packet = event.getPacket();
                if (packet instanceof CPacketPlayer && isAboveWater(Jesus.mc.player, true) && !isInWater(Jesus.mc.player) && !isAboveLand(Jesus.mc.player)) {
                    final int ticks = Jesus.mc.player.ticksExisted % 2;
                    if (ticks == 0) {
                        final CPacketPlayer cPacketPlayer = (CPacketPlayer) packet;
                        cPacketPlayer.y += 0.02;
                    }
                }
            }
        }
    });



    @EventHandler
    private final EventListener<AddCollisionBoxToListEvent> onCollision = new EventListener<>(event -> {
        if (mode.getMode("Solid").isToggled()) {
            if (Jesus.mc.player != null && event.getBlock() instanceof BlockLiquid && (EntityUtils.isDrivenByPlayer(event.getEntity()) || event.getEntity() == Jesus.mc.player) && !(event.getEntity() instanceof EntityBoat) && !Jesus.mc.player.isSneaking() && Jesus.mc.player.fallDistance < 3.0f && !isInWater(Jesus.mc.player) && (isAboveWater(Jesus.mc.player, false) || isAboveWater(Jesus.mc.player.getRidingEntity(), false)) && isAboveBlock(Jesus.mc.player, event.getPos())) {
                final AxisAlignedBB axisalignedbb = Jesus.WATER_WALK_AA.offset(event.getPos());
                if (event.getEntityBox().intersects(axisalignedbb)) {
                    event.getCollidingBoxes().add(axisalignedbb);
                }
                event.setCancelled(true);
            }
        }
    });

    public static boolean isAboveWater(final Entity entity, final boolean packet) {
        if (entity == null) {
            return false;
        }
        final double y = entity.posY - (packet ? 0.03 : (isPlayer(entity) ? 0.2 : 0.5));
        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInWater(Entity entity) {
        if (entity == null) return false;

        double y = entity.posY + 0.01;

        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); x++)
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); z++) {
                BlockPos pos = new BlockPos(x, (int) y, z);

                if (Wrapper.INSTANCE.world().getBlockState(pos).getBlock() instanceof BlockLiquid) return true;
            }

        return false;
    }

    public static boolean isInLiquid() {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }
        boolean inLiquid = false;
        final AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
        final int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!(block instanceof BlockAir) ) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

    private static boolean isAboveLand(Entity entity) {
        if (entity == null) return false;

        double y = entity.posY - 0.01;

        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); x++)
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); z++) {
                BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);

                if (Wrapper.INSTANCE.world().getBlockState(pos).getBlock().isFullBlock(Wrapper.INSTANCE.world().getBlockState(pos)))
                    return true;
            }
        return false;
    }
}
