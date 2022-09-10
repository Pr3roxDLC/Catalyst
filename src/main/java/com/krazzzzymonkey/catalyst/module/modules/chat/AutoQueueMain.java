package com.krazzzzymonkey.catalyst.module.modules.chat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


//TODO ADD DIFFERENT SERVER MODES
public class AutoQueueMain extends Modules {

    private BooleanValue showMessage;
    private IntegerValue delay;

    public AutoQueueMain() {
        super("AutoQueueMain", ModuleCategory.CHAT, "Automatically sends \"/queue main\" while waiting in 2b2t queue");
        this.showMessage = new BooleanValue("Show Message Sent", true, "Shows a debug message when the command is sent");
        this.delay = new IntegerValue("Delay", 50, 1, 500, "The delay at which the messages get sent");
        this.addValue(showMessage, delay);
    }

    int Delay = 0;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        Delay++;
        if (Delay > delay.getValue()) {
            if (this.shouldSendMessage(Minecraft.getMinecraft().player)) {
                Minecraft.getMinecraft().player.sendChatMessage("/queue main");
                if (showMessage.getValue()) {
                    ChatUtils.message("Sent: /queue main");
                }
            }
            Delay = 0;
        }

    });

    private boolean shouldSendMessage(EntityPlayer player) {
        boolean inEnd = player.dimension == 1;
        boolean atCorrectPosition = player.getPosition().equals(new Vec3i(0, 240, 0));
        return inEnd && atCorrectPosition;
    }
}
