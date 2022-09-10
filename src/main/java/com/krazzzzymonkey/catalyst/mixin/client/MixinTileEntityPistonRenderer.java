package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;

import net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPistonRenderer.class)
public class MixinTileEntityPistonRenderer {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void renderBook(TileEntityPiston te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        if (ModuleManager.getModule("NoRender").isToggled() && ModuleManager.getModule("NoRender").isToggledValue("Pistons")) {
            ci.cancel();
        }

    }
}
