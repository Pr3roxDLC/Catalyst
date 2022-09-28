package com.krazzzzymonkey.catalyst.lib.textures;

import com.krazzzzymonkey.catalyst.managers.FileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TextureResourceLocation extends ResourceLocation implements ITexture {
    // we do this to prevent out of mem errors
    public static HashMap<String, ResourceLocation> resources = new HashMap<>();
    private final String textureString;

    public TextureResourceLocation(String resourceString) {
        super(resourceString);
        textureString = resourceString;
    }

    @Override
    public void bind() {
        if (resources.get(textureString) == null) {
            File file = FileManager.getAssetFile(textureString);
            try {
                resources.put(textureString, Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Minecraft.getMinecraft().renderEngine.bindTexture(resources.get(textureString));
        }

    }
}
