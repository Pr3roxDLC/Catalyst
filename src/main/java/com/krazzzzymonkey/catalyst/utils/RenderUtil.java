package com.krazzzzymonkey.catalyst.utils;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    public static void drawCompleteImage(int posX, int posY, int width, int height) {
        glPushMatrix();

        glTranslatef(posX, posY, 0);
        glBegin(GL_QUADS);

        glTexCoord2f(0, 0);
        glVertex3f(0, 0, 0);
        glTexCoord2f(0, 1);
        glVertex3f(0, height, 0);
        glTexCoord2f(1, 1);
        glVertex3f(width, height, 0);
        glTexCoord2f(1, 0);
        glVertex3f(width, 0, 0);
        glEnd();

        glPopMatrix();
    }

    public static void drawPartialImage(int posX, int posY, int imageX, int imageY, int width, int height, int imagePartWidth, int imagePartHeight) {
        double imageWidth = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        double imageHeight = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);

        double einsTeilerWidth = 1F / imageWidth;
        double uvWidth = einsTeilerWidth * imagePartWidth;
        double uvX = einsTeilerWidth * imageX;

        double einsTeilerHeight = 1F / imageHeight;
        double uvHeight = einsTeilerHeight * imagePartHeight;
        double uvY = einsTeilerHeight * imageY;

        glPushMatrix();

        glTranslatef(posX, posY, 0);
        glBegin(GL_QUADS);

        glTexCoord2d(uvX, uvY);
        glVertex3f(0, 0, 0);
        glTexCoord2d(uvX, uvY + uvHeight);
        glVertex3f(0, height, 0);
        glTexCoord2d(uvX + uvWidth, uvY + uvHeight);
        glVertex3f(width, height, 0);
        glTexCoord2d(uvX + uvWidth, uvY);
        glVertex3f(width, 0, 0);
        glEnd();

        glPopMatrix();
    }


}

