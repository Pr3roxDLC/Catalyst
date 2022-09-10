package com.krazzzzymonkey.catalyst.utils.visual;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui;
import com.krazzzzymonkey.catalyst.module.modules.render.Tracers;
import com.krazzzzymonkey.catalyst.utils.TimerUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.xray.XRayBlock;
import com.krazzzzymonkey.catalyst.xray.XRayData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Locale;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils {

    private static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
    public static TimerUtils splashTimer = new TimerUtils();
    public static int splashTickPos = 0;
    public static boolean isSplash = false;
    public static Minecraft mc = Minecraft.getMinecraft();


    public static String DF(Number value, int maxvalue) {
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(maxvalue);
        return df.format(value);
    }

    public static void drawStringWithRect(String string, int x, int y, int colorString, int colorRect, int colorRect2) {

        RenderUtils.drawBorderedRect(x - 2, y - 2, x + Wrapper.INSTANCE.fontRenderer().getStringWidth(string) + 2, y + 10, 1, colorRect, colorRect2);
        Wrapper.INSTANCE.fontRenderer().drawString(string, x, y, colorString);
    }

    public static void drawCustomStringWithRect(String string, int x, int y, int colorString, int colorRect, int colorRect2) {

        RenderUtils.drawBorderedRect(x - 2, y - 2, x + Main.smallFontRenderer.getStringWidth(string) + 2, y + 8, 1, colorRect, colorRect2);
        Main.smallFontRenderer.drawString(string, x, y, colorString);
    }


    public static void drawToggleModule(String text) {
        ScaledResolution sr = new ScaledResolution(Wrapper.INSTANCE.mc());
        drawStringWithRect(text, sr.getScaledWidth() + 2 - splashTickPos, sr.getScaledHeight() - 10, ClickGui.getColor(), ColorUtils.color(0.0F, 0.0F, 0.0F, 0.0F), ColorUtils.color(0.0F, 0.0F, 0.0F, 0.5F));
        if (splashTimer.isDelay(10)) {
            splashTimer.setLastMS();
            if (isSplash) {
                splashTickPos++;
                if (splashTickPos == Wrapper.INSTANCE.fontRenderer().getStringWidth(text) + 10) {
                    isSplash = false;
                }
            } else {
                if (splashTickPos > 0) {
                    splashTickPos--;
                }
            }
        }
    }

    public static void drawBorderedRect(double x, double y, double x2, double y2, float l1, int col1, int col2) {
        drawRect((int) x, (int) y, (int) x2, (int) y2, col2);

        float f = (float) (col1 >> 24 & 0xFF) / 255F;
        float f1 = (float) (col1 >> 16 & 0xFF) / 255F;
        float f2 = (float) (col1 >> 8 & 0xFF) / 255F;
        float f3 = (float) (col1 & 0xFF) / 255F;

        //GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        //GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static double interpolate(double now, double then) {
        return then + (now - then) * Minecraft.getMinecraft().getRenderPartialTicks();
    }

    public static double[] interpolate(Entity entity) {
        double posX = interpolate(entity.posX, entity.lastTickPosX) - Minecraft.getMinecraft().getRenderManager().renderPosX;
        double posY = interpolate(entity.posY, entity.lastTickPosY) - Minecraft.getMinecraft().getRenderManager().renderPosY;
        double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - Minecraft.getMinecraft().getRenderManager().renderPosZ;
        return new double[]{posX, posY, posZ};
    }

    public static void drawLineToEntityWithStem(Entity e, float red, float green, float blue, float opacity, float stemHeight) {
        double[] xyz = interpolate(e);
        drawLine(xyz[0], xyz[1], xyz[2], 0, red, green, blue, opacity);
        drawLineFromPosToPos(xyz[0], xyz[1], xyz[2], xyz[0], xyz[1] + stemHeight, xyz[2], 0, red, green, blue, opacity);
    }

    public static void drawLineToEntity(Entity e, float red, float green, float blue, float opacity) {
        double[] xyz = interpolate(e);
        drawLine(xyz[0], xyz[1] + e.height / 2, xyz[2], 0, red, green, blue, opacity);
    }

    public static void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity) {
        Vec3d eyes = new Vec3d(0, 0, 1)
            .rotatePitch(-(float) Math
                .toRadians(Minecraft.getMinecraft().player.rotationPitch))
            .rotateYaw(-(float) Math
                .toRadians(Minecraft.getMinecraft().player.rotationYaw));

        drawLineFromPosToPos(eyes.x, eyes.y + Minecraft.getMinecraft().player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
    }

    public static void drawTracerPointer(float x, float y, float size, float widthDiv, float heightDiv, boolean outline, float outlineWidth, int color) {
        boolean blend = GL11.glIsEnabled(GL_BLEND);
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        hexColor(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d((x - size / widthDiv), (y + size));
        GL11.glVertex2d(x, (y + size / heightDiv));
        GL11.glVertex2d((x + size / widthDiv), (y + size));
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        if (outline) {
            GL11.glLineWidth(outlineWidth);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
            GL11.glBegin(2);
            GL11.glVertex2d(x, y);
            GL11.glVertex2d((x - size / widthDiv), (y + size));
            GL11.glVertex2d(x, (y + size / heightDiv));
            GL11.glVertex2d((x + size / widthDiv), (y + size));
            GL11.glVertex2d(x, y);
            GL11.glEnd();
        }
        GL11.glPopMatrix();
        GL11.glEnable(GL_TEXTURE_2D);
        if (!blend)
            GL11.glDisable(GL_BLEND);
        GL11.glDisable(GL_LINE_SMOOTH);
    }

    public static void hexColor(int hexColor) {
        float red = (hexColor >> 16 & 0xFF) / 255.0F;
        float green = (hexColor >> 8 & 0xFF) / 255.0F;
        float blue = (hexColor & 0xFF) / 255.0F;
        float alpha = (hexColor >> 24 & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawLine3D(float x, float y, float z, float x1, float y1, float z1, float thickness, float red, float green, float blue, float alpha) {

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        // GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        // GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL_SMOOTH);
        glLineWidth(thickness);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.disableDepth();
        glEnable(GL32.GL_DEPTH_CLAMP);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) x - mc.getRenderManager().viewerPosX, (double) y - mc.getRenderManager().viewerPosY, (double) z - mc.getRenderManager().viewerPosZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) x1 - mc.getRenderManager().viewerPosX, (double) y1 - mc.getRenderManager().viewerPosY, (double) z1 - mc.getRenderManager().viewerPosZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableDepth();
        glDisable(GL32.GL_DEPTH_CLAMP);
        // GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }


    public static void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(Tracers.lineWidth.getValue().floatValue());
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, opacity);

        GL11.glLoadIdentity();
        Minecraft.getMinecraft().entityRenderer.orientCamera(Minecraft.getMinecraft().getRenderPartialTicks());

        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(posx, posy, posz);
            GL11.glVertex3d(posx2, posy2, posz2);
            GL11.glVertex3d(posx2, posy2, posz2);
            GL11.glVertex3d(posx2, posy2 + up, posz2);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor3d(1d, 1d, 1d);
    }

	/*public static void drawTracer(Entity entity, float red, float green, float blue, float alpha, float ticks) {
        double renderPosX = Wrapper.INSTANCE.mc().getRenderManager().viewerPosX;
        double renderPosY = Wrapper.INSTANCE.mc().getRenderManager().viewerPosY;
        double renderPosZ = Wrapper.INSTANCE.mc().getRenderManager().viewerPosZ;
        double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
        double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks)  + entity.height / 2.0f - renderPosY;
        double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        Vec3d eyes = null;
        if(Scaffold.facingCam != null) {
            	eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(Scaffold.facingCam[1])).rotateYaw(-(float) Math.toRadians(Scaffold.facingCam[0]));
            }
        	else
        {
        	eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(Wrapper.INSTANCE.player().rotationPitch)).rotateYaw(-(float) Math.toRadians(Wrapper.INSTANCE.player().rotationYaw));
        }
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(eyes.x, Wrapper.INSTANCE.player().getEyeHeight() + eyes.y, eyes.z);
        GL11.glVertex3d(xPos, yPos, zPos);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
    }*/

    public static float[][] getBipedRotations(ModelBiped biped) {
        float[][] rotations = new float[5][];

        float[] headRotation = new float[3];
        headRotation[0] = biped.bipedHead.rotateAngleX;
        headRotation[1] = biped.bipedHead.rotateAngleY;
        headRotation[2] = biped.bipedHead.rotateAngleZ;
        rotations[0] = headRotation;

        float[] rightArmRotation = new float[3];
        rightArmRotation[0] = biped.bipedRightArm.rotateAngleX;
        rightArmRotation[1] = biped.bipedRightArm.rotateAngleY;
        rightArmRotation[2] = biped.bipedRightArm.rotateAngleZ;
        rotations[1] = rightArmRotation;

        float[] leftArmRotation = new float[3];
        leftArmRotation[0] = biped.bipedLeftArm.rotateAngleX;
        leftArmRotation[1] = biped.bipedLeftArm.rotateAngleY;
        leftArmRotation[2] = biped.bipedLeftArm.rotateAngleZ;
        rotations[2] = leftArmRotation;

        float[] rightLegRotation = new float[3];
        rightLegRotation[0] = biped.bipedRightLeg.rotateAngleX;
        rightLegRotation[1] = biped.bipedRightLeg.rotateAngleY;
        rightLegRotation[2] = biped.bipedRightLeg.rotateAngleZ;
        rotations[3] = rightLegRotation;

        float[] leftLegRotation = new float[3];
        leftLegRotation[0] = biped.bipedLeftLeg.rotateAngleX;
        leftLegRotation[1] = biped.bipedLeftLeg.rotateAngleY;
        leftLegRotation[2] = biped.bipedLeftLeg.rotateAngleZ;
        rotations[4] = leftLegRotation;

        return rotations;
    }

    public static void logoutSpots(double x, double y, double z, float colorRed, float colorGreen, float colorBlue, float colorAlpha, float ticks) {
        try {
            double renderPosX = Wrapper.INSTANCE.mc().getRenderManager().viewerPosX;
            double renderPosY = Wrapper.INSTANCE.mc().getRenderManager().viewerPosY;
            double renderPosZ = Wrapper.INSTANCE.mc().getRenderManager().viewerPosZ;
            double xPos = x;
            double yPos = y;
            double zPos = z;

            float playerViewY = Wrapper.INSTANCE.mc().getRenderManager().playerViewY;
            float playerViewX = Wrapper.INSTANCE.mc().getRenderManager().playerViewX;
            boolean thirdPersonView = Wrapper.INSTANCE.mc().getRenderManager().options.thirdPersonView == 2;

            GL11.glPushMatrix();

            GlStateManager.translate(xPos, yPos, zPos);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.0F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
            GL11.glBegin(1);

            GL11.glVertex3d(0, (double) 0 + 1, 0.0);
            GL11.glVertex3d((double) 0 - 0.5, (double) 0 + 0.5, 0.0);
            GL11.glVertex3d(0, (double) 0 + 1, 0.0);
            GL11.glVertex3d((double) 0 + 0.5, (double) 0 + 0.5, 0.0);

            GL11.glVertex3d(0, 0, 0.0);
            GL11.glVertex3d((double) 0 - 0.5, (double) 0 + 0.5, 0.0);
            GL11.glVertex3d(0, 0, 0.0);
            GL11.glVertex3d((double) 0 + 0.5, (double) 0 + 0.5, 0.0);

            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void drawESP(Entity entity, float colorRed, float colorGreen, float colorBlue, float colorAlpha, float ticks) {
        try {
            double renderPosX = Wrapper.INSTANCE.mc().getRenderManager().viewerPosX;
            double renderPosY = Wrapper.INSTANCE.mc().getRenderManager().viewerPosY;
            double renderPosZ = Wrapper.INSTANCE.mc().getRenderManager().viewerPosZ;
            double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
            double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks) + entity.height / 2.0f - renderPosY;
            double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

            float playerViewY = Wrapper.INSTANCE.mc().getRenderManager().playerViewY;
            float playerViewX = Wrapper.INSTANCE.mc().getRenderManager().playerViewX;
            boolean thirdPersonView = Wrapper.INSTANCE.mc().getRenderManager().options.thirdPersonView == 2;

            GL11.glPushMatrix();

            GlStateManager.translate(xPos, yPos, zPos);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.0F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
            GL11.glBegin(1);

            GL11.glVertex3d(0, (double) 0 + 1, 0.0);
            GL11.glVertex3d((double) 0 - 0.5, (double) 0 + 0.5, 0.0);
            GL11.glVertex3d(0, (double) 0 + 1, 0.0);
            GL11.glVertex3d((double) 0 + 0.5, (double) 0 + 0.5, 0.0);

            GL11.glVertex3d(0, 0, 0.0);
            GL11.glVertex3d((double) 0 - 0.5, (double) 0 + 0.5, 0.0);
            GL11.glVertex3d(0, 0, 0.0);
            GL11.glVertex3d((double) 0 + 0.5, (double) 0 + 0.5, 0.0);

            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public static void drawESPDiamond(Entity entity, float colorRed, float colorGreen, float colorBlue, float colorAlpha, float ticks, float width) {
        try {
            double renderPosX = Wrapper.INSTANCE.mc().getRenderManager().viewerPosX;
            double renderPosY = Wrapper.INSTANCE.mc().getRenderManager().viewerPosY;
            double renderPosZ = Wrapper.INSTANCE.mc().getRenderManager().viewerPosZ;
            double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
            double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks) + entity.height / 2.0f - renderPosY;
            double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

            float playerViewY = Wrapper.INSTANCE.mc().getRenderManager().playerViewY;
            float playerViewX = Wrapper.INSTANCE.mc().getRenderManager().playerViewX;
            boolean thirdPersonView = Wrapper.INSTANCE.mc().getRenderManager().options.thirdPersonView == 2;

            GL11.glPushMatrix();

            GlStateManager.translate(xPos, yPos, zPos);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(width);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
            GL11.glBegin(1);

            GL11.glVertex3d(0, (double) 0 + 1, 0.0);
            GL11.glVertex3d((double) 0 - 0.5, (double) 0 + 0.5, 0.0);
            GL11.glVertex3d(0, (double) 0 + 1, 0.0);
            GL11.glVertex3d((double) 0 + 0.5, (double) 0 + 0.5, 0.0);

            GL11.glVertex3d(0, 0, 0.0);
            GL11.glVertex3d((double) 0 - 0.5, (double) 0 + 0.5, 0.0);
            GL11.glVertex3d(0, 0, 0.0);
            GL11.glVertex3d((double) 0 + 0.5, (double) 0 + 0.5, 0.0);

            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void drawPlayerESPRect(Entity entity, float colorRed, float colorGreen, float colorBlue, float colorAlpha, float ticks, float width) {
        try {

            double height = entity.height;
            double entityWidth = entity.width;
            double renderPosX = Wrapper.INSTANCE.mc().getRenderManager().viewerPosX;
            double renderPosY = Wrapper.INSTANCE.mc().getRenderManager().viewerPosY;
            double renderPosZ = Wrapper.INSTANCE.mc().getRenderManager().viewerPosZ;
            double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
            double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks) + entity.height / 2.0f - renderPosY;
            double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

            float playerViewY = Wrapper.INSTANCE.mc().getRenderManager().playerViewY;
            float playerViewX = 0;
            boolean thirdPersonView = Wrapper.INSTANCE.mc().getRenderManager().options.thirdPersonView == 2;

            GL11.glPushMatrix();

            GlStateManager.translate(xPos, yPos, zPos);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(width);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
            GL11.glBegin(1);

            GL11.glVertex3d(entityWidth / 2f, -height / 2f, 0);
            GL11.glVertex3d(entityWidth / 2f, height / 2f, 0);
            GL11.glVertex3d(-entityWidth / 2f, height / 2f, 0);
            GL11.glVertex3d(-entityWidth / 2f, -height / 2f, 0);

            GL11.glVertex3d(-entityWidth / 2f, height / 2f, 0);
            GL11.glVertex3d(entityWidth / 2f, height / 2f, 0);
            GL11.glVertex3d(-entityWidth / 2f, -height / 2f, 0);
            GL11.glVertex3d(entityWidth / 2f, -height / 2f, 0);

            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public static void drawOutlineBox(Entity entity, float red, float green, float blue, float ticks, float thickness) {

        AxisAlignedBB aabb = entity.getRenderBoundingBox();
        float h = (float) (aabb.maxY - aabb.minY);
        float w = (float) (aabb.maxX - aabb.minX);
        float d = (float) (aabb.maxZ - aabb.minZ);
        float x = (float) (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - w / 2f;
        float y = (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks);
        float z = (float) (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - d / 2f;


        //Draw Vertical Lines
        drawLine3D(x, y, z, x, y + h, z, thickness, red, green, blue, 1f);
        drawLine3D(x, y, z + d, x, y + h, z + d, thickness, red, green, blue, 1f);
        drawLine3D(x + w, y, z, x + w, y + h, z, thickness, red, green, blue, 1f);
        drawLine3D(x + w, y, z + d, x + w, y + h, z + d, thickness, red, green, blue, 1f);
        //Draw Bottom Quad
        drawLine3D(x, y, z, x + w, y, z, thickness, red, green, blue, 1f);
        drawLine3D(x, y, z, x, y, z + d, thickness, red, green, blue, 1f);
        drawLine3D(x + w, y, z, x + w, y, z + d, thickness, red, green, blue, 1f);
        drawLine3D(x, y, z + d, x + w, y, z + d, thickness, red, green, blue, 1f);
        //Draw Top Quad
        drawLine3D(x, y + h, z, x + w, y + h, z, thickness, red, green, blue, 1f);
        drawLine3D(x, y + h, z, x, y + h, z + d, thickness, red, green, blue, 1f);
        drawLine3D(x + w, y + h, z, x + w, y + h, z + d, thickness, red, green, blue, 1f);
        drawLine3D(x, y + h, z + d, x + w, y + h, z + d, thickness, red, green, blue, 1f);


    }


    public static void drawESPCSGO(Entity entity, float colorRed, float colorGreen, float colorBlue, float colorAlpha, float ticks, float width) {
        try {


            double renderPosX = Wrapper.INSTANCE.mc().getRenderManager().viewerPosX;
            double renderPosY = Wrapper.INSTANCE.mc().getRenderManager().viewerPosY;
            double renderPosZ = Wrapper.INSTANCE.mc().getRenderManager().viewerPosZ;
            double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
            double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks) + entity.height / 2.0f - renderPosY;
            double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

            float playerViewY = Wrapper.INSTANCE.mc().getRenderManager().playerViewY;
            float playerViewX = 0;
            boolean thirdPersonView = Wrapper.INSTANCE.mc().getRenderManager().options.thirdPersonView == 2;

            GL11.glPushMatrix();

            GlStateManager.translate(xPos, yPos, zPos);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(width);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
            GL11.glBegin(1);

            GL11.glVertex3d(0.4, (double) entity.height / 2 /*1*/, 0);
            GL11.glVertex3d(0.4, (entity.height - 0.2) / 2/*0.8*/, 0);
            GL11.glVertex3d(-0.4, (double) -entity.height / 2/*-1*/, 0);
            GL11.glVertex3d(-0.4, (-entity.height + 0.2) / 2/*-0.8*/, 0);

            GL11.glVertex3d(-0.4, (double) -entity.height / 2/*-1*/, 0);
            GL11.glVertex3d(-0.2, (double) -entity.height / 2/*-1*/, 0);
            GL11.glVertex3d(0.2, (double) entity.height / 2/*1*/, 0);
            GL11.glVertex3d(0.4, (double) entity.height / 2/*1*/, 0);


            GL11.glVertex3d(-0.4, (double) entity.height / 2/*1*/, 0);
            GL11.glVertex3d(-0.4, (entity.height - 0.2) / 2/*0.8*/, 0);
            GL11.glVertex3d(-0.4, (double) entity.height / 2/*1*/, 0);
            GL11.glVertex3d(-0.2, (double) entity.height / 2/*1*/, 0);

            GL11.glVertex3d(0.4, (double) -entity.height / 2/*-1*/, 0);
            GL11.glVertex3d(0.2, (double) -entity.height / 2/*-1*/, 0);
            GL11.glVertex3d(0.4, (-entity.height + 0.2) / 2, 0);
            GL11.glVertex3d(0.4, (double) -entity.height / 2/*-1*/, 0);

            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

/*    public static void drawESPBox(Entity entity, float colorRed, float colorGreen, float colorBlue, float colorAlpha, float ticks) {
        try {


            double renderPosX = Wrapper.INSTANCE.mc().getRenderManager().viewerPosX;
            double renderPosY = Wrapper.INSTANCE.mc().getRenderManager().viewerPosY;
            double renderPosZ = Wrapper.INSTANCE.mc().getRenderManager().viewerPosZ;
            double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
            double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks) + entity.height / 2.0f - renderPosY;
            double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

            float playerViewY = Wrapper.INSTANCE.mc().getRenderManager().playerViewY;
            float playerViewX = Wrapper.INSTANCE.mc().getRenderManager().playerViewX;
            boolean thirdPersonView = Wrapper.INSTANCE.mc().getRenderManager().options.thirdPersonView == 2;

            GL11.glPushMatrix();

            GlStateManager.translate(xPos, yPos, zPos);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.0F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
            GL11.glBegin(1);

            GL11.glVertex3d(DEV.Ver1First(), DEV.Ver1Second(), DEV.Ver1third());
            GL11.glVertex3d(DEV.Ver2First(), DEV.Ver2Second(), DEV.Ver2third());
            GL11.glVertex3d(DEV.Ver3First(), DEV.Ver3Second(), DEV.Ver3third());
            GL11.glVertex3d(DEV.Ver4First(), DEV.Ver4Second(), DEV.Ver4third());

            GL11.glVertex3d(DEV.Ver5First(), DEV.Ver5Second(), DEV.Ver5third());
            GL11.glVertex3d(DEV.Ver6First(), DEV.Ver6Second(), DEV.Ver7third());
            GL11.glVertex3d(DEV.Ver7First(), DEV.Ver7Second(), DEV.Ver7third());
            GL11.glVertex3d(DEV.Ver8First(), DEV.Ver8Second(), DEV.Ver8third());

            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }*/

    public static void drawString2D(FontRenderer fontRendererIn, String str, Entity entity, double posX, double posY, double posZ, int colorString, float colorRed, float colorGreen, float colorBlue, float colorAlpha, int verticalShift) {
        try {
            if (str != "") {
                float distance = Wrapper.INSTANCE.player().getDistance(entity);
                float playerViewY = Wrapper.INSTANCE.mc().getRenderManager().playerViewY;
                float playerViewX = Wrapper.INSTANCE.mc().getRenderManager().playerViewX;
                boolean thirdPersonView = Wrapper.INSTANCE.mc().getRenderManager().options.thirdPersonView == 2;
                float f1 = entity.height + 0.5F;

                if (distance <= 50) {
                    GlStateManager.pushMatrix();
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GlStateManager.translate(posX, posY, posZ);
                    GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate((float) (thirdPersonView ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);

                    if (distance <= 11) {
                        GlStateManager.scale(-0.027F, -0.027F, 0.027F);
                    } else {
                        GlStateManager.scale(-distance / 350, -distance / 350, distance / 350);
                    }
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);

                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    int i = fontRendererIn.getStringWidth(str) / 2;
                    GlStateManager.disableTexture2D();
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    bufferbuilder.pos(-i - 1, -1 + verticalShift, 0.0D).color(colorRed, colorGreen, colorBlue, colorAlpha).endVertex();
                    bufferbuilder.pos(-i - 1, 8 + verticalShift, 0.0D).color(colorRed, colorGreen, colorBlue, colorAlpha).endVertex();
                    bufferbuilder.pos(i + 1, 8 + verticalShift, 0.0D).color(colorRed, colorGreen, colorBlue, colorAlpha).endVertex();
                    bufferbuilder.pos(i + 1, -1 + verticalShift, 0.0D).color(colorRed, colorGreen, colorBlue, colorAlpha).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.depthMask(true);
                    fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, colorString);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public static void drawNukerBlocks(Iterable<BlockPos> blocks, float r, float g, float b, double height) {

        WorldClient world = Wrapper.INSTANCE.world();
        EntityPlayerSP player = Wrapper.INSTANCE.player();

        for (BlockPos pos : blocks) {

            drawBlockESP(pos, r, g, b, height);
        }

    }

    public static void drawXRayBlocks(LinkedList<XRayBlock> blocks, float ticks) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL11.GL_LIGHTING);

        WorldClient world = Wrapper.INSTANCE.world();
        EntityPlayerSP player = Wrapper.INSTANCE.player();

        for (XRayBlock block : blocks) {
            BlockPos pos = block.getBlockPos();
            XRayData data = block.getxRayData();

            IBlockState iblockstate = world.getBlockState(pos);

            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) ticks;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) ticks;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) ticks;

            int color = new Color(data.getRed(), data.getGreen(), data.getBlue(), 255).getRGB();
            GLUtils.glColor(color);

            AxisAlignedBB boundingBox = iblockstate.getSelectedBoundingBox(world, pos).grow(0.0020000000949949026D).offset(-x, -y, -z);
            drawSelectionBoundingBox(boundingBox);
        }

        glEnable(GL11.GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void drawBlockESP(BlockPos pos, float red, float green, float blue, double height) {

        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX,
            pos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY, pos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ,
            pos.getX() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosX,
            pos.getY() + (1) - Minecraft.getMinecraft().getRenderManager().viewerPosY,
            pos.getZ() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosZ);

        drawOutlinedBox(bb, red, green, blue, 1f, height);
        drawFilledBox(bb, red, green, blue, 0.3f, height);


    }

    public static void drawBlockESP(BlockPos pos, float red, float green, float blue, double height, float alpha1, float alpha2) {

        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX,
            pos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY, pos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ,
            pos.getX() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosX,
            pos.getY() + (1) - Minecraft.getMinecraft().getRenderManager().viewerPosY,
            pos.getZ() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosZ);

        drawOutlinedBox(bb, red, green, blue, alpha1, height);
        drawFilledBox(bb, red, green, blue, alpha2, height);


    }


    public static void drawLogoutSpot(String name, double x, double y, double z, float red, float green, float blue, double height) {


        final AxisAlignedBB bb = new AxisAlignedBB(x - Minecraft.getMinecraft().getRenderManager().viewerPosX,
            y - Minecraft.getMinecraft().getRenderManager().viewerPosY, z - Minecraft.getMinecraft().getRenderManager().viewerPosZ,
            x + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosX,
            y + (1) - Minecraft.getMinecraft().getRenderManager().viewerPosY,
            z + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosZ);

        drawOutlinedBox(bb, red, green, blue, 1f, height);


    }

    public static void drawOutlineESP(BlockPos pos, float red, float green, float blue, double height) {

        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX,
            pos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY, pos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ,
            pos.getX() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosX,
            pos.getY() + (1) - Minecraft.getMinecraft().getRenderManager().viewerPosY,
            pos.getZ() + 1 - Minecraft.getMinecraft().getRenderManager().viewerPosZ);

        drawOutlinedBox(bb, red, green, blue, 1f, height);
        drawFilledBox(bb, red, green, blue, 0.3f, height);


    }


    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder vb = ts.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts X.
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        ts.draw();// Ends X.
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts Y.
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        ts.draw();// Ends Y.
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);// Starts Z.
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ)
            .color(red, green, blue, alpha).endVertex();
        ts.draw();// Ends Z.
    }

    public static void drawSolidBox() {

        drawSolidBox(DEFAULT_AABB);
    }

    public static void drawSolidBox(AxisAlignedBB bb) {

        glBegin(GL_QUADS);
        {
            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.minY, bb.maxZ);

            glVertex3d(bb.minX, bb.maxY, bb.minZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.minY, bb.minZ);

            glVertex3d(bb.maxX, bb.minY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            glVertex3d(bb.minX, bb.minY, bb.minZ);
            glVertex3d(bb.minX, bb.minY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            glVertex3d(bb.minX, bb.maxY, bb.minZ);
        }
        glEnd();
    }

    public static void drawFilledBox(AxisAlignedBB bb, float red, float green, float blue, float alpha, double height) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);


        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();

        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawOutlinedBox(AxisAlignedBB bb, float red, float green, float blue, float alpha, double height) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1f);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY + height, bb.maxZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.maxZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY + height, bb.minZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, 0.0F).endVertex();
        tessellator.draw();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }


    public static void drawTri(double x1, double y1, double x2, double y2, double x3, double y3, double width, Color c) {

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GLUtils.glColor(c);
        GL11.glLineWidth((float) width);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawHLine(float par1, float par2, float par3, int color) {

        if (par2 < par1) {
            float var5 = par1;
            par1 = par2;
            par2 = var5;
        }

        drawRect(par1, par3, par2 + 1, par3 + 1, color);
    }

    public static void drawVLine(float par1, float par2, float par3, int color) {

        if (par3 < par2) {
            float var5 = par2;
            par2 = par3;
            par3 = var5;
        }

        drawRect(par1, par2 + 1, par1 + 1, par3, color);
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {

        float var5;

        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }

        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        GLUtils.glColor(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(left, bottom);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glVertex2d(left, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void DrawFilledCircle(float x, float y, float radius, int color) {
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GLUtils.glColor(color);
        float y1 = y;
        float x1 = x;
        double pi = 3.14159265358979323846264338327950288419716939937510582097494459230781640628620899;

        for (int i = 0; i <= 360; i++) {
            float degInRad = i / (180 / (float) pi);
            float x2 = x + ((float) Math.cos(degInRad) * radius);
            float y2 = y + ((float) Math.sin(degInRad) * radius);
            GL11.glVertex2f(x, y);
            GL11.glVertex2f(x1, y1);
            GL11.glVertex2f(x2, y2);
            y1 = y2;
            x1 = x2;
        }
        GL11.glEnd();
    }

    public static void DrawRoundedBox(float x, float y, float width, float height, int color) {
        GLUtils.glColor(color);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x + 10, y);
        GL11.glVertex2f(x + width - 10, y);
        GL11.glVertex2f(x + width - 10, y + height);
        GL11.glVertex2f(x + 10, y + height);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y + 10);
        GL11.glVertex2f(x + width, y + 10);
        GL11.glVertex2f(x + width, y + height - 10);
        GL11.glVertex2f(x, y + height - 10);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        DrawFilledCircle(x + 9.7f, y + 9.7f, 10, color);
        DrawFilledCircle(x + width - 9.7f, y + 9.7f, 10, color);
        DrawFilledCircle(x + 9.7f, y + height - 9.7f, 10, color);
        DrawFilledCircle(x + width - 9.7f, y + height - 9.7f, 10, color);
        // OUTLINE
        // Gl.glColor4f(outlinecolor.x,outlinecolor.y,outlinecolor.z,outlinecolor.w);
        // Gl.glEnable(Gl.GL_BLEND);
        // Gl.glBegin(Gl.GL_LINE_LOOP);
        // Gl.glVertex2f(x, y + 10);
        // Gl.glVertex2f(x, y + height - 10);
        // Gl.glEnd();
        // Gl.glDisable(Gl.GL_BLEND);
        // Gl.glEnable(Gl.GL_BLEND);
        // Gl.glBegin(Gl.GL_LINE_LOOP);
        // Gl.glVertex2f(x + width - 1, y + 10);
        // Gl.glVertex2f(x + width - 1, y + height - 10);
        // Gl.glEnd();
        // Gl.glDisable(Gl.GL_BLEND);
        // Gl.glEnable(Gl.GL_BLEND);
        // Gl.glBegin(Gl.GL_LINE_LOOP);
        // Gl.glVertex2f(x + 10, y + 1);
        // Gl.glVertex2f(x + width - 10, y + 1);
        // Gl.glEnd();
        // Gl.glDisable(Gl.GL_BLEND);
        // Gl.glEnable(Gl.GL_BLEND);
        // Gl.glBegin(Gl.GL_LINE_LOOP);
        // Gl.glVertex2f(x + 10, y + height);
        // Gl.glVertex2f(x + width - 10, y + height);
        // Gl.glEnd();
        // Gl.glDisable(Gl.GL_BLEND);
        // DrawCircle(x + 10, y + 10, 10, outlinecolor);
        // DrawCircle(x + width - 10, y + 10, 10, outlinecolor);
        // DrawCircle(x + 10, y + height - 10, 10, outlinecolor);
        // DrawCircle(x + width - 10, y + height - 10, 10, outlinecolor);
        // DrawFilledCircle(x + 11, y + 11, 10, color);
        // DrawFilledCircle(x + width - 11, y + 11, 10, color);
        // DrawFilledCircle(x + 11, y + height - 11, 10, color);
        // DrawFilledCircle(x + width - 11, y + height - 11, 10, color);
        // DrawFilledCircle(x + 11, y + 13, 10, color);
        // DrawFilledCircle(x + width - 11, y + 13, 10, color);
        // DrawFilledCircle(x + 11, y + height - 13, 10, color);
        // DrawFilledCircle(x + width - 11, y + height - 13, 10, color);
        // DrawFilledCircle(x + 13, y + 11, 10, color);
        // DrawFilledCircle(x + width - 13, y + 11, 10, color);
        // DrawFilledCircle(x + 13, y + height - 11, 10, color);
        // DrawFilledCircle(x + width - 13, y + height - 11, 10, color);
        // }
    }


    public static void drawHaloESP(Entity entity, Color startColor, Color endColor, float thickness, float startOffset, float height, boolean renderGroundPlate, float groundPlateOpacity, float startOpacity, float endOpacity) {

        AxisAlignedBB bb = entity.getRenderBoundingBox();
        float alpha = 0.5f;
        float h = (float) (bb.maxY - bb.minY);
        float w = (float) (bb.maxX - bb.minX);
        float d = (float) (bb.maxZ - bb.minZ);
        float x = (float) bb.minX;
        float y = (float) bb.minY;
        float z = (float) bb.minZ;


        //Draw Bottom Quad
        if (thickness > 0) {
            drawLine3D(x, y + startOffset, z, x + w, y + startOffset, z, thickness, startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, 1f);
            drawLine3D(x, y + startOffset, z, x, y + startOffset, z + d, thickness, startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, 1f);
            drawLine3D(x + w, y + startOffset, z, x + w, y + startOffset, z + d, thickness, startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, 1f);
            drawLine3D(x, y + startOffset, z + d, x + w, y + startOffset, z + d, thickness, startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, 1f);
        }
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);


        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        // BOTTOM QUAD
        if (renderGroundPlate) {
            bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, groundPlateOpacity / 100f).endVertex();
            bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, groundPlateOpacity / 100f).endVertex();
            bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, groundPlateOpacity / 100f).endVertex();
            bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, groundPlateOpacity / 100f).endVertex();
        }

        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(endColor.getRed() / 255f, endColor.getGreen() / 255f, endColor.getBlue() / 255f, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(endColor.getRed() / 255f, endColor.getGreen() / 255f, endColor.getBlue() / 255f, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, startOpacity / 100f).endVertex();

        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(endColor.getRed() / 255f, endColor.getGreen() / 255f, endColor.getBlue() / 255f, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(endColor.getRed() / 255f, endColor.getGreen() / 255f, endColor.getBlue() / 255f, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, startOpacity / 100f).endVertex();

        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(endColor.getRed() / 255f, endColor.getGreen() / 255f, endColor.getBlue() / 255f, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(endColor.getRed() / 255f, endColor.getGreen() / 255f, endColor.getBlue() / 255f, endOpacity / 100f).endVertex();

        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(startColor.getRed() / 255f, startColor.getGreen() / 255f, startColor.getBlue() / 255f, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(endColor.getRed() / 255f, endColor.getGreen() / 255f, endColor.getBlue() / 255f, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(endColor.getRed() / 255f, endColor.getGreen() / 255f, endColor.getBlue() / 255f, endOpacity / 100f).endVertex();
        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();


    }

    public static void drawGradientRect(float x, float y, float w, float h, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float) (startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (startColor & 0xFF) / 255.0f;
        float f4 = (float) (endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float) (endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float) (endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float) (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos((double) x + (double) w, y, 0.0).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos(x, y, 0.0).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos(x, (double) y + (double) h, 0.0).color(f5, f6, f7, f4).endVertex();
        vertexbuffer.pos((double) x + (double) w, (double) y + (double) h, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    public static void drawHaloESP(AxisAlignedBB bb, float red, float green, float blue, float thickness, float startOffset, float height, boolean renderGroundPlate, float groundPlateOpacity, float startOpacity, float endOpacity) {


        float alpha = 0.5f;
        float h = (float) (bb.maxY - bb.minY);
        float w = (float) (bb.maxX - bb.minX);
        float d = (float) (bb.maxZ - bb.minZ);
        float x = (float) bb.minX;
        float y = (float) bb.minY;
        float z = (float) bb.minZ;


        //Draw Bottom Quad
        drawLine3D(x, y + startOffset, z, x + w, y + startOffset, z, thickness, red, green, blue, 1f);
        drawLine3D(x, y + startOffset, z, x, y + startOffset, z + d, thickness, red, green, blue, 1f);
        drawLine3D(x + w, y + startOffset, z, x + w, y + startOffset, z + d, thickness, red, green, blue, 1f);
        drawLine3D(x, y + startOffset, z + d, x + w, y + startOffset, z + d, thickness, red, green, blue, 1f);

        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);


        //Draw Vertical Lines
//        drawLine3D(x, y, z, x, y + h, z, thickness, red, green, blue, 1f);
//        drawLine3D(x, y, z + d, x, y + h, z + d, thickness, red, green, blue, 1f);
//        drawLine3D(x + w, y, z, x + w, y + h, z, thickness, red, green, blue, 1f);
//        drawLine3D(x + w, y, z + d, x + w, y + h, z + d, thickness, red, green, blue,1f);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        // BOTTOM QUAD
        if (renderGroundPlate) {
            bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, groundPlateOpacity / 100f).endVertex();
            bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, groundPlateOpacity / 100f).endVertex();
            bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, groundPlateOpacity / 100f).endVertex();
            bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, groundPlateOpacity / 100f).endVertex();
        }
        //TOP QUAD
//        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.maxY - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, 0).endVertex();
//        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.maxY - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, 0).endVertex();
//        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.maxY - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, 1).endVertex();
//        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.maxY - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, 1).endVertex();


        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, startOpacity / 100f).endVertex();

        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, startOpacity / 100f).endVertex();

        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.maxX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, endOpacity / 100f).endVertex();

        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, bb.minY + startOffset - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, startOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, endOpacity / 100f).endVertex();
        bufferbuilder.pos(bb.minX - mc.getRenderManager().viewerPosX, (bb.minY + bb.maxY) / 2f + height - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ).color(red, green, blue, endOpacity / 100f).endVertex();
        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();


    }

    public static float getAnimatedOffset(float intensity, float speed) {

        return (float) Math.sin((System.currentTimeMillis() % 360) / speed) * intensity;

    }

    public static Vec3d getInterpolatedLinearVec(Entity entity, float ticks) {
        return new Vec3d(
            lerp(entity.lastTickPosX, entity.posX, ticks),
            lerp(entity.lastTickPosY, entity.posY, ticks),
            lerp(entity.lastTickPosZ, entity.posZ, ticks)
        );
    }

    static double lerp(double a, double b, float ticks) {
        if (ticks == 1 || ticks == 5) {
            return b;
        }

        return (a + (b - a) * ticks);
    }


}


