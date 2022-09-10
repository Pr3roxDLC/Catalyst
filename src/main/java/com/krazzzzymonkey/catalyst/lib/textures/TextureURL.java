package com.krazzzzymonkey.catalyst.lib.textures;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.handler.LoadTextureURL;
import com.krazzzzymonkey.catalyst.lib.StringReplacer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

public class TextureURL implements ITexture
{
	URL url;
	int textureID;
	private BufferedImage bi;

	public TextureURL(String url)
	{
		this.textureID = -1;
		try
		{
			this.url = new URL(StringReplacer.replacePlaceholders(url));
		}
		catch (MalformedURLException e)
		{
			Main.logger.log(Level.ERROR, "Invalid URL: " + url);
			e.printStackTrace();
		}

		new LoadTextureURL(this).start();
	}

	@Override
	public void bind()
	{
		if (this.textureID != -1)
		{
			GlStateManager.bindTexture(this.textureID);
		}
		else
		{
			if (bi != null)
			{
				setTextureID(TextureUtil.uploadTextureImageAllocate(GL11.glGenTextures(), bi, false, false));
				bind();

			}
		}
	}

	public void finishLoading(BufferedImage bi)
	{
		this.bi = bi;
	}

	public URL getURL()
	{
		return url;
	}

	public void setTextureID(int textureID)
	{
		this.textureID = textureID;
	}

}
