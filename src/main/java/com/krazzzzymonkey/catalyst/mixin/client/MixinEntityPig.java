package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.entity.passive.EntityPig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPig.class)
public abstract class MixinEntityPig {
	  @Inject(method={"canBeSteered"}, at={@org.spongepowered.asm.mixin.injection.At("HEAD")}, cancellable=true)
	  private void isHorseSaddled(CallbackInfoReturnable<Boolean> callback)
	  {
	    if (ModuleManager.getModule("EntityControl").isToggled()) {
	      callback.setReturnValue(Boolean.valueOf(true));
	    }
	  }
}
