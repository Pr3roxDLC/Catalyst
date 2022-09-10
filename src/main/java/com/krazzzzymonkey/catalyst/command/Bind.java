package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import org.lwjgl.input.Keyboard;

public class Bind extends Command
{
	public Bind()
	{
		super("bind");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		if(args[0] == null || args[1] == null){
			ChatUtils.error("Usage: " + getSyntax());
			return;
		}
		try
		{
			for(Modules hack : ModuleManager.getModules()) {
				if(hack.getModuleName().equalsIgnoreCase(args[0])) {
					hack.setKey(Keyboard.getKeyIndex((args[1].toUpperCase())));
			 		ChatUtils.normalMessage(hack.getModuleName() + " keybind changed to \u00a7a" + Keyboard.getKeyName(hack.getKey()));
				}
			}
		}
		catch(Exception e)
		{
			ChatUtils.error("Usage: " + getSyntax());
		}
	}

	@Override
	public String getDescription()
	{
		return "Change keybind for specific module.";
	}

	@Override
	public String getSyntax()
	{
		return "bind <module> <key>";
	}
}
