package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.events.DamageBlockEvent;
import com.krazzzzymonkey.catalyst.events.ReachEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = {"onPlayerDamageBlock"}, at = @At("HEAD"), cancellable = true)
    private void onPlayerDamageBlock(final BlockPos blockPos, final EnumFacing enumFacing, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {

        try {
            Class[] params = {BlockPos.class, EnumFacing.class, CallbackInfoReturnable.class};
            ModuleManager.getMixinProxyClass().getMethod("postDamageBlockEvent", params).invoke(ModuleManager.getMixinProxyClass(), blockPos, enumFacing, callbackInfoReturnable);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true)
    private void onGetBlockReachDistance(final CallbackInfoReturnable<Float> cir) {

        try {
            Class[] params = {CallbackInfoReturnable.class};
            ModuleManager.getMixinProxyClass().getMethod("postReachEvent", params).invoke(ModuleManager.getMixinProxyClass(), cir);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "onStoppedUsingItem", at = @At("HEAD"), cancellable = true)
    private void onStoppedUsingItemInject(EntityPlayer playerIn, CallbackInfo ci) {
        if (playerIn.equals(Minecraft.getMinecraft().player)) {
            try {
                Class[] params = {EntityPlayer.class, CallbackInfo.class};
                ModuleManager.getMixinProxyClass().getMethod("postStopUsingItemEvent", params).invoke(ModuleManager.getMixinProxyClass(), playerIn, ci);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

}
