package com.krazzzzymonkey.catalyst.gui.account;

import com.krazzzzymonkey.catalyst.gui.GuiTextField;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import joptsimple.internal.Strings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;


public class GuiPasswordField extends GuiTextField
{
	public GuiPasswordField(int componentId, CFontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height)
	{
		super(componentId, fontrendererObj, x, y, par5Width, par6Height);
	}

	@Override
    public void drawTextBox(int colorBox, int colorFrame) {
		String password = getText();
		replaceText(Strings.repeat('*', getText().length()));
		super.drawTextBox(colorBox, colorFrame);
		replaceText(password);
	}

	@Override
	public boolean textboxKeyTyped(char typedChar, int keyCode)
	{
		// Ignore ctrl+c and ctrl+x to prevent copying the contents of the field
		return  !GuiScreen.isKeyComboCtrlC(keyCode) && !GuiScreen.isKeyComboCtrlX(keyCode) && super.textboxKeyTyped(typedChar, keyCode);
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		// Minecraft has variable-width fonts, so replace the text with asterisks so that the correct cursor position is calculated
		String password = getText();
		replaceText(Strings.repeat('*', getText().length()));
		super.mouseClicked(mouseX, mouseY, mouseButton);
		replaceText(password);
    return true;
	}

	/**
	 * Sets the text of the field while maintaining the cursor positions
	 * @param newText
	 */
	private void replaceText(String newText)
	{
		int cursorPosition = getCursorPosition();
		int selectionEnd = getSelectionEnd();
		setText(newText);
		setCursorPosition(cursorPosition);
		setSelectionPos(selectionEnd);
	}
}
