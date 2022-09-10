package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ESP extends Modules {

    //General Settings
    public static ModeValue Mode;
    public static BooleanValue Players;
    public static BooleanValue Mobs;
    public static BooleanValue Combat;
    public static BooleanValue ThirdPerson;
    //Can be removed since now Rainbow mode comes with Colors

    //WireFrame Settings
    public static DoubleValue lineWidth;



    //HALO Settings
    public static ColorValue colorStart = new ColorValue("Color", new Color(255, 255, 254), "Changes the color of esp");
    public static BooleanValue colorStartRainbow = new BooleanValue("ColorRainbow", false, "Makes esp cycle through colors");
    public static ColorValue colorEnd = new ColorValue("EndColor", new Color(255, 255, 254), "Ending color of halo esp mode");
    public static BooleanValue colorEndRainbow = new BooleanValue("EndColorRainbow", false, "Ending color of halo esp mode will change its hue");
    public static IntegerValue opacityStart = new IntegerValue("StartOpacity", 100, 0, 100, "Renders opacity of halo esp mode");
    public static IntegerValue opacityEnd = new IntegerValue("EndOpacity", 0, 0, 100, "Renders ending opacity of halo esp mode");
    public static DoubleValue startOffset = new DoubleValue("StartOffset", 0, 0, 2f, "Renders starting height of halo esp mode");
    public static DoubleValue endOffset = new DoubleValue("EndOffset", 0, 0, 2f, "Renders ending height of halo esp mode");
    public static BooleanValue groundPlate = new BooleanValue("DrawBottomQuad", true, "Renders bottom side of halo esp mode");
    public static IntegerValue groundPlateOpacity = new IntegerValue("BottomQuadOpacity", 50, 0, 100, "Renders opacity of bottom side halo esp");


    //TODO MERGE WITH OTHER ESP MODULES
    //TODO FIX HALO NOT WORKING WITH NAMETAGS
    public ESP() {
        super("ESP", ModuleCategory.RENDER, "Shows you where entities are");
        Players = new BooleanValue("Players", true, "");

        Mobs = new BooleanValue("Mobs", false, "Renders esp on entities");
        Combat = new BooleanValue("InCombat", false, "Changes esp color if in combat");
        ThirdPerson = new BooleanValue("ThirdPerson", false, "Renders esp on yourself if in third person");
        Mode = new ModeValue("ESPMode", new Mode("Rect", true), new Mode("Glow", false), new Mode("CSGO", false), new Mode("Diamond", false), new Mode("Box", false), new Mode("Halo", false), new Mode("WireFrame", false), new Mode("Outline", false));
        lineWidth = new DoubleValue("LineWidth", 1, 1, 10, "Width of line in wireframe esp mode");
        this.addValue(Mode, lineWidth, Players, Mobs, Combat, ThirdPerson, colorStart, colorStartRainbow, opacityStart, colorEnd, colorEndRainbow, opacityEnd, startOffset, endOffset, groundPlate, groundPlateOpacity);

    }

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        float ticks = e.getPartialTicks();
        ArrayList<Entity> mobs = new ArrayList<>(Wrapper.INSTANCE.world().loadedEntityList.stream().filter(n -> !(n instanceof EntityPlayer)).collect(Collectors.toList()));
        ArrayList<Entity> players = new ArrayList<>(Wrapper.INSTANCE.world().loadedEntityList.stream().filter(n -> n instanceof EntityPlayer).filter(n -> (n != Wrapper.INSTANCE.player()) || (mc.gameSettings.thirdPersonView != 0) && ThirdPerson.getValue()).collect(Collectors.toList()));

        float red = 1;
        float green = 1;
        float blue = 1;
        Color startColor;
        Color endColor;


        if (colorStartRainbow.getValue()) {
            red = ColorUtils.rainbow().getRed() / 255f;
            green = ColorUtils.rainbow().getGreen() / 255f;
            blue = ColorUtils.rainbow().getBlue() / 255f;
            startColor = ColorUtils.rainbow();

        } else {
            red = colorStart.getColor().getRed() / 255f;
            green = colorStart.getColor().getGreen() / 255f;
            blue = colorStart.getColor().getBlue() / 255f;
            startColor = colorStart.getColor();
        }

        if (colorEndRainbow.getValue()) {
            endColor = ColorUtils.rainbow();
        } else {
            endColor = colorEnd.getColor();
        }

        for (Entity entity : mobs) {
            entity.setGlowing(Mobs.getValue() && Mode.getMode("Glow").isToggled());
            if (Mobs.getValue() && Mode.getMode("CSGO").isToggled())
                RenderUtils.drawESPCSGO(entity, red, green, blue, 1.0f, ticks, lineWidth.getValue().floatValue());
            if (Mobs.getValue() && Mode.getMode("Rect").isToggled())
                RenderUtils.drawPlayerESPRect(entity, red, green, blue, 1.0f, ticks, lineWidth.getValue().floatValue());
            if (Mobs.getValue() && Mode.getMode("Diamond").isToggled())
                RenderUtils.drawESPDiamond(entity, red, green, blue, 1.0f, ticks, lineWidth.getValue().floatValue());
            if (Mobs.getValue() && Mode.getMode("Box").isToggled())
                RenderUtils.drawOutlineBox(entity, red, green, blue, ticks, lineWidth.getValue().floatValue());
            if (Mobs.getValue() && Mode.getMode("Halo").isToggled())
                RenderUtils.drawHaloESP(entity, startColor, endColor, 0, startOffset.getValue().floatValue(), endOffset.getValue().floatValue(), groundPlate.getValue(), groundPlateOpacity.getValue(), opacityStart.getValue(), opacityEnd.getValue());

        }
        for (Entity entity : players) {
            if (Players.getValue() && Mode.getMode("CSGO").isToggled())
                RenderUtils.drawESPCSGO(entity, red, green, blue, 1.0f, ticks, lineWidth.getValue().floatValue());
            if (Players.getValue() && Mode.getMode("Rect").isToggled())
                RenderUtils.drawPlayerESPRect(entity, red, green, blue, 1.0f, ticks, lineWidth.getValue().floatValue());
            if (Players.getValue() && Mode.getMode("Diamond").isToggled())
                RenderUtils.drawESPDiamond(entity, red, green, blue, 1.0f, ticks, lineWidth.getValue().floatValue());
            if (Players.getValue() && Mode.getMode("Box").isToggled())
                RenderUtils.drawOutlineBox(entity, red, green, blue, ticks, lineWidth.getValue().floatValue());
            if (Players.getValue() && Mode.getMode("Halo").isToggled())
                RenderUtils.drawHaloESP(entity, startColor, endColor, 0, startOffset.getValue().floatValue(), endOffset.getValue().floatValue(), groundPlate.getValue(), groundPlateOpacity.getValue(), opacityStart.getValue(), opacityEnd.getValue());

        }
    });

    @Override
    public void onDisable() {
        if(mc.world == null)return;
        for (Object object : Wrapper.INSTANCE.world().loadedEntityList) {
            Entity entity = (Entity) object;
            if (entity.isGlowing()) {
                entity.setGlowing(false);
            }
        }
        super.onDisable();
    }


    public static void renderOne(final float lineWidth) {
        checkSetupFBO();
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void renderTwo() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6914);
    }

    public static void renderThree() {
        GL11.glStencilFunc(514, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void renderFour(final Color color) {
        setColor(color);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0f, -2000000.0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
    }

    public static void renderFive() {
        GL11.glPolygonOffset(1.0f, 2000000.0f);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }

    public static void setColor(final Color color) {
        GL11.glColor4d(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static void checkSetupFBO() {
        final Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    private static void setupFBO(final Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        final int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
    }

}
