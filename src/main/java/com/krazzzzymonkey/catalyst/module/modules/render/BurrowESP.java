package com.krazzzzymonkey.catalyst.module.modules.render;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ColorUtils;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;

import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import com.krazzzzymonkey.catalyst.events.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class BurrowESP extends Modules {


    public static IntegerValue remain = new IntegerValue("Remain", 50, 0, 500, "How long the burrow location should be highlighted after the person left the burrow location");
    public static ColorValue activeColor = new ColorValue("ActiveColor", Color.CYAN, "The Color of the Box ESP when a player is in the block");
    public static BooleanValue activeRainbow = new BooleanValue("ActiveRainbow", false, "Change the color in a rainbow pattern");
    public static BooleanValue fade = new BooleanValue("Fade", true, "Slowly fades out the box after the player is not in it anymore");
    public static ColorValue leftColor = new ColorValue("LeftColor", Color.CYAN, "The Color of the Box ESP after the player left the block");
    public static BooleanValue leftRainbow = new BooleanValue("LeftRainbow", false, "Change the Color in a rainbow pattern");
    public static DoubleValue height = new DoubleValue("Height", 1, 0, 2, "The height of the box");
    public static BooleanValue self = new BooleanValue("Self", false, "Should a box be drawn if you burrow yourself");
    public static ModeValue mode = new ModeValue("DetectionMode", new Mode("Obsidian", false), new Mode("EnderChest", false), new Mode("Both", false), new Mode("NonAir", false), new Mode("Any", true));
    public static ConcurrentHashMap<Integer, BurrowEntry> burrowEntryHashmap = new ConcurrentHashMap<>();
    int id = 0;

    // public static ArrayList<BurrowEntry> burrowEntries = new ArrayList<>();

    public BurrowESP() {
        super("BurrowESP", ModuleCategory.RENDER, "Shows people burrowing");
        addValue(mode, remain, activeColor, activeRainbow, leftColor, leftRainbow, fade, height, self);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        burrowEntryHashmap.values().forEach(n -> {
            BlockPos playerPos = new BlockPos(n.getPlayer().posX, n.getPlayer().posY, n.getPlayer().posZ);
            if (!n.getPos().equals(playerPos) && n.getTicksSincePlayerLeftBurrow() == -1) {
                n.setTicksSincePlayerLeftBurrow(1);
            }
        });

    });

    @EventHandler
    private final EventListener<RenderWorldLastEvent> onRenderWorldLast = new EventListener<>(e -> {

        ArrayList<BurrowEntry> old = new ArrayList<>();
        burrowEntryHashmap.values().forEach(n -> {
            if (n.getTicksSincePlayerLeftBurrow() > remain.getValue()) {
                old.add(n);
            }
        });
        burrowEntryHashmap.values().removeAll(old);


        burrowEntryHashmap.values().forEach(n -> {

            if (n.getTicksSincePlayerLeftBurrow() == -1) {
                //Player is still in block
                if (activeRainbow.getValue()) {
                    RenderUtils.drawBlockESP(n.getPos(), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, height.getValue());
                } else {
                    RenderUtils.drawBlockESP(n.getPos(), activeColor.getColor().getRed() / 255f, activeColor.getColor().getGreen() / 255f, activeColor.getColor().getBlue() / 255f, height.getValue());
                }
            } else {
                if (fade.getValue()) {
                    if(leftRainbow.getValue()){
                        RenderUtils.drawBlockESP(n.getPos(), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, height.getValue(), (1f - (n.getTicksSincePlayerLeftBurrow() / remain.getValue().floatValue())), (1f - (n.getTicksSincePlayerLeftBurrow() / remain.getValue().floatValue())) * 0.3f);
                    }else {
                        RenderUtils.drawBlockESP(n.getPos(), leftColor.getColor().getRed() / 255f, leftColor.getColor().getGreen() / 255f, leftColor.getColor().getBlue() / 255f, height.getValue(), (1f - (n.getTicksSincePlayerLeftBurrow() / remain.getValue().floatValue())), (1f - (n.getTicksSincePlayerLeftBurrow() / remain.getValue().floatValue())) * 0.3f);
                    }
                } else {
                    if (leftRainbow.getValue()) {
                        RenderUtils.drawBlockESP(n.getPos(), ColorUtils.rainbow().getRed() / 255f, ColorUtils.rainbow().getGreen() / 255f, ColorUtils.rainbow().getBlue() / 255f, height.getValue());
                    } else {
                        RenderUtils.drawBlockESP(n.getPos(), leftColor.getColor().getRed() / 255f, leftColor.getColor().getGreen() / 255f, leftColor.getColor().getBlue() / 255f, height.getValue());
                    }
                }
            }

        });

//        if(!fade.getValue()){
//        burrowEntryHashmap.values().forEach(n -> RenderUtils.drawBlockESP(n.getPos(), 1, 1, 1, 1));}
//        else{
//            burrowEntryHashmap.values().forEach(n -> RenderUtils.drawBlockESP(n.getPos(), 1, 1, 1, 1, (1f - (n.getTicksSincePlayerLeftBurrow()/ remain.getValue().floatValue())),(1f - (n.getTicksSincePlayerLeftBurrow()/ remain.getValue().floatValue())) * 0.3f));
//        }

    });


    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {

        if (mc.player == null || mc.world == null) return;
        if(event.getSide() == PacketEvent.Side.IN) {

            if (event.getPacket() instanceof SPacketBlockChange) {

                SPacketBlockChange sPacketBlockChange = (SPacketBlockChange) event.getPacket();

                if (mode.getMode("Obsidian").isToggled() && sPacketBlockChange.getBlockState().getBlock() != Blocks.OBSIDIAN)
                    return;
                if (mode.getMode("EnderChest").isToggled() && sPacketBlockChange.getBlockState().getBlock() != Blocks.ENDER_CHEST)
                    return;
                if (mode.getMode("Both").isToggled() && sPacketBlockChange.getBlockState().getBlock() != Blocks.ENDER_CHEST && sPacketBlockChange.getBlockState().getBlock() != Blocks.OBSIDIAN)
                    return;
                if (mode.getMode("NonAir").isToggled() && sPacketBlockChange.getBlockState().getBlock() == Blocks.AIR)
                    return;


                mc.world.playerEntities.stream().
                    filter(n -> {
                        if (!self.getValue()) {
                            return n != mc.player;
                        } else return true;
                    })
                    .filter(n -> sPacketBlockChange.getBlockPosition().equals(new BlockPos(n.posX, n.posY, n.posZ)))
                    .findFirst()
                    .ifPresent(n -> {

                        burrowEntryHashmap.put(id, new BurrowEntry(sPacketBlockChange.getBlockPosition(), n));
                        id++;
                    });

            }
        }
    });

}

class BurrowEntry {

    private BlockPos pos;

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Entity getPlayer() {
        return player;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public int getTicksSincePlayerLeftBurrow() {
        return ticksSincePlayerLeftBurrow;
    }

    public void setTicksSincePlayerLeftBurrow(int ticksSincePlayerLeftBurrow) {
        this.ticksSincePlayerLeftBurrow = ticksSincePlayerLeftBurrow;
    }

    private Entity player;
    private int ticksSincePlayerLeftBurrow = -1;

    public BurrowEntry(BlockPos pos, Entity player) {
        this.player = player;
        this.pos = pos;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (ticksSincePlayerLeftBurrow >= 0) {
            ticksSincePlayerLeftBurrow++;
        }

        if (this.ticksSincePlayerLeftBurrow > BurrowESP.remain.getValue()) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

    });


}
