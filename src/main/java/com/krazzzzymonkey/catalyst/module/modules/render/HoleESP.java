package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HoleESP extends Modules {

    public ModeValue modes = new ModeValue("RenderMode", new Mode("Box", true), new Mode("Outline", false), new Mode("Halo", false));

    private final BooleanValue doubleHole = new BooleanValue("DoubleHole", false, "");
    private final ColorValue bedrockColorValue = new ColorValue("BedrockColor", Color.GREEN, "Color of hole made out of bedrock");
    private final BooleanValue bedrockRainbowColor = new BooleanValue("BedrockRainbow", false, "Makes the bedrock color cycle through colors");
    private final ColorValue obsidianColorValue = new ColorValue("ObsidianColor", Color.RED, "Color of hole made out of at least 1 obsidian");
    private final BooleanValue obsidianRainbowColor = new BooleanValue("ObsidianRainbow", false, "Color of hole made out of obsidian");
    public static DoubleValue sphereRange = new DoubleValue("RenderRange", 10D, 0D, 40D, "Rendering range of hole esp");
    public static IntegerValue opacityStart = new IntegerValue("StartOpacity", 100, 0, 100, "Renders opacity of halo esp mode");
    public static IntegerValue opacityEnd = new IntegerValue("EndOpacity", 0, 0, 100, "Renders ending opacity of halo esp mode");
    public static DoubleValue startOffset = new DoubleValue("StartOffset", 0, -1f, 2f, "");
    public static DoubleValue height = new DoubleValue("Height", 0, 0, 2f, "Render height of hole esp");
    public static BooleanValue groundPlate = new BooleanValue("DrawBottomQuad", false, "Renders bottom side of halo esp mode");
    public static IntegerValue groundPlateOpacity = new IntegerValue("BottomQuadOpacity", 50, 0, 100, "Renders opacity of bottom side halo esp mode");

    public HoleESP() {
        super("HoleESP", ModuleCategory.RENDER, "Shows you where a safe holes are when crystal pvping");

        this.addValue(modes, sphereRange, height, opacityStart, opacityEnd, groundPlate, groundPlateOpacity, bedrockColorValue, bedrockRainbowColor, obsidianColorValue, obsidianRainbowColor);
    }

    private final BlockPos[] surroundOffset = {
            new BlockPos(0, -1, 0), // down
            new BlockPos(0, 0, -1), // north
            new BlockPos(1, 0, 0), // east
            new BlockPos(0, 0, 1), // south
            new BlockPos(-1, 0, 0) // west
    };

    private ConcurrentHashMap<BlockPos, Boolean> safeHoles;
    public static HashSet<AxisAlignedBB> doubleHoles = new HashSet<>();
    Thread f = new Thread(new HoleCheck());


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (safeHoles == null) {
            safeHoles = new ConcurrentHashMap<>();
        } else {
            safeHoles.clear();
        }

        int range = (int) Math.ceil(sphereRange.getValue());

        List<BlockPos> blockPosList = getSphere(getPlayerPos(), range, range, false, true, 0);

        if(!f.isAlive() && doubleHole.getValue()) {
            doubleHoles.clear();
            f.run();
        }


        for (BlockPos pos : blockPosList) {

            // block gotta be air
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            // block 1 above gotta be air
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            // block 2 above gotta be air
            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }


            boolean isSafe = true;
            boolean isBedrock = true;

            for (BlockPos offset : surroundOffset) {
                Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (block != Blocks.BEDROCK) {
                    isBedrock = false;
                }
                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) {
                    isSafe = false;
                    break;
                }
            }

            if (isSafe) {
                safeHoles.put(pos, isBedrock);
            }


        }
    });

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (mc.player == null || safeHoles == null) {
            return;
        }

        doubleHoles.forEach(n -> RenderUtils.drawHaloESP(n, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 0.1f, 0, height.getValue().floatValue(), groundPlate.getValue(), groundPlateOpacity.getValue(), opacityStart.getValue(), opacityEnd.getValue()));


        if (safeHoles.isEmpty()) {
            return;
        }


        if (modes.getMode("Box").isToggled()) {
            safeHoles.forEach((blockPos, isBedrock) -> {
                if (isBedrock) {
                    if (bedrockRainbowColor.getValue()) {
                        RenderUtils.drawBlockESP(blockPos, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, height.getValue());
                    } else {
                        RenderUtils.drawBlockESP(blockPos, bedrockColorValue.getColor().getRed() / 255f, bedrockColorValue.getColor().getGreen() / 255f, bedrockColorValue.getColor().getBlue() / 255f, height.getValue());
                    }
                } else {
                    if (obsidianRainbowColor.getValue()) {
                        RenderUtils.drawBlockESP(blockPos, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, height.getValue());
                    } else {
                        RenderUtils.drawBlockESP(blockPos, obsidianColorValue.getColor().getRed() / 255f, obsidianColorValue.getColor().getGreen() / 255f, obsidianColorValue.getColor().getBlue() / 255f, height.getValue());
                    }

                }
            });
        }
        if (modes.getMode("Outline").isToggled()) {
            safeHoles.forEach((blockPos, isBedrock) -> {
                if (isBedrock) {
                    if (bedrockRainbowColor.getValue()) {
                        RenderUtils.drawOutlinedBox(AxisAligned(blockPos), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 1f, height.getValue());
                    } else {
                        RenderUtils.drawOutlinedBox(AxisAligned(blockPos), bedrockColorValue.getColor().getRed() / 255f, bedrockColorValue.getColor().getGreen() / 255f, bedrockColorValue.getColor().getBlue() / 255f, 1f, height.getValue());
                    }
                } else {
                    if (obsidianRainbowColor.getValue()) {
                        RenderUtils.drawOutlinedBox(AxisAligned(blockPos), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 1f, height.getValue());
                    } else {
                        RenderUtils.drawOutlinedBox(AxisAligned(blockPos), obsidianColorValue.getColor().getRed() / 255f, obsidianColorValue.getColor().getGreen() / 255f, obsidianColorValue.getColor().getBlue() / 255f, 1f, height.getValue());
                    }

                }
            });
        }
        if (modes.getMode("Halo").isToggled()) {
            safeHoles.forEach((blockPos, isBedrock) -> {
                if (isBedrock) {
                    if (bedrockRainbowColor.getValue()) {
                        RenderUtils.drawHaloESP(new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 0.1f, 0, height.getValue().floatValue(), groundPlate.getValue(), groundPlateOpacity.getValue(), opacityStart.getValue(), opacityEnd.getValue());
                    } else {
                        RenderUtils.drawHaloESP(new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1), bedrockColorValue.getColor().getRed() / 255f, bedrockColorValue.getColor().getGreen() / 255f, bedrockColorValue.getColor().getBlue() / 255f, 0.1f, 0, height.getValue().floatValue(), groundPlate.getValue(), groundPlateOpacity.getValue(), opacityStart.getValue(), opacityEnd.getValue());
                    }
                } else {
                    if (obsidianRainbowColor.getValue()) {
                        RenderUtils.drawHaloESP(new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 0.1f, 0, height.getValue().floatValue(), groundPlate.getValue(), groundPlateOpacity.getValue(), opacityStart.getValue(), opacityEnd.getValue());
                    } else {
                        RenderUtils.drawHaloESP(new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1), obsidianColorValue.getColor().getRed() / 255f, obsidianColorValue.getColor().getGreen() / 255f, obsidianColorValue.getColor().getBlue() / 255f, 0.1f, 0, height.getValue().floatValue(), groundPlate.getValue(), groundPlateOpacity.getValue(), opacityStart.getValue(), opacityEnd.getValue());
                    }
                }
            });
        }

    });

    //Credit Finz0 && 086
    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static AxisAlignedBB AxisAligned(BlockPos pos) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX,
                pos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY, pos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ,
                pos.getX() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosX,
                pos.getY() + (1) - Minecraft.getMinecraft().getRenderManager().viewerPosY,
                pos.getZ() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosZ);
        return bb;
    }
}


