package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//TODO ADD SPIDER MODE
public class Step extends Modules {

    public ModeValue mode;
    public DoubleValue height;
    public float tempHeight;
    public int ticks = 0;

    public Step() {
        super("Step", ModuleCategory.MOVEMENT, "Allows you to walk up blocks greater than one");

        this.mode = new ModeValue("Mode",new Mode("NCP", true), new Mode("AAC", false), new Mode("Simple", false));
        height = new DoubleValue("Height", 2D, 0D, 10D, "The height you want to step");
        this.addValue(mode, height);
    }

    @Override
    public void onEnable() {
        ticks = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(mc.player == null)return;
        Wrapper.INSTANCE.player().stepHeight = 0.5f;
        super.onDisable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (mode.getMode("AAC").isToggled()) {
            EntityPlayerSP player = Wrapper.INSTANCE.player();
            if (player.collidedHorizontally) {
                switch (ticks) {
                    case 0:
                        if (player.onGround)
                            player.jump();
                        break;
                    case 7:
                        player.motionY = 0;
                        break;
                    case 8:
                        if (!player.onGround)
                            player.setPosition(player.posX, player.posY + 1, player.posZ);
                        break;
                }
                ticks++;
            } else {
                ticks = 0;
            }
        } else if (mode.getMode("NCP").isToggled()) {

            double[] dir = forward(0.1);
            boolean twofive = false;
            boolean two = false;
            boolean onefive = false;
            boolean one = false;
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.6, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.4, dir[1])).isEmpty()) {
                twofive = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.1, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.9, dir[1])).isEmpty()) {
                two = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.6, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.4, dir[1])).isEmpty()) {
                onefive = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.0, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 0.6, dir[1])).isEmpty()) {
                one = true;
            }
            if (mc.player.collidedHorizontally && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                if (one && this.height.getValue() >= 1.0) {
                    final double[] oneOffset = {0.42, 0.753};
                    for (double v : oneOffset) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ);
                    this.ticks = 1;
                }
                if (onefive && this.height.getValue() >= 1.5) {
                    final double[] oneFiveOffset = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
                    for (int i = 0; i < oneFiveOffset.length; i++) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + oneFiveOffset[i], mc.player.posZ, mc.player.onGround));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5, mc.player.posZ);
                    this.ticks = 1;
                }
                if (two && this.height.getValue() >= 2.0) {
                    final double[] twoOffset = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
                    for (int i = 0; i < twoOffset.length; i++) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + twoOffset[i], mc.player.posZ, mc.player.onGround));
                    }

                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
                    this.ticks = 2;
                }
                if (twofive && this.height.getValue() >= 2.5) {
                    final double[] twoFiveOffset = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
                    for (int i = 0; i < twoFiveOffset.length; i++) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + twoFiveOffset[i], mc.player.posZ, mc.player.onGround));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5, mc.player.posZ);
                    this.ticks = 2;
                }
            }
        }else if (mode.getMode("Simple").isToggled()) {

            Wrapper.INSTANCE.player().stepHeight = height.getValue().floatValue();

        }
    });

    public static double[] forward(final double speed) {
        float forward = Minecraft.getMinecraft().player.movementInput.moveForward;
        float side = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

}

