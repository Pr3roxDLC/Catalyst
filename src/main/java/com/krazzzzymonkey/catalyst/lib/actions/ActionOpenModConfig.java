package com.krazzzzymonkey.catalyst.lib.actions;

import com.krazzzzymonkey.catalyst.gui.GuiCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ActionOpenModConfig implements IAction
{
	String modid;

	public ActionOpenModConfig(String modid)
	{
		this.modid = modid;
	}

	@Override
	public void perform(Object source, GuiCustom parent)
	{
		for (ModContainer mod : Loader.instance().getModList())
		{
			if (mod.getModId().equals(modid))
			{
				IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(mod);

				if (guiFactory != null)
				{
					GuiScreen newScreen = guiFactory.createConfigGui(parent);
					Minecraft.getMinecraft().displayGuiScreen(newScreen);
				}
			}
		}
	}

}
