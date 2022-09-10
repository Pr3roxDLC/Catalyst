package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.events.MotionEvent;
import com.krazzzzymonkey.catalyst.events.PlayerMoveEvent;
import com.krazzzzymonkey.catalyst.gui.chest.CustomGuiChest;
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

import static com.krazzzzymonkey.catalyst.managers.ModuleManager.EVENT_MANAGER;

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
        EVENT_MANAGER.post(new MotionEvent.PRE());
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    public void onMovePost(CallbackInfo callbackInfo) {
        EVENT_MANAGER.post(new MotionEvent.POST());
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
            Minecraft.getMinecraft().displayGuiScreen(new CustomGuiChest(Minecraft.getMinecraft().player.inventory, inventory));
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
        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y ,z);
        EVENT_MANAGER.post(event);
    }

    public boolean shouldSprint(EntityPlayerSP player) {
        return !mc.gameSettings.keyBindSneak.isKeyDown()
            && player.getFoodStats().getFoodLevel() > 6
            && !player.isElytraFlying()
            && !mc.player.capabilities.isFlying
            && (ModuleManager.getModule("AutoSprint").isToggledValue("AllDirections") ? (player.moveForward != 0.0f || player.moveStrafing != 0.0f) : player.moveForward > 0.0f);
    }
}
