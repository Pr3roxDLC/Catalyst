package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;

import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;


public class Trajectories extends Modules {

    private ColorValue color;
    private BooleanValue rainbow;

    public Trajectories() {
        super("Trajectories", ModuleCategory.RENDER, "Shows where trajectories are going to land");
        this.color = new ColorValue("Color", Color.CYAN, "Changes the color of trajectories");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes trajectories cycle through colors");
        this.addValue(color, rainbow);
    }

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        EntityPlayerSP player = Wrapper.INSTANCE.player();

        float yawAim = Wrapper.INSTANCE.player().rotationYaw;
        float pitchAim = Wrapper.INSTANCE.player().rotationPitch;

        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null)
            return;

        Item item = stack.getItem();
        if (!(item instanceof ItemBow || item instanceof ItemSnowball
                || item instanceof ItemEgg || item instanceof ItemEnderPearl
                || item instanceof ItemSplashPotion
                || item instanceof ItemLingeringPotion
                || item instanceof ItemFishingRod
                || item instanceof ItemExpBottle))
            return;

        boolean usingBow =
                player.inventory.getCurrentItem().getItem() instanceof ItemBow;

        double arrowPosX = player.lastTickPosX
                + (player.posX - player.lastTickPosX) * e.getPartialTicks()
                - MathHelper.cos((float) Math.toRadians(yawAim)) * 0.16F;
        double arrowPosY = player.lastTickPosY
                + (player.posY - player.lastTickPosY)
                * e.getPartialTicks()
                + player.getEyeHeight() - 0.1;
        double arrowPosZ = player.lastTickPosZ
                + (player.posZ - player.lastTickPosZ)
                * e.getPartialTicks()
                - MathHelper.sin((float) Math.toRadians(yawAim)) * 0.16F;

        float arrowMotionFactor = usingBow ? 1F : 0.4F;
        float yaw = (float) Math.toRadians(yawAim);
        float pitch = (float) Math.toRadians(pitchAim);
        float arrowMotionX =
                -MathHelper.sin(yaw) * MathHelper.cos(pitch) * arrowMotionFactor;
        float arrowMotionY = -MathHelper.sin(pitch) * arrowMotionFactor;
        float arrowMotionZ =
                MathHelper.cos(yaw) * MathHelper.cos(pitch) * arrowMotionFactor;
        double arrowMotion = Math.sqrt(arrowMotionX * arrowMotionX
                + arrowMotionY * arrowMotionY + arrowMotionZ * arrowMotionZ);
        arrowMotionX /= arrowMotion;
        arrowMotionY /= arrowMotion;
        arrowMotionZ /= arrowMotion;
        if (usingBow) {
            float bowPower = (72000 - player.getItemInUseCount()) / 20F;
            bowPower = (bowPower * bowPower + bowPower * 2F) / 3F;

            if (bowPower > 1F)
                bowPower = 1F;

            if (bowPower <= 0.1F)
                bowPower = 1F;

            bowPower *= 3F;
            arrowMotionX *= bowPower;
            arrowMotionY *= bowPower;
            arrowMotionZ *= bowPower;
        } else {
            arrowMotionX *= 1.5D;
            arrowMotionY *= 1.5D;
            arrowMotionZ *= 1.5D;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.8F);

        RenderManager renderManager = Wrapper.INSTANCE.mc().getRenderManager();

        double gravity = usingBow ? 0.05D : (item instanceof ItemPotion || item instanceof ItemExpBottle) ? 0.4D
                : item instanceof ItemFishingRod ? 0.15D : 0.03D;
        Vec3d playerVector = new Vec3d(player.posX,
                player.posY + player.getEyeHeight(), player.posZ);
        if (rainbow.getValue()) {
            GL11.glColor3d(ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f);
        } else {
            GL11.glColor3d(color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f);
        }

        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i < 1000; i++) {
            GL11.glVertex3d(arrowPosX - renderManager.viewerPosX,
                    arrowPosY - renderManager.viewerPosY,
                    arrowPosZ - renderManager.viewerPosZ);

            arrowPosX += arrowMotionX * 0.1;
            arrowPosY += arrowMotionY * 0.1;
            arrowPosZ += arrowMotionZ * 0.1;
            arrowMotionX *= 0.999D;
            arrowMotionY *= 0.999D;
            arrowMotionZ *= 0.999D;
            arrowMotionY -= gravity * 0.1;

            if (Wrapper.INSTANCE.world().rayTraceBlocks(playerVector,
                    new Vec3d(arrowPosX, arrowPosY, arrowPosZ)) != null)
                break;
        }
        GL11.glEnd();

        double renderX = arrowPosX - renderManager.viewerPosX;
        double renderY = arrowPosY - renderManager.viewerPosY;
        double renderZ = arrowPosZ - renderManager.viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(renderX - 0.5, renderY - 0.5,
                renderZ - 0.5, renderX + 0.5, renderY + 0.5, renderZ + 0.5);

        GL11.glColor4f(1F, 1F, 1F, 0.15F);
        //RenderUtils.drawColorBox(bb, 1F, 1F, 1F, 0.15F);
        GL11.glColor4d(1, 1, 1, 0.5F);
        //RenderUtils.drawSelectionBoundingBox(bb);

        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(GL13.GL_MULTISAMPLE);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();

    });

}
