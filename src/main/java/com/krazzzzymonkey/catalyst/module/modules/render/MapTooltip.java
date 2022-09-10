package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

//TODO MERGE WITH NORENDER
public class MapTooltip extends Modules {

    private static int x;
    private static int y;

    public MapTooltip() {
        super("MapTooltip", ModuleCategory.RENDER, "Displays map content as tooltip");
    }

    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof ItemMap) {
            event.getToolTip().clear();
            event.getToolTip().add(event.getItemStack().getDisplayName());
        }
    }

    @SubscribeEvent
    public void onPostBackgroundTooltipRender(RenderTooltipEvent.PostBackground event) {
        if (event.getStack().getItem() instanceof ItemMap) {
            x = event.getX();
            y = event.getY();
        }
    }

    @SubscribeEvent
    public void onPostDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.getGui() instanceof GuiContainer))
            return;
        if (!(mc.player.inventory.getItemStack().getItem() instanceof net.minecraft.item.ItemAir))
            return;
        Slot slotUnderMouse = ((GuiContainer) event.getGui()).hoveredSlot;
        if (slotUnderMouse == null || !slotUnderMouse.getHasStack())
            return;
        ItemStack itemUnderMouse = slotUnderMouse.getStack();
        if (!(itemUnderMouse.getItem() instanceof ItemMap))
            return;
        MapData mapdata = ((ItemMap) itemUnderMouse.getItem()).getMapData(itemUnderMouse, mc.world);
        if (mapdata == null)
            return;
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.translate(x, y + 15.5D, 0.0D);
        GlStateManager.scale(0.5D, 0.5D, 1.0D);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }


}
