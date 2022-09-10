package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MovementUtil;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Breadcrumbs extends Modules {

    public static IntegerValue ticks;
    public static IntegerValue maxPoints;
    public static IntegerValue lineWidth;
    public static ColorValue colorValue;
    public static BooleanValue traceEnderPearls;
    public static BooleanValue disableOnFreecam;
    public static BooleanValue self;

    int ticksPassed = 0;

    public static LinkedList<Vec3d> points = new LinkedList<>();
    public static HashMap<Entity, LinkedList<Vec3d>> tracedEntities = new HashMap<>();
    public static ArrayList<String> names = new ArrayList<String>();
    public static HashMap<Entity, LinkedList<Vec3d>> tracedPlayers = new HashMap<>();

    public Breadcrumbs() {
        super("Breadcrumbs", ModuleCategory.RENDER, "Traces the movement of entities and players");
        ticks = new IntegerValue("Ticks", 1, 1, 100, "The ticks between each update");
        maxPoints = new IntegerValue("MaxPoints", 5000, 1, 50000, "The maximum amounts of points in the breadcrumbs");
        lineWidth = new IntegerValue("LineWidth", 2, 1, 10, "The width of the line");
        colorValue = new ColorValue("Color", new Color(255, 254, 254), "The color of the breadcrumbs line");
        traceEnderPearls = new BooleanValue("EnderPearls", true, "Should it render breadcrumbs for ender pearls");
        self = new BooleanValue("Self", true, "Should it render breadcrumbs for yourself");
        disableOnFreecam = new BooleanValue("DisableInFreeCam", true, "Prevent Breadcrumbs from tracing you when in freecam");
        addValue(self, traceEnderPearls, ticks, maxPoints, colorValue, lineWidth);
    }


    @Override
    public void onEnable() {
        super.onEnable();
        ticksPassed = 0;
        points = new LinkedList<>();
        tracedEntities = new HashMap<>();
        names = new ArrayList<String>();
        tracedPlayers = new HashMap<>();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ticksPassed = 0;
        points = new LinkedList<>();
        tracedEntities = new HashMap<>();
        names = new ArrayList<String>();
        tracedPlayers = new HashMap<>();
    }


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (mc.player != null) {
            if (self.getValue()) {
                if (disableOnFreecam.getValue() && ModuleManager.getModule("Freecam").isToggled())
                    return;
                ticksPassed++;
                if (ticksPassed % ticks.getValue() == 0) {
                    if (MovementUtil.isMoving())
                        points.addLast(new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ));
                    if (points.size() > maxPoints.getValue()) {
                        points.removeFirst();
                    }
                }
            } else {
                points.clear();
            }

            if (traceEnderPearls.getValue()) {
                mc.world.loadedEntityList.stream().filter(n -> n instanceof EntityEnderPearl).forEach(entity -> {
                    if (!tracedEntities.containsKey(entity)) {
                        LinkedList<Vec3d> list = new LinkedList<>();
                        list.addLast(new Vec3d(entity.posX, entity.posY, entity.posZ));
                        tracedEntities.put(entity, list);
                    } else {
                        tracedEntities.get(entity).addLast(new Vec3d(entity.posX, entity.posY, entity.posZ));
                    }
                });
            }

            mc.world.loadedEntityList.forEach(n -> {
                if (names.contains(n.getName().toLowerCase())) {
                    if (!tracedPlayers.containsKey(n)) {
                        LinkedList<Vec3d> list = new LinkedList<>();
                        list.addLast(new Vec3d(n.posX, n.posY, n.posZ));
                        tracedPlayers.put(n, list);
                    } else {
                        tracedPlayers.get(n).addLast(new Vec3d(n.posX, n.posY, n.posZ));
                    }
                }
            });

        } else {
            points.clear();
            tracedEntities.clear();
        }

        //tracedEntities.keySet().stream().filter(n -> !mc.world.loadedEntityList.contains(n)).collect(Collectors.toSet()).forEach(n -> tracedEntities.remove(n));
    });


    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {

        if (self.getValue()) {
            if (!points.isEmpty()) {
                final Vec3d[] currentPoint = {points.getFirst()};
                points.iterator().forEachRemaining(n -> {
                    RenderUtils.drawLine3D((float) currentPoint[0].x, (float) currentPoint[0].y, (float) currentPoint[0].z, (float) n.x, (float) n.y, (float) n.z, lineWidth.getValue(), colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1);
                    currentPoint[0] = n;
                });
                if (!(disableOnFreecam.getValue() && ModuleManager.getModule("Freecam").isToggled())) {
                    RenderUtils.drawLine3D((float) points.getLast().x, (float) points.getLast().y, (float) points.getLast().z, (float) mc.player.posX, (float) mc.player.posY, (float) mc.player.posZ, lineWidth.getValue(), colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1);
                }
            }
        }

        if (traceEnderPearls.getValue()) {
            tracedEntities.forEach((entity, n) -> {
                if (mc.world.loadedEntityList.contains(entity)) {
                    if (!tracedEntities.get(entity).isEmpty()) {
                        final Vec3d[] currentPoint1 = {n.getFirst()};
                        n.iterator().forEachRemaining(vec3d -> {
                            RenderUtils.drawLine3D((float) currentPoint1[0].x, (float) currentPoint1[0].y, (float) currentPoint1[0].z, (float) vec3d.x, (float) vec3d.y, (float) vec3d.z, lineWidth.getValue(), colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1);
                            currentPoint1[0] = vec3d;
                        });

                    }
                }
            });
        }
        if (!tracedPlayers.isEmpty()) {
            tracedPlayers.forEach(((entity, vec3ds) -> {
                if (mc.world.loadedEntityList.contains(entity)) {
                    if (!tracedPlayers.get(entity).isEmpty()) {
                        final Vec3d[] currentPoint2 = {vec3ds.getFirst()};
                        vec3ds.iterator().forEachRemaining(vec3d -> {
                            RenderUtils.drawLine3D((float) currentPoint2[0].x, (float) currentPoint2[0].y, (float) currentPoint2[0].z, (float) vec3d.x, (float) vec3d.y, (float) vec3d.z, lineWidth.getValue(), colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1);
                            currentPoint2[0] = vec3d;
                        });
                        RenderUtils.drawLine3D((float) vec3ds.getLast().x, (float) vec3ds.getLast().y, (float) vec3ds.getLast().z, (float) entity.posX, (float) entity.posY, (float) entity.posZ, lineWidth.getValue(), colorValue.getColor().getRed() / 255f, colorValue.getColor().getGreen() / 255f, colorValue.getColor().getBlue() / 255f, 1);
                    }
                }
            }));
        }
    });


}
