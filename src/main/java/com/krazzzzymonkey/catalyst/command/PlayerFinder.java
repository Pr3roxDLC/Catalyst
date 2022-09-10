package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;

public class PlayerFinder extends Command
{
	public PlayerFinder()
	{
		super("pfind");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		try
		{
			ArrayList<String> list = new ArrayList<String>();
			
			if(args[0].equalsIgnoreCase("all")) {
				for(NetworkPlayerInfo npi : Wrapper.INSTANCE.mc().getConnection().getPlayerInfoMap()) {
					list.add("\n" + npi.getGameProfile().getName());
				}
			}
			else
			if(args[0].equalsIgnoreCase("creatives")) {
				for(NetworkPlayerInfo npi : Wrapper.INSTANCE.mc().getConnection().getPlayerInfoMap()) {
					if(npi.getGameType().isCreative()) {
						list.add("\n" + npi.getGameProfile().getName());
					}
				}	
			}
			
			if(list.isEmpty()) {
				ChatUtils.error("List is empty.");
			}
			else
			{
				Wrapper.INSTANCE.copy(list.toString());
				ChatUtils.message("List copied to clipboard.");
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
		return "Get list of players.";
	}

	@Override
	public String getSyntax()
	{
		return "pfind <all/creatives>";
	}
}