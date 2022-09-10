package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MovementUtil;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//TODO ADD OnMotion (only place blocks if motion was 0 for x ms)
public class Surround extends Modules {

    private static BooleanValue noGhostBlock;
    private BooleanValue onlyOnSneak;
    private BooleanValue stopOnMotion;
    private BooleanValue bottomBlock;
    private BooleanValue rotate;
    private BooleanValue center;
    private IntegerValue tick;
    private IntegerValue stationaryTicks;
    private ModeValue modes;

    public Surround() {
        super("Surround", ModuleCategory.COMBAT, "Places obsidian around your feet");

        modes = new ModeValue("Mode", new Mode("Normal", true), new Mode("Long", false), new Mode("Square", false), new Mode("AntiPiston", false), new Mode("Tall", false));
        this.stopOnMotion = new BooleanValue("StopOnMotion", true, "Stops trying to place obsidian when you are moving");
        this.stationaryTicks = new IntegerValue("StationaryTicks", 10, 1, 40, "The amount of ticks needed to activate when standing still");
        this.onlyOnSneak = new BooleanValue("ActiveOnSneak", false, "Only activate on sneak");
        this.rotate = new BooleanValue("Rotate", false, "Send rotation packets to the server");
        this.center = new BooleanValue("CenterInBlock", true, "Center yourself in the block when activated");
        this.bottomBlock = new BooleanValue("BottomBlock", true, "Check the block under the player");
        this.tick = new IntegerValue("PlaceSpeed", 4, 1, 12, "The amount of blocks placed in one tick");
        noGhostBlock = new BooleanValue("NoGhostBlock", false, "Makes it less likely to get ghost blocks");
        this.addValue(modes, stopOnMotion, stationaryTicks, onlyOnSneak, rotate, center, bottomBlock, tick, noGhostBlock);
    }

    static Minecraft mc = Minecraft.getMinecraft();
    private final List<Block> whiteList = Collections.singletonList(Blocks.OBSIDIAN);

