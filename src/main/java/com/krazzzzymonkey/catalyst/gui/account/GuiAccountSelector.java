package com.krazzzzymonkey.catalyst.gui.account;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.GuiTextField;
import com.krazzzzymonkey.catalyst.gui.chest.CustomGuiButton;
import com.krazzzzymonkey.catalyst.gui.click.theme.dark.DarkFrame;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.managers.accountManager.AlreadyLoggedInException;
import com.krazzzzymonkey.catalyst.managers.accountManager.Config;
import com.krazzzzymonkey.catalyst.managers.accountManager.EnumBool;
import com.krazzzzymonkey.catalyst.managers.accountManager.ExtendedAccountData;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.AccountData;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.AltDatabase;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.AltManager;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.EncryptionTools;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.HttpTools;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.JavaTools;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.SkinTools;
import com.krazzzzymonkey.catalyst.utils.ChatColor;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The GUI where you can log in to, add, and remove accounts
 *
 * @author The_Fireplace
 */
public class GuiAccountSelector extends GuiScreen {
    private int selectedAccountIndex = 0;
    public GuiScreen Screen = this;
    private int prevIndex = 0;
    private Throwable loginfailed;
    private ArrayList<ExtendedAccountData> queriedaccounts = convertData();
    private List accountsgui;
    //Buttons that can be disabled need to be here
    private CustomGuiButton login;
    private CustomGuiButton loginoffline;
    private CustomGuiButton delete;
    private CustomGuiButton edit;
    private CustomGuiButton reloadskins;
    //Search
    private String query;
    private com.krazzzzymonkey.catalyst.gui.GuiTextField search;


    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        accountsgui = new List(this.mc);
        accountsgui.registerScrollButtons(5, 6);
        query = "Look for account";
        this.buttonList.clear();

