package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.utils.LoginUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

public class Login extends Command
{
	public Login()
	{
		super("login");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		try
		{
			if(args.length > 1 || args[0].contains(":")) {
				String email = "";
				String password = "";
				if(args[0].contains(":")) {
					String[] split = args[0].split(":", 2);
					email = split[0];
					password = split[1];
				}
				else
				{
					email = args[0];
					password = args[1];
				}
				String log = LoginUtils.loginAlt(email, password);
				ChatUtils.message(log);
			} 
			else 
			{
				LoginUtils.changeCrackedName(args[0]);
				ChatUtils.warning("[Non Premium] Successfully Logged into " + Wrapper.INSTANCE.mc().getSession().getUsername());
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
		return "Change session.";
	}

	@Override
	public String getSyntax()
	{
		return "login <<email> <password>>/<username>";
	}
}