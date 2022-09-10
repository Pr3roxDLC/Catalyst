package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.TimerUtils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;

//TODO ADD 2B2T BYPASS
public class Criticals extends Modules {

    public ModeValue mode;
    TimerUtils timer;

    boolean cancelSomePackets;

    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT, "Makes all your hits do a critical amount of damage");
        this.mode = new ModeValue("Mode", new Mode("Packet", true), new Mode("Jump", false));
        this.addValue(mode);
        this.timer = new TimerUtils();

    }

    @EventHandler
    private final EventListener<PacketEvent> onPacketSend = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            Packet packet = e.getPacket();
            if (packet instanceof CPacketUseEntity) {
                if (Wrapper.INSTANCE.player().onGround) {
                    CPacketUseEntity attack = (CPacketUseEntity) packet;
                    if (attack.getAction() == Action.ATTACK) {
                        if (mode.getMode("Packet").isToggled()) {
                            if (Wrapper.INSTANCE.player().collidedVertically && this.timer.isDelay(500)) {
                                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(Wrapper.INSTANCE.player().posX, Wrapper.INSTANCE.player().posY + 0.0627, Wrapper.INSTANCE.player().posZ, false));
                                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(Wrapper.INSTANCE.player().posX, Wrapper.INSTANCE.player().posY, Wrapper.INSTANCE.player().posZ, false));
                                Entity entity = attack.getEntityFromWorld(Wrapper.INSTANCE.world());
                                if (entity != null) {
                                    Wrapper.INSTANCE.player().onCriticalHit(entity);
                                }
                                this.timer.setLastMS();
                                this.cancelSomePackets = true;
                            }
                        } else if (mode.getMode("Jump").isToggled()) {
                            if (canJump()) {
                                Wrapper.INSTANCE.player().jump();
                            }
                        }
                    }
                } else if (mode.getMode("Packet").isToggled() && packet instanceof CPacketPlayer) {
                    if (cancelSomePackets) {
                        cancelSomePackets = false;
                        e.setCancelled(true);
                    }
                }

            }
        }
    });

    boolean canJump() {
        if (Wrapper.INSTANCE.player().isOnLadder()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isInWater()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isInLava()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isSneaking()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isRiding()) {
            return false;
        }
        return !Wrapper.INSTANCE.player().isPotionActive(MobEffects.BLINDNESS);
    }
}
