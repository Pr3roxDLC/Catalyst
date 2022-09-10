package com.krazzzzymonkey.catalyst.mixin.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(value = Render.class, priority = 9998)
public abstract class MixinRender <T extends Entity>{

	

}
