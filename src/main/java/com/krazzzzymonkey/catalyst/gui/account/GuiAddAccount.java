package com.krazzzzymonkey.catalyst.gui.account;

import com.krazzzzymonkey.catalyst.managers.accountManager.ExtendedAccountData;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.AltDatabase;
import com.krazzzzymonkey.catalyst.managers.accountManager.msauth.MSAuthScreen;
import net.minecraft.client.gui.GuiButton;


/**
 * The GUI where the alt is added
 * @author The_Fireplace
 * @author evilmidget38
 */
public class GuiAddAccount extends AbstractAccountGui {

	public GuiAddAccount()
	{
		super("Add Account");
	}

	@Override
	public void complete()
	{
		AltDatabase.getInstance().getAlts().add(new ExtendedAccountData(getUsername(), getPassword(), getUsername()));
	}
}
