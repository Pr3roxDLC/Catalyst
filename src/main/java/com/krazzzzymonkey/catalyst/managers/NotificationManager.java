package com.krazzzzymonkey.catalyst.managers;

import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.utils.visual.Notification;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationManager {

    static Minecraft mc = Minecraft.getMinecraft();

    private final List<Notification> notifications = new ArrayList<Notification>();

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if(mc.player == null || mc.world == null)return;

        int i = 1;

        notifications.addAll(Notification.notifications);
        Collections.reverse(notifications);

        for (Notification notification : notifications){
            notification.render(i*35);
            i++;
        }
        notifications.clear();
    });


}
