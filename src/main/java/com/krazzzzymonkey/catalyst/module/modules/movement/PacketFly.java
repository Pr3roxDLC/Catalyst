package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
//TODO REWRITE
public class PacketFly extends Modules {

    private ModeValue mode;

    public PacketFly() {
        super("PacketFly", ModuleCategory.MOVEMENT, "Allows you to fly with packets");
        mode = new ModeValue("Mode", new Mode("Infinite", true), new Mode("Bypass", false), new Mode("Old", false));
        this.addValue(mode);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)return;

        if (mode.getMode("Infinite").isToggled()) {
            float forward = 0.0f;
            float strafe = 0.0f;
            double speed = 2.7999999999999999999999;
            float var5 = MathHelper.sin(Wrapper.INSTANCE.player().rotationYaw * 3.1415927f / 180.0f);
            float var6 = MathHelper.cos(Wrapper.INSTANCE.player().rotationYaw * 3.1415927f / 180.0f);
            if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() && !Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown() && !Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown() && !Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
                // forward
                forward += 0.1f;
            } else if (!Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
                // backwards
                forward -= 0.1f;
            } else if (!Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
                // left
                strafe += 0.1f;
            }
            if (!Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
                // right
                strafe -= 0.1f;
            } else if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
                // forwards and left
                forward += 0.0624f;
                strafe += 0.0624f;
            } else if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
                // forwards and right
                forward += 0.0624f;
                strafe -= 0.0624f;
            } else if (!Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
                // backwards and left
                forward -= 0.0624f;
                strafe += 0.0624f;
            } else if (!Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()
                    && !Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
                    && Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
                // backwards and right
                forward -= 0.0624f;
                strafe -= 0.0624f;
            }

            double motionX = (strafe * var6 - forward * var5) * speed;
            double motionZ = (forward * var6 + strafe * var5) * speed;
            double motionY = 0;

            motionY = (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() ? 0.0624 : 0)
                    - (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown() ? 0.0624 : 0);

            setCurrentMS();

            Wrapper.INSTANCE.player().motionY = 0;

            if (hasDelayRun(500) && !Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {

                Wrapper.INSTANCE.player().connection.sendPacket(new CPacketPlayer.PositionRotation(Wrapper.INSTANCE.player().posX,
                        Wrapper.INSTANCE.player().posY - 0.0624, Wrapper.INSTANCE.player().posZ, Wrapper.INSTANCE.player().rotationYaw,
                        Wrapper.INSTANCE.player().rotationPitch, false));
                Wrapper.INSTANCE.player().connection.sendPacket(new CPacketPlayer.PositionRotation(Wrapper.INSTANCE.player().posX,
                        Wrapper.INSTANCE.player().posY - 999.0D, Wrapper.INSTANCE.player().posZ, Wrapper.INSTANCE.player().rotationYaw,
                        Wrapper.INSTANCE.player().rotationPitch, true));
                setLastMS();

                return;
            }

            if ((Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() || Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()
                    || Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()
                    || Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()
                    || Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()
                    || Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) && !hasDelayRun(1800)
                    || Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
                Wrapper.INSTANCE.player().connection
                        .sendPacket(new CPacketPlayer.PositionRotation(Wrapper.INSTANCE.player().posX + motionX,
                                Wrapper.INSTANCE.player().posY + motionY, Wrapper.INSTANCE.player().posZ + motionZ,
                                Wrapper.INSTANCE.player().rotationYaw, Wrapper.INSTANCE.player().rotationPitch, false));
                Wrapper.INSTANCE.player().connection
                        .sendPacket(new CPacketPlayer.PositionRotation(Wrapper.INSTANCE.player().posX + motionX,
                                Wrapper.INSTANCE.player().posY - 999.0D, Wrapper.INSTANCE.player().posZ + motionZ,
                                Wrapper.INSTANCE.player().rotationYaw, Wrapper.INSTANCE.player().rotationPitch, true));
            }
        }
        if (mode.getMode("Old").isToggled()) {

            // Wrapper.INSTANCE.player().motionX = 0;
            // Wrapper.INSTANCE.player().motionY = 0;
            // Wrapper.INSTANCE.player().motionZ = 0;
            Wrapper.INSTANCE.player().connection.sendPacket(new CPacketPlayer.PositionRotation(
                    Wrapper.INSTANCE.player().posX + Wrapper.INSTANCE.player().motionX
                            + (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() ? 0.0624 : 0)
                            - (Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown() ? 0.0624 : 0),
                    Wrapper.INSTANCE.player().posY + (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() ? 0.0624 : 0)
                            - (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown() ? 0.0624 : 0),
                    Wrapper.INSTANCE.player().posZ + Wrapper.INSTANCE.player().motionZ
                            + (Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown() ? 0.0624 : 0)
                            - (Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown() ? 0.0624 : 0),
                    Wrapper.INSTANCE.player().rotationYaw, Wrapper.INSTANCE.player().rotationPitch, false));
            Wrapper.INSTANCE.player().connection.sendPacket(
                    new CPacketPlayer.PositionRotation(Wrapper.INSTANCE.player().posX + Wrapper.INSTANCE.player().motionX,
                            Wrapper.INSTANCE.player().posY - 42069, Wrapper.INSTANCE.player().posZ + Wrapper.INSTANCE.player().motionZ,
                            Wrapper.INSTANCE.player().rotationYaw, Wrapper.INSTANCE.player().rotationPitch, true));
        }
        if (mode.getMode("Bypass").isToggled()) {
            int angle;

            boolean forward = Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown();
            boolean left = Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown();
            boolean right = Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown();
            boolean back = Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown();

            if (!forward || !left || !right || !back) {
                Minecraft.getMinecraft().player.motionX = 0;
                Minecraft.getMinecraft().player.motionZ = 0;
            }

            if (left && right) angle = forward ? 0 : back ? 180 : -1;
            else if (forward && back) angle = left ? -90 : (right ? 90 : -1);
            else {
                angle = left ? -90 : (right ? 90 : 0);
                if (forward) angle /= 2;
                else if (back) angle = 180 - (angle / 2);
            }

            if (angle != -1 && (forward || left || right || back)) {
                float yaw = Minecraft.getMinecraft().player.rotationYaw + angle;
                Minecraft.getMinecraft().player.motionX = getRelativeX(yaw) * 0.2f;
                Minecraft.getMinecraft().player.motionZ = getRelativeZ(yaw) * 0.2f;
            }

            Minecraft.getMinecraft().player.motionY = 0;
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.PositionRotation(Minecraft.getMinecraft().player.posX + Minecraft.getMinecraft().player.motionX, Minecraft.getMinecraft().player.posY + (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() ? 0.0622 : 0) - (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown() ? 0.0622 : 0), Minecraft.getMinecraft().player.posZ + Minecraft.getMinecraft().player.motionZ, Minecraft.getMinecraft().player.rotationYaw, Minecraft.getMinecraft().player.rotationPitch, false));
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.PositionRotation(Minecraft.getMinecraft().player.posX + Minecraft.getMinecraft().player.motionX, Minecraft.getMinecraft().player.posY - 42069, Minecraft.getMinecraft().player.posZ + Minecraft.getMinecraft().player.motionZ, Minecraft.getMinecraft().player.rotationYaw, Minecraft.getMinecraft().player.rotationPitch, true));

        }
    });


    private long currentMS = 0L;
    private long lastMS = -1L;

    public void setCurrentMS() {
        currentMS = System.nanoTime() / 1000000;
    }

    public boolean hasDelayRun(long time) {
        return (currentMS - lastMS) >= time;
    }

    public void setLastMS() {
        lastMS = System.nanoTime() / 1000000;
    }

    public void reset() {
        currentMS = System.nanoTime() / 1000000;
    }

    public static double getRelativeX(float yaw) {
        return MathHelper.sin(-yaw * 0.017453292F);
    }

    public static double getRelativeZ(float yaw) {
        return MathHelper.cos(yaw * 0.017453292F);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if (mode.getMode("Infinite").isToggled()) {
            if (event.getSide() == PacketEvent.Side.IN) {

                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    SPacketPlayerPosLook posLook = (SPacketPlayerPosLook) event.getPacket();
                    if (mc.player != null && mc.player.rotationYaw != -180.0f
                        && mc.player.rotationPitch != 0.0f) {
                        posLook.yaw = mc.player.rotationYaw;
                        posLook.pitch = mc.player.rotationYaw;
                    }
                }
            }
        }
    });
}
