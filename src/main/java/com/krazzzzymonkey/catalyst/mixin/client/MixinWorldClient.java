package com.krazzzzymonkey.catalyst.mixin.client;


import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient{


    //    @Inject(method = "doVoidFogParticles", at = @At(value = "INVOKE", args = "log=true")) for logging what method calls are done + what their signatures are
    @Inject(method = "doVoidFogParticles", at = @At(value = "INVOKE"))
    public void aaa(CallbackInfo callbackInfo){
    }



    @ModifyArg(method = "doVoidFogParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;showBarrierParticles(IIIILjava/util/Random;ZLnet/minecraft/util/math/BlockPos$MutableBlockPos;)V"), index = 5)
    private boolean adjustYCoord(boolean holdingBarrier) {
        return ModuleManager.getModule("BarrierView").isToggled();
    }




}
