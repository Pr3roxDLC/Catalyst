package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.events.KeyDownEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.InvocationTargetException;

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "processKeyBinds", at = @At("HEAD"), cancellable = true)
    public void processKeyBinds(CallbackInfo callbackInfo){
        if(Minecraft.getMinecraft().player == null) return;
    }



    @Inject(method = "dispatchKeypresses", at = @At("HEAD"))
    public void onDispatchKeypresses(CallbackInfo callbackInfo) {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            KeyDownEvent e = new KeyDownEvent(i);
            EVENT_MANAGER.post(e);
        }
    }

    @Redirect(method = {"sendClickBlockToController"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    private boolean isHandActiveWrapper(EntityPlayerSP player) {
        return !ModuleManager.getModule("MultiTask").isToggled() && player.isHandActive();
    }

    @Redirect(method = {"rightClickMouse"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0))
    private boolean isHittingBlockHook(PlayerControllerMP playerController) {
        return !ModuleManager.getModule("MultiTask").isToggled() && playerController.getIsHittingBlock();
    }
}
