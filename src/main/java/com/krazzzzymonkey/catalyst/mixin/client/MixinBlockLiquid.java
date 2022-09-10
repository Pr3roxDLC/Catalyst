package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.player.LiquidInteract;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BlockLiquid.class)
public abstract class MixinBlockLiquid extends Block {

    protected MixinBlockLiquid() {
        super(null);
    }


    @Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
    public void canCollideCheck(final IBlockState blockState, final boolean hitIfLiquid, final CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue((hitIfLiquid && (int) blockState.getValue((IProperty) BlockLiquid.LEVEL) == 0) || ModuleManager.getModule("LiquidInteract").isToggled());
    }
}