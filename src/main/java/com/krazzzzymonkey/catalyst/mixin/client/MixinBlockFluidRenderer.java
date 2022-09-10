package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(BlockFluidRenderer.class)
public class MixinBlockFluidRenderer {

    @Inject(method = {"renderFluid"}, at = {@At("HEAD")}, cancellable = true)
    public void renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, BufferBuilder bufferBuilderIn, CallbackInfoReturnable<Boolean> cir) {
        Method isInList = null;
    try {
        isInList = ModuleManager.getMixinProxyClass().getMethod("isInList", net.minecraft.block.Block.class);

        if (ModuleManager.getModule("XRay").isToggled() && !Boolean.TRUE.equals(isInList.invoke(ModuleManager.getMixinProxyClass(), blockStateIn.getBlock()))) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    } catch (Exception ignored){}

    }
}
