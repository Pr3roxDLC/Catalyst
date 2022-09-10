package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.Utils;
import com.krazzzzymonkey.catalyst.utils.ValidUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;

//TODO CLEAN UP CODE, look at ShitClient Tracer

public class Tracers extends Modules {

    public BooleanValue Players;
    public BooleanValue Friends;
    public BooleanValue Mobs;
    public BooleanValue invisibles;
    public BooleanValue Combat;
    public static DoubleValue lineWidth;
    public ModeValue mode;
    public ColorValue color;
    public BooleanValue stem;
    public DoubleValue stemHeight;

    public Tracers() {
        super("Tracers", ModuleCategory.RENDER, "Draws line to entity");
        mode = new ModeValue("Mode", new Mode("Arrows", true), new Mode("Lines", false));
        color = new ColorValue("Color", new Color(0, 255, 255, 15), "Changes the color of the tracers");
        Players = new BooleanValue("Players", true, "Draws a line to players");
        Friends = new BooleanValue("Friends", false, "Draws a line to friends");
        Mobs = new BooleanValue("Mobs", false, "Draws a line to mobs");
        invisibles = new BooleanValue("Invisibles", false, "Draws a line to invisibles");
        Combat = new BooleanValue("InCombat", false, "Makes the rendered line red when the entity takes damage");
        lineWidth = new DoubleValue("LineWidth", 1D, 1D, 15D, "The width of the rendered lines");
        stem = new BooleanValue("Stem", false, "Draws a line up from the tracer");
        stemHeight = new DoubleValue("StemHeight", 1, 0, 3, "The height of the stem");
        this.addValue(mode, color, Players, Friends, Mobs, invisibles, Combat, lineWidth, stem, stemHeight);
    }

    @EventHandler
    private final EventListener<RenderGameOverlayEvent> onRenderGameOverlay = new EventListener<>(event -> {
        if (mode.getMode("Arrows").isToggled()) {

            for (Entity e : mc.world.loadedEntityList) {

                if (e instanceof EntityLivingBase) {
                    Vec3d ePos = new Vec3d(e.lastTickPosX + (e.posX - e.lastTickPosX) * mc.getRenderPartialTicks(),
                        e.lastTickPosY + (e.posY - e.lastTickPosY) * mc.getRenderPartialTicks(),
                        e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * mc.getRenderPartialTicks()).add(0, e.getEyeHeight(), 0);
                    if (!isOnScreen(ePos) && !isInViewFrustrum(e)) {
                        renderArrow((EntityLivingBase) e);
                    }

                }
            }
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
    });


    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        if (mode.getMode("Lines").isToggled()) {
            for (Object object : Wrapper.INSTANCE.world().loadedEntityList) {
                if (object instanceof EntityLivingBase && !(object instanceof EntityArmorStand)) {
                    if (object != Minecraft.getMinecraft().player) {
                        EntityLivingBase entity = (EntityLivingBase) object;
                        this.render(entity);
                    }
                }
            }
        }
    });

    void render(EntityLivingBase entity) {

        if (Mobs.getValue()) {
            if (ValidUtils.isValidEntityTracers(entity) || entity == Wrapper.INSTANCE.player()) {
                RenderUtils.drawLineToEntity(entity, 1.0f, 1.0f, 1.0f, 0.5f);
                return;
            }
        }

        if (ValidUtils.isValidEntityTracers(entity) || entity == Wrapper.INSTANCE.player()) {
            return;
        }

        if (Friends.getValue()) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                String ID = Utils.getPlayerName(player);
                if (FriendManager.friendsList.contains(ID)) {
                    if (!stem.getValue()) {
                        RenderUtils.drawLineToEntity(entity, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 0.5f);
                    } else {
                        RenderUtils.drawLineToEntityWithStem(entity, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 0.5f, stemHeight.getValue().floatValue());
                    }
                    return;
                }
            }
        }

        if (invisibles.getValue()) {
            if (entity.isInvisible()) {
                if (!stem.getValue()) {
                    RenderUtils.drawLineToEntity(entity, color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f, 0.5f);
                } else {
                    RenderUtils.drawLineToEntityWithStem(entity, color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f, 0.5f, stemHeight.getValue().floatValue());
                }
                return;
            }
        }
        if (Combat.getValue()) {
            if (entity.hurtTime > 0) {
                if (!stem.getValue()) {
                    RenderUtils.drawLineToEntity(entity, 1.0f, 1.0f, 1.0f, 0.5f);
                } else {
                    RenderUtils.drawLineToEntityWithStem(entity, 1.0f, 1.0f, 1.0f, 0.5f, stemHeight.getValue().floatValue());
                }
                return;
            }
        }

        if (Players.getValue() && entity != Wrapper.INSTANCE.player()) {
            if (!stem.getValue()) {
                RenderUtils.drawLineToEntity(entity, color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f, 0.5f);
            } else {
                RenderUtils.drawLineToEntityWithStem(entity, color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f, 0.5f, stemHeight.getValue().floatValue());
            }
        }


        if (!stem.getValue()) {
            RenderUtils.drawLineToEntity(entity, color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f, 0.5f);
        } else {
            RenderUtils.drawLineToEntityWithStem(entity, color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f, 0.5f, stemHeight.getValue().floatValue());
        }
    }


    void renderArrow(EntityLivingBase entity) {

        if (Mobs.getValue()) {
            if (ValidUtils.isValidEntityTracers(entity) || entity == Wrapper.INSTANCE.player()) {
                renderArrowTracer(entity, new Color(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue(), 100));
                return;
            }
        }

        if (ValidUtils.isValidEntityTracers(entity) || entity == Wrapper.INSTANCE.player()) {
            return;
        }

        if (Friends.getValue()) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                String ID = Utils.getPlayerName(player);
                if (FriendManager.friendsList.contains(ID)) {
                    renderArrowTracer(entity, ColorUtils.rainbow());
                    return;
                }
            }
        }

        if (invisibles.getValue()) {
            if (entity.isInvisible()) {
                renderArrowTracer(entity, new Color(255, 255, 255, 100));
                return;
            }
        }
        if (Combat.getValue()) {
            if (entity.hurtTime > 0) {
                renderArrowTracer(entity, new Color(255, 0, 0, color.getColor().getAlpha()));
                return;
            }
        }

        if (Players.getValue() && entity != Wrapper.INSTANCE.player()) {
            renderArrowTracer(entity, color.getColor());
        }


        renderArrowTracer(entity, color.getColor());
    }


    void renderArrowTracer(Entity e, Color color) {
        GL11.glPushMatrix();
        int x = Display.getWidth() / 2 / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale);
        int y = Display.getHeight() / 2 / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale);
        float yaw = getRotations(e) - mc.player.rotationYaw;
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-x, -y, 0.0F);
        RenderUtils.drawTracerPointer(x, y - 30, 2 * 5F, 2.0F, 1.0F, false, lineWidth.getValue().floatValue(), color.getRGB());
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-x, -y, 0.0F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
    }

    private boolean isOnScreen(Vec3d pos) {
        if (pos.x > -1.0D && pos.y < 1.0D)
            return (pos.x / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale) >= 0.0D && pos.x / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale) <= Display.getWidth() && pos.y / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale) >= 0.0D && pos.y / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale) <= Display.getHeight());
        return false;
    }

    private float getRotations(Entity entity) {
        double x = entity.posX - mc.player.posX;
        double z = entity.posZ - mc.player.posZ;
        return (float) -(Math.atan2(x, z) * (180 / Math.PI));
    }

    private static final Frustum frustrum = new Frustum();

    public static boolean isInViewFrustrum(Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }
}
