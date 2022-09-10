package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MovementUtil;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

public class AntiHunger extends Modules {
    private final BooleanValue spoofSprint = new BooleanValue("SpoofSprint", true, "Spoofs packets to the server saying you are sprinting");
    private final BooleanValue spoofGround = new BooleanValue("SpoofOnGround", true, "Spoofs movement packets to the server saying you are on the ground");

    public AntiHunger() {
        super("AntiHunger", ModuleCategory.PLAYER, "Prevents the player from loosing hunger");
        this.addValue(spoofGround, spoofSprint);
    }

    private boolean isOnGround = false;

    public void onEnable() {
        super.onEnable();
        if (spoofSprint.getValue() && mc.player != null) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    public void onDisable() {
        super.onDisable();
        if (spoofSprint.getValue() && mc.player != null && mc.player.isSprinting()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            if (e.getPacket() instanceof CPacketEntityAction) {
                CPacketEntityAction action = (CPacketEntityAction) e.getPacket();
                if (spoofSprint.getValue() && (action.getAction() == CPacketEntityAction.Action.START_SPRINTING || action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING)) {
                    e.setCancelled(true);
                }
            }

            if (e.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer player = (CPacketPlayer) e.getPacket();
                boolean ground = mc.player.onGround;
                if (spoofGround.getValue() && isOnGround && ground && player.getY(0.0) == (!MovementUtil.isMoving() ? 0.0 : mc.player.posY)) {
                    mc.player.onGround = false;
                }
                isOnGround = ground;
            }
        }
    });

}
