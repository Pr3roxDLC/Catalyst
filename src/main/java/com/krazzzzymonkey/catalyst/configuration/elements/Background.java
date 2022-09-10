package com.krazzzzymonkey.catalyst.configuration.elements;

import java.util.Locale;

import com.krazzzzymonkey.catalyst.configuration.GuiConfig;
import com.krazzzzymonkey.catalyst.lib.MODE;
import com.krazzzzymonkey.catalyst.lib.textures.ITexture;

public class Background extends Element
{
	public static final Background OPTIONS_BACKGROUND = new Background(null, null);
	
	public ITexture image;
	public MODE mode;

	public boolean ichBinEineSlideshow;
	public Slideshow slideShow;

	public Background(GuiConfig parent, ITexture iTexture)
	{
		super(parent);
		this.image = iTexture;
		this.mode = MODE.FILL;

		this.ichBinEineSlideshow = false;
		this.slideShow = null;
	}

	public void setMode(String newMode)
	{
		this.mode = MODE.valueOf(newMode.toUpperCase(Locale.US));
	}
}
