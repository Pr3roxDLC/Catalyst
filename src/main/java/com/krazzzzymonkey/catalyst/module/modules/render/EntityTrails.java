package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;

public class EntityTrails extends Modules {

    private final ConcurrentHashMap<Integer, ThrownEntity> thrownEntities = new ConcurrentHashMap<>();

    private ColorValue color = new ColorValue("Color", Color.CYAN, "");
    private BooleanValue timeout = new BooleanValue("Timeout", true, "");
    private IntegerValue timeoutSeconds = new IntegerValue("Seconds", 10, 0, 100, "");

    public EntityTrails() {
        super("EntityTrails", ModuleCategory.RENDER, "Renders trails behind moving entities");
        this.addValue(color, timeout, timeoutSeconds);
    }


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(event -> {

        if (mc.player == null || mc.world == null) {
            thrownEntities.clear();
            return;
        }
        mc.world.loadedEntityList.stream()
            .filter(entity -> mc.player != entity)
            .forEach(entity -> {
                if (entity.ticksExisted > 1 && isProjectile(entity)) {
                    if (!thrownEntities.containsKey(entity.getEntityId())) {
                        final ArrayList<Vec3d> list = new ArrayList<>();

                        list.add(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ));

                        thrownEntities.put(entity.getEntityId(), new ThrownEntity(System.currentTimeMillis(), list));
                    } else {
                        thrownEntities.get(entity.getEntityId()).getVertices().add(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ));
                        thrownEntities.get(entity.getEntityId()).setTime(System.currentTimeMillis());
                    }
                }

            });

        if (timeout.getValue()) {
            thrownEntities.forEach((id, thrownEntity) -> {
                if (System.currentTimeMillis() - thrownEntity.getTime() > 1000L * timeoutSeconds.getValue()) {
                    thrownEntities.remove(id);
                }
            });
        }

    });

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(event -> {
        if (mc.player == null || mc.world == null) return;

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable(2848);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GL11.glHint(3154, 4354);
        GlStateManager.depthMask(false);
        GlStateManager.color(color.getColor().getRed() / 255.0F, color.getColor().getGreen() / 255.0F, color.getColor().getBlue() / 255.0F, color.getColor().getAlpha() / 255.0F);

        double x = (mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks());
        double y = (mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks());
        double z = (mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks());

        thrownEntities.forEach((id, thrownEntity) -> {
            GL11.glBegin(GL_LINE_STRIP);
            for (Vec3d vertex : thrownEntity.getVertices()) {
                Vec3d vec = vertex.subtract(x, y, z);
                GL11.glVertex3d(vec.x, vec.y, vec.z);
            }
            GL11.glEnd();
        });

        GL11.glPopMatrix();

        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();

    });

    private boolean isProjectile(Entity entity) {
        if (entity instanceof EntitySnowball) return true;

        else if (entity instanceof EntityArrow) return true;

        else if (entity instanceof EntityEnderPearl) return true;

        else if (entity instanceof EntityEgg) return true;

        return false;
    }

    private static class ThrownEntity {

        private long time;
        private ArrayList<Vec3d> vertices;

        public ThrownEntity(long time, ArrayList<Vec3d> vertices) {
            this.time = time;
            this.vertices = vertices;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public ArrayList<Vec3d> getVertices() {
            return vertices;
        }

        public void setVertices(ArrayList<Vec3d> vertices) {
            this.vertices = vertices;
        }

    }

}
