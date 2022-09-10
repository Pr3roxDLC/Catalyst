package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat extends Gui {


    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;
    @Shadow
    private int scrollPos;
    @Shadow
    private boolean isScrolled;


    @Inject(method = "drawChat", at = @At("HEAD"), cancellable = true)
    private void drawScreen(int updateCounter, CallbackInfo ci) {

        if (this.mc.ingameGUI == null) {
            return;
        }

        final GuiNewChat chat = this.mc.ingameGUI.getChatGUI();
        ci.cancel();
        Method getRainbow;
        Color rainbow = new Color(-1);
        try {
            Class[] noParams = {};
            getRainbow = ModuleManager.getMixinProxyClass().getMethod("getRainbow", noParams);
            rainbow = (Color) getRainbow.invoke(ModuleManager.getMixinProxyClass(), (Object[]) null);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = chat.getLineCount();
            int j = this.drawnChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            if (j > 0) {
                boolean flag = chat.getChatOpen();

                float f1 = chat.getChatScale();
                int k = MathHelper.ceil((float) chat.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 8.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);
                int l = 0;

                int i1;
                int j1;
                int l1;
                for (i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline != null) {
                        j1 = updateCounter - chatline.getUpdatedCounter();
                        if (j1 < 200 || flag) {
                            double d0 = (double) j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 *= 10.0D;
                            d0 = MathHelper.clamp(d0, 0.0D, 1.0D);
                            d0 *= d0;
                            l1 = (int) (255.0D * d0);
                            if (flag) {
                                l1 = 255;
                            }

                            l1 = (int) ((float) l1 * f);
                            ++l;
                            if (l1 > 3) {
                                int j2 = -i1 * 9;
                                if (ModuleManager.getModule("CustomChat").isToggled() && !ModuleManager.getModule("CustomChat").isToggledValue("Background")) {

                                } else {
                                    drawRect(-2, j2 - 9, 0 + k + 4, j2, l1 / 2 << 24);
                                }

                                String s = chatline.getChatComponent().getFormattedText();
                                if (ModuleManager.getModule("ChatMention").isToggled()) {


                                    Method getFormatting;
                                    String formatting = "";
                                    try {
                                        Class[] noParams = {};
                                        getFormatting = ModuleManager.getMixinProxyClass().getMethod("getFormatting", noParams);
                                        formatting = (String) getFormatting.invoke(ModuleManager.getModuleClass("ChatMention"), (Object[]) null);
                                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                                    }
                                    if (ModuleManager.getModule("ChatMention").isToggledValue("Name")) {
                                        if (!chatline.getChatComponent().getUnformattedText().contains("<" + mc.player.getName() + ">")) {


                                            s = s.replace(mc.player.getName(), formatting + mc.player.getName() + "\u00A7r");
                                        }


                                    }
                                    Method getMentionList;
                                    ArrayList<String> mentionList;
                                    try {
                                        Class[] noParams = {};
                                        getMentionList = ModuleManager.getMixinProxyClass().getMethod("getMentionList", noParams);
                                        mentionList = (ArrayList<String>) getMentionList.invoke(ModuleManager.getMixinProxyClass(), (Object[]) null);

                                        for (String word : mentionList.toArray(new String[0])) {
                                            if (s.contains(word)) {
                                                s = s.replace(word, formatting + word + "\u00A7r");
                                            }
                                        }
                                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                                    }

                                }
                                GlStateManager.enableBlend();

                                if (s.startsWith("\u00a78[" + "\u00A7r" + "Catalyst" + "\u00a78]\u00a77")) {
                                    if (ModuleManager.getModule("CustomChat").isToggledValue("CustomFont") && ModuleManager.getModule("CustomChat").isToggled()) {


                                        try {
                                            Class[] params = {String.class, double.class, double.class, int.class};
                                            ModuleManager.getMixinProxyClass().getMethod("drawStringWithShadow", params).invoke(ModuleManager.getMixinProxyClass(), s, (double)0.0, (double) (j2 - 8), (int)(rainbow.getRGB() + (l1 << 24)));
                                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                            e.printStackTrace();
                                        }


                                    } else
                                        this.mc.fontRenderer.drawStringWithShadow(s, 0.0F, (float) (j2 - 8), rainbow.getRGB() + (l1 << 24));
                                } else {
                                    if (ModuleManager.getModule("CustomChat").isToggledValue("CustomFont") && ModuleManager.getModule("CustomChat").isToggled()) {
                                        try {
                                            Class[] params = {String.class, double.class, double.class, int.class};
                                            ModuleManager.getMixinProxyClass().getMethod("drawStringWithShadow", params).invoke(ModuleManager.getMixinProxyClass(),
                                                s, (double)0.0, (double) (j2 - 8), (int)(16777215 + (l1 << 24)));
                                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    } else
                                        this.mc.fontRenderer.drawStringWithShadow(s, 0.0F, (float) (j2 - 8), 16777215 + (l1 << 24));
                                }

                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                if (flag) {
                    i1 = this.mc.fontRenderer.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = j * i1 + j;
                    j1 = l * i1 + l;
                    int j3 = this.scrollPos * j1 / j;
                    int k1 = j1 * j1 / l2;
                    if (l2 != j1) {
                        l1 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 13382451 : 3355562;

                        if (ModuleManager.getModule("CustomChat").isToggled() && !ModuleManager.getModule("CustomChat").isToggledValue("Background")) {

                        } else {
                            drawRect(0, -j3, 2, -j3 - k1, l3 + (l1 << 24));
                            drawRect(2, -j3, 1, -j3 - k1, 13421772 + (l1 << 24));
                        }
                    }
                }

                GlStateManager.popMatrix();
            }
        }
        ci.cancel();

    }
}
