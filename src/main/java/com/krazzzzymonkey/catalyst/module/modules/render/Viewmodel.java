package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.events.RenderItemEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.util.EnumHand;

import java.awt.*;

public class Viewmodel extends Modules {

    public static ColorValue mainColor = new ColorValue("ArmColor", new Color(255, 255, 254), "");
    public static BooleanValue rainbowMain = new BooleanValue("Rainbow", false, "");
    public static IntegerValue alpha = new IntegerValue("Alpha", 100, 0, 100, "");
    public static BooleanValue textured = new BooleanValue("Textured", true, "Should your hand be drawn with a texture");


    DoubleValue mainX = new DoubleValue("xOffsetMain", 1.2, 0.0, 6.0, "");
    DoubleValue mainY = new DoubleValue("yOffsetMain", -0.95, -3.0, 3.0, "");
    DoubleValue mainZ = new DoubleValue("zOffsetMain", -1.45, -5.0, 5.0, "");
    DoubleValue offX = new DoubleValue("xOffsetOffhand", -1.2, -6.0, 0.0, "");
    DoubleValue offY = new DoubleValue("yOffsetOffhand", -0.95, -3.0, 3.0, "");
    DoubleValue offZ = new DoubleValue("zOffsetOffhand", -1.45, -5.0, 5.0, "");
    DoubleValue mainAngel = new DoubleValue("MainhandAngle", 0.0, 0.0, 360.0, "");
    DoubleValue mainRx = new DoubleValue("mainRotationPointX", 0.0, -1.0, 1.0, "");
    DoubleValue mainRy = new DoubleValue("mainRotationPointY", 0.0, -1.0, 1.0, "");
    DoubleValue mainRz = new DoubleValue("mainRotationPointZ", 0.0, -1.0, 1.0, "");
    DoubleValue offAngel = new DoubleValue("OffhandAngle", 0.0, 0.0, 360.0, "");
    DoubleValue offRx = new DoubleValue("offRotationPointX", 0.0, -1.0, 1.0, "");
    DoubleValue offRy = new DoubleValue("offRotationPointY", 0.0, -1.0, 1.0, "");
    DoubleValue offRz = new DoubleValue("offRotationPointZ", 0.0, -1.0, 1.0, "");
    DoubleValue mainScaleX = new DoubleValue("xScaleMain", 1.0, -5.0, 10.0, "");
    DoubleValue mainScaleY = new DoubleValue("yScaleMain", 1.0, -5.0, 10.0, "");
    DoubleValue mainScaleZ = new DoubleValue("zScaleMain", 1.0, -5.0, 10.0, "");
    DoubleValue offScaleX = new DoubleValue("xScaleOffhand", 1.0, -5.0, 10.0, "");
    DoubleValue offScaleY = new DoubleValue("yScaleOffhand", 1.0, -5.0, 10.0, "");
    DoubleValue offScaleZ = new DoubleValue("zScaleOffhand", 1.0, -5.0, 10.0, "");

    public Viewmodel() {
        super("Viewmodel", ModuleCategory.RENDER, "Allows you to customize your viewmodel");
        addValue(mainColor, rainbowMain, alpha, textured, mainX, mainY, mainZ, mainRx, mainRy, mainRz, mainAngel, mainScaleX, mainScaleY, mainScaleZ, offX, offY, offZ, offRx, offRy, offRz, offAngel, offScaleX, offScaleY, offScaleZ);
    }


    @EventHandler
    private final EventListener<RenderItemEvent> onItemRender = new EventListener<>(e -> {
        if (!this.isToggled() || mc.player == null || mc.world == null) return;

        //if mainhand is not active:
        if(!(mc.player.getActiveHand() == EnumHand.MAIN_HAND && mc.player.isHandActive())) {
            e.setMainX(mainX.getValue());
            e.setMainY(mainY.getValue());
            e.setMainZ(mainZ.getValue());

            e.setMainRAngel(mainAngel.getValue());
            e.setMainRx(mainRx.getValue());
            e.setMainRy(mainRy.getValue());
            e.setMainRz(mainRz.getValue());

            e.setMainHandScaleX(mainScaleX.getValue());
            e.setMainHandScaleY(mainScaleY.getValue());
            e.setMainHandScaleZ(mainScaleZ.getValue());
        }
        // if offhand is not active:
        if(!(mc.player.getActiveHand() == EnumHand.OFF_HAND  && mc.player.isHandActive())) {
            e.setOffX(offX.getValue());
            e.setOffY(offY.getValue());
            e.setOffZ(offZ.getValue());

            e.setOffRAngel(offAngel.getValue());
            e.setOffRx(offRx.getValue());
            e.setOffRy(offRy.getValue());
            e.setOffRz(offRz.getValue());

            e.setOffHandScaleX(offScaleX.getValue());
            e.setOffHandScaleY(offScaleY.getValue());
            e.setOffHandScaleZ(offScaleZ.getValue());
        }
    });

}
