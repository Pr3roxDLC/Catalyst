package com.krazzzzymonkey.catalyst.managers.accountManager.config;

import com.krazzzzymonkey.catalyst.Main;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;


/**
 * This is the config gui
 * @author The_Fireplace
 */
public class ConfigGui extends GuiConfig {

	public ConfigGui(GuiScreen parentScreen) {
		super(parentScreen, new ConfigElement(Main.altConfig.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Main.MODID, false, false, GuiConfig.getAbridgedConfigPath(Main.altConfig.toString()));
	}

}
