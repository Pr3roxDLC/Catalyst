package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

//TODO ANTI STUCK(DISABLE SPEED IF ABT TO COLLIDE)
public class EntitySpeed extends Modules {

    private DoubleValue speed;

    public EntitySpeed() {
        super("EntitySpeed", ModuleCategory.MOVEMENT, "Allows you to go faster on entities");
        this.speed = new DoubleValue("EntitySpeed", 1D, 0D, 5D, "Sets the speed of the entity" );
        this.addValue(speed);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        if (Wrapper.INSTANCE.player().getRidingEntity() != null) {
            MovementInput movementInput = Wrapper.INSTANCE.player().movementInput;
            double forward = movementInput.moveForward;
            double strafe = movementInput.moveStrafe;
            float yaw = Wrapper.INSTANCE.player().rotationYaw;
            if ((forward == 0.0D) && (strafe == 0.0D)) {
                Wrapper.INSTANCE.player().getRidingEntity().motionX = 0.0D;
                Wrapper.INSTANCE.player().getRidingEntity().motionZ = 0.0D;
            } else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        yaw += (forward > 0.0D ? -45 : 45);
                    } else if (strafe < 0.0D) {
                        yaw += (forward > 0.0D ? 45 : -45);
                    }
                    strafe = 0.0D;
                    if (forward > 0.0D) {
                        forward = 1.0D;
                    } else if (forward < 0.0D) {
                        forward = -1.0D;
                    }
                }

                double cos = Math.cos(Math.toRadians(yaw + 90.0F));
                double sin = Math.sin(Math.toRadians(yaw + 90.0F));

                Wrapper.INSTANCE.player().getRidingEntity().motionX = (forward * speed.getValue() * cos + strafe * speed.getValue() * sin);
                Wrapper.INSTANCE.player().getRidingEntity().motionZ = (forward * speed.getValue() * sin - strafe * speed.getValue() * cos);

                if (Wrapper.INSTANCE.player().getRidingEntity() instanceof EntityMinecart) {
                    Wrapper.INSTANCE.player().getRidingEntity().setVelocity(forward * speed.getValue() * cos + strafe * speed.getValue() * sin,
                            Wrapper.INSTANCE.player().getRidingEntity().motionY,
                            forward * speed.getValue() * sin - strafe * speed.getValue() * cos);
                }

            }
        }
    });
}
