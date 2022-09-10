package com.krazzzzymonkey.catalyst.lib.actions;

import com.krazzzzymonkey.catalyst.gui.GuiCustom;

public class ActionQuit implements IAction
{
	@Override
	public void perform(Object source, GuiCustom menu)
	{
		menu.mc.shutdown();
	}
}
