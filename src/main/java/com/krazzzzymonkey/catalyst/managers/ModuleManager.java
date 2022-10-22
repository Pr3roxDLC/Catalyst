package com.krazzzzymonkey.catalyst.managers;


import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.KeyDownEvent;
import com.krazzzzymonkey.catalyst.events.KeyReleaseEvent;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.theme.dark.DarkTheme;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.chat.*;
import com.krazzzzymonkey.catalyst.module.modules.combat.*;
import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.module.modules.gui.HudEditor;
import com.krazzzzymonkey.catalyst.module.modules.hud.*;
import com.krazzzzymonkey.catalyst.module.modules.misc.*;
import com.krazzzzymonkey.catalyst.module.modules.movement.*;
import com.krazzzzymonkey.catalyst.module.modules.player.*;
import com.krazzzzymonkey.catalyst.module.modules.render.XRay;
import com.krazzzzymonkey.catalyst.module.modules.render.*;
import com.krazzzzymonkey.catalyst.module.modules.world.*;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.Value;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ModuleManager {
    public static dev.tigr.simpleevents.EventManager EVENT_MANAGER = new dev.tigr.simpleevents.EventManager(); // Create new EventManager
    private static Modules toggleModule = null;
    private static ArrayList<Modules> modules;
    private static Modules mixinProxy;
    @EventHandler
    private final EventListener<KeyDownEvent> onKeyDownEvent = new EventListener<>(e -> {
        if (Wrapper.INSTANCE.mc().currentScreen != null) {
            return;
        }

        for (Modules module : getModules()) {
            if (module.getKey() == e.getKeyId()) {
                module.toggle();
                toggleModule = module;
            }
        }
    });
    @EventHandler
    private final EventListener<KeyReleaseEvent> onKeyRelease = new EventListener<>(e -> {
        if (Wrapper.INSTANCE.mc().currentScreen != null) {
            return;
        }

        for (Modules module : getModules()) {
            if (module.isBindHold() && module.isToggled()) {
                if (module.getKey() == e.getKey()) {
                    module.toggle();
                    toggleModule = module;
                }
            }
        }
    });
    private GuiManager guiManager;
    private HudEditorManager hudManager;
    private ClickGuiScreen guiScreen;
    private HudGuiScreen hudGuiScreen;

    public ModuleManager() {
        MinecraftForge.EVENT_BUS.register(ModuleManager.class);
        EVENT_MANAGER.register(this);
        modules = new ArrayList<>();
        addModule(NoRender.class);
        addModule(NoRender.class);
        addModule(Crosshair.class);
        addModule(PopChams.class);
        addModule(BossStack.class);
        addModule(Profile.class);
        addModule(XRay.class);
        addModule(SkeletonESP.class);
        addModule(Anchor.class);
        addModule(LiquidInteract.class);
        addModule(AutoWeb.class);
        addModule(ToggleMessages.class);
        addModule(Burrow.class);
        addModule(DurabilityAlert.class);
        addModule(AutoMend.class);
        addModule(BreakESP.class);
        addModule(AutoCrystalRewrite.class);
        addModule(FakePlayer.class);
        //addModule(AutoDupe.class);
        addModule(ArmorHUD.class);
        addModule(CustomMainMenu.class);
        addModule(AcidMode.class);
        addModule(LostFocus.class);
        addModule(SafeWalk.class);
        addModule(Graphs.class);
        addModule(PacketFly.class);
        addModule(NoChat.class);
        addModule(EntitySpeed.class);
        addModule(NewChunks.class);
        addModule(LogoutSpots.class);
        addModule(PortalGodMode.class);
        addModule(NoSwing.class);
        addModule(MultiTask.class);
        addModule(ShulkerPreview.class);
        addModule(AutoTool.class);
        addModule(RenderChams.class);
        addModule(ItemChams.class);
        addModule(EntityControl.class);
        addModule(NoSlow.class);
        addModule(CrystalPlaceSpeed.class);
        addModule(ReverseStep.class);
        addModule(Step.class);
        addModule(EnchantColor.class);
        addModule(CameraClip.class);
        addModule(SelfTrap.class);
        addModule(AutoQueueMain.class);
        addModule(PVPModules.class);
        addModule(IceSpeed.class);
        addModule(HoleFill.class);
        addModule(AutoTrap.class);
        addModule(NoGlobalSounds.class);
        addModule(EnderChestMiner.class);
        addModule(ObsidianReplace.class);
        addModule(AutoHotbarRefill.class);
        addModule(Surround.class);
        addModule(MapTooltip.class);
        addModule(Coordinates.class);
        addModule(Greeter.class);
        addModule(PortalChat.class);
        addModule(NoRotate.class);
        addModule(ShulkerNuker.class);
        addModule(DeathAnnouncer.class);
        addModule(AutoEat.class);
        addModule(MiddleClickFriends.class);
        addModule(VisualRange.class);
        addModule(XCarry.class);
        addModule(TotemPopCounter.class);
        addModule(FastPlace.class);
        addModule(BobIntensity.class);
        addModule(AutoRespawn.class);
        addModule(RPC.class);
        addModule(HoleESP.class);
        addModule(AutoCrystal.class);
        addModule(AutoGG.class);
        addModule(Announcer.class);
        addModule(Nametags.class);
        addModule(TabFriends.class);
        addModule(ChatSuffix.class);
        addModule(InventoryWalk.class);
        addModule(ChatTimeStamps.class);
        addModule(CustomChat.class);
        addModule(InvPreview.class);
        addModule(Timer.class);
        addModule(FancyChat.class);
        addModule(PVPInfo.class);
        addModule(FastXP.class);
        addModule(FastFall.class);
        addModule(ElytraFly.class);
        addModule(BowRelease.class);
        addModule(ActiveModules.class);
        addModule(Watermark.class);
        addModule(CustomFOV.class);
        addModule(ChatMention.class);
        addModule(LowOffHand.class);
        //addModule(LowMainHand.class);
        addModule(Trajectories.class);
        addModule(ESP.class);
        addModule(ItemESP.class);
        addModule(StorageESP.class);
        addModule(Tracers.class);
        addModule(FullBright.class);
        addModule(Insulter.class);
        addModule(Criticals.class);
        addModule(KillAura.class);
        addModule(Velocity.class);
        addModule(Reach.class);
        addModule(AutoSprint.class);
        addModule(ChestStealer.class);
        addModule(Nuker.class);
        addModule(Blink.class);
        addModule(Scaffold.class);
        addModule(Freecam.class);
        addModule(BlockOverlay.class);
        addModule(PacketCanceller.class);
        addModule(PlayerRadar.class);
        addModule(AutoWalk.class);
        addModule(Jesus.class);
        addModule(FastBreak.class);
        addModule(Disconnect.class);
        addModule(PlayerInfo.class);
        addModule(ClickGui.class);
        addModule(Offhand.class);
        addModule(CustomFont.class);
        addModule(Speed.class);
        addModule(DispenserMeta.class);
        // addModule(NetherSky.class);
        addModule(FogColors.class);
        addModule(HudEditor.class);
        addModule(Flight.class);
        addModule(AutoArmor.class);
        addModule(HopperNuker.class);
        addModule(DonkeyDrop.class);
        addModule(DonkeyFinder.class);
        addModule(NoEntityTrace.class);
        addModule(TargetHUD.class);
        addModule(BarrierView.class);
        addModule(Breadcrumbs.class);
        addModule(Viewmodel.class);
        // addModule(CevBreaker.class);
        addModule(Sounds.class);
        addModule(PotionEffects.class);
        addModule(BurrowESP.class);
        addModule(MiddleClickPearl.class);
        //addModule(Notifications.class);
        addModule(ChorusControl.class);
        addModule(AntiLevitation.class);
        addModule(AspectRatio.class);
        addModule(PingSpoof.class);
        addModule(TunnelESP.class);
        //addModule(EntityTrails.class);
        addModule(AntiHunger.class);
        addModule(Quiver.class);
        // addModule(HoleBreakNotifier.class);
        // addModule(NewPacketFly.class);
        addModule(NoFall.class);
        //addModule(NoCluster.class);
        addModule(InventoryCleaner.class);
        addModule(PearlBait.class);
        modules.sort(Comparator.comparing(Modules::getModuleName));

    }

    public static Modules getModule(String name) {
        Modules module = null;
        for (Modules m : getModules()) {
            if (m.getModuleName().equalsIgnoreCase(name)) {
                module = m;
            }
        }
        return module;
    }

    public static List<Modules> getSortedHacks() {
        final List<Modules> list = new ArrayList<Modules>();
        for (final Modules module : getModules()) {
            if (module.isToggled()) {
                if (!module.isDrawn()) {
                    continue;
                }
                list.add(module);
            }
        }
        list.sort((h1, h2) -> {
            String s1 = h1.getModuleName();
            String s2 = h2.getModuleName();
            for (Value value : h1.getValues()) {
                if (value instanceof ModeValue) {
                    ModeValue modeValue = (ModeValue) value;
                    if (!modeValue.getModeName().equals("Priority")) {
                 /*       for (Mode mode : modeValue.getModes()) {
                            if (mode.isToggled()) {
                                s1 = s1 + " " + mode.getName();
                            }
                        }*/
                    }
                }
            }
            for (Value value : h2.getValues()) {
                if (value instanceof ModeValue) {
                    ModeValue modeValue = (ModeValue) value;
                    if (!modeValue.getModeName().equals("Priority")) {
                       /* for (Mode mode : modeValue.getModes()) {
                            if (mode.isToggled()) {
                                s2 = s2 + " " + mode.getName();
                            }
                        }*/
                    }
                }
            }
            final int cmp;
            if (ModuleManager.getModule("CustomFont").isToggled()) {
                cmp = Main.fontRenderer.getStringWidth(s2) - Main.fontRenderer.getStringWidth(s1);
            } else {
                cmp = Wrapper.INSTANCE.fontRenderer().getStringWidth(s2) - Wrapper.INSTANCE.fontRenderer()
                                                                                           .getStringWidth(s1);
            }
            return (cmp != 0) ? cmp : s2.compareTo(s1);
        });
        return list;
    }

    public static void addModule(Class<? extends Modules> clazz) {
        try {
            Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
                                               .filter(c -> c.getParameterCount() == 0)
                                               .findAny()
                                               .orElseThrow(() -> new IllegalArgumentException(
                                                   "Missing no-arg constructor!"));
            Modules instance = (Modules) constructor.newInstance();
            modules.add(instance);
        } catch (Throwable t) {
            System.out.println("Loading module " + clazz.getSimpleName() + " faild with exception: " + t.getMessage());
        }
    }

    public static ArrayList<Modules> getModules() {
        return modules;
    }

    public static Modules getToggledModules() {
        return toggleModule;
    }

    public static Class getModuleClass(String clazz) {
        for (Modules m : ModuleManager.getModules()) {
            if (m.getModuleName().equals(clazz)) {
                return m.getClass();
            }
        }
        return null;
    }

    public void setGuiManager(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    public ClickGuiScreen getGui() {
        if (this.guiManager == null) {
            this.guiManager = new GuiManager();
            this.guiScreen = new ClickGuiScreen();
            ClickGuiScreen.clickGui = this.guiManager;
            this.guiManager.Init();
            this.guiManager.setTheme(new DarkTheme());
        }
        return this.guiManager;
    }

    public HudEditorManager getHudGui() {
        if (this.hudManager == null) {
            this.hudManager = new HudEditorManager();
            this.hudGuiScreen = new HudGuiScreen();
            HudGuiScreen.hudGui = this.hudManager;
            this.hudManager.Initialization();
            this.hudManager.setTheme(new DarkTheme());
        }

        return this.hudManager;
    }
}
