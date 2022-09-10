package com.krazzzzymonkey.catalyst.mixin.client;


import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(RenderItem.class)
public class MixinRenderItem {
    @Shadow
    private void renderModel(IBakedModel model, int color) {
        throw new AbstractMethodError("Shadow");
    }

    @Shadow
    private void renderModel(IBakedModel model, ItemStack stack) {
        throw new AbstractMethodError("Shadow");
    }

    @ModifyArg(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index = 1)
    public int getColor(int color) {
        if (ModuleManager.getModule("EnchantColor").isToggled()) {
            if (!ModuleManager.getModule("EnchantColor").isToggledValue("Rainbow")) {
                return ModuleManager.getModule("EnchantColor").getColorValue("Color").getRGB();
            }
            if (ModuleManager.getModule("EnchantColor").isToggledValue("Rainbow")) {
                Method getRainbow;
                Color rainbow = new Color(-1);
                try {
                    Class[] noParams = {};
                    getRainbow = ModuleManager.getMixinProxyClass().getMethod("getRainbow", noParams);
                    rainbow = (Color) getRainbow.invoke(ModuleManager.getMixinProxyClass(), (Object[]) null);
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return rainbow.getRGB();
            }
        }
        return -8372020;
    }

}
