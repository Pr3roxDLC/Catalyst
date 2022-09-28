package com.krazzzzymonkey.catalyst.gui.click.theme.dark;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.Tooltip;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.base.Component;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import com.krazzzzymonkey.catalyst.gui.click.elements.ColorPicker;
import com.krazzzzymonkey.catalyst.gui.click.theme.Theme;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class DarkColorPicker extends ComponentRenderer {
    boolean clicking = false;

    File file = FileManager.getAssetFile("gui" + File.separator + "colorline.png");
    File file2 = FileManager.getAssetFile("gui" + File.separator + "colortransparent.png");
    File file3 = FileManager.getAssetFile("gui" + File.separator + "colortransparentoverlay.png");

    ResourceLocation resourceLocation;
    ResourceLocation transparentResourceLocation;
    ResourceLocation transparentOverlayResourceLocation;

    {
        try {
            resourceLocation = Minecraft.getMinecraft()
                                        .getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(),
                                                                                                   new DynamicTexture(
                                                                                                       ImageIO.read(file)));
            transparentResourceLocation = Minecraft.getMinecraft()
                                                   .getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(),
                                                                                                              new DynamicTexture(
                                                                                                                  ImageIO.read(
                                                                                                                      file2)));
            transparentOverlayResourceLocation = Minecraft.getMinecraft()
                                                          .getRenderManager().renderEngine.getDynamicTextureLocation(
                    file.getName(),
                    new DynamicTexture(ImageIO.read(file3)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static BufferedImage image;

    public DarkColorPicker(Theme theme) {
        super(ComponentType.COLOR_PICKER, theme);
    }

    public static ColorPicker colorPicker;


    @Override
    public void drawComponent(Component component, int mouseX, int mouseY) {

        colorPicker = (ColorPicker) component;


        Point a = new Point(colorPicker.getX() + 2, colorPicker.getY() + 16); // top left
        Point b = new Point((int) (colorPicker.getX() + colorPicker.getDimension().getWidth()) - 30, colorPicker.getY() + 16); // top right
        Point c = new Point((int) (colorPicker.getX() + colorPicker.getDimension().getWidth()) - 30, colorPicker.getY() + 56); // bottom right
        Point d = new Point(colorPicker.getX() + 2, colorPicker.getY() + 56); // bottom left

        Color lineColor = colorPicker.getLineColor();
        int triX = colorPicker.getTriPos()[0];
        int triY = colorPicker.getTriPos()[1];

        Color backgroundColor = new Color(20, 20, 20, 255);
        if (colorPicker.getSubMenu() != null) {
            backgroundColor = new Color(11, 11, 11, 255);
        }

        //Draw the Background
        RenderUtils.drawBorderedRect(colorPicker.getX() + 1, colorPicker.getY() - 1, colorPicker.getX() + colorPicker.getDimension().width - 2, colorPicker.getY() + colorPicker.getDimension().getHeight(), 1, ColorUtils.color(0f, 0f, 0f, 1f), backgroundColor.getRGB());
        //Box with the name in it
        //RenderUtils.drawBorderedRect(colorPicker.getX() + 1, colorPicker.getY() - 1, colorPicker.getX() + colorPicker.getDimension().width - 2, colorPicker.getY() + 15, 2, ColorUtils.color(0f, 0f, 0f, 1f), backgroundColor.getRGB());
        //Draw the Rect displaying the current color
        RenderUtils.drawBorderedRect(colorPicker.getX() + colorPicker.getDimension().width - 18, colorPicker.getY() + 1, colorPicker.getX() + colorPicker.getDimension().width - 8, colorPicker.getY() + 11, 1, ColorUtils.color(0f, 0f, 0f, 1f), colorPicker.getColor().getRGB());
        //Draw the Color Picker Name
        Main.smallFontRenderer.drawString(colorPicker.getText(), colorPicker.getX() + 5, colorPicker.getY() + 3, -1);
        //Draw border around color picker
        RenderUtils.drawBorderedRect(colorPicker.getX() + 2, colorPicker.getY() + 16, colorPicker.getX() + colorPicker.getDimension().getWidth() - 30, colorPicker.getY() + 56, 1, ColorUtils.color(0f, 0f, 0f, 1f), ColorUtils.color(0f, 0f, 0f, 0f));
        //Render the quad / two triangles
        //Hehe krazzzzy doesnt know this but im still using triangles lel
        drawGradientRect(lineColor.getRGB(), a, b, c, d);

        if (getColorBrightness(colorPicker.getColor()) > 25) {
            RenderUtils.drawBorderedRect(colorPicker.getX() - triX - 2, colorPicker.getY() - triY - 2, colorPicker.getX() - triX + 2, colorPicker.getY() - triY + 2, 1, new Color(0f, 0f, 0f, 1f).getRGB(), colorPicker.getColor().getRGB());
        } else {
            RenderUtils.drawBorderedRect(colorPicker.getX() - triX - 2, colorPicker.getY() - triY - 2, colorPicker.getX() - triX + 2, colorPicker.getY() - triY + 2, 1, new Color(255, 255, 255).getRGB(), colorPicker.getColor().getRGB());
        }

        GlStateManager.enableAlpha();
        GlStateManager.disableDepth();
        Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
        GlStateManager.color(1, 1, 1, 1);

        double scale = 0.7;
        GL11.glScaled(scale, scale - 0.2, 1.0D);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect((int) ((colorPicker.getX() + colorPicker.getDimension().getWidth() - 27) * (1 / scale)), (int) ((colorPicker.getY() + 16) * ((1 / (scale - 0.2)))), 0, 0, 12, 80);
        Minecraft.getMinecraft().renderEngine.bindTexture(transparentResourceLocation);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect((int) ((colorPicker.getX() + colorPicker.getDimension().getWidth() - 15) * (1 / scale)), (int) ((colorPicker.getY() + 16) * ((1 / (scale - 0.2)))), 0, 0, 12, 80);
        Minecraft.getMinecraft().renderEngine.bindTexture(transparentOverlayResourceLocation);
        GlStateManager.color(colorPicker.getColor().getRed()/255f, colorPicker.getColor().getGreen()/255f, colorPicker.getColor().getBlue()/255f, 1);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect((int) ((colorPicker.getX() + colorPicker.getDimension().getWidth() - 15) * (1 / scale)), (int) ((colorPicker.getY() + 16) * ((1 / (scale - 0.2)))), 0, 0, 12, 80);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glScaled(1.0D / scale, 1.0D / (scale - 0.2), 1.0D);
        GlStateManager.disableAlpha();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        // TODO allow the user to input values
        Main.smallFontRenderer.drawCenteredString("R: " + colorPicker.getColor().getRed() + " G: " + colorPicker.getColor().getGreen() + " B: " + colorPicker.getColor().getBlue() + " A: " + colorPicker.getColor().getAlpha(), (int) (colorPicker.getX() + (colorPicker.getDimension().getWidth() / 2) - 1), (int) (colorPicker.getY() + colorPicker.getDimension().getHeight() - 8), -1);

        RenderUtils.drawBorderedRect(colorPicker.getX() + colorPicker.getDimension().getWidth() - 28, colorPicker.getY() + colorPicker.getSelColorY(), colorPicker.getX() + colorPicker.getDimension().getWidth() - 18, colorPicker.getY() + colorPicker.getSelColorY() + 2, 1, new Color(0, 0, 0).getRGB(), lineColor.getRGB());

        int opacity = (int)((colorPicker.getSelOpacityY() -15) *6.54);
        if(opacity > 255)opacity=255;
        if(opacity < 0)opacity= 0;
        int renderOpacity = opacity;
        // IDK why but this renders black if I don't do this
        if(opacity == 255){
            renderOpacity = opacity -1;
        }
        RenderUtils.drawBorderedRect(colorPicker.getX() + colorPicker.getDimension().getWidth() - 16, colorPicker.getY() + colorPicker.getSelOpacityY(), colorPicker.getX() + colorPicker.getDimension().getWidth() - 6, colorPicker.getY() + colorPicker.getSelOpacityY() + 2, 1, new Color(0, 0, 0).getRGB(), new Color(colorPicker.getColor().getRed(), colorPicker.getColor().getGreen(), colorPicker.getColor().getBlue(), renderOpacity).getRGB());
                try {

            image = ImageIO.read(file);
            try {
                if (colorPicker.isMouseOver(mouseX, mouseY) && MouseUtils.isLeftClicked() && !colorPicker.isRainbow) {

                    Color triangleColor = getColorFromTriangle(mouseX, mouseY, lineColor, a, b, c, d);
                    if (triangleColor != null ) {
                        clicking = true;
                        colorPicker.setTriPos(colorPicker.getX() - mouseX, colorPicker.getY() - mouseY);
                        colorPicker.setColor(new Color(triangleColor.getRed(), triangleColor.getGreen(), triangleColor.getBlue(), opacity));

                    } else if (!clicking && mouseX > (colorPicker.getX() + colorPicker.getDimension().getWidth()) - 27 && mouseX < colorPicker.getX() + colorPicker.getDimension().getWidth() - 20 && mouseY > colorPicker.getY() + 16 && mouseY < colorPicker.getY() + colorPicker.getDimension().getHeight() - 10 && MouseUtils.isLeftClicked()) {
                        colorPicker.setLineColor(new Color(resizeImage(image, 256, (int) (256 * (scale - 0.2))+ 3).getRGB(6, (mouseY - colorPicker.getY() - 16))));
                        colorPicker.setSelColorY(mouseY - colorPicker.getY() - 2);
                        Color color =getColorFromTriangle(colorPicker.getX() - colorPicker.getTriPos()[0], colorPicker.getY() - colorPicker.getTriPos()[1], lineColor, a, b, c, d);
                        if(color != null){
                            colorPicker.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
                        }
                    }else if(!clicking && mouseX > (colorPicker.getX() + colorPicker.getDimension().getWidth()) - 15 && mouseX < colorPicker.getX() + colorPicker.getDimension().getWidth() - 8 && mouseY > colorPicker.getY() + 16 && mouseY < colorPicker.getY() + colorPicker.getDimension().getHeight() - 10 && MouseUtils.isLeftClicked()){
                        colorPicker.setSelOpacityY(mouseY - colorPicker.getY() -2);
                        colorPicker.setColor(new Color(colorPicker.getColor().getRed(), colorPicker.getColor().getGreen(), colorPicker.getColor().getBlue(), opacity));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!MouseUtils.isLeftClicked()) {
            clicking = false;
        }

        String description = colorPicker.getDescription();
        if (description != null && !description.equals("") && colorPicker.isMouseOver(mouseX, mouseY) && ModuleManager.getModule("ClickGui").isToggledValue("Tooltip")) {
            if (Minecraft.getMinecraft().currentScreen instanceof ClickGuiScreen) {
                ClickGuiScreen.tooltip = new Tooltip(description, (int) (mouseX * com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), (int) (mouseY * com.krazzzzymonkey.catalyst.module.modules.gui.ClickGui.clickGuiScale.getValue()), fontRenderer);
            } else {
                ClickGuiScreen.tooltip = new Tooltip(description, mouseX, mouseY, fontRenderer);
            }
        }

    }

    @Override
    public void doInteractions(Component component, int mouseX, int mouseY) {

    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public Color getColorFromTriangle(double mouseX, double mouseY, Color lineColor, Point a, Point b, Point c, Point d) {
        Color color = null;
        color = getColor(mouseX, mouseY,
            new double[]{a.x, a.y, b.x, b.y, c.x, c.y},
            new Color[]{Color.BLACK, Color.BLACK, lineColor});
        if (color != null) return color;
        color = getColor(mouseX, mouseY,
            new double[]{a.x, a.y, c.x, c.y, d.x, d.y},
            new Color[]{Color.BLACK, lineColor, Color.WHITE});
        return color;
    }


    //Render the "quad" using two triangles for easier color value getting, having two triangles allows me to keep using the barycentric cord method
    public void drawGradientRect(int startColor, Point a, Point b, Point c, Point d) {
        float f = (float) (startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float) (startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (startColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        vertexbuffer.pos(c.x, c.y, 0.0).color(f1, f2, f3, 1f).endVertex();
        vertexbuffer.pos(a.x, a.y, 0.0).color(0f, 0f, 0f, 1f).endVertex();
        vertexbuffer.pos(d.x, d.y, 0.0).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.draw();

        vertexbuffer.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(b.x, b.y, 0.0).color(0f, 0f, 0f, 1f).endVertex();
        vertexbuffer.pos(a.x, a.y, 0.0).color(0f, 0f, 0f, 1f).endVertex();
        vertexbuffer.pos(c.x, c.y, 0.0).color(f1, f2, f3, 1f).endVertex();


        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    //Takes a point and the vertices of the triangle
    public double[] cartesian2barycentric(double x, double y, double x1, double y1, double x2, double y2, double x3, double y3) {
        double y2y3 = y2 - y3,
            x3x2 = x3 - x2,
            x1x3 = x1 - x3,
            y1y3 = y1 - y3,
            y3y1 = y3 - y1,
            xx3 = x - x3,
            yy3 = y - y3;
        double d = y2y3 * x1x3 + x3x2 * y1y3,
            lambda1 = (y2y3 * xx3 + x3x2 * yy3) / d,
            lambda2 = (y3y1 * xx3 + x1x3 * yy3) / d;
        return new double[]{lambda1, lambda2, 1 - lambda1 - lambda2};
    }

    //get the Color from the triangle based on the click
    public Color getColor(double x, double y, double[] vertices, Color[] colors) {
        double[] t = cartesian2barycentric(x, y, vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5]);
        if (Arrays.stream(t).anyMatch(n -> n < 0)) {
            return null;
        }


//        System.out.println((float) Math.floor(colors[0].getRed() * t[0] + colors[1].getRed() * t[1] + colors[2].getRed() * t[2]) + " " +
//            (float) Math.floor(colors[0].getGreen() * t[0] + colors[1].getGreen() * t[1] + colors[2].getGreen() * t[2]) + " " +
//            (float) Math.floor(colors[0].getBlue() * t[0] + colors[1].getBlue() * t[1] + colors[2].getBlue() * t[2]));

        return new Color(
            (float) Math.floor(colors[0].getRed() * t[0] + colors[1].getRed() * t[1] + colors[2].getRed() * t[2]) / 255f,
            (float) Math.floor(colors[0].getGreen() * t[0] + colors[1].getGreen() * t[1] + colors[2].getGreen() * t[2]) / 255f,
            (float) Math.floor(colors[0].getBlue() * t[0] + colors[1].getBlue() * t[1] + colors[2].getBlue() * t[2]) / 255f
        );
    }


    public double getColorBrightness(Color color) {
        return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getGreen());
    }

}
