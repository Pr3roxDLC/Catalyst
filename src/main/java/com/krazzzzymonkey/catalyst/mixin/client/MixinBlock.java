package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(method = { "isFullCube" }, at = { @At("HEAD") }, cancellable = true)
    public void isFullCube(IBlockState state, CallbackInfoReturnable<Boolean> cir) {
            if (ModuleManager.getModule("XRay").isToggled()) {
                cir.setReturnValue(XRay.blocks.contains(state.getBlock()));
                cir.cancel();
            }
    }
}
