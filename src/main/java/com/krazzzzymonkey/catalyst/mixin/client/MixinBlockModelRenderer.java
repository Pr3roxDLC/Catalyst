package com.krazzzzymonkey.catalyst.mixin.client;


import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {


    @Inject(method = { "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Z)Z" }, at = { @At("HEAD") }, cancellable = true)
    private void renderModel(final IBlockAccess blockAccess, final IBakedModel bakedModel, final IBlockState blockState, final BlockPos blockPos, final BufferBuilder bufferBuilder, final boolean b, final CallbackInfoReturnable<Boolean> cir) {

        Method isInList = null;

        try {
            isInList = ModuleManager.getMixinProxyClass().getMethod("isInList", net.minecraft.block.Block.class);

            if (ModuleManager.getModule("XRay").isToggled() && !Boolean.TRUE.equals(isInList.invoke(ModuleManager.getMixinProxyClass(), blockState.getBlock()))) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
        catch (Exception ignored) {

        }
    }

    @ModifyArg(method = { "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderModelFlat(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z"))
    private boolean renderModelFlat(final boolean b) {
        try {
            if (ModuleManager.getModule("XRay").isToggled()) {
                return false;
            }
        }
        catch (Exception ignored) {

        }
        return b;
    }

    @ModifyArg(method = { "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderModelSmooth(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z"))
    private boolean renderModelSmooth(final boolean b) {
        try {
            if (ModuleManager.getModule("XRay").isToggled()) {
                return false;
            }
        }
        catch (Exception ignored) {

        }
        return b;
    }

}
