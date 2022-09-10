package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {


    @Shadow
    private float alpha = 1.0F;


    @Inject(method = "renderEnchantedGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;matrixMode(I)V"))
    private static void renderEnchantedGlint(RenderLivingBase<?> p_188364_0_, EntityLivingBase p_188364_1_, ModelBase model, float p_188364_3_, float p_188364_4_, float p_188364_5_, float p_188364_6_, float p_188364_7_, float p_188364_8_, float p_188364_9_, CallbackInfo callbackInfo) {
        if (ModuleManager.getModule("EnchantColor").isToggled()) {
            if (!ModuleManager.getModule("EnchantColor").isToggledValue("Rainbow")) {
                GlStateManager.color(ModuleManager.getModule("EnchantColor").getColorValue("Color").getRed() / 255f, ModuleManager.getModule("EnchantColor").getColorValue("Color").getGreen() / 255f, ModuleManager.getModule("EnchantColor").getColorValue("Color").getBlue() / 255f);
            }
            if (ModuleManager.getModule("EnchantColor").isToggledValue("Rainbow")) {
                Color rainbow =  ColorUtils.rainbow();
                GlStateManager.color(rainbow.getRed() / 255f, rainbow.getGreen() / 255f, rainbow.getBlue() / 255f);
            }
        }
    }

    @Inject(method = "renderArmorLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/LayerArmorBase;renderEnchantedGlint(Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/model/ModelBase;FFFFFFF)V"), cancellable = true)
    private void renderArmorLayer2(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo callbackInfo) {
        if (ModuleManager.getModule("NoRender").isToggled() && ModuleManager.getModule("NoRender").isToggledValue("Enabled")) {
            //System.out.println("NoCluster is active");

            if(entityLivingBaseIn == Minecraft.getMinecraft().player)return;

            double startDistance = ModuleManager.getModule("NoRender").getDoubleValue("StartingDistance");
            double minOpacity = ModuleManager.getModule("NoRender").getDoubleValue("MinimumOpacity");
            boolean friendsOnly = ModuleManager.getModule("NoRender").isToggledValue("FriendsOnly");
            boolean applyGradient = true;

            ArrayList<String> friendsList = FriendManager.friendsList;

            if (friendsOnly && !friendsList.contains(entityLivingBaseIn.getName())) applyGradient = false;
            //System.out.println("Passed the friendsOnly Check : " + applyGradient);
//            if (startDistance > Minecraft.getMinecraft().player.getDistance(entity)) applyGradient = false;
//            System.out.println("Passed the distance check : " + applyGradient);
//            if (!(entity instanceof EntityOtherPlayerMP)) applyGradient = false;
//            System.out.println("Passed the Entity Check : " + applyGradient);
            if (applyGradient) {
                //System.out.println("Applying Gradient");
                float distancePercentage = (float) (Minecraft.getMinecraft().player.getDistance(entityLivingBaseIn) / startDistance);
                float alpha = (float) (minOpacity + distancePercentage * (1 - minOpacity));
                if (alpha < 1) {
                    callbackInfo.cancel();
                }
            }
        }


    }

    @Inject(method = "renderArmorLayer", at = @At("HEAD"), cancellable = true)
    private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo callbackInfo) {

        boolean doRender = false;

        if (slotIn == EntityEquipmentSlot.LEGS) {
            if ((!ModuleManager.getModule("NoRender").isToggledValue("Leggings") && ModuleManager.getModule("NoRender").isToggled()) || !ModuleManager.getModule("NoRender").isToggled()) {
                doRender = true;
            }
        } else if (slotIn == EntityEquipmentSlot.FEET) {
            if ((!ModuleManager.getModule("NoRender").isToggledValue("Boots") && ModuleManager.getModule("NoRender").isToggled()) || !ModuleManager.getModule("NoRender").isToggled()) {
                doRender = true;
            }
        } else if (slotIn == EntityEquipmentSlot.HEAD) {
            if ((!ModuleManager.getModule("NoRender").isToggledValue("Helmet")) || !ModuleManager.getModule("NoRender").isToggled()) {
                doRender = true;
            }
        } else if (slotIn == EntityEquipmentSlot.CHEST) {
            if ((!ModuleManager.getModule("NoRender").isToggledValue("Chestplate") && ModuleManager.getModule("NoRender").isToggled()) || !ModuleManager.getModule("NoRender").isToggled()) {
                doRender = true;
            }

            if (!doRender) {
                callbackInfo.cancel();
            }
        }

    }
}
