package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;

import com.krazzzzymonkey.catalyst.utils.visual.GLSLSandboxShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    File fragShaderFile = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "shader" + File.separator + "fragment.fsh");


    private GLSLSandboxShader backgroundShader;
    private long initTime = System.currentTimeMillis(); // Initialize as a failsafe

    @Shadow
    public static ResourceLocation MINECRAFT_TITLE_TEXTURES;
    @Shadow
    private float panoramaTimer;
    @Final
    @Shadow
    private float minceraftRoll;
    @Shadow
    private static final ResourceLocation field_194400_H = new ResourceLocation("textures/gui/title/edition.png");
    @Shadow
    private String splashText;
    @Shadow
    private int widthCopyrightRest;
    @Shadow
    private int widthCopyright;
    @Shadow
    private String openGLWarning1;
    @Shadow
    private String openGLWarning2;
    @Shadow
    private int openGLWarningX1;
    @Shadow
    private int openGLWarningY1;
    @Shadow
    private int openGLWarningX2;
    @Shadow
    private int openGLWarningY2;
    @Shadow
    private int openGLWarning2Width;
    @Shadow
    private GuiScreen realmsNotification;



    @Inject(method = "<init>", at = @At("RETURN"))
    void constructorReturn(CallbackInfo ci) {
        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "mainmenu" + File.separator + "minecraft.png");
        try {
            if (ModuleManager.getModule("CustomMainMenu").isToggledValue("CustomLogo") && ModuleManager.getModule("CustomMainMenu").isToggled()) {
                MINECRAFT_TITLE_TEXTURES = Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file)));
            } else {
                MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //TODO replace this with a ReflectionsCall to ModuleManger for Loader Compatability
            //System.out.println(file.getAbsolutePath());
            //FUCK ME, took me 2 hrs to figure out why this wasnt compiling, turns out i was trying to compile the PNG from file because i had the wrong variable
            File shaderDir = new File(String.format("%s%s%s%s%s%s", Minecraft.getMinecraft().gameDir, File.separator, Main.NAME, File.separator, "Shader", File.separator));
            this.backgroundShader = new GLSLSandboxShader(shaderDir.getAbsolutePath() + File.separator + ModuleManager.getModule("CustomMainMenu").getToggledMode("Shader").getName());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load backgound shader", e);
        }

    }

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setConnectedToRealms(Z)V"))
    void initGui(CallbackInfo ci) {
        initTime = System.currentTimeMillis();
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (ModuleManager.getModule("CustomMainMenu").isToggled()) {
            this.panoramaTimer += partialTicks;
//        GlStateManager.disableAlpha();
//        this.renderSkybox(mouseX, mouseY, partialTicks);
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            int i = 274;
            int j = this.width / 2 - 137;
            int k = 30;
            this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
            this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);

            if (ModuleManager.getModule("CustomMainMenu").isToggledMode("Mode", "Shader")) {

                this.backgroundShader.useShader(this.width, this.height, mouseX, mouseY, (System.currentTimeMillis() - initTime) / 1000f);

                GL11.glBegin(GL11.GL_QUADS);

                GL11.glVertex2f(-1f, -1f);
                GL11.glVertex2f(-1f, 1f);
                GL11.glVertex2f(1f, 1f);
                GL11.glVertex2f(1f, -1f);

                GL11.glEnd();

                // Unbind shader
                GL20.glUseProgram(0);
            }


            this.mc.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if ((double) this.minceraftRoll < 1.0E-4D) {
                this.drawTexturedModalRect(j + 0, 30, 0, 0, 99, 44);
                this.drawTexturedModalRect(j + 99, 30, 129, 0, 27, 44);
                this.drawTexturedModalRect(j + 99 + 26, 30, 126, 0, 3, 44);
                this.drawTexturedModalRect(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
                this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
            } else {
                this.drawTexturedModalRect(j + 0, 30, 0, 0, 155, 44);
                this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
            }

            this.mc.getTextureManager().bindTexture(field_194400_H);
            drawModalRectWithCustomSizedTexture(j + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);

            this.splashText = net.minecraftforge.client.ForgeHooksClient.renderMainMenu(((GuiMainMenu) ((Object) this)), this.fontRenderer, this.width, this.height, this.splashText);

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (this.width / 2 + 90), 70.0F, 0.0F);
            GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
            float f = 1.8F - MathHelper.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
            f = f * 100.0F / (float) (this.fontRenderer.getStringWidth(this.splashText) + 32);
            GlStateManager.scale(f, f, f);
            this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, -256);
            GlStateManager.popMatrix();
            String s = "Minecraft 1.12.2";

            if (this.mc.isDemo()) {
                s = s + " Demo";
            } else {
                s = s + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType());
            }

            java.util.List<String> brandings = com.google.common.collect.Lists.reverse(net.minecraftforge.fml.common.FMLCommonHandler.instance().getBrandings(true));
            for (int brdline = 0; brdline < brandings.size(); brdline++) {
                String brd = brandings.get(brdline);
                if (!com.google.common.base.Strings.isNullOrEmpty(brd)) {
                    this.drawString(this.fontRenderer, brd, 2, this.height - (10 + brdline * (this.fontRenderer.FONT_HEIGHT + 1)), 16777215);
                }
            }

            this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, -1);

            if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow()) {
                drawRect(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, -1);
            }

            if (this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
                drawRect(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2, this.openGLWarningY2 - 1, 1428160512);
                this.drawString(this.fontRenderer, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, -1);
                this.drawString(this.fontRenderer, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2, (this.buttonList.get(0)).y - 12, -1);
            }

            super.drawScreen(mouseX, mouseY, partialTicks);

            if (this.areRealmsNotificationsEnabled()) {
                this.realmsNotification.drawScreen(mouseX, mouseY, partialTicks);
            }
            ci.cancel();
        }

    }

    @Shadow
    private boolean areRealmsNotificationsEnabled() {
        throw new AbstractMethodError("Shadow");
    }

    @Shadow
    private void renderSkybox(int mouseX, int mouseY, float partialTicks) {
        throw new AbstractMethodError("Shadow");
    }

}
