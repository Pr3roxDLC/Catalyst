package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


import java.util.HashSet;


@SuppressWarnings("unused")
public class InventoryCleaner extends Modules {

    public static ModeValue mode = new ModeValue("Mode", new Mode("Blacklist", true), new Mode("Whitelist", false));
    public static ModeValue toggleMode = new ModeValue("ToggleMode", new Mode("OnInventoryOpen", true), new Mode("OnModuleToggle", false), new Mode("OnTick", false));
    public static IntegerValue delay = new IntegerValue("Delay", 5, 0, 40, "The amount of ticks between each drop");

    private static boolean delayPassed = true;
    private static int ticksPassed = 0;
    private static boolean keepCleaning = true;
    //This gets populated by a json file or by the user using a command to add items to it
    public static HashSet<Item> listItems = new HashSet<>();


    public InventoryCleaner() {
        super("InventoryCleaner", ModuleCategory.MISC, "Clears your inventory of unwanted items");
        addValue(mode, toggleMode, delay);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onTick = new EventListener<>(e -> {
        ticksPassed++;
        if (ticksPassed > delay.getValue()) delayPassed = true;
        boolean invOpen = mc.currentScreen instanceof GuiContainer;
        if (invOpen && toggleMode.getMode("OnInventoryOpen").isToggled() && delayPassed) {
            dropNext();
            ticksPassed = 0;
            delayPassed = false;
        }
        if (toggleMode.getMode("OnTick").isToggled() && delayPassed) {
            dropNext();
            ticksPassed = 0;
            delayPassed = false;
        }
        if(toggleMode.getMode("OnModuleToggle").isToggled() && keepCleaning){
            if(!dropNext()){
                keepCleaning=false;
                toggle();
            }
            ticksPassed = 0;
            delayPassed = false;
        }
    });

    @Override
    public void onEnable(){
        super.onEnable();
        if(toggleMode.getMode("OnModuleToggle").isToggled()){
            keepCleaning = true;
        }
    }

    public static boolean dropNext() {
        int slot = 0;
        for (ItemStack itemStack : mc.player.inventory.mainInventory) {
            if (itemStack.getItem() != Items.AIR) {
                if (mode.getMode("Blacklist").isToggled()) {
                    if (listItems.contains(itemStack.item)) {
                        //Fuck you Notch, couldnt just have used 0-9 for hotbar slots
                        if(slot > 8) {
                            mc.playerController.windowClick(0, slot, 1, ClickType.THROW, mc.player);
                        }else{
                            mc.playerController.windowClick(0, slot + 36, 1, ClickType.THROW, mc.player);
                        }
                        System.out.println("Dropping Item from slot: " + slot);
                        return true;
                    }
                }
                if (mode.getMode("Whitelist").isToggled()) {
                    if (!listItems.contains(itemStack.item)) {
                        if(slot > 8) {
                            mc.playerController.windowClick(0, slot, 1, ClickType.THROW, mc.player);
                        }else{
                            mc.playerController.windowClick(0, slot + 36, 1, ClickType.THROW, mc.player);
                        }                        return true;
                    }
                }
            }
            slot++;
        }
        return false;
    }

}



