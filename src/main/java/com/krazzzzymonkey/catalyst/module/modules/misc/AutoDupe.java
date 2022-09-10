package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;

import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.awt.event.InputEvent;


//TODO MAKE BETTER
public class AutoDupe extends Modules {

    private IntegerValue x;
    private IntegerValue y;

    public AutoDupe() {
        super("AutoDupe", ModuleCategory.MISC, "Does the Crafting Dupe automatically");

        this.x = new IntegerValue("x", 50, 500, 2000, "");
        this.y = new IntegerValue("y", 50, 500, 2000, "");
        this.addValue(x, y);
    }


    int i = 0;
    int r = 0;
    int p = 0;


    int prevAmount;
    Robot robot;
    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        i = 0;
        r = 0;
        p = 0;
        super.onDisable();
    }
    @Override
    public void onEnable() {
        i = 0;
        r = 0;
        p = 0;
        super.onEnable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if(Minecraft.getMinecraft().world == null) this.toggle();
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Minecraft.getMinecraft().currentScreen instanceof GuiGameOver) return;


        ItemStack slot = Minecraft.getMinecraft().player.inventory.getStackInSlot(0);

        i++;
        if (i == 3) {
            if (slot.getItem() == Items.AIR) {
                ChatUtils.warning("No Item in hotbar slot 1!");
                i = 0;
                return;
            }
            Minecraft.getMinecraft().player.rotationPitch = 90;
            prevAmount = slot.getCount();



        }

        if (i > 15 && i < 18){
            Minecraft.getMinecraft().playerController.windowClick(0, 36, 1, ClickType.THROW, Minecraft.getMinecraft().player);
        }
        if (i == 30) {
            Minecraft.getMinecraft().player.rotationPitch = 0;
            Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(Minecraft.getMinecraft().player));
        }
        if(i >= 40 && i < 100 ){
            robot.mouseMove(x.getValue(), y.getValue());
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }

        if(i == 100){
            Minecraft.getMinecraft().getConnection().sendPacket(new CPacketCloseWindow());
        }
        if (i >= 101) {
            Minecraft.getMinecraft().player.inventory.currentItem = 0;

          if(slot.getCount() > 1) {
                Minecraft.getMinecraft().displayGuiScreen(null);

                ChatUtils.message("Successfully duped " + slot.getDisplayName());
            }

            if ( i % 2 == 0 ) {
                r++;
                if (r % 2 == 0) {
                    p++;
                    if (p % 2 == 0) {

                        if (slot.getCount() > 2) {
                            Minecraft.getMinecraft().player.dropItem(false);
                        }
                        if (slot.getCount() == 2) {
                            r = 0;
                            i = 0;
                            p = 0;
                        }
                        if(slot.getCount() == 0){
                            ChatUtils.warning("Could not check if dupe has been successful!");
                        }

                        if(p == 160){
                            ChatUtils.warning("Restarting dupe, Something failed!");
                            r = 0;
                            i= 0;
                            p = 0;
                        }
                    }
                }
            }
        }
    });

}
