package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat extends GuiScreen {

    String predictedCommand;

    @Shadow
    protected GuiTextField inputField;


    @Inject(method = "drawScreen", at = @At("HEAD"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {

        try {
            Class[] params = {int.class, int.class, GuiTextField.class};
            ModuleManager.getMixinProxyClass().getMethod("guiChatInit", params).invoke(ModuleManager.getMixinProxyClass(), this.width, this.height, inputField);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
