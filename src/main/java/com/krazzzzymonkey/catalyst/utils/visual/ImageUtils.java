package com.krazzzzymonkey.catalyst.utils.visual;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage flip(BufferedImage image) {
        AffineTransform transformer = new AffineTransform();
        transformer.concatenate(AffineTransform.getScaleInstance(1, -1));
        transformer.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));

        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flipped.createGraphics();
        g.transform(transformer);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return flipped;
    }

    public static BufferedImage rotate(BufferedImage image, double angle) {
        BufferedImage rotated = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.rotate(Math.toRadians(angle), image.getWidth() / 2f, image.getHeight() / 2f);
        graphic.drawImage(image, 0, 0, null);
        graphic.dispose();
        return rotated;
    }

}
