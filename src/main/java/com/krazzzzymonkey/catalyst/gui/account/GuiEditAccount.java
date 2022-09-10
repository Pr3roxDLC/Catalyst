package com.krazzzzymonkey.catalyst.gui.account;


import com.krazzzzymonkey.catalyst.managers.accountManager.EnumBool;
import com.krazzzzymonkey.catalyst.managers.accountManager.ExtendedAccountData;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.AccountData;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.AltDatabase;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.EncryptionTools;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.JavaTools;

/**
 * The GUI where the alt is added
 * @author The_Fireplace
 * @author evilmidget38
 */
class GuiEditAccount extends AbstractAccountGui {
	private final ExtendedAccountData data;
	private final int selectedIndex;

	public GuiEditAccount(int index){
		super("Edit Account");
		this.selectedIndex=index;
		AccountData data = AltDatabase.getInstance().getAlts().get(index);

		if(data instanceof ExtendedAccountData){
			this.data = (ExtendedAccountData) data;
		}else{
			this.data = new ExtendedAccountData(data.user, data.pass, data.alias, 0, JavaTools.getJavaCompat().getDate(), EnumBool.UNKNOWN);
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		setUsername(EncryptionTools.decode(data.user));
		setPassword(EncryptionTools.decode(data.pass));
	}

	@Override
	public void complete()
	{
		AltDatabase.getInstance().getAlts().set(selectedIndex, new ExtendedAccountData(getUsername(), getPassword(), hasUserChanged ? getUsername() : data.alias, data.useCount, data.lastused, data.premium));
	}

}
