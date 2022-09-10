package com.krazzzzymonkey.catalyst.mixin.client;


import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.render.ShulkerPreview;
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
                    ShulkerPreview.nbt = blockEntityTag;
                    ShulkerPreview.itemStack = is;
                    ShulkerPreview.active = true;
                    ShulkerPreview.pinned = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
                    if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                        ShulkerPreview.drawX = x;
                        ShulkerPreview.drawY = y;
                    }
                }
            }
        }
    }

}
