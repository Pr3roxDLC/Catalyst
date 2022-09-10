package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

//TODO NOT COMPATIBLE WITH LOADER (Outline utils)
@Mixin(ModelEnderCrystal.class)
public class MixinModelEnderCrystal extends ModelBase {

    @Shadow
    private final ModelRenderer glass = new ModelRenderer(this, "glass");

    @Shadow
    private final ModelRenderer cube = new ModelRenderer(this, "cube");

    @Shadow
    private ModelRenderer base;


    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo callbackInfo) {

        this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
        GlStateManager.pushMatrix(); // 1
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.translate(0.0F, -0.5F, 0.0F);
        if (this.base != null) {
            this.base.render(scale);
        }

        Method getRainbow;
        Color rainbow = new Color(-1);
        try {
            Class[] noParams = {};
            getRainbow = ModuleManager.getMixinProxyClass().getMethod("getRainbow", noParams);
            rainbow = (Color) getRainbow.invoke(ModuleManager.getMixinProxyClass(), (Object[]) null);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (ModuleManager.getModule("ESP").isToggled() && ModuleManager.getModule("ESP").isToggledMode("ESPMode", "WireFrame")) {

            boolean valid = !(entityIn instanceof EntityPlayer) || ModuleManager.getModule("ESP").isToggledValue("Players");
            if (!(entityIn instanceof EntityPlayer) && !ModuleManager.getModule("ESP").isToggledValue("Mobs")) valid = false;
            if (entityIn instanceof EntityPlayer && Minecraft.getMinecraft().player == entityIn && !ModuleManager.getModule("ThirdPerson").isToggledValue("Players"))
                valid = false;
            if (valid) {
                GL11.glPushMatrix(); //2
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                //GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                if (ModuleManager.getModule("ESP").isToggledValue("ColorRainbow")) {
                    GL11.glColor4f(rainbow.getRed() / 255f, rainbow.getGreen() / 255f, rainbow.getBlue() / 255f, 255);
                } else {
                    GL11.glColor4f(ModuleManager.getModule("ESP").getColorValue("Color").getRed() / 255f, ModuleManager.getModule("ESP").getColorValue("Color").getGreen() / 255f, ModuleManager.getModule("ESP").getColorValue("Color").getBlue() / 255f, 255);
                }
                GL11.glLineWidth((float) ModuleManager.getModule("ESP").getDoubleValue("LineWidth"));
                GlStateManager.pushMatrix(); //3
                GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
                GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
                this.glass.render(scale);
                GlStateManager.scale(0.875F, 0.875F, 0.875F);
                GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
                GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
                this.glass.render(scale);
                GlStateManager.scale(0.875F, 0.875F, 0.875F);
                GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
                GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
                this.cube.render(scale);
                GlStateManager.popMatrix(); // 2
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GlStateManager.pushMatrix(); // 3
                GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
                GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
                this.glass.render(scale);
                GlStateManager.scale(0.875F, 0.875F, 0.875F);
                GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
                GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
                this.glass.render(scale);
                GlStateManager.scale(0.875F, 0.875F, 0.875F);
                GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
                GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
                this.cube.render(scale);
                GlStateManager.popMatrix(); // 2
                GL11.glPopAttrib();
                GL11.glPopMatrix(); // 1
                //callbackInfo.cancel();
            }
        }
        if (ModuleManager.getModule("ESP").isToggled() && ModuleManager.getModule("ESP").isToggledMode("ESPMode", "Outline")) {

            //Normal Render
            GlStateManager.pushMatrix();
            if (this.base != null)
            {
                this.base.render(scale);
            }
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            float f = 0.875F;
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GlStateManager.popMatrix();


            //Outline Render
            Minecraft.getMinecraft().gameSettings.fancyGraphics = false;
            GlStateManager.resetColor();
            Color color = ModuleManager.getModule("ESP").getColorValue("Color");
            if(ModuleManager.getModule("ESP").isToggledValue("ColorRainbow"))color = rainbow;


            try{
                Class[] params = {Color.class};
                ModuleManager.getMixinProxyClass().getMethod("setColor", params).invoke(ModuleManager.getMixinProxyClass(), color);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

            try{
                Class[] params = {float.class};
                ModuleManager.getMixinProxyClass().getMethod("renderOne", params).invoke(ModuleManager.getMixinProxyClass(), (float) ModuleManager.getModule("ESP").getDoubleValue("LineWidth"));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

            GlStateManager.pushMatrix();

            if (this.base != null) {
                this.base.render(scale);
            }

            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            f = 0.875F;
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GlStateManager.popMatrix();

            try{
                Class[] params = {};
                ModuleManager.getMixinProxyClass().getMethod("renderTwo", params).invoke(ModuleManager.getMixinProxyClass(), (Object[]) null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

            GlStateManager.pushMatrix();

            if (this.base != null) {
                this.base.render(scale);
            }

            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            f = 0.875F;
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GlStateManager.popMatrix();

            try{
                Class[] params = {};
                ModuleManager.getMixinProxyClass().getMethod("renderThree", params).invoke(ModuleManager.getMixinProxyClass(), (Object[]) null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }


            GlStateManager.pushMatrix();

            if (this.base != null) {
                this.base.render(scale);
            }

            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            f = 0.875F;
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GlStateManager.popMatrix();

            try{
                Class[] params = {Color.class};
                ModuleManager.getMixinProxyClass().getMethod("renderFour", params).invoke(ModuleManager.getMixinProxyClass(), color);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

            GlStateManager.pushMatrix();

            if (this.base != null) {
                this.base.render(scale);
            }

            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            f = 0.875F;
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GlStateManager.popMatrix();
            try{
                Class[] params = {};
                ModuleManager.getMixinProxyClass().getMethod("renderFive", params).invoke(ModuleManager.getMixinProxyClass(), (Object[]) null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }


            try{
                Class[] params = {Color.class};
                ModuleManager.getMixinProxyClass().getMethod("setColor", params).invoke(ModuleManager.getMixinProxyClass(), Color.WHITE);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }


            callbackInfo.cancel();
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
            GlStateManager.pushMatrix();
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GlStateManager.popMatrix();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glColor4f((float) visibleColor.getRed() / 255.0f, (float) visibleColor.getGreen() / 255.0f, (float) visibleColor.getBlue() / 255.0f, 1);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GlStateManager.popMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glPopAttrib();
            callbackInfo.cancel();
        } else if (ModuleManager.getModule("Chams").isToggled() && ModuleManager.getModule("Chams").isToggledMode("Mode", "Basic")) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -4000000F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, 4000000F);
            callbackInfo.cancel();
        } else if (ModuleManager.getModule("Chams").isToggled() && ModuleManager.getModule("Chams").isToggledMode("Mode", "Color")) {

            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            if (!ModuleManager.getModule("Chams").isToggledValue("RainbowColor")) {
                GL11.glColor4f(ModuleManager.getModule("Chams").getColorValue("SingleColor").getRed() / 255f, ModuleManager.getModule("Chams").getColorValue("SingleColor").getGreen() / 255f, ModuleManager.getModule("Chams").getColorValue("SingleColor").getBlue() / 255f, 1f);
            } else {
                GL11.glColor4f(rainbow.getRed() / 255f, rainbow.getGreen() / 255f, rainbow.getBlue() / 255f, 1f);
            }
            GL11.glPolygonOffset(1.0F, -4000000F);

            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.glass.render(scale);
            GlStateManager.scale(0.875F, 0.875F, 0.875F);
            GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
            GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
            this.cube.render(scale);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glColor4f(1f, 1f, 1f, 1f);
            GL11.glPolygonOffset(1.0F, 4000000F);
            callbackInfo.cancel();
        }

        GL11.glPopMatrix(); // 0

    }


}


