package com.krazzzzymonkey.catalyst.utils.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.Packet;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class Wrapper {

	public static volatile Wrapper INSTANCE = new Wrapper();
	
    public Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    public EntityPlayerSP player() {
        return Wrapper.INSTANCE.mc().player;
    }

    public WorldClient world() {
        return Wrapper.INSTANCE.mc().world;
    }

    public GameSettings mcSettings() {
        return Wrapper.INSTANCE.mc().gameSettings;
    }

    public FontRenderer fontRenderer() {
        return Wrapper.INSTANCE.mc().fontRenderer;
    }

    public InventoryPlayer inventory() {
        return this.player().inventory;
    }

    public void sendPacket(Packet packet) {
        this.player().connection.sendPacket(packet);
    }
    
    public void copy(String content) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content), null);
    }
}
