package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer {

    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    private void renderLivingLabel(AbstractClientPlayer entity, double x, double y, double z, String name, double distanceSq, CallbackInfo callbackInfo) {
        if (ModuleManager.getModule("Nametags").isToggled()) {
            callbackInfo.cancel();
        }

    }

    @Inject(method = "renderRightArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderPlayer;getMainModel()Lnet/minecraft/client/model/ModelPlayer;"))
    public void renderRightArmHookHEAD(AbstractClientPlayer clientPlayer, CallbackInfo callbackInfo) {
        if (ModuleManager.getModule("Viewmodel").isToggled()) {
            if (!ModuleManager.getModule("Viewmodel").isToggledValue("Rainbow")) {
                GlStateManager.enableAlpha();
                if(!ModuleManager.getModule("Viewmodel").isToggledValue("Textured"))GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(ModuleManager.getModule("Viewmodel").getColorValue("ArmColor").getRed() / 255f, ModuleManager.getModule("Viewmodel").getColorValue("ArmColor").getGreen() / 255f, ModuleManager.getModule("Viewmodel").getColorValue("ArmColor").getBlue() / 255f, ModuleManager.getModule("Viewmodel").getIntegerValue("Alpha") / 100f);
            } else {
                GlStateManager.enableAlpha();
                if(!ModuleManager.getModule("Viewmodel").isToggledValue("Textured"))GL11.glDisable(GL11.GL_TEXTURE_2D);
                Color rainbow = ColorUtils.rainbow();
                GL11.glColor4f(rainbow.getRed() / 255f, rainbow.getGreen() / 255f, rainbow.getBlue() / 255f, ModuleManager.getModule("Viewmodel").getIntegerValue("Alpha") / 100f);
            }
        }
    }

    @Inject(method = "renderRightArm", at = @At("RETURN"))
    public void renderRightArmHookRETURN(AbstractClientPlayer clientPlayer, CallbackInfo callbackInfo){
        if(!ModuleManager.getModule("Viewmodel").isToggledValue("Textured"))GL11.glEnable(GL11.GL_TEXTURE_2D);
    }



    @Shadow
    public ModelPlayer getMainModel() {
        throw new AbstractMethodError("Shadow");
    }

    @Shadow
    private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
        throw new AbstractMethodError("Shadow");
    }
}
