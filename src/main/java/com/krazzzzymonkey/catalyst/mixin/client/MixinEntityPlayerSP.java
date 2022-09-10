package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {


    @Shadow
    protected Minecraft mc;

    @Shadow
    @Nullable
    public abstract EntityItem dropItem(boolean dropAll);

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    public void onMovePre(CallbackInfo callbackInfo) {

        try {
            Class[] params = {String.class};
            ModuleManager.getMixinProxyClass().getMethod("postMotionEvent", params).invoke(ModuleManager.getMixinProxyClass(), "PRE");

        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    public void onMovePost(CallbackInfo callbackInfo) {

        try {
            Class[] params = {String.class};
            ModuleManager.getMixinProxyClass().getMethod("postMotionEvent", params).invoke(ModuleManager.getMixinProxyClass(), "POST");

        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }



    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void noPushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.getModule("Velocity").isToggled() &&  ModuleManager.getModule("Velocity").isToggledValue("NoPush")) {
            cir.cancel();
        }
    }

    @Inject(method = "displayGUIChest", at = @At("HEAD"), cancellable = true)
    public void displayGUIChest(IInventory inventory, CallbackInfo ci) {
        String id = inventory instanceof IInteractionObject ? ((IInteractionObject) inventory).getGuiID() : "minecraft:container";
        if (id.equals("minecraft:chest") || inventory.getName().equals("Ender Chest") || id.equals("minecraft:shulker_box")) {
            try{
                Class[] params = {IInventory.class};
                ModuleManager.getMixinProxyClass().getMethod("initFakeInventory", params).invoke(ModuleManager.getMixinProxyClass(), inventory);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            ci.cancel();
        }
    }


    @ModifyArg(method = "setSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;setSprinting(Z)V"), index = 0)
    public boolean modifySprinting(boolean sprinting) {
        if (ModuleManager.getModule("AutoSprint").isToggled()) {
            if (mc.player != null && shouldSprint(mc.player)) {
                return true;
            }
        }
        return sprinting;

    }


    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        try{
            Class[] params = {MoverType.class, double.class, double.class, double.class};
            Object[] values = (Object[]) ModuleManager.getMixinProxyClass().getMethod("onPlayerMove", params).invoke(ModuleManager.getMixinProxyClass(), type, x, y, z);
            type = (MoverType) values[0];
            x = (double) values[1];
            y = (double) values[2];
            z = (double) values[3];
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean shouldSprint(EntityPlayerSP player) {
        return !mc.gameSettings.keyBindSneak.isKeyDown()
            && player.getFoodStats().getFoodLevel() > 6
            && !player.isElytraFlying()
            && !mc.player.capabilities.isFlying
            && (ModuleManager.getModule("AutoSprint").isToggledValue("AllDirections") ? (player.moveForward != 0.0f || player.moveStrafing != 0.0f) : player.moveForward > 0.0f);
    }
}
