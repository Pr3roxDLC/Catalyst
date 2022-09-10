package com.krazzzzymonkey.catalyst.module.modules.world;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.utils.TimerUtils;
import com.krazzzzymonkey.catalyst.utils.Utils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

//TODO FIX TOWER MODE
public class Scaffold extends Modules {

    boolean shouldKeepVelocity = false;
    public TimerUtils timer;
    boolean isBridging = false;
    BlockPos blockDown = null;
    public static float[] facingCam = null;
    float startYaw = 0;
    float startPitch = 0;

    private final BooleanValue towerMode;
    private BooleanValue rotate;
    private BooleanValue downOnSneak;
    private final ModeValue placeMode;

    public Scaffold() {
        super("Scaffold", ModuleCategory.WORLD, "Automatically places blocks under player");
        placeMode = new ModeValue("PlaceMode", new Mode("Normal", false), new Mode("Legit", false), new Mode("Simple", true));
        this.downOnSneak = new BooleanValue("DownOnSneak", true, "Allows you to scaffold downwards when holding sneak");
        this.towerMode = new BooleanValue("TowerMode", false, "Rapidly places blocks under you to build high towers");
        this.addValue(placeMode, downOnSneak, towerMode);

        this.timer = new TimerUtils();
    }

    public static boolean cancelSneak = false;

