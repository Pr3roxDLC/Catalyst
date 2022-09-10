package com.krazzzzymonkey.catalyst.lib.texts;

public class TextString implements IText
{
	String string;
	
	public TextString(String string)
	{
		this.string = string;
	}

	@Override
	public String get()
	{
		return string;
	}

}
