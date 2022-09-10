package com.krazzzzymonkey.catalyst.gui.account;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.chest.CustomGuiButton;
import com.krazzzzymonkey.catalyst.gui.click.theme.dark.DarkFrame;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.*;


import com.krazzzzymonkey.catalyst.managers.accountManager.msauth.MSAuthScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.EncryptionTools;

import java.awt.*;
import java.io.IOException;

/**
 * @author evilmidget38
 * @author The_Fireplace
 */
public abstract class AbstractAccountGui extends GuiScreen
{
	private final String actionString;
	private com.krazzzzymonkey.catalyst.gui.GuiTextField username;
	private GuiPasswordField password;
	private CustomGuiButton complete;
	protected boolean hasUserChanged = false;

	public AbstractAccountGui(String actionString)
	{
		this.actionString = actionString;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(complete = new CustomGuiButton(2, this.width / 2 - 152, this.height - 28, 150, 20, this.actionString, -1, new Color(0,0,0,100).getRGB()));
		this.buttonList.add(new CustomGuiButton(3, this.width / 2 + 2, this.height - 28, 150, 20, I18n.format("gui.cancel"), -1, new Color(0,0,0,100).getRGB()));
        this.buttonList.add(new CustomGuiButton(13, width / 2 - 100, 130, 200, 20, "Add a Microsoft Account", -1, new Color(0,0,0,100).getRGB()));
		username = new com.krazzzzymonkey.catalyst.gui.GuiTextField(0, DarkFrame.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		username.setFocused(true);
		username.setMaxStringLength(64);
		password = new GuiPasswordField(1, DarkFrame.fontRenderer, this.width / 2 - 100, 90, 200, 20);
        password.setFocused(false);
		password.setMaxStringLength(64);

		complete.enabled = false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();

        Main.fontRenderer.drawCenteredString(I18n.format(this.actionString), this.width / 2f, 7, -1);
        Main.fontRenderer.drawCenteredString( "Mojang Account", this.width / 2f, 43, -1);
        Main.fontRenderer.drawCenteredString( "Username: ", this.width / 2f - 130, 66, -1);
        Main.fontRenderer.drawCenteredString( I18n.format("Password: "), this.width / 2f - 130, 96, -1);
        username.drawTextBox(new Color(0,0,0).getRGB(),-1);
        Main.fontRenderer.drawCenteredString( "Microsoft Account", this.width / 2f, 118, -1);

		password.drawTextBox(new Color(0,0,0).getRGB(),-1);
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void keyTyped(char character, int keyIndex) {
		if (keyIndex == Keyboard.KEY_ESCAPE) {
			escape();
		} else if (keyIndex == Keyboard.KEY_RETURN) {
			if(username.isFocused()){
				username.setFocused(false);
				password.setFocused(true);
			}else if(password.isFocused() && complete.enabled){
				complete();
				escape();
			}
		} else if(keyIndex == Keyboard.KEY_TAB){
			username.setFocused(!username.isFocused());
			password.setFocused(!password.isFocused());
		} else {
			// GuiTextField checks if it's focused before doing anything
			username.textboxKeyTyped(character, keyIndex);
			password.textboxKeyTyped(character, keyIndex);
			if(username.isFocused())
				hasUserChanged = true;
		}
	}

	@Override
	public void updateScreen()
	{
		username.updateCursorCounter();
		password.updateCursorCounter();
		complete.enabled = canComplete();
	}

	@Override
	protected void actionPerformed(GuiButton button){

		if (button.enabled)
		{
            if (button.id == 13) {
                mc.displayGuiScreen(new MSAuthScreen(this));
            } else if(button.id == 2){
				complete();
				escape();
			}else if(button.id == 3){
				escape();
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		username.mouseClicked(mouseX, mouseY, mouseButton);
		password.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Return to the Account Selector
	 */
	private void escape(){
		mc.displayGuiScreen(new GuiAccountSelector());
	}

	public String getUsername()
	{
		return username.getText();
	}

	public String getPassword()
	{
		return password.getText();
	}

	public void setUsername(String username)
	{
		this.username.setText(username);
	}

	public void setPassword(String password)
	{
		this.password.setText(password);
	}

	protected boolean accountNotInList(){
		for(AccountData data : AltDatabase.getInstance().getAlts())
			if(EncryptionTools.decode(data.user).equals(getUsername()))
				return false;
		return true;
	}

	public boolean canComplete()
	{
		return getUsername().length() > 0 && accountNotInList();
	}

	public abstract void complete();
}
