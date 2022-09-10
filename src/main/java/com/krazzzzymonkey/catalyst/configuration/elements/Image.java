package com.krazzzzymonkey.catalyst.configuration.elements;

import com.krazzzzymonkey.catalyst.configuration.Alignment;
import com.krazzzzymonkey.catalyst.configuration.GuiConfig;
import com.krazzzzymonkey.catalyst.lib.textures.ITexture;

public class Image extends Element
{
	public int posX;
	public int posY;

	public int width;
	public int height;

	public ITexture image;
	public ITexture hoverImage;
	public Alignment alignment;
	
	public boolean ichBinEineSlideshow;
	public Slideshow slideShow;

	public Image(GuiConfig parent,int posX, int posY, int width, int height, Alignment alignment)
	{
		super(parent);
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;

		this.alignment = alignment;
	}
}
