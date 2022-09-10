package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderModelEntityLivingEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.EntityUtils;

import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SkeletonESP extends Modules {

    private final DoubleValue lineWidth;
    private ColorValue colorValue;
    private BooleanValue rainbow;
    private ColorValue friendColorValue;
    private BooleanValue friendRainbow;

    public SkeletonESP() {
        super("SkeletonESP", ModuleCategory.RENDER, "Renders a skeleton for players");
        this.lineWidth = new DoubleValue("LineWidth", 3, 1, 10, "The width of skeleton esp");
        this.colorValue = new ColorValue("Color", Color.RED, "The color of skeleton esp");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes skeleton esp cycle through colors");
        this.friendColorValue = new ColorValue("FriendColor", Color.CYAN, "The color of skeleton esp for friends");
        this.friendRainbow = new BooleanValue("FriendRainbow", false, "Makes skeleton esp cycle through colors for friends");

        this.addValue(lineWidth, colorValue, rainbow, friendColorValue, friendRainbow);
    }

    private final Map<EntityPlayer, float[][]> rotationList = new HashMap<>();

    @Override
    public void onDisable() {
        rotationList.clear();
        super.onDisable();
    }

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {

        Color color = colorValue.getColor();
        Color friendColor = friendColorValue.getColor();

        if (rainbow.getValue()) color = ColorUtils.rainbow();
        if (friendRainbow.getValue()) friendColor = ColorUtils.rainbow();


        for (EntityPlayer entity : mc.world.playerEntities) {
            if (entity != null && entity != mc.getRenderViewEntity() && entity.isEntityAlive() && !entity.isPlayerSleeping() && !entity.isElytraFlying()) {
                if (rotationList.get(entity) != null && mc.player.getDistanceSq(entity) < 2500) {
                    renderSkeleton(entity, rotationList.get(entity), FriendManager.friendsList.contains(entity.getName()) ? friendColor : color);
                }
            }
        }

    });

    @EventHandler
    private final EventListener<RenderModelEntityLivingEvent> onRenderModel = new EventListener<>(event -> {
        if (event.getEntityLivingBase() instanceof EntityPlayer) {
            if (event.getModelBase() instanceof ModelBiped) {
                ModelBiped biped = (ModelBiped) event.getModelBase();
                EntityPlayer player = (EntityPlayer) event.getEntityLivingBase();
                float[][] rotations = RenderUtils.getBipedRotations(biped);
                rotationList.put(player, rotations);
            }
        }
    });

    private void renderSkeleton(EntityPlayer player, float[][] rotations, Color color) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth.getValue().floatValue());
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);


        Vec3d interpolatedVec = EntityUtils.getInterpolatedLinearVec(player, mc.getRenderPartialTicks());

        double pX = interpolatedVec.x - mc.getRenderManager().renderPosX;
        double pY = interpolatedVec.y - mc.getRenderManager().renderPosY;
        double pZ = interpolatedVec.z - mc.getRenderManager().renderPosZ;

        GlStateManager.translate(pX, pY, pZ);
        GlStateManager.rotate(-player.renderYawOffset, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0D, 0.0D, player.isSneaking() ? -0.235D : 0.0D);
        float sneak = player.isSneaking() ? 0.6F : 0.75F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.125D, sneak, 0.0D);
        if (rotations[3][0] != 0.0F) {
            GlStateManager.rotate(rotations[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[3][1] != 0.0F) {
            GlStateManager.rotate(rotations[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[3][2] != 0.0F) {
            GlStateManager.rotate(rotations[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, (-sneak), 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.125D, sneak, 0.0D);
        if (rotations[4][0] != 0.0F) {
            GlStateManager.rotate(rotations[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[4][1] != 0.0F) {
            GlStateManager.rotate(rotations[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[4][2] != 0.0F) {
            GlStateManager.rotate(rotations[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, (-sneak), 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.translate(0.0D, 0.0D, player.isSneaking() ? 0.25D : 0.0D);
        GlStateManager.pushMatrix();
        double sneakOffset = 0.0;
        if (player.isSneaking()) {
            sneakOffset = -0.05;
        }

        GlStateManager.translate(0.0D, sneakOffset, player.isSneaking() ? -0.01725D : 0.0D);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.375D, sneak + 0.55D, 0.0D);
        if (rotations[1][0] != 0.0F) {
            GlStateManager.rotate(rotations[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[1][1] != 0.0F) {
            GlStateManager.rotate(rotations[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[1][2] != 0.0F) {
            GlStateManager.rotate(-rotations[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, -0.5D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.375D, sneak + 0.55D, 0.0D);
        if (rotations[2][0] != 0.0F) {
            GlStateManager.rotate(rotations[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        if (rotations[2][1] != 0.0F) {
            GlStateManager.rotate(rotations[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (rotations[2][2] != 0.0F) {
            GlStateManager.rotate(-rotations[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, -0.5D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
        if (rotations[0][0] != 0.0F) {
            GlStateManager.rotate(rotations[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, 0.3D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.rotate(player.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);

        if (player.isSneaking()) {
            sneakOffset = -0.16175D;
        }

        GlStateManager.translate(0.0D, sneakOffset, player.isSneaking() ? -0.48025D : 0.0D);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak, 0.0D);
        GlStateManager.glBegin(3);
        GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
        GL11.glVertex3d(0.125D, 0.0D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak, 0.0D);
        GlStateManager.glBegin(3);
        GL11.glVertex3d(0.0D, 0.0D, 0.0D);
        GL11.glVertex3d(0.0D, 0.55D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
        GlStateManager.glBegin(3);
        GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
        GL11.glVertex3d(0.375D, 0.0D, 0.0D);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
    }
}
