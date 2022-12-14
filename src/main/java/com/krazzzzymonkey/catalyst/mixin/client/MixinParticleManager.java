package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.misc.Sounds;
import net.minecraft.client.particle.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    public void spawnEffectParticle(Particle particle, CallbackInfo info) {

        if (ModuleManager.getModule("Sounds").isToggled()) {
            if (particle instanceof ParticleExplosionHuge) {
                Sounds.onExplosion();
                return;
            }
        }


        if (ModuleManager.getModule("NoRender").isToggled()) {
            if (ModuleManager.getModule("NoRender").isToggledValue("AllParticles")) {
                info.cancel();
            } else {
                if (ModuleManager.getModule("NoRender").isToggledValue("Totems") && particle instanceof ParticleTotem) {
                    info.cancel();
                } else if (ModuleManager.getModule("NoRender").isToggledValue("Criticals") && particle instanceof ParticleCrit) {
                    info.cancel();
                }
            }
        }
    }
}
