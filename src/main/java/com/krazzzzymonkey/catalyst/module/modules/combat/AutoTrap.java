package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.utils.PlayerControllerUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AutoTrap extends Modules {

    private BooleanValue rotate;
    public static BooleanValue noGhostBlock;
    private final DoubleValue range;
    private DoubleValue blocksPerTick;
    private ModeValue mode;
    ModeValue swing;
    static Minecraft mc = Minecraft.getMinecraft();

    public AutoTrap() {
        super("AutoTrap", ModuleCategory.COMBAT, "Automatically traps a player in obsidian");

        this.mode = new ModeValue("Mode",
            new Mode("FullRoof", true),
            new Mode("ExtraTop", false),
            new Mode("FacePlace", false),
            new Mode("Normal", false),
            new Mode("CrystalMinimum", false),
            new Mode("Crystal", false),
            new Mode("CrystalFull", false));
        this.rotate = new BooleanValue("Rotate", true, "Sends rotation packets to the server");
        swing = new ModeValue("Swing", new Mode("Mainhand", true), new Mode("Offhand", false), new Mode("Cancel", false));
        this.range = new DoubleValue("Range", 5D, 0D, 10D, "The maximum range for a target to be auto trapped");
        this.blocksPerTick = new DoubleValue("BlocksPerTick", 8D, 1D, 15D, "The amount of blocks placed in one tick");
        noGhostBlock = new BooleanValue("AntiGhostBlock", false, "Makes it less likely to get ghost blocks");
        this.addValue(mode, rotate, swing, range, blocksPerTick, noGhostBlock);
    }


    private int offsetStep = 0;
    private int yLevel;
    private String lastTickTargetName = "";
    private boolean firstRun = true;
    public static EntityPlayer target;

    @Override
    public void onEnable() {
        if(mc.player == null)return;
        yLevel = (int) Math.round(mc.player.posY);
        firstRun = true;
        super.onEnable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null || PlayerControllerUtils.findObiInHotbar() == -1)
            return;

        target = findClosestTarget();

        if (target == null) {
            this.setExtraInfo("");
            return;
        }
        this.setExtraInfo(target.getName());

        if ((int) Math.round(mc.player.posY) != yLevel) {
            return;
        }

        if (firstRun) {
            firstRun = false;
            lastTickTargetName = target.getName();
        } else if (!lastTickTargetName.equals(target.getName())) {
            lastTickTargetName = target.getName();
            offsetStep = 0;
        }

        final List<Vec3d> place_targets = new ArrayList<Vec3d>();

        if (mode.getMode("FullRoof").isToggled()) {
            Collections.addAll(place_targets, offsetsDefault);
        } else if (mode.getMode("ExtraTop").isToggled()) {
            Collections.addAll(place_targets, offsetsExtra);
        } else if (mode.getMode("Normal").isToggled()) {
            Collections.addAll(place_targets, TRAP);
        } else if (mode.getMode("CrystalMinimum").isToggled()) {
            Collections.addAll(place_targets, CRYSTALEXA);
        } else if (mode.getMode("Crystal").isToggled()) {
            Collections.addAll(place_targets, CRYSTAL);
        } else if (mode.getMode("CrystalFull").isToggled()) {
            Collections.addAll(place_targets, CRYSTALFULLROOF);
        } else {
            Collections.addAll(place_targets, offsetsFace);
        }

        int blocks_placed = 0;

        while (blocks_placed < blocksPerTick.getValue()) {

            if (offsetStep >= place_targets.size()) {
                offsetStep = 0;
                break;
            }

            final BlockPos offset_pos = new BlockPos(place_targets.get(offsetStep));
            final BlockPos target_pos = new BlockPos(target.getPositionVector()).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());
            boolean should_try_place = mc.world.getBlockState(target_pos).getMaterial().isReplaceable();

            for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    should_try_place = false;
                    break;
                }
            }

            if (should_try_place && BlockUtils.placeBlock(target_pos, PlayerControllerUtils.findObiInHotbar(), rotate.getValue(), rotate.getValue(), swing, noGhostBlock.getValue())) {
                ++blocks_placed;
            }

            offsetStep++;

        }

    });

    public EntityPlayer findClosestTarget() {

        if (mc.world.playerEntities.isEmpty())
            return null;

        EntityPlayer closestTarget = null;

        for (final EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player || !target.isEntityAlive())
                continue;
            if (mc.player.getDistance(target) > range.getValue())
                continue;
            if (FriendManager.friendsList.contains(target.getName()))
                continue;

            if (target.getHealth() <= 0.0f)
                continue;

            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;

            closestTarget = target;
        }

        return closestTarget;
    }

    private static final Vec3d[] offsetsDefault = new Vec3d[]{
        new Vec3d(0.0, 0.0, -1.0),
        new Vec3d(1.0, 0.0, 0.0),
        new Vec3d(0.0, 0.0, 1.0),
        new Vec3d(-1.0, 0.0, 0.0),
        new Vec3d(0.0, 1.0, -1.0),
        new Vec3d(1.0, 1.0, 0.0),
        new Vec3d(0.0, 1.0, 1.0),
        new Vec3d(-1.0, 1.0, 0.0),
        new Vec3d(0.0, 2.0, -1.0),
        new Vec3d(1.0, 2.0, 0.0),
        new Vec3d(0.0, 2.0, 1.0),
        new Vec3d(-1.0, 2.0, 0.0),
        new Vec3d(0.0, 3.0, -1.0),
        new Vec3d(0.0, 3.0, 1.0),
        new Vec3d(1.0, 3.0, 0.0),
        new Vec3d(-1.0, 3.0, 0.0),
        new Vec3d(0.0, 3.0, 0.0)
    };

    private static final Vec3d[] offsetsFace = new Vec3d[]{
        new Vec3d(0.0, 0.0, -1.0),
        new Vec3d(1.0, 0.0, 0.0),
        new Vec3d(0.0, 0.0, 1.0),
        new Vec3d(-1.0, 0.0, 0.0),
        new Vec3d(0.0, 1.0, -1.0),
        new Vec3d(1.0, 1.0, 0.0),
        new Vec3d(0.0, 1.0, 1.0),
        new Vec3d(-1.0, 1.0, 0.0),
        new Vec3d(0.0, 2.0, -1.0),
        new Vec3d(0.0, 3.0, -1.0),
        new Vec3d(0.0, 3.0, 1.0),
        new Vec3d(1.0, 3.0, 0.0),
        new Vec3d(-1.0, 3.0, 0.0),
        new Vec3d(0.0, 3.0, 0.0)
    };


    private static final Vec3d[] offsetsExtra = new Vec3d[]{
        new Vec3d(0.0, 0.0, -1.0),
        new Vec3d(1.0, 0.0, 0.0),
        new Vec3d(0.0, 0.0, 1.0),
        new Vec3d(-1.0, 0.0, 0.0),
        new Vec3d(0.0, 1.0, -1.0),
        new Vec3d(1.0, 1.0, 0.0),
        new Vec3d(0.0, 1.0, 1.0),
        new Vec3d(-1.0, 1.0, 0.0),
        new Vec3d(0.0, 2.0, -1.0),
        new Vec3d(1.0, 2.0, 0.0),
        new Vec3d(0.0, 2.0, 1.0),
        new Vec3d(-1.0, 2.0, 0.0),
        new Vec3d(0.0, 3.0, -1.0),
        new Vec3d(0.0, 3.0, 0.0),
        new Vec3d(0.0, 4.0, 0.0)
    };

    private static final Vec3d[] TRAP = {
        new Vec3d(0, 0, -1),
        new Vec3d(1, 0, 0),
        new Vec3d(0, 0, 1),
        new Vec3d(-1, 0, 0),
        new Vec3d(0, 1, -1),
        new Vec3d(1, 1, 0),
        new Vec3d(0, 1, 1),
        new Vec3d(-1, 1, 0),
        new Vec3d(0, 2, -1),
        new Vec3d(1, 2, 0),
        new Vec3d(0, 2, 1),
        new Vec3d(-1, 2, 0),
        new Vec3d(0, 3, -1),
        new Vec3d(0, 3, 0)
    };


    private static final Vec3d[] CRYSTALEXA = {
        new Vec3d(0, 0, -1),
        new Vec3d(0, 1, -1),
        new Vec3d(0, 2, -1),
        new Vec3d(1, 2, 0),
        new Vec3d(0, 2, 1),
        new Vec3d(-1, 2, 0),
        new Vec3d(-1, 2, -1),
        new Vec3d(1, 2, 1),
        new Vec3d(1, 2, -1),
        new Vec3d(-1, 2, 1),
        new Vec3d(0, 3, -1),
        new Vec3d(0, 3, 0)
    };

    private static final Vec3d[] CRYSTAL = {
        new Vec3d(0, 0, -1),
        new Vec3d(1, 0, 0),
        new Vec3d(0, 0, 1),
        new Vec3d(-1, 0, 0),
        new Vec3d(-1, 0, 1),
        new Vec3d(1, 0, -1),
        new Vec3d(-1, 0, -1),
        new Vec3d(1, 0, 1),
        new Vec3d(-1, 1, -1),
        new Vec3d(1, 1, 1),
        new Vec3d(-1, 1, 1),
        new Vec3d(1, 1, -1),
        new Vec3d(0, 2, -1),
        new Vec3d(1, 2, 0),
        new Vec3d(0, 2, 1),
        new Vec3d(-1, 2, 0),
        new Vec3d(-1, 2, 1),
        new Vec3d(1, 2, -1),
        new Vec3d(0, 3, -1),
        new Vec3d(0, 3, 0)
    };

    private static final Vec3d[] CRYSTALFULLROOF = {
        new Vec3d(0, 0, -1),
        new Vec3d(1, 0, 0),
        new Vec3d(0, 0, 1),
        new Vec3d(-1, 0, 0),
        new Vec3d(-1, 0, 1),
        new Vec3d(1, 0, -1),
        new Vec3d(-1, 0, -1),
        new Vec3d(1, 0, 1),
        new Vec3d(-1, 1, -1),
        new Vec3d(1, 1, 1),
        new Vec3d(-1, 1, 1),
        new Vec3d(1, 1, -1),
        new Vec3d(0, 2, -1),
        new Vec3d(1, 2, 0),
        new Vec3d(0, 2, 1),
        new Vec3d(-1, 2, 0),
        new Vec3d(-1, 2, 1),
        new Vec3d(1, 2, -1),
        new Vec3d(0, 3, -1),
        new Vec3d(1, 3, 0),
        new Vec3d(0, 3, 1),
        new Vec3d(-1, 3, 0),
        new Vec3d(0, 3, 0)
    };


}
