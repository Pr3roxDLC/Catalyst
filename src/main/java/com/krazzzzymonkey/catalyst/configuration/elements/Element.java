package com.krazzzzymonkey.catalyst.configuration.elements;

import com.krazzzzymonkey.catalyst.configuration.GuiConfig;

public abstract class Element
{
	GuiConfig parent;
	
	public Element(GuiConfig parent)
	{
		this.parent = parent;
	}
}
