package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayDeque;

public class StorageESP extends Modules {

    private final int maxChests = 1000;
    public boolean shouldInform = true;
    private TileEntityChest openChest;
    private final ArrayDeque<TileEntityChest> emptyChests = new ArrayDeque<TileEntityChest>();
    private final ArrayDeque<TileEntityChest> nonEmptyChests = new ArrayDeque<TileEntityChest>();

    private ColorValue chestColor;
    private BooleanValue chestRainbow;

    private ColorValue enderChestColor;
    private BooleanValue enderChestRainbow;

    private ColorValue trappedChestColor;
    private BooleanValue trappedChestRainbow;

    private ColorValue shulkerBoxColor;
    private BooleanValue shulkerBoxRainbow;

    public StorageESP() {
        super("StorageESP", ModuleCategory.RENDER, "Shows where storage containers are");

        this.chestColor = new ColorValue("ChestColor", Color.YELLOW, "Changes the color chests");
        this.chestRainbow = new BooleanValue("ChestRainbow", false, "Makes chests cycles through colors");

        this.trappedChestColor = new ColorValue("TrappedChestColor", Color.RED, "Changes the color of trapped chests");
        this.trappedChestRainbow = new BooleanValue("TrappedChestRainbow", false, "Makes trapped chests cycle through colors");

        this.enderChestColor = new ColorValue("EnderChestColor", Color.MAGENTA, "Changes the color ender chests");
        this.enderChestRainbow = new BooleanValue("EnderChestRainbow", false, "Makes ender chests cycle through colors");

        this.shulkerBoxColor = new ColorValue("ShulkerBoxColor", Color.PINK, "Changes the color of shulker boxes");
        this.shulkerBoxRainbow = new BooleanValue("ShulkerBoxRainbow", false, "Makes shulker boxes cycle through colors");

        this.addValue(chestColor, chestRainbow, trappedChestColor, trappedChestRainbow, enderChestColor, enderChestRainbow, shulkerBoxColor, shulkerBoxRainbow);
    }

    @Override
    public void onEnable() {
        shouldInform = true;
        emptyChests.clear();
        nonEmptyChests.clear();
        super.onEnable();
    }

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        int chests = 0;
        for (int i = 0; i < Wrapper.INSTANCE.world().loadedTileEntityList.size(); i++) {
            TileEntity tileEntity = Wrapper.INSTANCE.world().loadedTileEntityList.get(i);
            if (chests >= maxChests) {
                break;
            }

            if (tileEntity instanceof TileEntityChest) {
                chests++;
                TileEntityChest chest = (TileEntityChest) tileEntity;
                boolean trapped = chest.getChestType() == BlockChest.Type.TRAP;

                if (trapped) {
                    drawESP(trappedChestRainbow, trappedChestColor, chest);

                } else {
                    drawESP(chestRainbow, chestColor, chest);

                }

            } else if (tileEntity instanceof TileEntityEnderChest) {
                chests++;

                drawESP(enderChestRainbow, enderChestColor, tileEntity);

            } else if (tileEntity instanceof TileEntityShulkerBox) {
                chests++;
                drawESP(shulkerBoxRainbow, shulkerBoxColor, tileEntity);

            }
        }

        for (int i = 0; i < Wrapper.INSTANCE.world().loadedEntityList.size(); i++) {
            Entity entity = Wrapper.INSTANCE.world().loadedEntityList.get(i);
            if (chests >= maxChests) {
                break;
            }
            if (entity instanceof EntityMinecartChest) {
                chests++;
                drawESP(chestRainbow, chestColor, entity);
            }
        }

        if (chests >= maxChests && shouldInform) {
            ChatUtils.message("Only rendering " + maxChests + " chests to prevent lag.");
            shouldInform = false;
        } else if (chests < maxChests) {
            shouldInform = true;
        }

    });

    private void drawESP(BooleanValue RainbowValue, ColorValue colorValue, TileEntity tileEntity) {
        if (RainbowValue.getValue()) {
            RenderUtils.drawBlockESP(tileEntity.getPos(), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 1);
        } else {
            RenderUtils.drawBlockESP(tileEntity.getPos(), colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1);
        }
    }

    private void drawESP(BooleanValue RainbowValue, ColorValue colorValue, Entity entity) {
        if (RainbowValue.getValue()) {
            RenderUtils.drawBlockESP(entity.getPosition(), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 1);
        } else {
            RenderUtils.drawBlockESP(entity.getPosition(), colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1);
        }
    }
}
