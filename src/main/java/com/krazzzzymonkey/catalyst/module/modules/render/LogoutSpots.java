package com.krazzzzymonkey.catalyst.module.modules.render;

import com.google.common.collect.Lists;
import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.LogOutSpot;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class LogoutSpots extends Modules {

    private static BooleanValue coordinates;
    private static BooleanValue friendRainbow;
    private static BooleanValue rainbow;
    private ColorValue friendColor;
    private ColorValue color;

    public LogoutSpots() {
        super("LogoutSpots", ModuleCategory.RENDER, "Shows you where a player has logged out");

        color = new ColorValue("Color", Color.RED, "Changes the color of rendered logout spots");
        rainbow = new BooleanValue("Rainbow", false, "Makes the logout spots cycle through colors");

        friendColor = new ColorValue("FriendColor", Color.CYAN, "Changes the color of rendered logout spots for friends");
        friendRainbow = new BooleanValue("FriendRainbow", true, "Makes the logout spots cycle through colors for friends");
        coordinates = new BooleanValue("Coordinates", true, "Shows coordinates above the logout spot and in chat");
        this.addValue(color, rainbow, friendColor, friendRainbow, coordinates);
    }


    static ArrayList<NetworkPlayerInfo> playerMap = new ArrayList<>();
    static int cachePlayerCount;
    boolean isOnServer;
    private static ArrayList<Object> j = new ArrayList<>();
    public static List<LogOutSpot> logoutPositions = Lists.newArrayList();


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
            if (!playerMap.isEmpty()) {
                playerMap = new ArrayList<>();
                j = new ArrayList<>();
                logoutPositions = Lists.newArrayList();
            }
            return;
        }

        if (Minecraft.getMinecraft().isSingleplayer()) {
            return;
        }

        if (Minecraft.getMinecraft().player.ticksExisted % 10 == 0) {
            checkPlayers();
            resetArraylist();
        }
    });

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {

        for (LogOutSpot position : logoutPositions) {

            if (FriendManager.friendsList.contains(position.name)) {
                renderLogoutSpot(position, friendRainbow, friendColor);
            } else {
                renderLogoutSpot(position, rainbow, color);
            }


        }

    });

    private void renderLogoutSpot(LogOutSpot position, BooleanValue friendRainbow, ColorValue friendColor) {
        if (friendRainbow.getValue()) {
            drawNametag(position.x + 0.5d, position.y + 1 + 0.5d, position.z + 0.5d, position.name, new Color(ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 1f).getRGB());
            RenderUtils.drawLogoutSpot(position.name, position.x, position.y, position.z, ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, 2f);
        } else {
            drawNametag(position.x + 0.5d, position.y + 1 + 0.5d, position.z + 0.5d, position.name, new Color(friendColor.getColor().getRed() / 255f, friendColor.getColor().getGreen() / 255f, friendColor.getColor().getBlue() / 255f, 1f).getRGB());
            RenderUtils.drawLogoutSpot(position.name, position.x, position.y, position.z, friendColor.getColor().getRed() / 255f, friendColor.getColor().getGreen() / 255f, friendColor.getColor().getBlue() / 255f, 2f);
        }
    }

    private void checkPlayers() {
        ArrayList<NetworkPlayerInfo> infoMap = new ArrayList(Minecraft.getMinecraft().getConnection().getPlayerInfoMap());

        int currentPlayerCount = infoMap.size();
        if (currentPlayerCount != cachePlayerCount) {
            ArrayList<NetworkPlayerInfo> currentInfoMap = (ArrayList) infoMap.clone();
            currentInfoMap.removeAll(playerMap);
            if (currentInfoMap.size() > 5) {
                cachePlayerCount = playerMap.size();
                onJoinServer();
                return;
            }
            final ArrayList<NetworkPlayerInfo> playerMapClone = (ArrayList<NetworkPlayerInfo>) playerMap.clone();
            playerMapClone.removeAll(infoMap);
            for (final NetworkPlayerInfo npi : playerMapClone) {
                this.playerLeft(npi);
            }
            for (final NetworkPlayerInfo npi : currentInfoMap) {
                this.playerJoined(npi);
            }
            cachePlayerCount = playerMap.size();
            this.onJoinServer();

        }
    }

    private void onJoinServer() {
        playerMap = new ArrayList(Minecraft.getMinecraft().getConnection().getPlayerInfoMap());
        cachePlayerCount = playerMap.size();
        this.isOnServer = true;
    }

    protected void playerLeft(NetworkPlayerInfo playerInfo) {
        Iterator localIterator = j.iterator();
        while (localIterator.hasNext()) {
            Entity entity = (Entity) localIterator.next();
            if (entity instanceof EntityPlayer) {

                if (entity.getName().equalsIgnoreCase(playerInfo.getGameProfile().getName()) && !entity.getName().equals(Minecraft.getMinecraft().player.getName())) {
                    if (coordinates.getValue()) {
                        ChatUtils.message(playerInfo.getGameProfile().getName() + " has logged out at, x: "
                            + entity.getPosition().getX() + " y: " + entity.getPosition().getY() + " z: "
                            + entity.getPosition().getZ());
                    } else {
                        ChatUtils.message(playerInfo.getGameProfile().getName() + " has logged out");
                    }

                    logoutPositions.add(new LogOutSpot(entity.posX, entity.posY, entity.posZ, entity.getName()));
                }

            }
        }
    }

    protected void playerJoined(NetworkPlayerInfo playerInfo) {

        for (int i = 0; i < logoutPositions.size(); i++) {
            if (logoutPositions.get(i).name.equals(playerInfo.getGameProfile().getName())
                && !logoutPositions.get(i).name.equals(Minecraft.getMinecraft().player.getName())) {
                logoutPositions.remove(i);
                i--;
            }
        }

    }

    private void resetArraylist() {
        j.clear();
        j.addAll(Minecraft.getMinecraft().world.getLoadedEntityList());


    }

    public void onEnable() {
        super.onEnable();
        if (mc.world == null || mc.player == null) return;
        onJoinServer();
        this.resetArraylist();
    }

    public void onDisable() {
        super.onDisable();
        if (mc.world == null || mc.player == null) return;

        this.resetArraylist();

    }

    private static void drawNametag(double x, double y, double z, String text, int color) {
        double dist = Minecraft.getMinecraft().player.getDistance(x, y, z);
        double scale = 1, offset = 0;
        int start = 0;
        scale = -((int) dist) / 6.0;
        if (scale < 1) scale = 1;
        scale *= 2.0 / 75.0;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - Minecraft.getMinecraft().getRenderManager().viewerPosX, y + offset - Minecraft.getMinecraft().getRenderManager().viewerPosY, z - Minecraft.getMinecraft().getRenderManager().viewerPosZ);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1 : 1, 0, 0);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();

        GlStateManager.enableTexture2D();

        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text + "'s Logout Spot", (-Main.fontRenderer.getStringWidth(text + "'s Logout Spot") / 2f), -40, color);
        if (coordinates.getValue()) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("X: " + x + " Y: " + y + " Z: " + z, (-Main.fontRenderer.getStringWidth("X: " + x + " Y: " + y + " Z: " + z) / 2f), -30, color);
        }
        GlStateManager.enableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();

    }
}
