package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer;
import net.minecraft.tileentity.TileEntityShulkerBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityShulkerBoxRenderer.class)
public class MixinTileEntityShulkerBoxRenderer {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void renderBook(TileEntityShulkerBox te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        if (ModuleManager.getModule("NoRender").isToggled() && ModuleManager.getModule("NoRender").isToggledValue("ShulkerBoxes")) {
            ci.cancel();
        }

    }
}
