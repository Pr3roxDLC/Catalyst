package com.krazzzzymonkey.catalyst.configuration.elements;

import com.krazzzzymonkey.catalyst.configuration.Alignment;
import com.krazzzzymonkey.catalyst.configuration.GuiConfig;
import com.krazzzzymonkey.catalyst.lib.ANCHOR;
import com.krazzzzymonkey.catalyst.lib.actions.ActionOpenLink;
import com.krazzzzymonkey.catalyst.lib.actions.IAction;
import com.krazzzzymonkey.catalyst.lib.texts.IText;

public class Label extends Element
{
	public IText text;
	public IText hoverText;
	public String name;
	public int posX;
	public int posY;
	public int color;
	public int hoverColor;
	public Alignment alignment;
	public IAction action;
	
	public String pressSound;
	public String hoverSound;
	
	public float fontSize;
	
	public ANCHOR anchor;

	public Label(GuiConfig parent,String name, IText text, int posX, int posY, Alignment alignment, int color)
	{
		super(parent);
		this.name = name;
		this.text = this.hoverText = text;

		this.posX = posX;
		this.posY = posY;
		this.color = this.hoverColor = color;

		this.alignment = alignment;
		if (this.alignment == null)
		{
			this.alignment = parent.getAlignment("top_left");
		}
		
		this.fontSize = 1;
		
		this.anchor = ANCHOR.START;
	}

	public Label(GuiConfig parent,String name, IText text, int posX, int posY)
	{
		this(parent,name, text, posX, posY, parent.getAlignment("top_left"), -1);
	}
	
	public void setHoverColor(int hoverColor)
	{
		this.hoverColor = hoverColor;
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public void setAlignment(String alignment)
	{
		this.alignment = parent.getAlignment(alignment);
	}
	
	public void setLink(String link)
	{
		this.action = new ActionOpenLink(link);
	}
}
