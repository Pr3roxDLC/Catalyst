package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.events.RenderModelEntityLivingEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.render.ESP;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;


@Mixin(value = RenderLivingBase.class, priority = 9998)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends MixinRender<T> {


    @Inject(method = "doRender", at = @At("HEAD"))
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callback) {

        if (ModuleManager.getModule("NoRender").isToggled() && ModuleManager.getModule("NoRender").isToggledValue("Enabled")) {
            //System.out.println("NoCluster is active");
            double startDistance = ModuleManager.getModule("NoRender").getDoubleValue("StartingDistance");
            double minOpacity = ModuleManager.getModule("NoRender").getDoubleValue("MinimumOpacity");
            boolean friendsOnly = ModuleManager.getModule("NoRender").isToggledValue("FriendsOnly");
            boolean applyGradient = true;

            Method getFriends = null;
            ArrayList<String> friendsList = FriendManager.friendsList;

            if (friendsOnly && !friendsList.contains(entity.getName())) applyGradient = false;
            if (entity == Minecraft.getMinecraft().player) applyGradient = false;
            //System.out.println("Passed the friendsOnly Check : " + applyGradient);
//            if (startDistance > Minecraft.getMinecraft().player.getDistance(entity)) applyGradient = false;
//            System.out.println("Passed the distance check : " + applyGradient);
//            if (!(entity instanceof EntityOtherPlayerMP)) applyGradient = false;
//            System.out.println("Passed the Entity Check : " + applyGradient);
            if (applyGradient) {
                //System.out.println("Applying Gradient");
                float distancePercentage = (float) (Minecraft.getMinecraft().player.getDistance(entity) / startDistance);
                float alpha = (float) (minOpacity + distancePercentage * (1 - minOpacity));
                GL11.glColor4f(1, 1, 1, alpha);
            }


        }

        if (ModuleManager.getModule("Chams").isToggled() && ModuleManager.getModule("Chams").isToggledMode("Mode", "Basic")) {
            if (entity instanceof EntityLivingBase) {
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(1.0F, -4000000F);
            }
        }

        if (ModuleManager.getModule("Chams").isToggled() && ModuleManager.getModule("Chams").isToggledMode("Mode", "Color")) {
            if (entity instanceof EntityLivingBase) {
                GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                GlStateManager.disableLighting();
                if (!ModuleManager.getModule("Chams").isToggledValue("RainbowColor")) {
                    GL11.glColor4f(ModuleManager.getModule("Chams").getColorValue("SingleColor").getRed() / 255f, ModuleManager.getModule("Chams").getColorValue("SingleColor").getGreen() / 255f, ModuleManager.getModule("Chams").getColorValue("SingleColor").getBlue() / 255f, 1f);
                } else {
                    Method getRainbow;
                    Color rainbow = ColorUtils.rainbow();
                    GL11.glColor4f(rainbow.getRed() / 255f, rainbow.getGreen() / 255f, rainbow.getBlue() / 255f, 1f);
                }
                GL11.glPolygonOffset(1.0F, -4000000F);
            }
        }
    }

    @Redirect(method = {"renderModel"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void renderModelHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {


        final RenderModelEntityLivingEvent event = new RenderModelEntityLivingEvent((EntityLivingBase) entityIn, modelBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        EVENT_MANAGER.post(event);
        if (!event.isCancelled()) {
            event.getModelBase().render(entityIn, event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        }


        boolean valid = !(entityIn instanceof EntityPlayer) || ModuleManager.getModule("ESP").isToggledValue("Players");

        if (!(entityIn instanceof EntityPlayer) && !ModuleManager.getModule("ESP").isToggledValue("Mobs"))
            valid = false;
        if (entityIn instanceof EntityPlayer && Minecraft.getMinecraft().player == entityIn && !ModuleManager.getModule("ESP").isToggledValue("ThirdPerson"))
            valid = false;

        if (ModuleManager.getModule("ESP").isToggled() && ModuleManager.getModule("ESP").isToggledMode("ESPMode", "WireFrame")) {
            if (valid) {
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                //GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                if (ModuleManager.getModule("ESP").isToggledValue("ColorStartRainbow")) {
                    Method getRainbow;
                    Color rainbow = ColorUtils.rainbow();
                    GL11.glColor4f(rainbow.getRed() / 255f, rainbow.getGreen() / 255f, rainbow.getBlue() / 255f, 1);
                } else {
                    GL11.glColor4f(ModuleManager.getModule("ESP").getColorValue("Color").getRed() / 255f, ModuleManager.getModule("ESP").getColorValue("Color").getGreen() / 255f, ModuleManager.getModule("ESP").getColorValue("Color").getBlue() / 255f, 255);
                }
                GL11.glLineWidth(ModuleManager.getModule("ESP").getIntegerValue("LineWidth"));
                modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GL11.glPopAttrib();
                GL11.glPopMatrix();

            }
        } else if (ModuleManager.getModule("ClickGui").isToggled() && ModuleManager.getModule("ClickGui").isToggledMode("ESPMode", "Outline")) {

            Minecraft.getMinecraft().gameSettings.fancyGraphics = false;
            GlStateManager.resetColor();
            final Color color = ModuleManager.getModule("ESP").getColorValue("Color");
            ESP.setColor(color);
            ESP.renderOne(ModuleManager.getModule("ESP").getIntegerValue("LineWidth"));
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            ESP.setColor(color);
            ESP.renderTwo();
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            ESP.setColor(color);
            ESP.renderThree();
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            ESP.setColor(color);
            ESP.renderFour(color);
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            ESP.setColor(color);
            ESP.renderFive();
            ESP.setColor(Color.WHITE);

        }


        if (ModuleManager.getModule("Chams").isToggled() && ModuleManager.getModule("Chams").isToggledMode("Mode", "TwoColor")) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(GL11.GL_STENCIL_TEST);

            Color hiddenColor = ModuleManager.getModule("Chams").getColorValue("HiddenColor");
            Color visibleColor = ModuleManager.getModule("Chams").getColorValue("VisibleColor");

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
            GL11.glColor4f((float) hiddenColor.getRed() / 255.0f, (float) hiddenColor.getGreen() / 255.0f, (float) hiddenColor.getBlue() / 255.0f, 1f);
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glColor4f((float) visibleColor.getRed() / 255.0f, (float) visibleColor.getGreen() / 255.0f, (float) visibleColor.getBlue() / 255.0f, 1);
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glPopAttrib();
        } else {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }

        GlStateManager.disableOutlineMode();

    }

    @Inject(method = "doRender", at = @At("RETURN"))
    public void doRenderlast(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callback) {
        if (ModuleManager.getModule("Chams").isToggled() && ModuleManager.getModule("Chams").isToggledMode("Mode", "Basic")) {
            if (entity instanceof EntityLivingBase) {
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(1.0F, 4000000F);
            }
        }
        if (ModuleManager.getModule("Chams").isToggled() && ModuleManager.getModule("Chams").isToggledMode("Mode", "Color")) {
            if (entity instanceof EntityLivingBase) {
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                GL11.glPolygonOffset(1.0F, 4000000F);
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
    }
}