    public static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable())
                return true;
        }
        return false;
    }

    int i = 0;

    @Override
    public void onDisable() {
        i = 0;
        isMoving = false;
        notMovingTicks = 0;
        super.onDisable();
    }

    boolean isMoving = false;
    int notMovingTicks = 0;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (!MovementUtil.isMoving() || !stopOnMotion.getValue()) {
            notMovingTicks++;
            if (stopOnMotion.getValue())
                if (!(notMovingTicks >= stationaryTicks.getValue())) return;
            i++;
            if (onlyOnSneak.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown()) return;
            if (!isToggled() || mc.player == null) return;
            Vec3d vec3d = getInterpolatedPos(mc.player, 0);
            BlockPos northBlockPos = new BlockPos(vec3d).north();
            BlockPos southBlockPos = new BlockPos(vec3d).south();
            BlockPos eastBlockPos = new BlockPos(vec3d).east();
            BlockPos westBlockPos = new BlockPos(vec3d).west();
            BlockPos downBlockPos = new BlockPos(mc.player).down();

            BlockPos secondNorthBlockPos = northBlockPos.add(0, 0, -1);
            BlockPos secondSouthBlockPos = southBlockPos.add(0, 0, 1);
            BlockPos secondEastBlockPos = eastBlockPos.add(1, 0, 0);
            BlockPos secondWestBlockPos = westBlockPos.add(-1, 0, 0);

            BlockPos antiPistonNorthBlockPos = northBlockPos.add(0, 1, -1);
            BlockPos antiPistonSouthBlockPos = southBlockPos.add(0, 1, 1);
            BlockPos antiPistonEastBlockPos = eastBlockPos.add(1, 1, 0);
            BlockPos antiPistonWestBlockPos = westBlockPos.add(-1, 1, 0);

            int blocksPlaced = 0;


            int newSlot = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack stack =
                    mc.player.inventory.getStackInSlot(i);

                if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                    continue;
                }

                Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (!whiteList.contains(block)) {
                    continue;
                }

                newSlot = i;
                break;
            }

            if (newSlot == -1)
                return;

            int oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = newSlot;

            A:
            if (!hasNeighbour(northBlockPos)) {
                for (EnumFacing side : EnumFacing.values()) {
                    BlockPos neighbour = northBlockPos.offset(side);
                    if (hasNeighbour(neighbour)) {
                        northBlockPos = neighbour;
                        break A;
                    }
                }
                return;
            }

            B:
            if (!hasNeighbour(southBlockPos)) {
                for (EnumFacing side : EnumFacing.values()) {
                    BlockPos neighbour = southBlockPos.offset(side);
                    if (hasNeighbour(neighbour)) {
                        southBlockPos = neighbour;
                        break B;
                    }
                }
                return;
            }

            C:
            if (!hasNeighbour(eastBlockPos)) {
                for (EnumFacing side : EnumFacing.values()) {
                    BlockPos neighbour = eastBlockPos.offset(side);
                    if (hasNeighbour(neighbour)) {
                        eastBlockPos = neighbour;
                        break C;
                    }
                }
                return;
            }

            D:
            if (!hasNeighbour(westBlockPos)) {
                for (EnumFacing side : EnumFacing.values()) {
                    BlockPos neighbour = westBlockPos.offset(side);
                    if (hasNeighbour(neighbour)) {
                        westBlockPos = neighbour;
                        break D;
                    }
                }
                return;
            }
            if (bottomBlock.getValue()) {
                E:
                if (!hasNeighbour(downBlockPos)) {
                    for (EnumFacing side : EnumFacing.values()) {
                        BlockPos neighbour = downBlockPos.offset(side);
                        if (hasNeighbour(neighbour)) {
                            downBlockPos = neighbour;
                            break E;
                        }
                    }
                    return;
                }
            }


            if (center.getValue() && onlyOnSneak.getValue()) {
                centerPlayer();
            }
            if (center.getValue() && !onlyOnSneak.getValue()) {
                if (i < 10 || (notMovingTicks - stationaryTicks.getValue() < 5)) {
                    centerPlayer();
                }
            }
            if (bottomBlock.getValue()) {
                if (mc.world.getBlockState(downBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(downBlockPos)) {
                        placeBlockScaffold(downBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(downBlockPos) && mc.world.getBlockState(downBlockPos).getMaterial().isReplaceable()) {
                        placeBlockScaffold(downBlockPos, rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }
            }

            if (mc.world.getBlockState(northBlockPos).getMaterial().isReplaceable()) {
                if (isEntitiesEmpty(northBlockPos)) {
                    placeBlockScaffold(northBlockPos, rotate.getValue());
                    blocksPlaced++;
                } else if (isEntitiesEmpty(northBlockPos.north()) && mc.world.getBlockState(northBlockPos).getMaterial().isReplaceable()) {
                    placeBlockScaffold(northBlockPos.north(), rotate.getValue());
                    blocksPlaced++;
                }
            }
            if (blocksPlaced >= tick.getValue()) {
                mc.player.inventory.currentItem = oldSlot;
                return;
            }

            if (mc.world.getBlockState(southBlockPos).getMaterial().isReplaceable()) {
                if (isEntitiesEmpty(southBlockPos)) {
                    placeBlockScaffold(southBlockPos, rotate.getValue());
                    blocksPlaced++;
                } else if (isEntitiesEmpty(southBlockPos.south()) && mc.world.getBlockState(southBlockPos.south()).getMaterial().isReplaceable()) {
                    placeBlockScaffold(southBlockPos.south(), rotate.getValue());
                    blocksPlaced++;
                }
            }
            if (blocksPlaced >= tick.getValue()) {
                mc.player.inventory.currentItem = oldSlot;
                return;
            }

            if (mc.world.getBlockState(eastBlockPos).getMaterial().isReplaceable()) {
                if (isEntitiesEmpty(eastBlockPos)) {
                    placeBlockScaffold(eastBlockPos, rotate.getValue());
                    blocksPlaced++;
                } else if (isEntitiesEmpty(eastBlockPos.east()) && mc.world.getBlockState(eastBlockPos.east()).getMaterial().isReplaceable()) {
                    placeBlockScaffold(eastBlockPos.east(), rotate.getValue());
                    blocksPlaced++;
                }
            }
            if (blocksPlaced >= tick.getValue()) {
                mc.player.inventory.currentItem = oldSlot;
                return;
            }

            if (mc.world.getBlockState(westBlockPos).getMaterial().isReplaceable()) {
                if (isEntitiesEmpty(westBlockPos)) {
                    placeBlockScaffold(westBlockPos, rotate.getValue());
                    blocksPlaced++;
                } else if (isEntitiesEmpty(westBlockPos.west()) && mc.world.getBlockState(westBlockPos.west()).getMaterial().isReplaceable()) {
                    placeBlockScaffold(westBlockPos.west(), rotate.getValue());
                    blocksPlaced++;
                }
            }
            if (blocksPlaced >= tick.getValue()) {
                mc.player.inventory.currentItem = oldSlot;
                return;
            }

            if (modes.getMode("Tall").isToggled()) {

                if (mc.world.getBlockState(northBlockPos.add(0, 1, 0)).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(northBlockPos.add(0, 1, 0))) {
                        placeBlockScaffold(northBlockPos.add(0, 1, 0), rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(northBlockPos.add(0, 1, 0)) && mc.world.getBlockState(northBlockPos.add(1, 0, 0)).getMaterial().isReplaceable()) {
                        placeBlockScaffold(northBlockPos.add(0, 1, 0).north(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(southBlockPos.add(0, 1, 0)).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(southBlockPos.add(0, 1, 0))) {
                        placeBlockScaffold(southBlockPos.add(0, 1, 0), rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(southBlockPos.add(0, 1, 0).south()) && mc.world.getBlockState(southBlockPos.add(-1, 0, 0).south()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(southBlockPos.add(0, 1, 0).south(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(eastBlockPos.add(0, 1, 0)).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(eastBlockPos.add(0, 1, 0))) {
                        placeBlockScaffold(eastBlockPos.add(0, 1, 0), rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(eastBlockPos.add(0, 1, 0).east()) && mc.world.getBlockState(eastBlockPos.add(0, 0, 1).east()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(eastBlockPos.add(0, 1, 0).east(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(westBlockPos.add(0, 1, 0)).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(westBlockPos.add(0, 1, 0))) {
                        placeBlockScaffold(westBlockPos.add(0, 1, 0), rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(westBlockPos.add(0, 1, 0).west()) && mc.world.getBlockState(westBlockPos.add(0, 0, -1).west()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(westBlockPos.add(0, 1, 0).west(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }
            }


            if (modes.getMode("Square").isToggled()) {

                if (mc.world.getBlockState(northBlockPos.add(1, 0, 0)).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(northBlockPos.add(1, 0, 0))) {
                        placeBlockScaffold(northBlockPos.add(1, 0, 0), rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(northBlockPos.add(1, 0, 0)) && mc.world.getBlockState(northBlockPos.add(1, 0, 0)).getMaterial().isReplaceable()) {
                        placeBlockScaffold(northBlockPos.add(1, 0, 0).north(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(southBlockPos.add(-1, 0, 0)).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(southBlockPos.add(-1, 0, 0))) {
                        placeBlockScaffold(southBlockPos.add(-1, 0, 0), rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(southBlockPos.add(-1, 0, 0).south()) && mc.world.getBlockState(southBlockPos.add(-1, 0, 0).south()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(southBlockPos.add(-1, 0, 0).south(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(eastBlockPos.add(0, 0, 1)).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(eastBlockPos.add(0, 0, 1))) {
                        placeBlockScaffold(eastBlockPos.add(0, 0, 1), rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(eastBlockPos.add(0, 0, 1).east()) && mc.world.getBlockState(eastBlockPos.add(0, 0, 1).east()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(eastBlockPos.add(0, 0, 1).east(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(westBlockPos.add(0, 0, -1)).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(westBlockPos.add(0, 0, -1))) {
                        placeBlockScaffold(westBlockPos.add(0, 0, -1), rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(westBlockPos.add(0, 0, -1).west()) && mc.world.getBlockState(westBlockPos.add(0, 0, -1).west()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(westBlockPos.add(0, 0, -1).west(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }
            }

            if (modes.getMode("Long").isToggled() || modes.getMode("AntiPiston").isToggled()) {
                if (mc.world.getBlockState(secondNorthBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(secondNorthBlockPos)) {
                        placeBlockScaffold(secondNorthBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(secondNorthBlockPos.north()) && mc.world.getBlockState(secondNorthBlockPos.north()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(secondNorthBlockPos.north(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(secondSouthBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(secondSouthBlockPos)) {
                        placeBlockScaffold(secondSouthBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(secondSouthBlockPos.south()) && mc.world.getBlockState(secondSouthBlockPos.south()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(secondSouthBlockPos.south(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(secondEastBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(secondEastBlockPos)) {
                        placeBlockScaffold(secondEastBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(secondEastBlockPos.east()) && mc.world.getBlockState(secondEastBlockPos.east()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(secondEastBlockPos.east(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(secondWestBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(secondWestBlockPos)) {
                        placeBlockScaffold(secondWestBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(secondWestBlockPos.west()) && mc.world.getBlockState(secondWestBlockPos.west()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(secondWestBlockPos.west(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }


            }
            if (modes.getMode("AntiPiston").isToggled()) {
                if (mc.world.getBlockState(antiPistonNorthBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(antiPistonNorthBlockPos)) {
                        placeBlockScaffold(antiPistonNorthBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(antiPistonNorthBlockPos.north()) && mc.world.getBlockState(antiPistonNorthBlockPos.north()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(antiPistonNorthBlockPos.north(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(antiPistonSouthBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(antiPistonSouthBlockPos)) {
                        placeBlockScaffold(antiPistonSouthBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(antiPistonSouthBlockPos.south()) && mc.world.getBlockState(antiPistonSouthBlockPos.south()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(antiPistonSouthBlockPos.south(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(antiPistonEastBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(antiPistonEastBlockPos)) {
                        placeBlockScaffold(antiPistonEastBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(antiPistonEastBlockPos.east()) && mc.world.getBlockState(antiPistonEastBlockPos.east()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(antiPistonEastBlockPos.east(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }

                if (mc.world.getBlockState(antiPistonWestBlockPos).getMaterial().isReplaceable()) {
                    if (isEntitiesEmpty(antiPistonWestBlockPos)) {
                        placeBlockScaffold(antiPistonWestBlockPos, rotate.getValue());
                        blocksPlaced++;
                    } else if (isEntitiesEmpty(antiPistonWestBlockPos.west()) && mc.world.getBlockState(antiPistonWestBlockPos.west()).getMaterial().isReplaceable()) {
                        placeBlockScaffold(antiPistonWestBlockPos.west(), rotate.getValue());
                        blocksPlaced++;
                    }
                }
                if (blocksPlaced >= tick.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    return;
                }
            }


            mc.player.inventory.currentItem = oldSlot;
        } else notMovingTicks = 0;
    });

    private void centerPlayer() {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(roundHalf(Wrapper.INSTANCE.player().posX), Wrapper.INSTANCE.player().posY, roundHalf(Wrapper.INSTANCE.player().posZ), true));
        Wrapper.INSTANCE.player().setPosition(roundHalf(Wrapper.INSTANCE.player().posX), Wrapper.INSTANCE.player().posY, roundHalf(Wrapper.INSTANCE.player().posZ));
    }

    private boolean isEntitiesEmpty(BlockPos pos) {
        List<Entity> entities = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream()
            .filter(e -> !(e instanceof EntityItem))
            .filter(e -> !(e instanceof EntityXPOrb))
            .collect(Collectors.toList());
        return entities.isEmpty();
    }

    public static void placeBlockScaffold(BlockPos pos, boolean rotate) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            if (!canBeClicked(neighbor))
                continue;

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
                .add(new Vec3d(side2.getDirectionVec()).scale(0.5));


            if (rotate)
                faceVectorPacketInstant(hitVec);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            processRightClickBlock(neighbor, side2, hitVec);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.rightClickDelayTimer = 0;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

            return;
        }

    }

    private static PlayerControllerMP getPlayerController() {
        return mc.playerController;
    }

    public static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
        getPlayerController().processRightClickBlock(mc.player, mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
        if (noGhostBlock.getValue() && !mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
        }
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getNeededRotations2(vec);

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
            rotations[1], mc.player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
            mc.player.rotationYaw
                + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
            mc.player.rotationPitch + MathHelper
                .wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX,
            mc.player.posY + mc.player.getEyeHeight(),
            mc.player.posZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
            (entity.posX - entity.lastTickPosX) * x,
            (entity.posY - entity.lastTickPosY) * y,
            (entity.posZ - entity.lastTickPosZ) * z
        );
    }

    public static double roundHalf(double number) {
        if (number < 0) {
            return (int) number - 0.5;
        } else {
            return (int) number + 0.5;
        }

    }
}

