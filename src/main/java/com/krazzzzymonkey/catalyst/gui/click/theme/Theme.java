package com.krazzzzymonkey.catalyst.gui.click.theme;

import java.util.HashMap;

import com.krazzzzymonkey.catalyst.gui.click.base.ComponentRenderer;
import com.krazzzzymonkey.catalyst.gui.click.base.ComponentType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.model.obj.OBJModel.Texture;

public class Theme {

    public FontRenderer fontRenderer;

    public Texture icons;

    private final HashMap<ComponentType, ComponentRenderer> rendererHashMap = new HashMap<ComponentType, ComponentRenderer>();

    private final String themeName;

    private final int frameHeight = 15;

    public Theme(String themeName) {

        this.themeName = themeName;
    }

    public void addRenderer(ComponentType componentType, ComponentRenderer componentRenderer) {

        this.rendererHashMap.put(componentType, componentRenderer);
    }

    public HashMap<ComponentType, ComponentRenderer> getRenderer() {

        return rendererHashMap;
    }

    public String getThemeName() {

        return themeName;
    }

    public FontRenderer getFontRenderer() {

        return fontRenderer;
    }

    public int getFrameHeight() {

        return frameHeight;
    }
}
