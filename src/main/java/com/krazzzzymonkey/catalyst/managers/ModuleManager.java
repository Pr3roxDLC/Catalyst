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
import com.krazzzzymonkey.catalyst.module.modules.render.*;
import com.krazzzzymonkey.catalyst.module.modules.render.XRay;
import com.krazzzzymonkey.catalyst.module.modules.world.*;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.Value;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleManager {
    public static dev.tigr.simpleevents.EventManager EVENT_MANAGER = new dev.tigr.simpleevents.EventManager(); // Create new EventManager
    private static Modules toggleModule = null;
    private static ArrayList<Modules> modules;
    private static Modules mixinProxy;
    private GuiManager guiManager;
    private HudEditorManager hudManager;

    private ClickGuiScreen guiScreen;
    private HudGuiScreen hudGuiScreen;

    public ModuleManager() {
        MinecraftForge.EVENT_BUS.register(ModuleManager.class);
        EVENT_MANAGER.register(this);
        modules = new ArrayList<Modules>();
        addModule(new NoRender());
        addModule(new Crosshair());
        addModule(new PopChams());
        addModule(new BossStack());
        addModule(new Profile());
        addModule(new XRay());
        addModule(new SkeletonESP());
        addModule(new Anchor());
        addModule(new LiquidInteract());
        addModule(new AutoWeb());
        addModule(new ToggleMessages());
        addModule(new Burrow());
        addModule(new DurabilityAlert());
        addModule(new AutoMend());
        addModule(new BreakESP());
        addModule(new AutoCrystalRewrite());
        addModule(new FakePlayer());
        //addModule(new AutoDupe());
        addModule(new ArmorHUD());
        //addModule(new CustomMainMenu());
        addModule(new AcidMode());
        addModule(new LostFocus());
        addModule(new SafeWalk());
        addModule(new Graphs());
        addModule(new PacketFly());
        addModule(new NoChat());
        addModule(new EntitySpeed());
        addModule(new NewChunks());
        addModule(new LogoutSpots());
        addModule(new PortalGodMode());
        addModule(new NoSwing());
        addModule(new MultiTask());
        addModule(new ShulkerPreview());
        addModule(new AutoTool());
        addModule(new RenderChams());
        addModule(new ItemChams());
        addModule(new EntityControl());
        addModule(new NoSlow());
        addModule(new CrystalPlaceSpeed());
        addModule(new ReverseStep());
        addModule(new Step());
        addModule(new EnchantColor());
        addModule(new CameraClip());
        addModule(new SelfTrap());
        addModule(new AutoQueueMain());
        addModule(new PVPModules());
        addModule(new IceSpeed());
        addModule(new HoleFill());
        addModule(new AutoTrap());
        addModule(new NoGlobalSounds());
        addModule(new EnderChestMiner());
        addModule(new ObsidianReplace());
        addModule(new AutoHotbarRefill());
        addModule(new Surround());
        addModule(new MapTooltip());
        addModule(new Coordinates());
        addModule(new Greeter());
        addModule(new PortalChat());
        addModule(new NoRotate());
        addModule(new ShulkerNuker());
        addModule(new DeathAnnouncer());
        addModule(new AutoEat());
        addModule(new MiddleClickFriends());
        addModule(new VisualRange());
        addModule(new XCarry());
        addModule(new TotemPopCounter());
        addModule(new FastPlace());
        addModule(new BobIntensity());
        addModule(new AutoRespawn());
        addModule(new RPC());
        addModule(new HoleESP());
        addModule(new AutoCrystal());
        addModule(new AutoGG());
        addModule(new Announcer());
        addModule(new Nametags());
        addModule(new TabFriends());
        addModule(new ChatSuffix());
        addModule(new InventoryWalk());
        addModule(new ChatTimeStamps());
        addModule(new CustomChat());
        addModule(new InvPreview());
        addModule(new Timer());
        addModule(new FancyChat());
        addModule(new PVPInfo());
        addModule(new FastXP());
        addModule(new FastFall());
        addModule(new ElytraFly());
        addModule(new BowRelease());
        addModule(new ActiveModules());
        addModule(new Watermark());
        addModule(new CustomFOV());
        addModule(new ChatMention());
        addModule(new LowOffHand());
        //addModule(new LowMainHand());
        addModule(new Trajectories());
        addModule(new ESP());
        addModule(new ItemESP());
        addModule(new StorageESP());
        addModule(new Tracers());
        addModule(new FullBright());
        addModule(new Insulter());
        addModule(new Criticals());
        addModule(new KillAura());
        addModule(new Velocity());
        addModule(new Reach());
        addModule(new AutoSprint());
        addModule(new ChestStealer());
        addModule(new Nuker());
        addModule(new Blink());
        addModule(new Scaffold());
        addModule(new Freecam());
        addModule(new BlockOverlay());
        addModule(new PacketCanceller());
        addModule(new PlayerRadar());
        addModule(new AutoWalk());
        addModule(new Jesus());
        addModule(new FastBreak());
        addModule(new Disconnect());
        addModule(new PlayerInfo());
        addModule(new ClickGui());
        addModule(new Offhand());
        addModule(new CustomFont());
        addModule(new Speed());
        addModule(new DispenserMeta());
      //  addModule(new NetherSky());
        addModule(new FogColors());
        addModule(new HudEditor());
        addModule(new Flight());
        addModule(new AutoArmor());
        addModule(new HopperNuker());
        addModule(new DonkeyDrop());
        addModule(new DonkeyFinder());
        addModule(new NoEntityTrace());
        addModule(new TargetHUD());
        addModule(new BarrierView());
        addModule(new Breadcrumbs());
        addModule(new Viewmodel());
       // addModule(new CevBreaker());
        addModule(new Sounds());
        addModule(new PotionEffects());
        addModule(new BurrowESP());
        addModule(new MiddleClickPearl());
        //addModule(new Notifications());
        addModule(new ChorusControl());
        addModule(new AntiLevitation());
        addModule(new AspectRatio());
        addModule(new PingSpoof());
        addModule(new TunnelESP());
        //addModule(new EntityTrails());
        addModule(new AntiHunger());
        addModule(new Quiver());
       // addModule(new HoleBreakNotifier());
       // addModule(new NewPacketFly());
        addModule(new NoFall());
        //addModule(new NoCluster());
        addModule(new InventoryCleaner());
        addModule(new PearlBait());
        modules.sort(Comparator.comparing(Modules::getModuleName));

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
                cmp = Wrapper.INSTANCE.fontRenderer().getStringWidth(s2) - Wrapper.INSTANCE.fontRenderer().getStringWidth(s1);
            }
            return (cmp != 0) ? cmp : s2.compareTo(s1);
        });
        return list;
    }

    public static void addModule(Modules module) {
        modules.add(module);
    }

    public static ArrayList<Modules> getModules() {
        return modules;
    }

    public static Modules getToggledModules() {
        return toggleModule;
    }

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



    public static Class getModuleClass(String clazz) {
        for(Modules m : ModuleManager.getModules()){
            if(m.getModuleName().equals(clazz)){
                return m.getClass();
            }
        }
        return null;
    }
}
