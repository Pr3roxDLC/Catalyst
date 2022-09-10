package com.krazzzzymonkey.catalyst.lib.actions;

import com.krazzzzymonkey.catalyst.gui.GuiCustom;
import com.krazzzzymonkey.catalyst.gui.GuiCustomConfirmOpenLink;
import com.krazzzzymonkey.catalyst.lib.StringReplacer;
import net.minecraft.client.Minecraft;

public class ActionOpenLink implements IAction
{
	String link;
	
	public ActionOpenLink(String link)
	{
		this.link = link;
	}

	@Override
	public void perform(Object source,GuiCustom menu)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiCustomConfirmOpenLink(menu, getLink(), -1, false));
		menu.beingChecked = source;
	}

	public String getLink()
	{
		return StringReplacer.replacePlaceholders(link);
	}
}
