package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.events.DamageBlockEvent;
import com.krazzzzymonkey.catalyst.events.ReachEvent;
import com.krazzzzymonkey.catalyst.events.StopUsingItemEvent;
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

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = {"onPlayerDamageBlock"}, at = @At("HEAD"), cancellable = true)
    private void onPlayerDamageBlock(final BlockPos blockPos, final EnumFacing enumFacing, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final DamageBlockEvent damageBlockEvent = new DamageBlockEvent(blockPos, enumFacing);
        EVENT_MANAGER.post(damageBlockEvent);
        if (damageBlockEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true)
    private void onGetBlockReachDistance(final CallbackInfoReturnable<Float> cir) {
        final ReachEvent e = new ReachEvent(cir.getReturnValue());
        EVENT_MANAGER.post(e);
        cir.setReturnValue(e.distance);
    }

    @Inject(method = "onStoppedUsingItem", at = @At("HEAD"), cancellable = true)
    private void onStoppedUsingItemInject(EntityPlayer playerIn, CallbackInfo ci) {
        if (playerIn.equals(Minecraft.getMinecraft().player)) {
            StopUsingItemEvent event = new StopUsingItemEvent();
            EVENT_MANAGER.post(event);
            if (event.isCancelled()) {
                if (event.isPacket()) {
                    Minecraft.getMinecraft().playerController.syncCurrentPlayItem();
                    playerIn.stopActiveHand();
                }
                ci.cancel();
            }
        }
    }

}
