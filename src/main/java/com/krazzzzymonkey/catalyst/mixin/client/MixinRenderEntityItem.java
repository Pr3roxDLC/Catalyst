package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem <T>extends Render<EntityItem>{
	
	protected MixinRenderEntityItem() {
		super(null);
	}

	@Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
	public void doRenderHead(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo)
    {
		if(ModuleManager.getModule("ItemChams").isToggled()) {
			GL11.glEnable(32823);
	  	      GL11.glPolygonOffset(1.0F, -1100000.0F);
    	}
    }
	
	@Inject(method = "doRender", at = @At("RETURN"), cancellable = true)
	public void doRenderReturn(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo)
    {
		if(ModuleManager.getModule("ItemChams").isToggled()) {
			GL11.glDisable(32823);
	  	      GL11.glPolygonOffset(1.0F, 1100000.0F);
    	}
    }
	
}
