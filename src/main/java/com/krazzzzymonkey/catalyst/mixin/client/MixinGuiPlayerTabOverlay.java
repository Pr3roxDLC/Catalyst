package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.modules.render.TabFriends;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerNameHead(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(getPlayerNameGS(networkPlayerInfoIn));
    }

    private String getPlayerNameGS(NetworkPlayerInfo networkPlayerInfoIn) {
        String displayName = networkPlayerInfoIn.getDisplayName() != null ?
            networkPlayerInfoIn.getDisplayName().getFormattedText() :
            ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());


        if (ModuleManager.getModule("TabFriends").isToggled()){
            ArrayList<String> friendList = FriendManager.friendsList;
            String color = TabFriends.color;
                if (friendList.contains(displayName)) {
                    return (ModuleManager.getModule("TabFriends").isToggledValue("Prefix") ? "\u00A77" + "[" + color + "F" + "\u00A77" + "] " : "") + color + displayName;
                } else {
                    return displayName;
                }
        }
        return displayName;
    }
}
