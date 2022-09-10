package com.krazzzzymonkey.catalyst.events;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.account.GuiAccountSelector;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiMainMenu;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.managers.accountManager.Config;
import com.krazzzzymonkey.catalyst.utils.system.Connection;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;


public class ClientEvents {

    ConcurrentSet<Integer> keyList = new ConcurrentSet<>();
    boolean connectionInitialized = false;
    public static GuiScreen mainMenu;
    @SubscribeEvent
    public void guiEvent(InitGuiEvent.Post event) {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiMainMenu) {
            mainMenu =  gui;
            event.getButtonList().add(new GuiButton(21, gui.width / 2 - 100, (gui.height / 4 + 48) + 72 + 12 + 25, 98, 20, "Click Gui"));
            event.getButtonList().add(new GuiButton(20, gui.width / 2 + 2, (gui.height / 4 + 48) + 72 + 12 + 25, 98, 20, "Account Manager"));
        }
    }

    @SubscribeEvent
    public void onClick(ActionPerformedEvent event) {
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == 20) {
            if (Config.getInstance() == null) {
                Config.load();
            }
            Minecraft.getMinecraft().displayGuiScreen(new GuiAccountSelector());
        }
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == 21) {
            Minecraft.getMinecraft().displayGuiScreen(new ClickGuiMainMenu(event.getGui()));
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent t) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiMultiplayer) {
            screen.drawString(Minecraft.getMinecraft().fontRenderer, "Logged in as: " + Minecraft.getMinecraft().getSession().getUsername(), 2, 2, -1);
            if (Minecraft.getMinecraft().getSession().getToken().equals("0")) {
                screen.drawString(Minecraft.getMinecraft().fontRenderer, "Non Premium", 2, 16, ColorUtils.color(247, 77, 77, 255));
            }
        }
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent event) {
        if (event.getModID().equals(Main.MODID)) {
            Main.syncConfig();
        }
    }

    @SubscribeEvent
    public void onClientTickEvent(TickEvent.ClientTickEvent e) {
        ClientTickEvent event = new ClientTickEvent(e.phase);
        try {
            EVENT_MANAGER.post(event);
            if (event.isCancelled()) {
                e.setCanceled(true);
            }
        } catch (Exception ignored) {
            // smh tigers event handler
        }
        if(Minecraft.getMinecraft().currentScreen == null){
            Keyboard.enableRepeatEvents(false);
        }
        for (int key : keyList) {
            if (!Keyboard.isKeyDown(key) && keyList.contains(key)) {
                EVENT_MANAGER.post(new KeyReleaseEvent(key));
                keyList.remove(key);
            }
        }
        if(Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null){
            if(connectionInitialized){
                connectionInitialized = false;
            }
        }else if(!connectionInitialized){
            new Connection();
            connectionInitialized = true;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent e) {
        com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent event = new com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent();
        EVENT_MANAGER.post(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Pre e) {
        com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent.Pre event = new com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent.Pre(e.getType());
        EVENT_MANAGER.post(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Post e) {
        com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent.Post event = new com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent.Post();
        EVENT_MANAGER.post(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text e) {
        com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent.Text event = new com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent.Text();
        EVENT_MANAGER.post(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(net.minecraftforge.client.event.RenderWorldLastEvent e) {
        RenderWorldLastEvent event = new RenderWorldLastEvent(e.getPartialTicks());
        EVENT_MANAGER.post(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e) {

        LeftClickBlockEvent event = new LeftClickBlockEvent(e.getEntityPlayer(), e.getPos(), e.getFace(), e.getHitVec());
        EVENT_MANAGER.post(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @EventHandler
    private final EventListener<KeyDownEvent> onKeyDownEvent = new EventListener<>(e -> {
        try {
            if (Keyboard.isKeyDown(e.getKeyId())) {
                keyList.add(e.getKeyId());
            }
        }catch (Exception ignored){}

    });

    @SubscribeEvent
    public void onFogRender(EntityViewRenderEvent.FogDensity event) {
        RenderFogDensityEvent renderFogDensityEvent = new RenderFogDensityEvent(event.getRenderer(), event.getEntity(), event.getState(), event.getRenderPartialTicks());
        EVENT_MANAGER.post(renderFogDensityEvent);
        if (renderFogDensityEvent.isCancelled()) {
            event.setCanceled(true);
        }
    }
}
