package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraft.block.Block;

import java.util.ArrayList;

public class XRay extends Modules {
    public static ArrayList<Block> blocks = new ArrayList<Block>();

    public XRay() {
        super("XRay", ModuleCategory.RENDER, "Allows you to see ores and blocks underground");


        blocks.add(Block.getBlockFromName("coal_ore"));
        blocks.add(Block.getBlockFromName("iron_ore"));
        blocks.add(Block.getBlockFromName("gold_ore"));
        blocks.add(Block.getBlockFromName("redstone_ore"));
        blocks.add(Block.getBlockById(74));
        blocks.add(Block.getBlockFromName("lapis_ore"));
        blocks.add(Block.getBlockFromName("diamond_ore"));
        blocks.add(Block.getBlockFromName("emerald_ore"));
        blocks.add(Block.getBlockFromName("quartz_ore"));
        blocks.add(Block.getBlockFromName("clay"));
        blocks.add(Block.getBlockFromName("glowstone"));
        blocks.add(Block.getBlockById(8));
        blocks.add(Block.getBlockById(9));
        blocks.add(Block.getBlockById(10));
        blocks.add(Block.getBlockById(11));
        blocks.add(Block.getBlockFromName("crafting_table"));
        blocks.add(Block.getBlockById(61));
        blocks.add(Block.getBlockById(62));
        blocks.add(Block.getBlockFromName("torch"));
        blocks.add(Block.getBlockFromName("ladder"));
        blocks.add(Block.getBlockFromName("tnt"));
        blocks.add(Block.getBlockFromName("coal_block"));
        blocks.add(Block.getBlockFromName("iron_block"));
        blocks.add(Block.getBlockFromName("gold_block"));
        blocks.add(Block.getBlockFromName("diamond_block"));
        blocks.add(Block.getBlockFromName("emerald_block"));
        blocks.add(Block.getBlockFromName("redstone_block"));
        blocks.add(Block.getBlockFromName("lapis_block"));
        blocks.add(Block.getBlockFromName("fire"));
        blocks.add(Block.getBlockFromName("mossy_cobblestone"));
        blocks.add(Block.getBlockFromName("mob_spawner"));
        blocks.add(Block.getBlockFromName("end_portal_frame"));
        blocks.add(Block.getBlockFromName("enchanting_table"));
        blocks.add(Block.getBlockFromName("bookshelf"));
        blocks.add(Block.getBlockFromName("command_block"));
        blocks.add(Block.getBlockFromName("bone_block"));
    }


    @Override
    public void onEnable() {
        super.onEnable();
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.renderGlobal.loadRenderers();
    }



}
