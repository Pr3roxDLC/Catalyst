package com.krazzzzymonkey.catalyst.mixin.client;


import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;

@Mixin(value = GuiScreen.class, priority = 9999)
public class MixinGuiScreen {
    @Shadow public Minecraft mc;

    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    public void renderToolTip(ItemStack is, int x, int y, CallbackInfo ci) {
        if (ModuleManager.getModule("ShulkerPreview").isToggled() && is.getItem() instanceof ItemShulkerBox) {
            NBTTagCompound tagCompound = is.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
                NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
                if (blockEntityTag.hasKey("Items", 9)) {
                    ci.cancel();


                    Class[] params = {NBTTagCompound.class, ItemStack.class, boolean.class, boolean.class};
                    try{
                        ModuleManager.getMixinProxyClass().getMethod("updateFields", params).invoke(ModuleManager.getMixinProxyClass(), blockEntityTag, is , true, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                        Class[] param = {int.class, int.class};
                        try{
                            ModuleManager.getMixinProxyClass().getMethod("updateFields", param).invoke(ModuleManager.getMixinProxyClass(), x, y);
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

}
