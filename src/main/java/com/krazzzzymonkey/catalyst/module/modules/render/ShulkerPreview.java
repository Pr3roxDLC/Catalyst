package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;


//TODO DO IT LIKE FORGEHAX, KRAZZY KNOWS WHAT THIS MEANS
public class ShulkerPreview extends Modules {

    private final BooleanValue itemFrame = new BooleanValue("ItemFramePeek", true, "Renders a preview of a shulker in an item frame");

    public ShulkerPreview() {
        super("ShulkerPreview", ModuleCategory.RENDER, "Show shulker contents when you hover over them");
        this.addValue(itemFrame);
    }

    public static boolean pinned = false;
    public static int drawX = 0;
    public static int drawY = 0;
    public static NBTTagCompound nbt;
    public static ItemStack itemStack;
    public static boolean active;
    public static int mouseX = 0;
    public static int mouseY = 0;
    public static int guiLeft = 0;
    public static int guiTop = 0;

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;

        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) pinned = false;

        if (!itemFrame.getValue()) return;
        try {
            if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                if (mc.objectMouseOver.entityHit instanceof EntityItemFrame) {
                    if (((EntityItemFrame) mc.objectMouseOver.entityHit).getDisplayedItem().getItem() instanceof ItemShulkerBox) {

                        itemStack = ((EntityItemFrame) mc.objectMouseOver.entityHit).getDisplayedItem();
                    }
                }
            } else itemStack = null;
        } catch (Exception ignored) {
            itemStack = null;
            //ignored catch block
        }
    });

    @EventHandler
    private final EventListener<RenderGameOverlayEvent> onRenderGameOverlay = new EventListener<>(e -> {

        if (itemStack != null && itemFrame.getValue() && mc.currentScreen == null) {
            try {

                assert itemStack.getTagCompound() != null;
                nbt = itemStack.getTagCompound().getCompoundTag("BlockEntityTag");
                active = true;
                drawX = mc.displayWidth / 4;
                drawY = mc.displayHeight / 4;

                if (ShulkerPreview.active || ShulkerPreview.pinned) {
                    NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                    ItemStackHelper.loadAllItems(ShulkerPreview.nbt, nonnulllist);

                    mc.getRenderItem().zLevel = 300.0F;

                    int oldX = ShulkerPreview.drawX;
                    int oldY = ShulkerPreview.drawY;
                    RenderUtils.drawBorderedRect(oldX + 9, oldY - 14, oldX + 173, oldY + 52, 1, ColorUtils.rainbow().getRGB(), ColorUtils.getColor(10, 0, 0, 0));


                    Main.fontRenderer.drawStringWithShadow(ShulkerPreview.itemStack.getDisplayName(), oldX + 12, oldY - 14, 0xffffff);

                    RenderHelper.enableGUIStandardItemLighting();
                    for (int i = 0; i < nonnulllist.size(); i++) {
                        int iX = oldX + (i % 9) * 18 + 11;
                        int iY = oldY + (i / 9) * 18 - 11 + 8;
                        ItemStack itemStack = nonnulllist.get(i);
                        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, iX, iY);
                        renderItemOverlayIntoGUI(Main.fontRenderer, itemStack, iX, iY, null);
                        if (ShulkerPreview.pinned) {
                            if (isPointInRegion(iX, iY, 18, 18, ShulkerPreview.mouseX, ShulkerPreview.mouseY)) {
                                net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(itemStack);
                                // event.getGui().drawHoveringText(event.getGui().getItemToolTip(itemStack), iX, iY);
                                net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
                            }
                        }
                    }
                    RenderHelper.disableStandardItemLighting();
                    mc.getRenderItem().zLevel = 0.0F;

                    ShulkerPreview.active = false;
                }

            } catch (Exception exception) {
                // ignored catch block
            }


            mc.getTextureManager().bindTexture(Gui.ICONS);

        }


    });


    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent event) {
        if (ShulkerPreview.active || ShulkerPreview.pinned) {
            if(ShulkerPreview.itemStack == null) return;
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(ShulkerPreview.nbt, nonnulllist);

            GlStateManager.enableBlend();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            mc.getRenderItem().zLevel = 300.0F;

            int oldX = ShulkerPreview.drawX;
            int oldY = ShulkerPreview.drawY;
            RenderUtils.drawBorderedRect(oldX + 9, oldY - 14, oldX + 173, oldY + 52, 1, ColorUtils.rainbow().getRGB(), ColorUtils.getColor(180, 0, 0, 0));

            Main.fontRenderer.drawStringWithShadow(ShulkerPreview.itemStack.getDisplayName(), oldX + 12, oldY - 14, 0xffffff);

            GlStateManager.enableBlend();
            //GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            for (int i = 0; i < nonnulllist.size(); i++) {
                int iX = oldX + (i % 9) * 18 + 11;
                int iY = oldY + (i / 9) * 18 - 11 + 8;
                ItemStack itemStack = nonnulllist.get(i);
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, iX, iY);
                renderItemOverlayIntoGUI(Main.fontRenderer, itemStack, iX, iY, null);
                if (ShulkerPreview.pinned) {
                    if (isPointInRegion(iX, iY, 18, 18, ShulkerPreview.mouseX, ShulkerPreview.mouseY)) {

                        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(itemStack);
                        event.getGui().drawHoveringText(event.getGui().getItemToolTip(itemStack), iX, iY);
                        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
                    }
                }
            }
            RenderHelper.disableStandardItemLighting();
            mc.getRenderItem().zLevel = 0.0F;

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();

            ShulkerPreview.active = false;
        }
    }

    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        int i = ShulkerPreview.guiLeft;
        int j = ShulkerPreview.guiTop;
        pointX = pointX - i;
        pointY = pointY - j;
        return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
    }


    public void renderItemOverlayIntoGUI(CFontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (!stack.isEmpty()) {
            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                fr.drawStringWithShadow(s, (float) (xPosition + 19 - 2 - fr.getStringWidth(s)), (float) (yPosition + 6 + 3) - 2, 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                // Fixes opaque cooldown overlay a bit lower
                // TODO: check if enabled blending still screws things up down the line.
                GlStateManager.enableBlend();
            }

            if (stack.isItemDamaged()) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = getDurabilityForDisplay(stack);
                int rgbfordisplay = getRGBDurabilityForDisplay(stack);
                int i = Math.round(13.0F - (float) health * 13.0F);
                int j = rgbfordisplay;
                this.draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                this.draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
            float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());

            if (f3 > 0.0F) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                this.draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos(x + 0, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x + 0, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x + width, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }






    public double getDurabilityForDisplay(ItemStack stack) {
        return (double)stack.getItemDamage() / (double)stack.getMaxDamage();
    }


    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float)(1.0D - this.getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    }

}
