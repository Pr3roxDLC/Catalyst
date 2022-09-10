package com.krazzzzymonkey.catalyst.lib.actions;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.GuiCustom;

import net.minecraft.client.gui.GuiMainMenu;

import org.lwjgl.input.Keyboard;

public class ActionRefresh implements IAction
{

	@Override
	public void perform(Object source, GuiCustom menu)
	{
		Main.INSTANCE.reload();

		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			menu.mc.refreshResources();
		}
		menu.mc.displayGuiScreen(new GuiMainMenu());
	}

}