    @Override
    public void onDisable() {
        facingCam = null;
        cancelSneak = false;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        blockDown = null;
        facingCam = null;
        isBridging = false;
        startYaw = 0;
        startPitch = 0;
        cancelSneak = false;
        super.onEnable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        Simple();
    });

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (blockDown != null) {
            RenderUtils.drawBlockESP(blockDown, 1F, 1F, 1F, 1);
        }
    });


    void Simple() {
        blockDown = new BlockPos(Wrapper.INSTANCE.player()).down();

        if (mc.player.isSneaking() && downOnSneak.getValue()) {
            blockDown = blockDown.down();
            cancelSneak = true;
        } else cancelSneak = false;

        if (Minecraft.getMinecraft().player.motionX != 0 || Minecraft.getMinecraft().player.motionZ != 0 && towerMode.getValue()) {
            if (!Wrapper.INSTANCE.world().getBlockState(blockDown).getBlock().getMaterial(Wrapper.INSTANCE.world().getBlockState(blockDown).getBlock().getDefaultState()).isReplaceable()) {
                return;
            }
            int newSlot = -1;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = Wrapper.INSTANCE.player().inventory.getStackInSlot(i);
                if (stack != null
                    && stack.getItem() instanceof ItemBlock
                    && Block.getBlockFromItem(stack.getItem()).getDefaultState().getBlock().isFullBlock(Wrapper.INSTANCE.world().getBlockState(blockDown).getBlock().getDefaultState())) {
                    newSlot = i;
                    break;
                }
            }
            if (newSlot == -1) {
                return;
            }
            final int oldSlot = Wrapper.INSTANCE.player().inventory.currentItem;
            Wrapper.INSTANCE.player().inventory.currentItem = newSlot;
            if (!hasNeighbour(blockDown)) {
                for (EnumFacing side : EnumFacing.VALUES) {
                    BlockPos neighbour = blockDown.offset(side);
                    if (hasNeighbour(neighbour)) {
                        if (placeMode.getMode("Normal").isToggled()) {
                            Utils.placeBlockScaffold(neighbour);
                        } else if (placeMode.getMode("Legit").isToggled()) {
                            BlockUtils.placeBlockLegit(neighbour);
                        } else if (placeMode.getMode("Simple").isToggled()) {
                            BlockUtils.placeBlockSimple(neighbour);
                        }

                        break;
                    }
                }
            }
            if (placeMode.getMode("Normal").isToggled()) {
                Utils.placeBlockScaffold(blockDown);
            } else if (placeMode.getMode("Legit").isToggled()) {
                BlockUtils.placeBlockLegit(blockDown);
            } else if (placeMode.getMode("Simple").isToggled()) {
                BlockUtils.placeBlockSimple(blockDown);
            }
            Wrapper.INSTANCE.player().inventory.currentItem = oldSlot;


        } else if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() && towerMode.getValue() && mc.player.motionX == 0 && mc.player.motionZ == 0) {
            int newSlot = -1;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = Wrapper.INSTANCE.player().inventory.getStackInSlot(i);
                if (stack != null
                    && stack.getItem() instanceof ItemBlock
                    && Block.getBlockFromItem(stack.getItem()).getDefaultState().getBlock().isFullBlock(Wrapper.INSTANCE.world().getBlockState(blockDown).getBlock().getDefaultState())) {
                    newSlot = i;
                    break;
                }
            }
            if (newSlot == -1) {
                return;
            }
            final int oldSlot = Wrapper.INSTANCE.player().inventory.currentItem;
            Wrapper.INSTANCE.player().inventory.currentItem = newSlot;

            Minecraft.getMinecraft().player.setVelocity(0, 0.3f, 0);
            if (placeMode.getMode("Normal").isToggled()) {
                Utils.placeBlockScaffold(blockDown);
            } else if (placeMode.getMode("Legit").isToggled()) {
                BlockUtils.placeBlockLegit(blockDown);
            } else if (placeMode.getMode("Simple").isToggled()) {
                BlockUtils.placeBlockSimple(blockDown);
            }
            shouldKeepVelocity = true;
            Wrapper.INSTANCE.player().inventory.currentItem = oldSlot;
        } else {
            if (!Wrapper.INSTANCE.world().getBlockState(blockDown).getBlock().getMaterial(Wrapper.INSTANCE.world().getBlockState(blockDown).getBlock().getDefaultState()).isReplaceable()) {
                return;
            }
            int newSlot = -1;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = Wrapper.INSTANCE.player().inventory.getStackInSlot(i);
                if (stack != null
                    && stack.getItem() instanceof ItemBlock
                    && Block.getBlockFromItem(stack.getItem()).getDefaultState().getBlock().isFullBlock(Wrapper.INSTANCE.world().getBlockState(blockDown).getBlock().getDefaultState())) {
                    newSlot = i;
                    break;
                }
            }
            if (newSlot == -1) {
                return;
            }
            final int oldSlot = Wrapper.INSTANCE.player().inventory.currentItem;
            Wrapper.INSTANCE.player().inventory.currentItem = newSlot;
            if (!hasNeighbour(blockDown)) {
                for (EnumFacing side : EnumFacing.VALUES) {
                    BlockPos neighbour = blockDown.offset(side);
                    if (hasNeighbour(neighbour)) {
                        if (placeMode.getMode("Normal").isToggled()) {
                            Utils.placeBlockScaffold(neighbour);
                        } else if (placeMode.getMode("Legit").isToggled()) {
                            BlockUtils.placeBlockLegit(neighbour);
                        } else if (placeMode.getMode("Simple").isToggled()) {
                            BlockUtils.placeBlockSimple(neighbour);
                        }
                        break;
                    }
                }
            }
            if (placeMode.getMode("Normal").isToggled()) {
                Utils.placeBlockScaffold(blockDown);
            } else if (placeMode.getMode("Legit").isToggled()) {
                BlockUtils.placeBlockLegit(blockDown);
            } else if (placeMode.getMode("Simple").isToggled()) {
                BlockUtils.placeBlockSimple(blockDown);
            }
            Wrapper.INSTANCE.player().inventory.currentItem = oldSlot;

        }
        if (shouldKeepVelocity && !Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
            Minecraft.getMinecraft().player.motionY = -0.28f;
            shouldKeepVelocity = false;
        }
    }

    public static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (!Minecraft.getMinecraft().world.getBlockState(neighbour).getMaterial().isReplaceable())
                return true;
        }
        return false;
    }


}
