package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.block.BlockSlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSlime.class)
public class MixinBlockSlime {

    @Inject(method = "onEntityWalk", at = @At("HEAD"), cancellable = true)
    public void onEntityWalk(CallbackInfo info) {
        if (ModuleManager.getModule("NoSlow").isToggled()) {
            if (ModuleManager.getModule("NoSlow").isToggledValue("SlimeBlocks")) info.cancel();
        }
    }
}
