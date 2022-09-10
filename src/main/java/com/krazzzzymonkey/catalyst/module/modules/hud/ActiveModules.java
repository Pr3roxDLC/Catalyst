package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class ActiveModules extends Modules {

    public BooleanValue rainbow;
    private ColorValue colorValue;
    private DoubleValue hueOffset;
    private IntegerValue rainbowSpeed;

    private Number xOffset;
    private Number yOffset;
    private BooleanValue toggleModules;

    public ActiveModules() {
        super("ActiveModules", ModuleCategory.HUD, "Displays active modules on hud", true);
        this.colorValue = new ColorValue("Color", Color.CYAN, "Changes Color of the active modules");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes the active modules cycle through colors ");
        this.rainbowSpeed = new IntegerValue("RainbowSpeed", 100, 0, 100, "The speed at which the rainbow is");
        this.hueOffset = new DoubleValue("HueOffset", 0.999, 0.1D, 1D, "The difference of color between each module when in active modules");

        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("y Offset", 15.0);
        this.toggleModules = new BooleanValue("ToggleModule", true, "Displays when a module is toggled in the bottom right of the screen");

        this.addValue(hueOffset, rainbowSpeed, colorValue, rainbow, toggleModules, xOffset, yOffset);
    }


    int color;
    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;


    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;
        float offset = 0;

        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());

        color = colorValue.getColor().getRGB();


        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();


        GL11.glPushMatrix();


        for (Modules hack : ModuleManager.getSortedHacks()) {
            String extraInfo = ChatColor.GRAY + " " + hack.getExtraInfo();



         /*   if (hack.getModuleName().equals("Blink")) {
                modeName = " \u00a77" + Blink.INSTANCE.packets.size();
            } else if (hack.getModuleName().equals("AutoCrystalRewrite")) {
                if (AutoCrystalRewrite.INSTANCE.ezTarget == null) {
                    modeName = "";
                } else {
                    modeName = " \u00a77" + AutoCrystalRewrite.INSTANCE.ezTarget.getName();
                }
            } else if (hack.getModuleName().equals("KillAura")) {
                if (KillAura.target == null || !mc.world.getLoadedEntityList().contains(KillAura.target)) {
                    modeName = "";
                } else {
                    modeName = " \u00a77" + KillAura.target.getName();
                }
            } else if (hack.getModuleName().equals("AutoTrap")) {
                if (AutoTrap.target == null) {
                    modeName = "";
                } else {
                    modeName = " \u00a77" + AutoTrap.target.getName();
                }
           }else if(hack.getModuleName().equals("Timer")){
                if(Timer.INSTANCE != null) {
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    modeName = "  \u00a77" + formatter.format(Timer.INSTANCE.getMultiplier());
                }else{
                    modeName = "";
                }
            } else*/


            if (ModuleManager.getModule("CustomFont").isToggled()) {
                if (xPos > sr.getScaledWidth() / 2)
                    xPos = xOffset.getValue().intValue() - Main.fontRenderer.getStringWidth(extraInfo + hack.getModuleName());
                Main.fontRenderer.drawStringWithShadow(hack.getModuleName() + extraInfo, xPos, yPos, rainbow.getValue() ? ColorUtils.rainbow(offset, rainbowSpeed.getValue()).getRGB() : color);
            } else {
                if (xPos > sr.getScaledWidth() / 2)
                    xPos = xOffset.getValue().intValue() - Wrapper.INSTANCE.fontRenderer().getStringWidth(extraInfo + hack.getModuleName());
                Wrapper.INSTANCE.fontRenderer().drawStringWithShadow(hack.getModuleName() + extraInfo, xPos, yPos, rainbow.getValue() ? ColorUtils.HUDRainbow(offset, rainbowSpeed.getValue()).getRGB() : color);
            }
            offset += hueOffset.getValue() / 10;

            if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {
                if (ModuleManager.getModule("CustomFont").isToggled()) {
                    RenderUtils.drawRect(xPos, yPos, xPos + Main.fontRenderer.getStringWidth(hack.getModuleName() + extraInfo), yPos + 12,
                        ColorUtils.color(0, 0, 0, 100));
                } else
                    RenderUtils.drawRect(xPos, yPos, xPos + Wrapper.INSTANCE.fontRenderer().getStringWidth(hack.getModuleName() + extraInfo), yPos + 12,
                        ColorUtils.color(0, 0, 0, 100));
                if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(hack.getModuleName() + extraInfo), yPos, yPos + 12))) {
                    isAlreadyDragging = true;
                }

                if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                    isAlreadyDragging = false;
                }

                if (!isAlreadyDragging || isDragging) {
                    if (MouseUtils.isMouseOver(xPos, xPos + Main.fontRenderer.getStringWidth(hack.getModuleName() + extraInfo), yPos, yPos + 12)) {
                        isDragging = true;
                    }


                    if (MouseUtils.isLeftClicked() && isDragging) {
                        finalMouseX = MouseUtils.getMouseX();
                        finalMouseY = MouseUtils.getMouseY();


                        xOffset.value = (double) finalMouseX;

                        yOffset.value = (double) finalMouseY;
                        MouseUtils.isDragging = true;
                    } else isDragging = false;

                }
            }

            if (yOffset.getValue() > sr.getScaledHeight() / 2) {
                yPos -= 12;
            } else {
                yPos += 12;
            }

        }
        GL11.glPopMatrix();

        if (toggleModules.getValue()) {
            Modules toggledModules = ModuleManager.getToggledModules();
            if (toggledModules != null) {
                GL11.glPushMatrix();
                RenderUtils.drawToggleModule(ChatColor.GRAY + (toggledModules.isToggled() ? toggledModules.getModuleName() + " - " + ChatColor.GREEN + "Enabled" : "\u00a77" + toggledModules.getModuleName() + " - " + ChatColor.RED + "Disabled"));
                GL11.glPopMatrix();
            }

        }
    });
}






