package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.Notification;
import com.krazzzzymonkey.catalyst.utils.visual.RenderUtils;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HoleBreakNotifier extends Modules {

    public List<BreakEntry> entries = Collections.synchronizedList(new ArrayList<BreakEntry>());
    public IntegerValue delay = new IntegerValue("Delay", 2000, 0, 10000, "After how much time should the same block be announced again");
    public ModeValue mode = new ModeValue("CheckMode", new Mode("Self", true), new Mode("Others", false), new Mode("All", false));
    public ModeValue notifMode = new ModeValue("NotificationMode", new Mode("Notification", true), new Mode("Chat", false), new Mode("ClientSideChat", false));
    public BooleanValue reportSelf = new BooleanValue("ReportSelf", false, "Notify yourself if you are breaking someones hole bocks");
    public BooleanValue render = new BooleanValue("Render", true, "Render the hole blocks being mined");

    public HoleBreakNotifier() {
        super("HoleBreakNotifier", ModuleCategory.COMBAT, "Notifies you when someone is trying to break your hole blocks");
        addValue(mode, notifMode, reportSelf, render);
    }


    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.IN) {

            if (e.getPacket() instanceof SPacketBlockBreakAnim) {
                ChatUtils.normalMessage("Recieved SPacketBlockBreakAnim");
                SPacketBlockBreakAnim sPacketBlockBreakAnim = (SPacketBlockBreakAnim) e.getPacket();
                if (sPacketBlockBreakAnim.getBreakerId() == mc.player.getEntityId() && !reportSelf.getValue()) return;
                ChatUtils.normalMessage("Passed SelfReport Check");
                if ((mode.getMode("Self").isToggled()) && isPlayersHoleBlock(mc.player, sPacketBlockBreakAnim.getPosition())) {
                    ChatUtils.normalMessage("Passed Hole Check for SelfMode");
                    if (entries.stream().anyMatch(n -> n.pos.equals(sPacketBlockBreakAnim.getPosition()))) return;
                    entries.add(new BreakEntry(System.currentTimeMillis(), sPacketBlockBreakAnim.getPosition()));
                    ChatUtils.normalMessage("Passed Entry Check for Self Mode");
                    if (notifMode.getMode("Notification").isToggled()) {
                        new Notification("HoleBreakNotifier", Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName() + " is trying to break your hole blocks", 5000, Color.BLACK);
                    } else if (notifMode.getMode("Chat").isToggled()) {
                        mc.player.sendChatMessage(Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName() + " is trying to break my hole blocks");
                    } else if (notifMode.getMode("ClientSideChat").isToggled()) {
                        ChatUtils.normalMessage(Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName() + " is trying to break your hole blocks");
                    }
                }
                if (mode.getMode("All").isToggled()) {
                    mc.world.playerEntities.forEach(n -> {
                        if (entries.stream().anyMatch(p -> p.pos.equals(sPacketBlockBreakAnim.getPosition()))) return;
                        entries.add(new BreakEntry(System.currentTimeMillis(), sPacketBlockBreakAnim.getPosition()));
                        if (isPlayersHoleBlock(n, sPacketBlockBreakAnim.getPosition())) {
                            if (notifMode.getMode("Notification").isToggled()) {
                                new Notification("HoleBreakNotifier", n.getName() + "'s hole is being broken by " + Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName(), 5000, Color.BLACK);
                            } else if (notifMode.getMode("Chat").isToggled()) {
                                mc.player.sendChatMessage("Watch out " + n.getName() + " your hole is being broken by " + Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName() + "!");
                            } else if (notifMode.getMode("ClientSideChat").isToggled()) {
                                ChatUtils.normalMessage("Watch out " + n.getName() + "your hole is being broken by " + Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName() + "!");
                            }
                        }
                    });
                }
                if (mode.getMode("Others").isToggled()) {
                    mc.world.playerEntities.stream().filter(n -> n != mc.player).forEach(n -> {
                        if (entries.stream().anyMatch(p -> p.pos.equals(sPacketBlockBreakAnim.getPosition()))) return;
                        entries.add(new BreakEntry(System.currentTimeMillis(), sPacketBlockBreakAnim.getPosition()));
                        if (isPlayersHoleBlock(n, sPacketBlockBreakAnim.getPosition())) {
                            if (notifMode.getMode("Notification").isToggled()) {
                                new Notification("HoleBreakNotifier", n.getName() + "'s hole is being broken by " + Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName(), 5000, Color.BLACK);
                            } else if (notifMode.getMode("Chat").isToggled()) {
                                mc.player.sendChatMessage("Watch out " + n.getName() + "your hole is being broken by " + Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName() + "!");
                            } else if (notifMode.getMode("ClientSideChat").isToggled()) {
                                ChatUtils.normalMessage("Watch out " + n.getName() + "your hole is being broken by " + Objects.requireNonNull(mc.world.getEntityByID(sPacketBlockBreakAnim.getBreakerId())).getName() + "!");
                            }
                        }
                    });
                }
            }
        }
    });

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        ArrayList<BreakEntry> remove = new ArrayList<>();
        synchronized (entries) {
            Iterator it = entries.iterator();
            while (it.hasNext()) {
                BreakEntry entry = (BreakEntry) it.next();
                if (System.currentTimeMillis() - entry.tick > delay.getValue()) {
                    remove.add(entry);
                }
            }
        }
        remove.forEach(n -> {
            entries.remove(n);
        });

    }

    //TODO make this customizable when ColorPicker doesn't crash my game anymore
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {

        synchronized (entries) {
            Iterator it = entries.iterator();
            while (it.hasNext()) {
                BreakEntry entry = (BreakEntry) it.next();
                RenderUtils.drawBlockESP(entry.pos, 1, 1, 1, 1);
            }
        }
    }

    private boolean isPlayersHoleBlock(EntityPlayer player, BlockPos pos) {
        double posX = Math.floor(player.posX);
        double posZ = Math.floor(player.posZ);
        Block block = mc.world.getBlockState(pos).getBlock();
        if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
            if (pos.getY() != player.posY) return false;
            if (pos.getX() == posX + 1 || pos.getX() == posX - 1 || pos.getZ() == posZ + 1 || pos.getZ() == posZ - 1) {
                return true;
            }
        }
        return false;
    }

}

class BreakEntry {

    public BreakEntry(long tick, BlockPos pos) {
        this.pos = pos;
        this.tick = tick;
    }

    public long tick;
    public BlockPos pos;
}
