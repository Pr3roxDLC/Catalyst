package com.krazzzzymonkey.catalyst.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;

public class GuiCustomConfirmOpenLink extends GuiYesNo
{
    private final String openLinkWarning;
    private final String copyLinkButtonText;
    private final String linkText;
    private boolean showSecurityWarning = true;
    private static final String __OBFID = "CL_00000683";

    public GuiCustomConfirmOpenLink(GuiYesNoCallback p_i1084_1_, String p_i1084_2_, int p_i1084_3_, boolean p_i1084_4_)
    {
        super(p_i1084_1_, I18n.format(p_i1084_4_ ? "chat.link.confirmTrusted" : "chat.link.confirm"), p_i1084_2_, p_i1084_3_);
        this.confirmButtonText = I18n.format(p_i1084_4_ ? "chat.link.open" : "gui.yes");
        this.cancelButtonText = I18n.format(p_i1084_4_ ? "gui.cancel" : "gui.no");
        this.copyLinkButtonText = I18n.format("chat.copy");
        this.openLinkWarning = I18n.format("chat.link.warning");
        this.linkText = p_i1084_2_;
    }

    public void initGui()
    {
    	super.initGui();
    	this.buttonList.remove(0);
    	this.buttonList.remove(0);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.confirmButtonText));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.cancelButtonText));
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 2)
        {
            this.copyLinkToClipboard();
        }

        this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
    }

    public void copyLinkToClipboard()
    {
        setClipboardString(this.linkText);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.showSecurityWarning)
        {
            this.drawCenteredString(this.fontRenderer, this.openLinkWarning, this.width / 2, 110, 16764108);
        }
    }

    public void disableSecurityWarning()
    {
        this.showSecurityWarning = false;
    }
}
