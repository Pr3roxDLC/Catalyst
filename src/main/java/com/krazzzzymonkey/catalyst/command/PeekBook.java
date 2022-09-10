package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.util.EnumHand;

import java.util.Optional;

public class PeekBook extends Command {

    public static Minecraft mc = Minecraft.getMinecraft();


    public PeekBook() {
        super("bookpeek");
    }

    @Override
    public void runCommand(String s, String[] args) {



        if (args[0].equals("")) {

            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemWrittenBook) {
                ItemStack itemStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
                new Thread(() -> {
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    mc.displayGuiScreen(new GuiScreenBook(mc.player, itemStack, false));
                }).start();


            } else {
                if (mc.objectMouseOver.entityHit instanceof EntityItemFrame) {
                    EntityItemFrame itemFrame = (EntityItemFrame) mc.objectMouseOver.entityHit;
                    ItemStack itemStack = itemFrame.getDisplayedItem();

                    if (itemStack.getItem() instanceof ItemWrittenBook) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(200L);
                            } catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            mc.displayGuiScreen(new GuiScreenBook(mc.player, itemStack, false));
                        }).start();
                    }
                }

            }
        }

        if(args.length == 1 && !args[0].equals("")){
            String playerName = args[0];
            Optional<EntityPlayer> player = mc.world.playerEntities.stream().filter(n -> n.getName().equals(playerName)).findFirst();
            if(player.isPresent()){
                ItemStack itemStack = player.get().getHeldItem(EnumHand.MAIN_HAND);
                if(itemStack.getItem() instanceof ItemWrittenBook){
                    new Thread(() -> {
                        try {
                            Thread.sleep(200L);
                        } catch (InterruptedException interruptedException) {
                            // empty catch block
                        }
                        mc.displayGuiScreen(new GuiScreenBook(mc.player, itemStack, false));
                    }).start();
                }else{
                    ChatUtils.error(playerName + " is not holding a book");
                }
            }else{
                ChatUtils.error("Player must be in render distance");
            }

        }

    }

    @Override
    public String getDescription() {
        return "Allows you to peek into any book in your inventory, in an item frame or in the hand of another player";
    }

    @Override
    public String getSyntax() {
        return "bookpeek [Player]";
    }
}
