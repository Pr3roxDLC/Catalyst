package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.world.Scaffold;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@Mixin(Entity.class)
public class MixinEntity {

    @Redirect(method = "applyEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void velocity(Entity entity, double x, double y, double z) {
        if (ModuleManager.getModule("Velocity").isToggled() && ModuleManager.getModule("Velocity").isToggledValue("NoPush") && entity.equals(Minecraft.getMinecraft().player)) {
            //empty if statement to cancel Collision
        } else {
            entity.motionX += x;
            entity.motionY += y;
            entity.motionZ += z;
            entity.isAirBorne = true;
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        if (ModuleManager.getModule("SafeWalk").isToggled()) return true;
        if (ModuleManager.getModule("Scaffold").isToggled() && Scaffold.cancelSneak) {
            return false;
        }
        return (entity.isSneaking());
    }

}