class HoleCheck implements Runnable {

    public static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void run() {
        int range = (int) Math.ceil(HoleESP.sphereRange.getValue());
        List<BlockPos> blockPosList = HoleESP.getSphere(HoleESP.getPlayerPos(), range, range, false, true, 0);
        HashMap<BlockPos, EnumFacing> doubleHoles = new HashMap<>();


        blockPosList.stream().filter(n -> mc.world.getBlockState(n).getBlock() == Blocks.AIR && mc.world.getBlockState(n.up()).getBlock() == Blocks.AIR).forEach(n -> {
            List<EnumFacing> facings = new ArrayList<>();

            //Check how many sides of the initial hole are air blocks, if there is exactly 1 air block, its potential double hole
            if (mc.world.getBlockState(n.west()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(n.west().up()).getBlock().equals(Blocks.AIR)) {
                facings.add(EnumFacing.WEST);
            }
            if (mc.world.getBlockState(n.east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(n.east().up()).getBlock().equals(Blocks.AIR)) {
                facings.add(EnumFacing.EAST);
            }

            if (mc.world.getBlockState(n.north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(n.north().up()).getBlock().equals(Blocks.AIR)) {
                facings.add(EnumFacing.NORTH);
            }

            if (mc.world.getBlockState(n.south()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(n.south().up()).getBlock().equals(Blocks.AIR)) {
                facings.add(EnumFacing.SOUTH);
            }

            if(facings.size() == 1){
               EnumFacing enumFacing = facings.get(0);
                List<EnumFacing> facings1 = new ArrayList<>();

                //Check how many sides of the initial hole are air blocks, if there is exactly 1 air block, its potential double hole
                if (mc.world.getBlockState(n.offset(facings.get(0)).west()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(n.offset(facings.get(0)).west().up()).getBlock().equals(Blocks.AIR)) {
                    facings1.add(EnumFacing.WEST);
                }
                if (mc.world.getBlockState(n.offset(facings.get(0)).east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(n.offset(facings.get(0)).east().up()).getBlock().equals(Blocks.AIR)) {
                    facings1.add(EnumFacing.EAST);
                }

                if (mc.world.getBlockState(n.offset(facings.get(0)).north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(n.offset(facings.get(0)).north().up()).getBlock().equals(Blocks.AIR)) {
                    facings1.add(EnumFacing.NORTH);
                }

                if (mc.world.getBlockState(n.offset(facings.get(0)).south()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(n.offset(facings.get(0)).south().up()).getBlock().equals(Blocks.AIR)) {
                    facings1.add(EnumFacing.SOUTH);
                }
                if(facings1.size() == 1){
                    ChatUtils.message("Found Double Hole at: " + n.getX() + " " + n.getY() + " " + n.getZ() + " facing: " + facings.get(0));
                    HoleESP.doubleHoles.add(new AxisAlignedBB(n.getX(), n.getY(), n.getZ(), n.offset(facings.get(0)).getX(), n.offset(facings.get(0)).getY(), n.offset(facings.get(0)).getZ()));
                }
            }


        });


    }



}
