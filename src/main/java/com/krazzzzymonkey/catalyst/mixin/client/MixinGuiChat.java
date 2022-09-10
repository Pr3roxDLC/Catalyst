package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.command.Command;
import com.krazzzzymonkey.catalyst.events.CommandEvent;
import com.krazzzzymonkey.catalyst.managers.CommandManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

@Mixin(GuiChat.class)
public abstract class MixinGuiChat extends GuiScreen {

    String predictedCommand;

    @Shadow
    protected GuiTextField inputField;


    @Inject(method = "drawScreen", at = @At("HEAD"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        CommandEvent.inputField = inputField.getText();
        if (inputField.getText().isEmpty()) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Use " + CommandManager.prefix + "help for Catalyst commands.", 4, height - 12, -1);
        }
        if (inputField.getText().startsWith(CommandManager.prefix)) {
            RenderUtils.drawBorderedRect(2, height - 14, width - 2, height - 2, 2, ColorUtils.rainbow().getRGB(), new Color(0, 0, 0, 0).getRGB());
            if (inputField.getText().length() > 1) {

                for (Command command : CommandManager.getInstance().getCommands()) {
                    if ((CommandManager.prefix + command.getCommand()).contains(inputField.getText())) {
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(CommandManager.prefix + command.getSyntax(), 4, height - 12, -1);
                        predictedCommand = command.getCommand();
                        break;
                    }
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && !inputField.getText().contains(" ")) {
                    inputField.setText(CommandManager.prefix + predictedCommand);

                }
            }
        }
    }
}
