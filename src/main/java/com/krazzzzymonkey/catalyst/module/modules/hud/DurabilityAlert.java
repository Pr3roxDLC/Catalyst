package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentHashMap;

public class DurabilityAlert extends Modules {

    private BooleanValue friends;
    private BooleanValue yourself;

    ConcurrentHashMap<String, Integer> players;

    public DurabilityAlert() {
        super("DurabilityAlert", ModuleCategory.HUD, "Alerts you when someones armor durability is low");
        this.yourself = new BooleanValue("Yourself", true, "Alerts on low durability of your items");
        this.friends = new BooleanValue("Friends", true, "Alerts on low item durability of friended players");
        this.addValue(yourself, friends);

        this.players = new ConcurrentHashMap<String, Integer>();
    }


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;

        for (final EntityPlayer player : mc.world.playerEntities) {

            for (final ItemStack itemStack : player.getArmorInventoryList()) {
                if (itemStack != null && getDurabilityForDisplay(itemStack) > 0.75 && !this.players.containsKey(player.getName())) {

                    if (player.getName().equalsIgnoreCase(mc.player.getName()) && yourself.getValue()){
                        ChatUtils.message(ChatColor.RED + "You have low durability on your armor!");
                    }else if(FriendManager.friendsList.contains(player.getName()) && !player.getName().equalsIgnoreCase(mc.player.getName()) && friends.getValue()){
                        ChatUtils.message(ChatColor.RED + "Your friend " + ChatColor.AQUA + player.getName() + ChatColor.RED + " has low durability!");
                    }else if(!player.getName().equalsIgnoreCase(mc.player.getName())){
                        ChatUtils.message(ChatColor.GOLD + player.getName() + ChatColor.RED + " has low durability!");
                    }

                    this.players.put(player.getName(), 1500);
                }
            }
        }
        this.players.forEach((name, timeout) -> {
            if (timeout <= 0) {
                this.players.remove(name);
            } else {
                this.players.put(name, timeout - 1);
            }
        });
    });

    public double getDurabilityForDisplay(ItemStack stack) {
        return (double)stack.getItemDamage() / (double)stack.getMaxDamage();
    }

}
