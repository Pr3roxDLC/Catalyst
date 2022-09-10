package com.krazzzzymonkey.catalyst.module.modules.hud;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.gui.click.HudGuiScreen;
import com.krazzzzymonkey.catalyst.managers.FontManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.CrystalClickCounter;
import com.krazzzzymonkey.catalyst.managers.TimerManager;
import com.krazzzzymonkey.catalyst.utils.MouseUtils;
import com.krazzzzymonkey.catalyst.utils.font.CFontRenderer;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.Number;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO HOTBAR AND PERCENTAGE https://www.youtube.com/watch?v=Zqe0K3UQ5Cg
//TODO ROLLING AVERAGE

public class Graphs extends Modules {
    private final CrystalClickCounter IncomingPackets = new CrystalClickCounter();
    private final CrystalClickCounter OutgoingPackets = new CrystalClickCounter();
    private final CrystalClickCounter fpsCounter = new CrystalClickCounter();
    private final CrystalClickCounter breakSpeed = new CrystalClickCounter();

    private BooleanValue fpsBoolean;
    private BooleanValue pingBoolean;
    private BooleanValue BPSBoolean;
    private BooleanValue TPSBoolean;
    private BooleanValue CrystalsBoolean;
    private BooleanValue incomingBoolean;
    private BooleanValue outgoingBoolean;

    private Number xOffset;
    private Number yOffset;


    private List<Double> fps = new ArrayList<>();
    private List<Double> tps = new ArrayList<>();
    private List<Double> ping = new ArrayList<>();
    private List<Double> BPS = new ArrayList<>();
    private List<Double> CrystalsPS = new ArrayList<>();
    private List<Double> incomingPackets = new ArrayList<>();
    private List<Double> outgoingPackets = new ArrayList<>();

    public static CFontRenderer graphFontRenderer = new CFontRenderer(new Font(FontManager.font, Font.PLAIN, 12), true, true);

    public Graphs() {
        super("Graphs", ModuleCategory.HUD, "Displays various graphs on your hud", true);

        this.fpsBoolean = new BooleanValue("FPS", true, "Counts frames per second");
        this.pingBoolean = new BooleanValue("Ping", true, "Counts strength of connection to server");
        this.TPSBoolean = new BooleanValue("TPS", true, "Counts server ticks per second");
        this.BPSBoolean = new BooleanValue("BPS", true, "Counts speed in blocks per second");
        this.CrystalsBoolean = new BooleanValue("CrystalsPerSecond", true, "Counts speed of placing crystals per second");
        this.incomingBoolean = new BooleanValue("IncomingPackets", true, "Counts incoming packets");
        this.outgoingBoolean = new BooleanValue("OutgoingPackets", true, "Counts outgoing packets");


        this.xOffset = new Number("X Offset", 0.0);
        this.yOffset = new Number("Y Offset", 0.0);
        this.addValue(fpsBoolean, pingBoolean, TPSBoolean, BPSBoolean, CrystalsBoolean, incomingBoolean, outgoingBoolean, xOffset, yOffset);

    }

    int finalMouseX = 0, finalMouseY = 0;
    boolean isDragging = false;
    boolean isAlreadyDragging = false;

    int Ping = 0;
    String entityID;

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {

        if (Minecraft.getMinecraft().player.isSwingInProgress) {
            try {
                if (event.getTarget() instanceof EntityEnderCrystal) {
                    entityID = String.valueOf(event.getTarget().getEntityId());
                }

            } catch (NullPointerException e) {
                //empty catch block
            }
        }
    }


