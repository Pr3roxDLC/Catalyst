package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;


public class TotemPopCounter extends Modules {

    private ModeValue mode;
    private ModeValue arrayLocation;

    public TotemPopCounter() {
        super("TotemPopCounter", ModuleCategory.HUD, "Shows you how many totems a player has popped in render distance", true);
        this.mode = new ModeValue("Mode", new Mode("ArrayList", true), new Mode("Chat", false),  new Mode("Gui", false));
        this.arrayLocation = new ModeValue("ArrayLocation", new Mode("TopLeft", false), new Mode("TopRight", false),  new Mode("BottomLeft", false), new Mode("BottomRight", true));
        this.addValue(mode, arrayLocation);
    }

    public HashMap<String, Integer> TotemPopContainer = new HashMap<String, Integer>();
    private final HashMap<String, Integer> TotemPopContainerArray = new HashMap<String, Integer>();
    Entity entity;
    int count;
    int fade;
boolean isDead= false;
    int colorRect = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F);
    int colorRect2 = ColorUtils.color(0.0F, 0.0F, 0.0F, 0.5F);


    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {

        Packet packet = e.getPacket();

        if (packet instanceof SPacketEntityStatus) {
            SPacketEntityStatus sPacketEntityStatus = (SPacketEntityStatus) packet;

            if (sPacketEntityStatus.getOpCode() == 35) {
                entity = sPacketEntityStatus.getEntity(Minecraft.getMinecraft().world);

                if (sPacketEntityStatus != null) {


                    fade = 1;
                    count = 1;

                    if (TotemPopContainer.containsKey(entity.getName())) {
                        count = TotemPopContainer.get(entity.getName()).intValue();
                        TotemPopContainer.put(entity.getName(), ++count);
                    } else {
                        TotemPopContainer.put(entity.getName(), count);
                    }
                    TotemPopContainerArray.put(entity.getName(), count);
                    if(mode.getMode("Chat").isToggled()) {
                        if (count > 1) {
                            ChatUtils.message(ChatColor.GOLD + entity.getName() + ChatColor.WHITE + " just popped " + ChatColor.RED + count + ChatColor.WHITE +" totems.");

                        } else {
                            ChatUtils.message(ChatColor.GOLD + entity.getName() + ChatColor.WHITE + " just popped " + ChatColor.RED + count + ChatColor.WHITE +" totem.");
                        }
                    }
                }
            }
        }

    });

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        int y = 2;
        int x = 0;
        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());
        if(arrayLocation.getMode("BottomLeft").isToggled()) {
            y = sr.getScaledHeight() - 10;
        }
        if(arrayLocation.getMode("BottomRight").isToggled()) {
            y = sr.getScaledHeight() - 10;
        }
        if(fade > 1){
            if(mode.getMode("Gui").isToggled()) {
                if (count > 1) {
                    RenderUtils.drawStringWithRect(entity.getName() + " just popped " + "\u00A76\u00A7l" + count + "\u00A7r totems!", sr.getScaledWidth() / 2 - (Wrapper.INSTANCE.fontRenderer().getStringWidth(entity.getName() + " just popped " + count + " totem!") / 2), 10, ClickGui.getColor(),
                            colorRect, colorRect2);
                } else {
                    RenderUtils.drawStringWithRect(entity.getName() + " just popped " + "\u00A76\u00A7l" + count + "\u00A7r totem!", sr.getScaledWidth() / 2 - (Wrapper.INSTANCE.fontRenderer().getStringWidth(entity.getName() + " just popped " + count + " totem!") / 2), 10, ClickGui.getColor(),
                            colorRect, colorRect2);
                }
            }
        }

        for(Object o : Wrapper.INSTANCE.world().loadedEntityList) {
            if (o instanceof EntityPlayer) {
                if(entity == null)return;
                EntityPlayer player = (EntityPlayer) o;
                if ((TotemPopContainerArray.toString().contains(player.getName()))) {
                    String str;

                    String name = player.getGameProfile().getName();
                    isDead = player.isDead || player.getHealth() <= 0;

                    if(!isDead) {

                        if (count > 1) {
                            str = name + " popped " + "\u00A76\u00A7l" + TotemPopContainerArray.get(player.getName()) + "\u00A7r totems";
                        } else {
                            str = name + " popped " + "\u00A76\u00A7l" +TotemPopContainerArray.get(player.getName()) + "\u00A7r totem";
                        }

                    }else{
                        str = name + "\u00A7c Died\u00A7r after popping " + "\u00A76\u00A7l" + TotemPopContainerArray.get(player.getName()) + "\u00A7r totems";
                        TotemPopContainer.remove(player.getName());
                    }
                    int color;

                    color = ColorUtils.color(255, 255, 255, 255);

                    if(mode.getMode("ArrayList").isToggled()) {
                        if(arrayLocation.getMode("TopRight").isToggled()){
                          x =  sr.getScaledWidth() - Wrapper.INSTANCE.fontRenderer().getStringWidth(str);
                            Wrapper.INSTANCE.fontRenderer().drawString(str, x, y, color);
                          y += 12;
                        }
                        if(arrayLocation.getMode("TopLeft").isToggled()){
                            x =  6;
                            Wrapper.INSTANCE.fontRenderer().drawString(str, x, y, color);
                            y += 12;
                        }
                        if(arrayLocation.getMode("BottomRight").isToggled()){
                            x =  sr.getScaledWidth() - Wrapper.INSTANCE.fontRenderer().getStringWidth(str);
                            Wrapper.INSTANCE.fontRenderer().drawString(str, x, y, color);
                            y += -12;
                        }
                        if(arrayLocation.getMode("BottomLeft").isToggled()){
                            x = 6;
                            Wrapper.INSTANCE.fontRenderer().drawString(str, x, y, color);
                            y += - 12;
                        }

                    }

                }
            }
        }

    });

    boolean isDrawing;
    int clearAll = 0;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)return;

        if(fade == 1){
            isDrawing = true;
                try {
                    TotemPopContainerArray.put(entity.getName(), TotemPopContainer.get(entity.getName()));
                }catch (NullPointerException ignored){
                }
        }
        if(isDrawing){
            fade++;
            if(fade > 500){
                isDrawing = false;
                TotemPopContainerArray.remove(entity.getName());
                fade = 0;
            }
        }
        if(fade == 0){
            clearAll++;
            if(clearAll > 500){
                TotemPopContainerArray.clear();
                clearAll = 0;
            }
        }
    });
}