        //Top Row
        this.buttonList.add(new CustomGuiButton(0, this.width / 2 + 4 + 40, this.height - 52, 120, 20, "Add Account", -1, new Color(0, 0, 0, 100).getRGB()));
        this.buttonList.add(login = new CustomGuiButton(1, this.width / 2 - 154 - 10, this.height - 52, 120, 20, "Login", -1, new Color(0, 0, 0, 100).getRGB()));
        this.buttonList.add(edit = new CustomGuiButton(7, this.width / 2 - 40, this.height - 52, 80, 20, "Edit", -1, new Color(0, 0, 0, 100).getRGB()));
        //Bottom Row
        this.buttonList.add(loginoffline = new CustomGuiButton(2, this.width / 2 - 154 - 10, this.height - 28, 110, 20, "Offline Mode", -1, new Color(0, 0, 0, 100).getRGB()));
        this.buttonList.add(new CustomGuiButton(3, this.width / 2 + 4 + 50, this.height - 28, 110, 20, "Cancel", -1, new Color(0, 0, 0, 100).getRGB()));
        this.buttonList.add(delete = new CustomGuiButton(4, this.width / 2 - 50, this.height - 28, 100, 20, "Delete", -1, new Color(0, 0, 0, 100).getRGB()));
        search = new GuiTextField(8, DarkFrame.fontRenderer, this.width / 2 - 100, 16, 200, 12);
        search.setText(query);
        updateButtons();
        if (!queriedaccounts.isEmpty())
            SkinTools.buildSkin(queriedaccounts.get(selectedAccountIndex).alias);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.accountsgui.handleMouseInput();
    }

    @Override
    public void updateScreen() {
        this.search.updateCursorCounter();
        updateText();
        updateButtons();
        if (!(prevIndex == selectedAccountIndex)) {
            updateShownSkin();
            prevIndex = selectedAccountIndex;
        }
    }

    private void updateShownSkin() {
        if (!queriedaccounts.isEmpty())
            SkinTools.buildSkin(queriedaccounts.get(selectedAccountIndex).alias);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean flag = search.isFocused();
        this.search.mouseClicked(mouseX, mouseY, mouseButton);
        if (!flag && search.isFocused()) {
            query = "";
            updateText();
            updateQueried();
        }
    }

    private void updateText() {
        search.setText(query);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Config.save();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        accountsgui.drawScreen(par1, par2, par3);
        Main.fontRenderer.drawCenteredString("Catalyst Account Manager", this.width / 2, 4, -1);
        if (loginfailed != null) {
            Main.fontRenderer.drawCenteredString(loginfailed.getLocalizedMessage(), this.width / 2, this.height - 62, 16737380);
        }
        search.drawTextBox(ColorUtils.color(0, 0, 0, 255), -1);
        super.drawScreen(par1, par2, par3);
        Main.fontRenderer.drawString("Logged in as: " + Minecraft.getMinecraft().getSession().getUsername(), 3, 3, -1);
   /*     if (!queriedaccounts.isEmpty()) {
            if (queriedaccounts.get(selectedAccountIndex).premium == EnumBool.TRUE)
                Main.fontRenderer.drawString("(Premium)", 3, 15, 6618980);
            else if (queriedaccounts.get(selectedAccountIndex).premium == EnumBool.FALSE)
                Main.fontRenderer.drawString("(Non Premium)", 3, 15, 16737380);

        }*/
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 3) {
                escape();
            } else if (button.id == 0) {
                add();
            } else if (button.id == 4) {
                delete();
            } else if (button.id == 1) {
                login(selectedAccountIndex);
            } else if (button.id == 2) {
                logino(selectedAccountIndex);
            } else if (button.id == 7) {
                edit();
            } else if (button.id == 8) {
                reloadSkins();
            } else {
                accountsgui.actionPerformed(button);
            }
        }
    }

    /**
     * Reload Skins
     */
    private void reloadSkins() {
        Config.save();
        SkinTools.cacheSkins();
        updateShownSkin();
    }

    /**
     * Leave the gui
     */
    private void escape() {
        mc.displayGuiScreen(null);
    }

    /**
     * Delete the selected account
     */
    private void delete() {
        AltDatabase.getInstance().getAlts().remove(getCurrentAsEditable());
        if (selectedAccountIndex > 0)
            selectedAccountIndex--;
        updateQueried();
        updateButtons();
    }

    /**
     * Add an account
     */
    private void add() {
        mc.displayGuiScreen(new GuiAddAccount());
    }

    /**
     * Login to the account in offline mode, then return to main menu
     *
     * @param selected The index of the account to log in to
     */
    private void logino(int selected) {
        ExtendedAccountData data = queriedaccounts.get(selected);
        AltManager.getInstance().setUserOffline(data.alias);
        loginfailed = null;
        // Minecraft.getMinecraft().displayGuiScreen(null);
        ExtendedAccountData current = getCurrentAsEditable();
        current.useCount++;
        current.lastused = JavaTools.getJavaCompat().getDate();
    }

    /**
     * Attempt login to the account, then return to main menu if successful
     *
     * @param selected The index of the account to log in to
     */
    private void login(int selected) {
        ExtendedAccountData data = queriedaccounts.get(selected);
        loginfailed = AltManager.getInstance().setUser(data.user, data.pass);
        if (loginfailed == null) {
            //Minecraft.getMinecraft().displayGuiScreen(null);
            ExtendedAccountData current = getCurrentAsEditable();
            current.premium = EnumBool.TRUE;
            current.useCount++;
            current.lastused = JavaTools.getJavaCompat().getDate();
        } else if (loginfailed instanceof AlreadyLoggedInException) {
            getCurrentAsEditable().lastused = JavaTools.getJavaCompat().getDate();
        } else if (HttpTools.ping("https://minecraft.net")) {
            getCurrentAsEditable().premium = EnumBool.FALSE;
        }
    }

    /**
     * Edits the current account's information
     */
    private void edit() {
        mc.displayGuiScreen(new GuiEditAccount(selectedAccountIndex));
    }

    private void updateQueried() {
        queriedaccounts = convertData();
        if (!query.equals("Look for account") && !query.equals("")) {
            for (int i = 0; i < queriedaccounts.size(); i++) {
                if (!queriedaccounts.get(i).alias.toLowerCase().contains(query.toLowerCase())) {
                    queriedaccounts.remove(i);
                    i--;
                }
            }
        }
        if (!queriedaccounts.isEmpty()) {
            while (selectedAccountIndex >= queriedaccounts.size()) {
                selectedAccountIndex--;
            }
        }
    }

    @Override
    protected void keyTyped(char character, int keyIndex) {
        if (keyIndex == Keyboard.KEY_UP && !queriedaccounts.isEmpty()) {
            if (selectedAccountIndex > 0) {
                selectedAccountIndex--;
            }
        } else if (keyIndex == Keyboard.KEY_DOWN && !queriedaccounts.isEmpty()) {
            if (selectedAccountIndex < queriedaccounts.size() - 1) {
                selectedAccountIndex++;
            }
        } else if (keyIndex == Keyboard.KEY_ESCAPE) {
            escape();
        } else if (keyIndex == Keyboard.KEY_DELETE && delete.enabled) {
            delete();
        } else if (keyIndex == Keyboard.KEY_RETURN && !search.isFocused() && (login.enabled || loginoffline.enabled)) {
            if ((Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) && loginoffline.enabled) {
                logino(selectedAccountIndex);
            } else {
                if (login.enabled)
                    login(selectedAccountIndex);
            }
        } else if (keyIndex == Keyboard.KEY_BACK) {
            if (search.isFocused() && query.length() > 0) {
                query = query.substring(0, query.length() - 1);
                updateText();
                updateQueried();
            }
        } else if (keyIndex == Keyboard.KEY_F5) {
            reloadSkins();
        } else if (character != 0) {
            if (search.isFocused()) {
                if (keyIndex == Keyboard.KEY_RETURN) {
                    search.setFocused(false);
                    updateText();
                    updateQueried();
                    return;
                }
                query += character;
                updateText();
                updateQueried();
            }
        }
    }

    private ArrayList<ExtendedAccountData> convertData() {
        @SuppressWarnings("unchecked")
        ArrayList<AccountData> tmp = (ArrayList<AccountData>) AltDatabase.getInstance().getAlts().clone();
        ArrayList<ExtendedAccountData> converted = new ArrayList<>();
        int index = 0;
        for (AccountData data : tmp) {
            if (data instanceof ExtendedAccountData) {
                converted.add((ExtendedAccountData) data);
            } else {
                converted.add(new ExtendedAccountData(EncryptionTools.decode(data.user), EncryptionTools.decode(data.pass), data.alias));
                AltDatabase.getInstance().getAlts().set(index, new ExtendedAccountData(EncryptionTools.decode(data.user), EncryptionTools.decode(data.pass), data.alias));
            }
            index++;
        }
        return converted;
    }

    private ArrayList<AccountData> getAccountList() {
        return AltDatabase.getInstance().getAlts();
    }

    private ExtendedAccountData getCurrentAsEditable() {
        for (AccountData dat : getAccountList()) {
            if (dat instanceof ExtendedAccountData) {
                if (dat.equals(queriedaccounts.get(selectedAccountIndex))) {
                    return (ExtendedAccountData) dat;
                }
            }
        }
        return null;
    }

    private void updateButtons() {
        login.enabled = !queriedaccounts.isEmpty() && !EncryptionTools.decode(queriedaccounts.get(selectedAccountIndex).pass).equals("");
        loginoffline.enabled = !queriedaccounts.isEmpty();
        delete.enabled = !queriedaccounts.isEmpty();
        edit.enabled = !queriedaccounts.isEmpty();

    }

    class List extends GuiSlot {
        public List(Minecraft mcIn) {
            super(mcIn, GuiAccountSelector.this.width, GuiAccountSelector.this.height, 32, GuiAccountSelector.this.height - 64, 36);
        }

        File file = FileManager.getAssetFile("mainmenu/steve.png");

        ResourceLocation steve;
        HashMap<String, BufferedImage> avatar = new HashMap<>();
        public ArrayList<String> hasChecked = new ArrayList<String>();

        @Override
        protected int getSize() {
            return GuiAccountSelector.this.queriedaccounts.size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            GuiAccountSelector.this.selectedAccountIndex = slotIndex;
            GuiAccountSelector.this.updateButtons();

            if (isDoubleClick && GuiAccountSelector.this.login.enabled) {
                GuiAccountSelector.this.login(slotIndex);
            }
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return slotIndex == GuiAccountSelector.this.selectedAccountIndex;
        }

        @Override
        protected void drawSelectionBox(int insideLeft, int insideTop, int mouseXIn, int mouseYIn, float partialTicks) {
            int i = this.getSize();
            for (int j = 0; j < i; ++j) {
                int k = insideTop + j * this.slotHeight + this.headerPadding;
                int l = this.slotHeight - 4;
                if (k > this.bottom || k + l < this.top) {
                    this.updateItemPos(j, insideLeft, k, partialTicks);
                }
                drawSlot(j, insideLeft, k, l, mouseXIn, this.isSelected(j) ? 200 : 50);

            }

        }

        @Override
        protected int getContentHeight() {
            return GuiAccountSelector.this.queriedaccounts.size() * 15;
        }

        @Override
        protected void drawBackground() {
            // GuiAccountSelector.this.drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5, float v) {

        }

        protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int v) {

            ExtendedAccountData data = queriedaccounts.get(p_192637_1_);
            String s = data.alias;
            if (StringUtils.isEmpty(s)) {
                s = I18n.format("ias.alt") + " " + (p_192637_1_ + 1);
            }

            RenderUtils.drawBorderedRect(p_192637_2_ - 1, p_192637_3_ + 1, p_192637_2_ + 220 - 2, p_192637_3_ + 36 - 2, 2, new Color(197, 197, 197).getRGB(), new Color(0, 0, 0, v).getRGB());


            if (avatar.get(data.alias) == null && !hasChecked.contains(data.alias)) {
                hasChecked.add(data.alias);

                (new Thread(() -> {
                    try {
                        if (data.premium != EnumBool.TRUE) {
                            throw new Exception();
                        }
                        String json = readUrl("https://playerdb.co/api/player/minecraft/" + data.alias);
                        JsonParser jsonParser = new JsonParser();
                        JsonElement jo = jsonParser.parse(json);
                        String url = jo.getAsJsonObject().get("data").getAsJsonObject().get("player").getAsJsonObject().get("avatar").getAsString();
                        avatar.put(data.alias, ImageIO.read(getUrlStream(url)));

                    } catch (Exception e) {
                        try {
                            avatar.put(data.alias, ImageIO.read(file));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                })
                ).start();
            } else {
                if (hasChecked.contains(data.alias)) {
                    if (avatar.get(data.alias) == null) {
                        try {
                            Minecraft.getMinecraft().renderEngine.bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation(file.getName(), new DynamicTexture(ImageIO.read(file))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Minecraft.getMinecraft().renderEngine.bindTexture( Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation("catalyst/avatars", new DynamicTexture(avatar.get(data.alias))));
                    }
                }

                double scale = 0.12;
                GlStateManager.scale(scale, scale, scale);
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect((int) ((p_192637_2_) / scale), (int) ((p_192637_3_ + 2) / scale), 0, 0, 256, 256);
                GlStateManager.scale(1f / scale, 1f / scale, 1f / scale);
            }
            Main.fontRenderer.drawString(s, p_192637_2_ + 32, p_192637_3_ + 3, -1);
            if (Minecraft.getMinecraft().getSession().getUsername().equals(data.alias)) {
                Main.smallFontRenderer.drawStringWithShadow((data.premium == EnumBool.TRUE ? "Premium" : "Not Premium") + ChatColor.WHITE + " (Using)", p_192637_2_ + 32, p_192637_3_ + 14, data.premium == EnumBool.TRUE ? new Color(0, 255, 58).getRGB() : new Color(255, 42, 42).getRGB());
            } else {
                Main.smallFontRenderer.drawStringWithShadow(data.premium == EnumBool.TRUE ? "Premium" : "Not Premium", p_192637_2_ + 32, p_192637_3_ + 14, data.premium == EnumBool.TRUE ? new Color(0, 255, 58).getRGB() : new Color(255, 42, 42).getRGB());
            }

            Main.smallFontRenderer.drawStringWithShadow("Last Used: " + JavaTools.getFormattedDate(data.lastused), p_192637_2_ + 32, p_192637_3_ + 22, Color.LIGHT_GRAY.getRGB());

               /* GuiAccountSelector.this.drawString(GuiAccountSelector.this.fontRenderer, s, p_192637_2_ + 32, p_192637_3_ +data.premium == EnumBool.TRUE ? "Premium" : "Not Premium", p_192637_2_ + 32, p_192637_3_ + 12, data.premium == EnumBool.TRUE ? new Color(0, 255, 58).getRGB() : new Color(255, 42, 42).getRGB() 1, color);
                GuiAccountSelector.this.drawString(GuiAccountSelector.this.fontRenderer, );
                GuiAccountSelector.this.drawString(GuiAccountSelector.this.fontRenderer, "Last Used: " + JavaTools.getFormattedDate(data.lastused), p_192637_2_ + 32, p_192637_3_ + 22, Color.LIGHT_GRAY.getRGB());*/

        }

    }

    private static String readUrl(String urlString) throws IOException {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.addRequestProperty("User-Agent", "Catalyst Capes");

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private static InputStream getUrlStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        return con.getInputStream();
    }
}
