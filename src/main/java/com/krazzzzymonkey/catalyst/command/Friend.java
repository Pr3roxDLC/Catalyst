package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.utils.Utils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;

public class Friend extends Command
{
	public Friend()
	{
		super("friend");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		try
		{	
			if(args[0].equalsIgnoreCase("add")) {
				if(args[1].equalsIgnoreCase("all")) {
					for(Object object : Wrapper.INSTANCE.world().loadedEntityList) {
						if(object instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) object;
							if(!player.isInvisible()) {
								FriendManager.addFriend(Utils.getPlayerName(player));
							}
						}
					}
				} else {
					FriendManager.addFriend(args[1]);
				}
			}
			else
			if(args[0].equalsIgnoreCase("remove")) {
				FriendManager.removeFriend(args[1]);
			}
			else
			if(args[0].equalsIgnoreCase("clear")) {
				FriendManager.clear();
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
		return "Friend manager.";
	}

	@Override
	public String getSyntax()
	{
		return "friend <add/remove/clear> <nick>";
	}
}