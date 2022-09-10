package com.krazzzzymonkey.catalyst.lib.actions;

import com.google.common.util.concurrent.Runnables;
import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.GuiCustom;
import com.krazzzzymonkey.catalyst.gui.account.GuiAccountSelector;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.fml.client.GuiModList;

public class ActionOpenGUI implements IAction
{
	String guiName;


	public ActionOpenGUI(String guiName)
	{
		this.guiName = guiName;
	}

	@Override
	public void perform(Object source, GuiCustom menu)
	{
		GuiScreen gui = null;

		if (guiName.startsWith("custom."))
		{
			String customName = guiName.substring(7);

			gui = Main.config.getGUI(customName);
		}
		else
		{
			if (guiName.equalsIgnoreCase("mods"))
			{
				gui = new GuiModList(menu);
			}
			if (guiName.equalsIgnoreCase("clickGui"))
			{

				Wrapper.INSTANCE.mc().displayGuiScreen(new GuiAccountSelector());
			}
			if (guiName.equalsIgnoreCase("account"))
			{
				Minecraft.getMinecraft().displayGuiScreen(new GuiAccountSelector());
			}
			else if (guiName.equalsIgnoreCase("singleplayer"))
			{
				gui = new GuiWorldSelection(menu);
			}
			else if (guiName.equalsIgnoreCase("singleplayer.createworld"))
			{
				gui = new GuiCreateWorld(menu);
			}
			else if (guiName.equalsIgnoreCase("multiplayer"))
			{
				gui = new GuiMultiplayer(menu);
			}
			else if (guiName.equalsIgnoreCase("options"))
			{
				gui = new GuiOptions(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("languages"))
			{
				gui = new GuiLanguage(menu, menu.mc.gameSettings, menu.mc.getLanguageManager());
			}
			else if (guiName.equalsIgnoreCase("options.ressourcepacks"))
			{
				gui = new GuiScreenResourcePacks(menu);
			}
			else if (guiName.equalsIgnoreCase("options.snooper"))
			{
				gui = new GuiSnooper(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("options.sounds"))
			{
				gui = new GuiScreenOptionsSounds(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("options.skinsettings"))
			{
				gui = new GuiCustomizeSkin(menu);
			}
			else if (guiName.equalsIgnoreCase("options.video"))
			{
				gui = new GuiVideoSettings(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("options.controls"))
			{
				gui = new GuiControls(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("options.multiplayer"))
			{
				gui = new ScreenChatOptions(menu, menu.mc.gameSettings);
			}
			else if (guiName.equalsIgnoreCase("mainmenu"))
			{
				gui = new GuiMainMenu();
			}
			else if (guiName.equalsIgnoreCase("realms"))
			{
		        RealmsBridge realmsbridge = new RealmsBridge();
		        realmsbridge.switchToRealms(Minecraft.getMinecraft().currentScreen);
			}
			else if (guiName.equalsIgnoreCase("credits"))
			{
				gui = new GuiWinGame(false, Runnables.doNothing());
			}
		}

		if (gui != null)
		{
			Minecraft.getMinecraft().displayGuiScreen(gui);
		}

	}

}
