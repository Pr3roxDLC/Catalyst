package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DeathAnnouncer extends Modules {

    private final BooleanValue clientSide;

    public DeathAnnouncer() {
        super("DeathAnnouncer", ModuleCategory.CHAT, "Announces who dies in render distance (ClientSide)");
        this.clientSide = new BooleanValue("ClientSide", true, "Only shows the message clientside");
        this.addValue(clientSide);

    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Minecraft.getMinecraft().player == null) return;
        if (event.getEntity() instanceof EntityPlayer) {
            if (clientSide.getValue()) {
                ChatUtils.message(event.getEntity().getName() + " Just died in your render distance!");
            } else {
                Minecraft.getMinecraft().player.sendChatMessage(event.getEntity().getName() + " Just died in my render distance!");
            }
        }
    }

}
