package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;


//TODO ADD A DEDICATED GUI FOR PACKETSELECTER
public class PacketCanceller extends Modules {
    public final BooleanValue animation = new BooleanValue("Animation", false, "Cancels CPacketAnimation packets");
    public final BooleanValue input = new BooleanValue("Input", false, "Cancels CPacketInput packets");
    public final BooleanValue entityAction = new BooleanValue("EntityAction", false, "Cancels CPacketEntityAction packets");
    public final BooleanValue position = new BooleanValue("Position", false, "Cancels Position packets");
    public final BooleanValue rotation = new BooleanValue("Rotation", false, "Cancels Rotation packets");
    public final BooleanValue positionRotation = new BooleanValue("PositionRotation", false, "Cancels PositionRotation packets");
    public final BooleanValue useEntity = new BooleanValue("UseEntity", false, "Cancels CPacketUseEntity packets");
    public final BooleanValue vehicleMove = new BooleanValue("VehicleMove", false, "Cancels CPacketVehicleMove packets");

    public PacketCanceller() {
        super("PacketCanceller", ModuleCategory.MISC, "Allows you to cancel certain packets from being sent to the server");
        addValue(animation, input, entityAction, position, rotation, positionRotation, useEntity, vehicleMove);
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        switch (event.getPacket().getClass().getSimpleName()) {
            case "CPacketAnimation":
                if (animation.getValue()) event.setCancelled(true);
                break;
            case "CPacketInput":
                if (input.getValue()) event.setCancelled(true);
                break;
            case "CPacketEntityAction":
                if (entityAction.getValue()) event.setCancelled(true);
                break;
            case "Position":
                if (position.getValue()) event.setCancelled(true);
                break;
            case "Rotation":
                if (rotation.getValue()) event.setCancelled(true);
                break;
            case "PositionRotation":
                if (positionRotation.getValue()) event.setCancelled(true);
                break;
            case "CPacketUseEntity":
                if (useEntity.getValue()) event.setCancelled(true);
                break;
            case "CPacketVehicleMove":
                if (vehicleMove.getValue()) event.setCancelled(true);
                break;
        }
    });
}
