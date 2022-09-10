package com.krazzzzymonkey.catalyst.lib.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.krazzzzymonkey.catalyst.gui.GuiCustom;
import net.minecraft.client.Minecraft;

public class ActionOpenFolder implements IAction
{
	String folderName;

	public ActionOpenFolder(String folderName)
	{
		this.folderName = folderName;
	}

	@Override
	public void perform(Object source, GuiCustom parent)
	{
		File toOpen = new File(Minecraft.getMinecraft().gameDir, folderName);

		boolean isInMinecraftFolder = false;
		try
		{
			File parentFile = toOpen.getCanonicalFile();
			while ((parentFile = parentFile.getParentFile()) != null)
			{
				if (parentFile.getCanonicalPath().equals(Minecraft.getMinecraft().gameDir.getCanonicalPath()))
				{
					isInMinecraftFolder = true;
				}
			}

			if (isInMinecraftFolder)
			{
				if (toOpen.isDirectory() && Desktop.isDesktopSupported())
				{
					try
					{
						Desktop.getDesktop().open(toOpen);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (IOException e)
		{

		}

	}

}