    @EventHandler
    private final EventListener<PacketEvent> onPacketIn = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.IN) {
            IncomingPackets.onBreak();
            Packet packet = e.getPacket();

            if (packet instanceof SPacketDestroyEntities && entityID != null) {
                try {
                    SPacketDestroyEntities sPacketDestroyEntities = (SPacketDestroyEntities) packet;
                    if (Arrays.toString(sPacketDestroyEntities.getEntityIDs()).contains(entityID)) {
                        breakSpeed.onBreak();
                    }
                } catch (NullPointerException exception) {
                    //empty catch block
                }
            }
        }

    });
    @EventHandler
    private final EventListener<PacketEvent> onPacketOut = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {
            OutgoingPackets.onBreak();
        }
    });

    @Override
    public void onDisable() {
        fps = new ArrayList<>();
        tps = new ArrayList<>();
        ping = new ArrayList<>();
        BPS = new ArrayList<>();
        CrystalsPS = new ArrayList<>();
        incomingPackets = new ArrayList<>();
        outgoingPackets = new ArrayList<>();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        fps = new ArrayList<>();
        tps = new ArrayList<>();
        ping = new ArrayList<>();
        BPS = new ArrayList<>();
        CrystalsPS = new ArrayList<>();
        incomingPackets = new ArrayList<>();
        outgoingPackets = new ArrayList<>();
        super.onEnable();
    }

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;
        fpsCounter.onBreak();
        final double deltaX = Minecraft.getMinecraft().player.posX - Minecraft.getMinecraft().player.prevPosX;
        final double deltaZ = Minecraft.getMinecraft().player.posZ - Minecraft.getMinecraft().player.prevPosZ;
        final double tickRate = (Minecraft.getMinecraft().timer.tickLength / 1000.0f);
        double bps = (MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) / tickRate);

        try {
            Ping = Minecraft.getMinecraft().player.connection.getPlayerInfo(Minecraft.getMinecraft().player.getUniqueID()).getResponseTime();
        } catch (NullPointerException ignored) {
        }
        int yPos = yOffset.getValue().intValue();
        int xPos = xOffset.getValue().intValue();
        int offset = 0;
        if (fpsBoolean.getValue()) {
            makeGraph(xPos, yPos + offset, fps, fpsCounter.getCps(), "FPS", 1f, 1f, 1f);
            offset += 35;
        }
        if (pingBoolean.getValue()) {
            makeGraph(xPos, yPos + offset, ping, Ping, "Ping", 1f, 1f, 1f);
            offset += 35;
        }
        if (TPSBoolean.getValue()) {
            makeGraph(xPos, yPos + offset, tps, TimerManager.INSTANCE.getTickRate(), "TPS", 1f, 1f, 1f);
            offset += 35;
        }
        if (BPSBoolean.getValue()) {
            makeGraph(xPos, yPos + offset, BPS, bps, "BPS", 1f, 1f, 1f);
            offset += 35;
        }
        if (CrystalsBoolean.getValue()) {
            makeGraph(xPos, yPos + offset, CrystalsPS, breakSpeed.getCps(), "Crystals Per Second", 1f, 1f, 1f);
            offset += 35;
        }
        if (incomingBoolean.getValue()) {
            makeGraph(xPos, yPos + offset, incomingPackets, IncomingPackets.getCps(), "Incoming Packets", 1f, 1f, 1f);
            offset += 35;
        }
        if (outgoingBoolean.getValue()) {
            makeGraph(xPos, yPos + offset, outgoingPackets, OutgoingPackets.getCps(), "Outgoing Packets", 1f, 1f, 1f);
            offset += 35;
        }

        if (Minecraft.getMinecraft().currentScreen instanceof HudGuiScreen) {


            if (MouseUtils.isLeftClicked() && !(MouseUtils.isMouseOver(xPos, xPos + 100, yPos, yPos + 35))) {
                isAlreadyDragging = true;
            }

            if (!MouseUtils.isLeftClicked() && isAlreadyDragging) {
                isAlreadyDragging = false;
            }

            if (!isAlreadyDragging || isDragging) {
                if (MouseUtils.isMouseOver(xPos, xPos + 100, yPos, yPos + 35)) {
                    isDragging = true;
                }


                if (MouseUtils.isLeftClicked() && isDragging) {
                    finalMouseX = MouseUtils.getMouseX();
                    finalMouseY = MouseUtils.getMouseY();
                    xOffset.value = (double)finalMouseX - 50;
                    yOffset.value = (double)finalMouseY - 17;
                } else isDragging = false;

            }
        }


    });


    public void makeGraph(int xCoordinate, int yCoordinate, List<Double> graphList, double graphValues, String graphName, float red, float green, float blue) {
        yCoordinate = yCoordinate + 35;
        final DecimalFormat df = new DecimalFormat("0.0");
        double max = graphList.stream().max(Double::compareTo).orElse(1D);
        double transform = 35 / 2.0 / max;

        graphList.add(graphValues + 1);
        while (graphList.size() > 200) {
            graphList.remove(0);
        }

        int width = 0;
        GL11.glColor4f(red, green, blue, 1.0f);
        GL11.glLineWidth(2.0f);

        RenderUtils.drawBorderedRect(xCoordinate, yCoordinate, xCoordinate + 100, yCoordinate - 35, 1, ColorUtils.rainbow().getRGB(), ColorUtils.getColor(150, 0, 0, 0));
        graphFontRenderer.drawString(df.format(graphList.get(graphList.size() - 1) - 1) + " " + graphName, xCoordinate + 2, yCoordinate - 33, -1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glBegin(GL11.GL_LINE_STRIP);

        width += 3;

        double v = (100 - width) / (double) graphList.size();

        for (int j = 0; j < graphList.size(); j++) {
            double currValue = graphList.get(j);

            GL11.glVertex2d((width + j * v) + xCoordinate, (yCoordinate + transform) - transform * currValue);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);


    }


}
