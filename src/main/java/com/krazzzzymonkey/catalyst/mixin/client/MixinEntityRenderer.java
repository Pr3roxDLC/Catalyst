package com.krazzzzymonkey.catalyst.mixin.client;


import com.google.common.base.Predicate;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
    public RayTraceResult rayTraceBlocks(WorldClient world, Vec3d start, Vec3d end) {
        if (ModuleManager.getModule("CameraClip").isToggled()) {
            return null;
        } else {
            return world.rayTraceBlocks(start, end);
        }
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        if (ModuleManager.getModule("NoEntityTrace").isToggled()) {


            Method shouldEnable;
            Class[] noParams = {};
            try {
                shouldEnable = ModuleManager.getMixinProxyClass().getMethod("shouldEnable", noParams);
                if (Boolean.TRUE.equals(shouldEnable.invoke(ModuleManager.getMixinProxyClass(), (Object[]) null))) {
                    return new ArrayList<>();
                }
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float partialTicks, CallbackInfo callback) {
        if (ModuleManager.getModule("NoRender").isToggled() && ModuleManager.getModule("NoRender").isToggledValue("NoHurtCam")) {
            callback.cancel();
        }
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void projectInject0(float fovy, float aspect, float zNear, float zFar) {

        try {
            Class[] params = {float.class, float.class, float.class, float.class, boolean.class};
            ModuleManager.getMixinProxyClass().getMethod("project", params).invoke(ModuleManager.getMixinProxyClass(), fovy, aspect, zNear, zFar, false);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void projectInject1(float fovy, float aspect, float zNear, float zFar) {

        try {
            Class[] params = {float.class, float.class, float.class, float.class, boolean.class};
            ModuleManager.getMixinProxyClass().getMethod("project", params).invoke(ModuleManager.getMixinProxyClass(), fovy, aspect, zNear, zFar, true);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void projectInject2(float fovy, float aspect, float zNear, float zFar) {

        try {
            Class[] params = {float.class, float.class, float.class, float.class, boolean.class};
            ModuleManager.getMixinProxyClass().getMethod("project", params).invoke(ModuleManager.getMixinProxyClass(), fovy, aspect, zNear, zFar, false);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void projectInject3(float fovy, float aspect, float zNear, float zFar) {

        try {
            Class[] params = {float.class, float.class, float.class, float.class, boolean.class};
            ModuleManager.getMixinProxyClass().getMethod("project", params).invoke(ModuleManager.getMixinProxyClass(), fovy, aspect, zNear, zFar, false);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}


