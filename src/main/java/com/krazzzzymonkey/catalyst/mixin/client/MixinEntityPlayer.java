package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.events.PlayerUpdateEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    @Shadow
    public abstract GameProfile getGameProfile();

    @Inject(method = "isPushedByWater", at = @At("HEAD"), cancellable = true)
    private void isPushedByWater(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (ModuleManager.getModule("Velocity").isToggled() && ModuleManager.getModule("Velocity").isToggledValue("FlowingWater")) {
            callbackInfo.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "attackTargetEntityWithCurrentItem", constant = @Constant(doubleValue = 0.6D))
    private double multiplyMotion(double original) {
        if (ModuleManager.getModule("AutoSprint").isToggled()) {
            return 1.0;
        }
        return original;
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    public void onPlayerUpdate(CallbackInfo info) {
        EVENT_MANAGER.post(new PlayerUpdateEvent());
    }


}
