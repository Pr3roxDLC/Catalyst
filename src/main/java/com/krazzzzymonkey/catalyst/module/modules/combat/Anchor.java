package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

public class Anchor extends Modules {
    private DoubleValue height;

    public Anchor() {
        super("Anchor", ModuleCategory.COMBAT, "Stops all movement when above a hole");
        this.height = new DoubleValue("Height", 2d, 1d, 8d, "The max height above the hole at which it will activate");
        this.addValue(height);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        double blockX = Math.floor(mc.player.posX);
        double blockZ = Math.floor(mc.player.posZ);

        double offsetX = Math.abs(mc.player.posX - blockX);
        double offsetZ = Math.abs(mc.player.posZ - blockZ);


        if (offsetX < 0.3f || offsetX > 0.7f || offsetZ < 0.3f || offsetZ > 0.7f) return;

        BlockPos playerPos = new BlockPos(blockX, mc.player.posY, blockZ);

        if (mc.world.getBlockState(playerPos).getBlock() != Blocks.AIR) return;

        BlockPos currentBlock = playerPos.down();

        for (int i = 0; i <= height.getValue(); i++) {
            currentBlock = currentBlock.down();
            if (mc.world.getBlockState(currentBlock).getBlock() != Blocks.AIR) {
                if (getUnsafeSides(currentBlock.up())) {
                    mc.player.motionX = 0f;
                    mc.player.motionZ = 0f;
                }
            }
        }
    });


    public static boolean getUnsafeSides(BlockPos pos) {
        // block gotta be air
        if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
            return false;
        }

        // block 1 above gotta be air
        if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
            return false;
        }

        // block 2 above gotta be air
        if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
            return false;
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

        return isSafe;
    }

    private static final BlockPos[] surroundOffset = {
        new BlockPos(0, -1, 0), // down
        new BlockPos(0, 0, -1), // north
        new BlockPos(1, 0, 0), // east
        new BlockPos(0, 0, 1), // south
        new BlockPos(-1, 0, 0) // west
    };

}
