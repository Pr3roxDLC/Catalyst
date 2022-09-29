package com.krazzzzymonkey.catalyst;

import com.krazzzzymonkey.catalyst.configuration.Config;
import com.krazzzzymonkey.catalyst.configuration.ConfigurationLoader;
import com.krazzzzymonkey.catalyst.events.ClientEvents;
import com.krazzzzymonkey.catalyst.events.CommandEvent;
import com.krazzzzymonkey.catalyst.handler.CMMEventHandler;
import com.krazzzzymonkey.catalyst.managers.*;
import com.krazzzzymonkey.catalyst.managers.accountManager.AccountManager;
import com.krazzzzymonkey.catalyst.managers.accountManager.Standards;
import com.krazzzzymonkey.catalyst.managers.accountManager.config.ConfigValues;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;


@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION, clientSideOnly = true, guiFactory = "com.krazzzzymonkey.catalyst.managers.accountManager.config.GuiFactory", acceptableRemoteVersions = "*")
public class Main {

    public static Configuration altConfig;
    public static final Logger logger = LogManager.getLogger("@name@");

    public static void syncConfig() {
        ConfigValues.CASESENSITIVE = false;
        ConfigValues.ENABLERELOG = false;
        if (altConfig.hasChanged())
            altConfig.save();
    }

    public static final String MODID = "@modid@";
    public static final String NAME = "@name@";
    public static final String VERSION = "@version@";
    public static int initCount = 0;
    public static ModuleManager moduleManager;

    public static CFontRenderer fontRenderer;
    public static CFontRenderer smallFontRenderer;
    public static LuaManager luaManager;


    @Instance(value = MODID)
    public static Main INSTANCE;
    public static CMMEventHandler EVENT_HANDLER;
    public static NotificationManager notificationManager;
    public static ColorUtils ColorEvents;

    private ConfigurationLoader configLoader;
    public static Config config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent E) {


        logger.info("   ____      _        _           _      ____ _ _            _  ");
        logger.info("  / ___|__ _| |_ __ _| |_   _ ___| |_   / ___| (_) ___ _ __ | |_ ");
        logger.info(" | |   / _` | __/ _` | | | | / __| __| | |   | | |/ _ \\ '_ \\| __|");
        logger.info(" | |__| (_| | || (_| | | |_| \\__ \\ |_  | |___| | |  __/ | | | |_ ");
        logger.info("  \\____\\__,_|\\__\\__,_|_|\\__, |___/\\__|  \\____|_|_|\\___|_| |_|\\__|");
        logger.info("                        |___/                                    ");
        Display.setTitle("Initializing " + NAME + " " + VERSION);
        config = new Config();
        // Load Transparent


        ClientEvents clientEvents = new ClientEvents();
        MinecraftForge.EVENT_BUS.register(new CommandEvent());
        MinecraftForge.EVENT_BUS.register(clientEvents);
        MinecraftForge.EVENT_BUS.register(new RotationManager());


        EVENT_HANDLER = new CMMEventHandler();
        ColorEvents = new ColorUtils();
        notificationManager = new NotificationManager();

        MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
        MinecraftForge.EVENT_BUS.register(ColorEvents);
        FMLCommonHandler.instance().bus().register(EVENT_HANDLER);
        FMLCommonHandler.instance().bus().register(ColorEvents);
        ModuleManager.EVENT_MANAGER.register(notificationManager);
        ModuleManager.EVENT_MANAGER.register(clientEvents);

        configLoader = new ConfigurationLoader(config);

        try {
            configLoader.load();
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error while loading config file. Will have to crash here :(.");
            e.printStackTrace();

        }
        altConfig = new Configuration(E.getSuggestedConfigurationFile());
        altConfig.load();
        syncConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent E) throws IOException {
        if (initCount > 0) {
            return;
        }
        Standards.importAccounts();
        TimerManager.INSTANCE = new TimerManager();
        moduleManager = new ModuleManager();
        FileManager.init();
        luaManager = new LuaManager();
        fontRenderer = new CFontRenderer(new Font(FontManager.font, Font.PLAIN, 20), true, true);
        smallFontRenderer = new CFontRenderer(new Font(FontManager.font, Font.PLAIN, 15), true, true);
        AccountManager.init();

        initCount++;
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent E){
        // File file = FileManager.getAssetFile("gui" + File.separator + "watermark.png");

        Display.setTitle(NAME + " " + VERSION);

        Main.moduleManager.getGui();
        Main.moduleManager.getHudGui();
        com.krazzzzymonkey.catalyst.gui.click.ClickGui.onUpdate();
        com.krazzzzymonkey.catalyst.gui.click.HudEditor.onUpdate();
    }

    public void reload() {
        Config backup = config;
        config = new Config();
        configLoader = new ConfigurationLoader(config);
        try {
            configLoader.load();
            EVENT_HANDLER.displayMs = -1;
        } catch (Exception e) {
            e.printStackTrace();

            EVENT_HANDLER.displayMs = System.currentTimeMillis();
            logger.log(Level.ERROR, "Error while loading new config file, trying to keep the old one loaded.");
            config = backup;
        }
    }
}
