package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;


public class NewChunks extends Modules {

    private ColorValue color;
    private BooleanValue rainbow;
    private IntegerValue renderHeight;

    public static ArrayList<Chunk> coords = new ArrayList<>();

    public NewChunks() {
        super("NewChunks", ModuleCategory.RENDER, "Makes newly generated chunks glow");
        this.color = new ColorValue("Color", Color.RED, "Changes the color of new chunks");
        this.rainbow = new BooleanValue("Rainbow", false, "Makes new chunks cycle through colors");
        this.renderHeight = new IntegerValue("RenderHeight", 0, 0, 255, "Changes the render height of new chunks");
        this.addValue(color, rainbow, renderHeight);
    }

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {
        try {
            for (Chunk chunk : coords) {
                int x, y, z;
                x = chunk.x * 16;
                y = renderHeight.getValue();
                z = chunk.z * 16;
                if(rainbow.getValue()){
                    chunkESP(x, y, z, ColorUtils.rainbow());
                }else {
                    chunkESP(x, y, z, color.getColor());
                }
            }
        } catch (Exception ignored) {
        }

    });

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.IN) {

            Packet packet = e.getPacket();
            if (packet instanceof SPacketChunkData) {
                SPacketChunkData sPacketChunkData = (SPacketChunkData) packet;
                if (!sPacketChunkData.isFullChunk()) {
                    coords.add(Wrapper.INSTANCE.world().getChunk(sPacketChunkData.getChunkX(),
                        sPacketChunkData.getChunkZ()));
                }
            }
        }
    });

    public static void chunkESP(double x, double y, double z, Color color) {
        double posX = x - Minecraft.getMinecraft().getRenderManager().renderPosX;
        double posY = y - Minecraft.getMinecraft().getRenderManager().renderPosY;
        double posZ = z - Minecraft.getMinecraft().getRenderManager().renderPosZ;
        GL11.glPushMatrix();
        GL11.glEnable(2848); // tracer renderer
        GL11.glDisable(2929); // minecraft renderer
        GL11.glDisable(3553); // minecraft renderer
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(1);
        GL11.glColor3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        // 1
        // GL11.glColor3f(1f, 0, 0);
        GL11.glBegin(2);
        GL11.glVertex3d(posX, posY, posZ);
        GL11.glVertex3d(posX + 16, posY, posZ);
        GL11.glVertex3d(posX + 16, posY, posZ);
        GL11.glVertex3d(posX, posY, posZ);
        GL11.glEnd();
        // 2
        // GL11.glColor3f(0f, 1f, 0f);
        GL11.glBegin(2);
        GL11.glVertex3d(posX, posY, posZ);
        GL11.glVertex3d(posX, posY, posZ + 16);

        GL11.glEnd();
        // 3
        // GL11.glColor3f(0f, 0f, 1f);
        GL11.glBegin(2);
        GL11.glVertex3d(posX, posY, posZ + 16);
        GL11.glVertex3d(posX + 16, posY, posZ + 16);
        GL11.glVertex3d(posX + 16, posY, posZ + 16);
        GL11.glVertex3d(posX, posY, posZ + 16);
        GL11.glEnd();

        // 4
        // GL11.glColor3f(1f, 1f, 1f);
        GL11.glBegin(2);
        GL11.glVertex3d(posX + 16, posY, posZ + 16);
        GL11.glVertex3d(posX + 16, posY, posZ);
        GL11.glVertex3d(posX + 16, posY, posZ);
        GL11.glVertex3d(posX + 16, posY, posZ + 16);
        GL11.glColor3f(189, 0, 0);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}
