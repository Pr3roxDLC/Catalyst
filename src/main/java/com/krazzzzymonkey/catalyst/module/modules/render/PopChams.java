package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.mojang.authlib.GameProfile;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PopChams extends Modules {

    ColorValue color = new ColorValue("ChamColor", Color.CYAN, "The color of the popcham");
    BooleanValue colorRainbow = new BooleanValue("ChamRainbow", false, "Makes the cham cycle through colors");
    ColorValue outlineColor = new ColorValue("OutlineColor", Color.CYAN, "The color of the popcham");
    BooleanValue outlineRainbow = new BooleanValue("OutlineRainbow", false, "Makes the outline cycle through colors");
    BooleanValue self = new BooleanValue("Yourself", false, "Should popchams render when you pop a totem");
    DoubleValue speed = new DoubleValue("Speed", 1, 0.1, 5, "The speed at which the cham should move upwards");
    DoubleValue remain = new DoubleValue("Dissipate", 1, 0.1, 10, "The amount at which the cham should dissipate every tick");
    ModeValue renderMode = new ModeValue("RenderMode", new Mode("Normal", true), new Mode("Wireframe", false), new Mode("Single", false));

    public CopyOnWriteArrayList<PopCham> popList;

    public PopChams() {
        super("PopChams", ModuleCategory.RENDER, "Renders a player cham when someone pops a totem");
        this.addValue(renderMode, color, colorRainbow, outlineColor, outlineRainbow, self, speed, remain);
        this.popList = new CopyOnWriteArrayList<>();

    }

    @EventHandler
    public EventListener<PacketEvent> packetEvent = new EventListener<>(event -> {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus sPacketEntityStatus = (SPacketEntityStatus) event.getPacket();
            if (sPacketEntityStatus.getOpCode() == 35) {
                Entity entity = sPacketEntityStatus.getEntity(Minecraft.getMinecraft().world);
                if (!this.self.getValue() && entity == PopChams.mc.player) {
                    return;
                }
                final EntityPlayer entityPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(entity.getUniqueID(), entity.getName()));
                entityPlayer.copyLocationAndAnglesFrom(entity);
                this.popList.add(new PopCham(entityPlayer));
            }


        }
    });


    @EventHandler
    public EventListener<RenderWorldLastEvent> renderWorldLast = new EventListener<>(event -> {
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5f);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        popList.forEach(person -> {
            person.update(this.popList);
            person.modelPlayer.bipedLeftLegwear.showModel = false;
            person.modelPlayer.bipedRightLegwear.showModel = false;
            person.modelPlayer.bipedLeftArmwear.showModel = false;
            person.modelPlayer.bipedRightArmwear.showModel = false;
            person.modelPlayer.bipedBodyWear.showModel = false;
            person.modelPlayer.bipedHead.showModel = false;
            person.modelPlayer.bipedHeadwear.showModel = true;
            if(colorRainbow.getValue()){
                GlStateManager.color(ColorUtils.rainbow().getRed() / 255.0f, ColorUtils.rainbow().getGreen() / 255.0f, ColorUtils.rainbow().getBlue() / 255.0f, (float) person.alpha / 255.0f);
            }else {
                GlStateManager.color(this.color.getColor().getRed() / 255.0f, this.color.getColor().getGreen() / 255.0f, this.color.getColor().getBlue() / 255.0f, (float) person.alpha / 255.0f);
            }

            if (renderMode.getMode("Normal").isToggled() || renderMode.getMode("Single").isToggled()) {
                GL11.glPolygonMode(1032, 6914);
                this.renderEntity(person.player, person.modelPlayer, person.player.limbSwing, person.player.limbSwingAmount, (float) person.player.ticksExisted, person.player.rotationYawHead, person.player.rotationPitch, 1.0f);

            }
            if (renderMode.getMode("Normal").isToggled() || renderMode.getMode("Wireframe").isToggled()) {

                if(outlineRainbow.getValue()){
                    GlStateManager.color(ColorUtils.rainbow().getRed() / 255.0f, ColorUtils.rainbow().getGreen() / 255.0f, ColorUtils.rainbow().getBlue() / 255.0f, (float) person.alpha / 255.0f);
                }else {
                    GlStateManager.color(this.color.getColor().getRed() / 255.0f, this.color.getColor().getGreen() / 255.0f, this.color.getColor().getBlue() / 255.0f, (float) person.alpha / 255.0f);
                }

                GL11.glPolygonMode(1032, 6913);
                this.renderEntity(person.player, person.modelPlayer, person.player.limbSwing, person.player.limbSwingAmount, (float) person.player.ticksExisted, person.player.rotationYawHead, person.player.rotationPitch, 1.0f);
            }
            GL11.glPolygonMode(1032, 6914);
            return;
        });
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();

    });

    private void renderEntity(final EntityLivingBase entity, final ModelBase modelBase, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        if (modelBase instanceof ModelPlayer) {
            final ModelPlayer modelPlayer = (ModelPlayer) modelBase;
            modelPlayer.bipedBodyWear.showModel = false;
            modelPlayer.bipedLeftLegwear.showModel = false;
            modelPlayer.bipedRightLegwear.showModel = false;
            modelPlayer.bipedLeftArmwear.showModel = false;
            modelPlayer.bipedRightArmwear.showModel = false;
            modelPlayer.bipedHeadwear.showModel = true;
            modelPlayer.bipedHead.showModel = false;
        }
        final float partialTicks = mc.getRenderPartialTicks();
        final double x = entity.posX - mc.getRenderManager().viewerPosX;
        double y = entity.posY - mc.getRenderManager().viewerPosY;
        final double z = entity.posZ - mc.getRenderManager().viewerPosZ;
        GlStateManager.pushMatrix();
        if (entity.isSneaking()) {
            y -= 0.125;
        }
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(180.0f - entity.rotationYaw, 0.0f, 1.0f, 0.0f);
        final float f4 = this.prepareScale(entity, scale);
        final float yaw = entity.rotationYawHead;
        GlStateManager.enableAlpha();
        modelBase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        modelBase.setRotationAngles(limbSwing, limbSwingAmount, 0.0f, yaw, entity.rotationPitch, f4, entity);
        modelBase.render(entity, limbSwing, limbSwingAmount, 0.0f, yaw, entity.rotationPitch, f4);
        GlStateManager.popMatrix();
    }

    private float prepareScale(final EntityLivingBase entity, final float scale) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        final double widthX = entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX;
        final double widthZ = entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ;
        GlStateManager.scale(scale + widthX, scale * entity.height, scale + widthZ);
        final float f = 0.0625f;
        GlStateManager.translate(0.0f, -1.501f, 0.0f);
        return f;
    }

    private class PopCham {
        private double alpha;
        private final EntityPlayer player;
        private final ModelPlayer modelPlayer;

        public PopCham(final EntityPlayer player) {
            this.player = player;
            this.modelPlayer = new ModelPlayer(0.0f, false);
            this.alpha = color.getColor().getAlpha();
        }

        public void update(final CopyOnWriteArrayList<PopCham> arrayList) {
            if (this.alpha <= 0.0) {
                arrayList.remove(this);
                mc.world.removeEntity(this.player);
                return;
            }
            this.alpha -= remain.getValue();
            final EntityPlayer player = this.player;
            player.posY += speed.getValue() / 100;
        }
    }


}
