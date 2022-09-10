package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PVPModules extends Modules {
    private BooleanValue bAutoCrystal;
    private BooleanValue bAutoObsidian;
    private BooleanValue bHoleFill;
    private BooleanValue bAutoTrap;
    private BooleanValue bSelfTrap;
    private BooleanValue bObsidianReplace;
    private BooleanValue bKillAura;
    private BooleanValue bAutoWeb;

    private Number xOffset;
    private Number yOffset;
    private BooleanValue Short;

    public PVPModules() {
        super("PVPModules", ModuleCategory.HUD, "Displays if a pvp module is enabled or not", true);
        this.Short = new BooleanValue("Abbreviated", true, "Abbreviates the module names");
        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("Y Offset", 190.0);
        this.bAutoCrystal = new BooleanValue("AutoCrystal", true, "Shows if auto crystal is toggled");
        this.bAutoObsidian = new BooleanValue("Surround", true, "Shows if auto obsidian is toggled");
        this.bHoleFill = new BooleanValue("HoleFill", true, "Shows if hole fill is toggled");
        this.bAutoTrap = new BooleanValue("AutoTrap", true, "Shows if auto trap is toggled");
        this.bSelfTrap = new BooleanValue("SelfTrap", true, "Shows if self trap is toggled");
        this.bAutoWeb = new BooleanValue("AutoWeb", true, "Shows if auto web is toggled");
        this.bObsidianReplace = new BooleanValue("ObsidianReplace", true, "Shows if obsidian replace is toggled");
        this.bKillAura = new BooleanValue("KillAura", true, "Shows if kill aura is toggled");
        this.addValue(Short, bAutoCrystal, bAutoObsidian, bHoleFill, bAutoTrap, bSelfTrap, bAutoWeb, bObsidianReplace, bKillAura, xOffset, yOffset);
    }

    String AutoCrystal = "AutoCrystal: ";
    String AutoObsidian = "Surround: ";
    String HoleFill = "HoleFill: ";
    String AutoTrap = "AutoTrap: ";

    String ObsidianReplace = "ObsidianReplace: ";
    String KillAura = "KillAura: ";
    String SelfTrap = "SelfTrap: ";
    String AutoWeb = "AutoWeb: ";

    String autoCrystal = "AutoCrystal: ";
    String autoObsidian = "Surround: ";
    String holeFill = "HoleFill: ";
    String autoTrap = "AutoTrap: ";
    String obsidianReplace = "ObsidianReplace: ";
    String killAura = "KillAura: ";
    String selfTrap = "SelfTrap: ";
    String autoWeb = "AutoWeb: ";


    String off = "\u00A7cOFF";
    String on = "\u00A7aON";

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Short.getValue()) {
            AutoCrystal = "CA: ";
            AutoObsidian = "SU: ";
            HoleFill = "HF: ";
            AutoTrap = "AT: ";
            ObsidianReplace = "OR: ";
            KillAura = "KA: ";
            SelfTrap = "ST: ";
            AutoWeb = "AW: ";

        } else {
            AutoCrystal = "AutoCrystal: ";
            AutoObsidian = "Surround: ";
            HoleFill = "HoleFill: ";
            AutoTrap = "AutoTrap: ";
            ObsidianReplace = "ObsidianReplace: ";
            KillAura = "KillAura: ";
            SelfTrap = "SelfTrap: ";
            AutoWeb = "AutoWeb: ";
        }

    });

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {


        if (ModuleManager.getModule("AutoCrystal").isToggled() || ModuleManager.getModule("AutoCrystalRewrite").isToggled()) {
            autoCrystal = AutoCrystal.concat(on);
        } else autoCrystal = AutoCrystal.concat(off);
        if (ModuleManager.getModule("Surround").isToggled()) {
            autoObsidian = AutoObsidian.concat(on);
        } else autoObsidian = AutoObsidian.concat(off);
        if (ModuleManager.getModule("HoleFill").isToggled()) {
            holeFill = HoleFill.concat(on);
        } else holeFill = HoleFill.concat(off);
        if (ModuleManager.getModule("AutoTrap").isToggled()) {
            autoTrap = AutoTrap.concat(on);
        } else autoTrap = AutoTrap.concat(off);
        if (ModuleManager.getModule("SelfTrap").isToggled()) {
            selfTrap = SelfTrap.concat(on);
        } else selfTrap = SelfTrap.concat(off);
        if (ModuleManager.getModule("ObsidianReplace").isToggled()) {
            obsidianReplace = ObsidianReplace.concat(on);
        } else obsidianReplace = ObsidianReplace.concat(off);
        if (ModuleManager.getModule("KillAura").isToggled()) {
            killAura = KillAura.concat(on);
        } else killAura = KillAura.concat(off);
        if (ModuleManager.getModule("AutoWeb").isToggled()) {
            autoWeb = AutoWeb.concat(on);
        } else autoWeb = AutoWeb.concat(off);


        int y = yOffset.getValue().intValue();
        int x = xOffset.getValue().intValue();
        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {

            RenderUtils.drawRect(x, y, x + 50, y + 14,
                ColorUtils.color(0, 0, 0, 100));

            if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(x, x + 50, y, y + 14))) {
                isAlreadyDragging = true;
            }

            if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                isAlreadyDragging = false;
            }

            if (!isAlreadyDragging || isDragging) {
                if (MouseUtils.isMouseOver(x, x + 50, y, y + 14)) {
                    isDragging = true;
                }


                if (MouseUtils.isLeftClicked() && isDragging) {
                    finalMouseX = MouseUtils.getMouseX();
                    finalMouseY = MouseUtils.getMouseY();

                    xOffset.value = (double) finalMouseX - 50 / 2;
                    yOffset.value = (double) finalMouseY;
                    MouseUtils.isDragging = true;
                } else isDragging = false;

            }
        }
        if (ModuleManager.getModule("CustomFont").isToggled()) {
            if (bHoleFill.getValue()) {
                Main.fontRenderer.drawStringWithShadow(holeFill, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bKillAura.getValue()) {
                Main.fontRenderer.drawStringWithShadow(killAura, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bSelfTrap.getValue()) {
                Main.fontRenderer.drawStringWithShadow(selfTrap, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bAutoWeb.getValue()) {
                Main.fontRenderer.drawStringWithShadow(autoWeb, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bAutoTrap.getValue()) {
                Main.fontRenderer.drawStringWithShadow(autoTrap, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bAutoCrystal.getValue()) {
                Main.fontRenderer.drawStringWithShadow(autoCrystal, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bAutoObsidian.getValue()) {
                Main.fontRenderer.drawStringWithShadow(autoObsidian, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bObsidianReplace.getValue()) {
                Main.fontRenderer.drawStringWithShadow(obsidianReplace, xOffset.getValue().intValue(), y, -1);
            }
        } else {
            if (bHoleFill.getValue()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(holeFill, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bKillAura.getValue()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(killAura, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bSelfTrap.getValue()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(selfTrap, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bAutoWeb.getValue()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(autoWeb, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bAutoTrap.getValue()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(autoTrap, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bAutoCrystal.getValue()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(autoCrystal, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bAutoObsidian.getValue()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(autoObsidian, xOffset.getValue().intValue(), y, -1);
                y += 12;
            }
            if (bObsidianReplace.getValue()) {
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(obsidianReplace, xOffset.getValue().intValue(), y, -1);
            }
        }

    });
}
