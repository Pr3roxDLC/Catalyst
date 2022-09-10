package com.krazzzzymonkey.catalyst.handler;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.GuiCustom;
import com.krazzzzymonkey.catalyst.gui.GuiCustomButton;
import com.krazzzzymonkey.catalyst.gui.GuiFakeMain;
import com.krazzzzymonkey.catalyst.lib.Reference;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.render.CustomMainMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CMMEventHandler {
    public long displayMs = -1;

    Field guiField;
    GuiCustom customMainMenu;

    public CMMEventHandler() {
        try {
            guiField = GuiScreenEvent.class.getDeclaredField("gui");
            guiField.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void openGui(GuiOpenEvent event) {
        if(ModuleManager.getModule("CustomMainMenu").isToggled() && CustomMainMenu.mode.getMode("Image").isToggled()) {
            if (event.getGui() instanceof GuiMainMenu) {

                customMainMenu = Main.config.getGUI("mainmenu");

                if (customMainMenu != null) {
                    event.setGui(customMainMenu);
                }
            } else if (event.getGui() instanceof GuiCustom) {
                GuiCustom custom = (GuiCustom) event.getGui();

                GuiCustom target = Main.config.getGUI(custom.guiConfig.name);
                if (target != custom) {
                    event.setGui(target);
                }
            }
        }
    }

    GuiCustom actualGui;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void initGuiPostEarly(InitGuiEvent.Post event) {
        if(ModuleManager.getModule("CustomMainMenu").isToggled()  && CustomMainMenu.mode.getMode("Image").isToggled() ) {
            if (event.getGui() instanceof GuiCustom) {
                GuiCustom custom = (GuiCustom) event.getGui();
                if (custom.guiConfig.name.equals("mainmenu")) {
                    event.setButtonList(new ArrayList<GuiButton>());
                    actualGui = custom;
                    try {
                        guiField.set(event, new GuiFakeMain());
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void initGuiPost(InitGuiEvent.Post event) {
        if(ModuleManager.getModule("CustomMainMenu").isToggled()  && CustomMainMenu.mode.getMode("Image").isToggled()) {
            if (event.getGui() instanceof GuiFakeMain) {
                GuiFakeMain fake = (GuiFakeMain) event.getGui();

                HashMap<Integer, GuiButton> removedButtons = new HashMap<Integer, GuiButton>();

                Iterator<GuiButton> iterator = event.getButtonList().iterator();
                while (iterator.hasNext()) {
                    GuiButton o = iterator.next();
                    GuiButton b = o;
                    if (!(b instanceof GuiCustomButton)) {
                        iterator.remove();
                        removedButtons.put(b.id, b);
                        if (b.id == Reference.OPEN_EYE_BUTTON && Loader.isModLoaded(Reference.OPEN_EYE_MODID)) {
                            // OpenEye
                            Main.logger.log(Level.DEBUG, "Found OpenEye button, use a wrapped button to config this. (" + b.id + ")");
                        } else if (b.id == Reference.VERSION_CHECKER_BUTTON && Loader.isModLoaded(Reference.VERSION_CHECKER_MODID)) {
                            // VersionChecker
                            Main.logger.log(Level.DEBUG, "Found VersionChecker button, use a wrapped button to config this. (" + b.id + ")");
                        } else {
                            // Others
                            Main.logger.log(Level.DEBUG, "Found unsupported button, use a wrapped button to config this. (" + b.id + ")");
                        }
                    }
                }
                //   System.out.println("Actual button list: " + actualGui.getButtonList());

           /* for (GuiButton o : actualGui.getButtonList()) {
                System.out.println(o.toString());
                if (o instanceof GuiCustomWrappedButton) {
                    GuiCustomWrappedButton b = (GuiCustomWrappedButton) o;
                    Main.logger.log(Level.DEBUG, "Initiating Wrapped Button " + b.wrappedButtonID + " with " + removedButtons.get(b.wrappedButtonID));
                    b.init(removedButtons.get(b.wrappedButtonID));
                }
            }*/
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderScreenPost(DrawScreenEvent.Post event) {
        if(ModuleManager.getModule("CustomMainMenu").isToggled()  && CustomMainMenu.mode.getMode("Image").isToggled()) {
            if (displayMs != -1) {
                if (System.currentTimeMillis() - displayMs < 5000) {
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Error loading config file, see console for more information", 0, 80, 16711680);
                } else {
                    displayMs = -1;
                }
            }
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (ModuleManager.getModule("CustomMainMenu").isToggled()  && CustomMainMenu.mode.getMode("Image").isToggled()) {
            if (event.phase == TickEvent.Phase.END) {
                if (Main.config != null)
                    Main.config.tick();
            }
        }
    }
}
