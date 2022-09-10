package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AcidMode extends Modules {

    public AcidMode() {
        super("AcidMode", ModuleCategory.RENDER, "Renders acid shader over game");
    }

    public static boolean firstRun = true;

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {

        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (OpenGlHelper.shadersSupported) {
            if (Minecraft.getMinecraft().currentScreen != null) {
                firstRun = true;
            }
            if (Minecraft.getMinecraft().currentScreen == null && firstRun) {
                Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/wobble.json"));
                firstRun = false;
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) return;
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        firstRun = true;
        super.onDisable();
    }

}
