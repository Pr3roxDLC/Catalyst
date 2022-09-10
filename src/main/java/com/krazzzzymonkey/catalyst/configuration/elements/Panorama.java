package com.krazzzzymonkey.catalyst.configuration.elements;

import com.krazzzzymonkey.catalyst.configuration.GuiConfig;
import com.krazzzzymonkey.catalyst.lib.textures.ITexture;

public class Panorama extends Element
{
	String images;

	public ITexture[] locations;

	public boolean blur;
	public boolean gradient;
	public boolean animate;
	public boolean synced;
	public int position;
	public int animationSpeed;

	public Panorama(GuiConfig parent, String images, boolean blur, boolean gradient)
	{
		super(parent);
		this.images = images;
		this.blur = blur;
		this.gradient = gradient;
		this.animate = true;
		this.animationSpeed = 1;
		this.synced = false;

		locations = new ITexture[6];

		for (int i = 0; i < 6; i++)
		{
			String rl = images.replace("%c", i + "");
			locations[i] = GuiConfig.getWantedTexture(rl);
		}
	}

	public void setAnimate(boolean animate)
	{
		this.animate = animate;
	}

	public void setPosition(int position)
	{
		this.position = position;
	}

	public void setAnimationSpeed(int animationSpeed)
	{
		this.animationSpeed = animationSpeed;
	}
}
