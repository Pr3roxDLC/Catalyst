package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


//TODO THIS IS FUCKED WITH THE OBF


/**
 * Created by hub on 08 July 2020
 */
public class NetherSky extends Modules {

 /*   private static ISpaceRenderer skyboxSpaceRenderer;
    private static final Minecraft mc = Minecraft.getMinecraft();

    private final ModeValue mode;
    private boolean wasChanged; // TODO: pls use event system so i can stop doing this :3*/

    public NetherSky() {

        super("NetherSky", ModuleCategory.RENDER, "Nether Sky");

     /*   mode = new ModeValue("Mode",
            new Mode("Dickbutt", true),
            new Mode("Cow", false),
            new Mode("Grin", false),
            new Mode("0b0t", false),
            new Mode("Arthur", false),
            new Mode("Impact", false)
        );

        this.addValue(mode);
        skyboxSpaceRenderer = new SkyboxSpaceRenderer(mc);*/

    }
/*

    @Override
    public void onEnable() {
        wasChanged = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        disableBackgroundRenderer(mc.player.world);
        super.onDisable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;

        if (wasChanged) {
            return;
        }
        enableBackgroundRenderer(mc.player.world);
        wasChanged = true;
    });

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        wasChanged = false;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        wasChanged = false;
    }

    private void enableBackgroundRenderer(World world) {
        if (world.provider.getDimensionType() == DimensionType.NETHER) {
            world.provider.skyRenderer = new IRenderHandler() {
                @Override
                public void render(float partialTicks, WorldClient world, Minecraft mc) {
                    skyboxSpaceRenderer.render(mode);
                }
            });
        }
    }

    private void disableBackgroundRenderer(World world) {
        if (world.provider.getDimensionType() == DimensionType.NETHER) {
            world.provider.setSkyRenderer(new IRenderHandler() {
                @Override
                public void render(float partialTicks, WorldClient world, Minecraft mc) {
                }
            });
        }
    }

    public interface ISpaceRenderer {
        void render(ModeValue mode);
    }

    // thanks for renderer https://github.com/StarLegacy/SpaceCandy/
    public static class SkyboxSpaceRenderer implements ISpaceRenderer {

        private final Minecraft mc;

        public SkyboxSpaceRenderer(Minecraft mc) {
            this.mc = mc;
        }

        @Override
        public void render(ModeValue mode) {

            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.depthMask(false);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for (int i = 0; i < 6; ++i) {
                try {
                    if (mode.getMode("Dickbutt").isToggled()) {
                        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "skybox" + File.separator + "dickbutt_army.png");
                        mc.getTextureManager().bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
                    } else if (mode.getMode("Cow").isToggled()) {
                        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "skybox" + File.separator + "melon_cow_bg.png");
                        mc.getTextureManager().bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
                    } else if (mode.getMode("Grin").isToggled()) {
                        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "skybox" + File.separator + "evil_grin.png");
                        mc.getTextureManager().bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
                    } else if (mode.getMode("0b0t").isToggled()) {
                        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "skybox" + File.separator + "0b0t.png");
                        mc.getTextureManager().bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
                    } else if (mode.getMode("Arthur").isToggled()) {
                        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "skybox" + File.separator + "arthur.png");
                        mc.getTextureManager().bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
                    } else if (mode.getMode("Impact").isToggled()) {
                        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "skybox" + File.separator + "impact.png");
                        mc.getTextureManager().bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
                    } else {
                        File file = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator + "assets" + File.separator + "skybox" + File.separator + "melon_cow_bg.png");
                        mc.getTextureManager().bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                GlStateManager.pushMatrix();

                if (i == 1) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 2) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(180.0f, 0.0F, 1.0F, 0.0F);
                }

                if (i == 3) {
                    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 4) {
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(-90.0f, 0.0F, 1.0F, 0.0F);
                }

                if (i == 5) {
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(90.0f, 0.0F, 1.0F, 0.0F);
                }

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                double size = 100.0D;
                bufferbuilder.pos(-size, -size, -size).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
                bufferbuilder.pos(-size, -size, size).tex(0.0D, 1).color(255, 255, 255, 255).endVertex();
                bufferbuilder.pos(size, -size, size).tex(1, 1).color(255, 255, 255, 255).endVertex();
                bufferbuilder.pos(size, -size, -size).tex(1, 0.0D).color(255, 255, 255, 255).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();

            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableAlpha();

        }

    }
*/

}
