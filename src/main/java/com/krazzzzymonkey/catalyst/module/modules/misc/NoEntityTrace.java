package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoEntityTrace extends Modules {

    private static BooleanValue pickaxeOnly;
    private static BooleanValue obsidian;
    private static BooleanValue enderChests;

    public NoEntityTrace() {
        super("NoEntityTrace", ModuleCategory.MISC, "Allows you to mine or place blocks through entities");
        pickaxeOnly = new BooleanValue("Pickaxe", true, "Only active when you are holding a pickaxe");
        obsidian = new BooleanValue("Obsidian", true, "Only active when you are holding Obsidian");
        enderChests = new BooleanValue("EnderChest", true, "Only active when you are holding ender chests");

        this.addValue(pickaxeOnly, obsidian, enderChests);
    }

    public static boolean shouldEnable = false;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        shouldEnable = (pickaxeOnly.getValue() && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemPickaxe ||
                        obsidian.getValue() && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock) Minecraft.getMinecraft().player.getHeldItemMainhand().getItem()).getBlock() instanceof BlockObsidian ||
                        enderChests.getValue() && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock) Minecraft.getMinecraft().player.getHeldItemMainhand().getItem()).getBlock() instanceof BlockEnderChest);
    });


}
