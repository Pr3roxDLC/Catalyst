package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.module.modules.render.ShulkerPreview;
import com.krazzzzymonkey.catalyst.utils.BlockUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumHand;


import java.util.Optional;

public class Peek extends Command {

    public static Minecraft mc = Minecraft.getMinecraft();

    public Peek() {
        super("peek");
    }

    @Override
    public void runCommand(String s, String[] args) {

        if (args[0].equals("")) {

            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemShulkerBox) {
                ItemStack itemStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
                ItemShulkerBox itemShulkerBox = (ItemShulkerBox) itemStack.item;
                displayInv(itemStack, itemStack.getDisplayName());
            } else {
                if (mc.objectMouseOver.entityHit instanceof EntityItemFrame) {
                    EntityItemFrame itemFrame = (EntityItemFrame) mc.objectMouseOver.entityHit;
                    ItemStack itemStack = itemFrame.getDisplayedItem();
                    if (itemStack.getItem() instanceof ItemShulkerBox) {
                        displayInv(itemStack, itemStack.getDisplayName());
                    }
                }
            }
        }

        if(args.length == 1 && !args[0].equals("")){
            String playerName = args[0];
         Optional<EntityPlayer> player = mc.world.playerEntities.stream().filter(n -> n.getName().equals(playerName)).findFirst();
         if(player.isPresent()){
           ItemStack itemStack = player.get().getHeldItem(EnumHand.MAIN_HAND);
           if(itemStack.getItem() instanceof ItemShulkerBox){
               displayInv(itemStack, itemStack.getDisplayName());
           }else{
               ChatUtils.error(playerName + " is not holding a shulkerbox");
           }
         }else{
             ChatUtils.error("Player must be in render distance");
         }

        }

    }


    public static void displayInv(ItemStack stack, String name) {
        try {
            Item item = stack.getItem();
            TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
            ItemShulkerBox shulker = (ItemShulkerBox) item;
            entityBox.blockType = shulker.getBlock();
            entityBox.setWorld(mc.world);
            ItemStackHelper.loadAllItems(stack.getTagCompound().getCompoundTag("BlockEntityTag"), entityBox.items);
            entityBox.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
            entityBox.setCustomName(name == null ? stack.getDisplayName() : name);
            new Thread(() -> {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                mc.player.displayGUIChest(entityBox);
            }).start();
        } catch (Exception exception) {
            // empty catch block
        }
    }


    @Override
    public String getDescription() {
        return "Allows you to peek into shulkers";
    }

    @Override
    public String getSyntax() {
        return "peek <player>";
    }
}
