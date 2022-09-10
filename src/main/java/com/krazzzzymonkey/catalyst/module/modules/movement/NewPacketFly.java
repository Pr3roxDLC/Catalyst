package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.events.PlayerMoveEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.managers.TimerManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.MovementUtil;
import com.krazzzzymonkey.catalyst.utils.TimeVec3D;
import com.krazzzzymonkey.catalyst.utils.Timer;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class NewPacketFly extends Modules {
    public NewPacketFly() {
        super("NewPacketFly", ModuleCategory.MOVEMENT, "Allows you to fly, but new");
        addValue(type, packetMode, phase, axisMode, antiKickMode, strict, bounds, slowDown, speed, factor, limit, restrict, jitter, boost, motion);
    }

    //FACTOR, SETBACK, FAST, SLOW, DESYNC
    private static ModeValue type = new ModeValue("Mode", new Mode("Fast", true), new Mode("Normal", false), new Mode("Reset", false), new Mode("Factor", false), new Mode("Desynced", false));
    //UP, PRESERVE, DOWN, LIMITJITTER, BYPASS, OBSCURE
    private static ModeValue packetMode = new ModeValue("PacketMode", new Mode("Up", true), new Mode("Keep", false), new Mode("Down", false), new Mode("Jitter", false), new Mode("Bypass", false), new Mode("Random", false)); // down seems to work best especially on 9b/0b
    private static BooleanValue strict = new BooleanValue("Strict", true, "Use Bounds for Packet Limit");
    private static BooleanValue bounds = new BooleanValue("Bounds", true, "");
    //NONE, VANILLA, NCP
    private static ModeValue phase = new ModeValue("Phase", new Mode("Disable", false), new Mode("Vanilla", false), new Mode("NoCheatPlus", true));
    //Convert this to a mode value
    private static ModeValue axisMode = new ModeValue("AxisMode", new Mode("Single", true), new Mode("Multi", false));
    //private static Setting<Boolean> multiAxis = new Setting<>("MultiAxis", false);

    //Invert
    private static BooleanValue slowDown = new BooleanValue("SlowDown", false, "Slows you down when phasing through blocks");
    //private static BooleanValue noPhaseSlow = new Setting<>("NoPhaseSlow", false);

    private static DoubleValue speed = new DoubleValue("FlightSpeed", 1f, 0.1f, 2f, "");
    //private static Setting<Float> speed = new Setting<>("Speed", 1f, 2f, 0.1f, 0.1f);

    private static DoubleValue factor = new DoubleValue("Factor", 1f, 1f, 10f, "");
    //private static Setting<Float> factor = new Setting<>("Factor", 1F, 10F, 1F, 0.1F).withVisibility(() -> type.getValue() == Type.FACTOR || type.getValue() == Type.DESYNC);

    //NONE, NORMAL, LIMITED, STRICT
    private static ModeValue antiKickMode = new ModeValue("AntiKickMode", new Mode("None", true), new Mode("Basic", false), new Mode("Limit", false), new Mode("Stricter", false));
    //private static Setting<AntiKick> antiKickMode = new Setting<>("AntiKick", AntiKick.NORMAL);

    //NONE, STRONG, STRICT
    private static ModeValue limit = new ModeValue("LimitMode", new Mode("None", true), new Mode("Basic", false), new Mode("Strict", false));
    //private static Setting<Limit> limit = new Setting<>("Limit", Limit.NONE);

    private static BooleanValue restrict = new BooleanValue("Restrict", false, "");
    //private static Setting<Boolean> constrict = new Setting<>("Constrict", false);

    private static BooleanValue jitter = new BooleanValue("Jitter", false, "Send Random Posistion Packets");
    //private static Setting<Boolean> jitter = new Setting<>("Jitter", false);

    private static BooleanValue boost = new BooleanValue("Boost", false, "");
    //private static Setting<Boolean> boost = new Setting<>("Boost", false);

    //private static Setting<SubBind> facrotize = new Setting<>("Snap", new SubBind(Keyboard.KEY_NONE)).withVisibility(() -> type.getValue() == Type.FACTOR);

    private static DoubleValue motion = new DoubleValue("FactorDistance", 5f, 1f, 20f, "");
    //private static Setting<Float> motion = new Setting<>("Distance", 5F, 20F, 1F, 0.1F).withVisibility(() -> type.getValue() == Type.FACTOR);

    private int teleportId;

    private CPacketPlayer.Position startingOutOfBoundsPos;

    private ArrayList<CPacketPlayer> packets = new ArrayList<>();
    private Map<Integer, TimeVec3D> posLooks = new ConcurrentHashMap<>();

    private int antiKickTicks = 0;
    private int vDelay = 0;
    private int hDelay = 0;

    private boolean limitStrict = false;
    private int limitTicks = 0;
    private int jitterTicks = 0;

    private boolean oddJitter = false;

    double speedX = 0;
    double speedY = 0;
    double speedZ = 0;

    private float postYaw = -400F;
    private float postPitch = -400F;

    private int factorCounter = 0;

    private Timer intervalTimer = new Timer();

    private static final Random random = new Random();

    private static int entryID = -1;

    public enum Limit {
        NONE, STRONG, STRICT
    }

    public enum Mode2 {
        UP, PRESERVE, DOWN, LIMITJITTER, BYPASS, OBSCURE
    }

    public enum Type {
        FACTOR, SETBACK, FAST, SLOW, DESYNC
    }

    public enum Phase {
        NONE, VANILLA, NCP
    }

    private enum AntiKick {
        NONE, NORMAL, LIMITED, STRICT
    }


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        // Prevents getting kicked from messing up your game
        if (mc.currentScreen instanceof GuiDisconnected || mc.currentScreen instanceof GuiMainMenu || mc.currentScreen instanceof GuiMultiplayer ||
            mc.currentScreen instanceof GuiDownloadTerrain) {
            this.toggle();
        }

        if (boost.getValue()) {
            TimerManager.getMultiplier(entryID).setMultiplier(1.088d);
        } else {
            TimerManager.getMultiplier(entryID).setMultiplier(1);
        }
    });

    @EventHandler
    private final EventListener<ClientTickEvent> onPlayerUpdate = new EventListener<>(e -> { // PlayerUpdate works way better than most other events
        //Retard check
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }

        if (mc.player.ticksExisted % 20 == 0) {
            cleanPosLooks();
        }

        //setExtraInfo(type.getValue().name());

        mc.player.setVelocity(0.0D, 0.0D, 0.0D);

        if (teleportId <= 0 && !type.getMode("Reset").isToggled()) {
            // sending this without any other packets will probs cause server to send SPacketPlayerPosLook to fix our pos
            startingOutOfBoundsPos = new CPacketPlayer.Position(randomHorizontal(), 1, randomHorizontal(), mc.player.onGround);
            packets.add(startingOutOfBoundsPos);
            mc.player.connection.sendPacket(startingOutOfBoundsPos);
            return;
        }

        boolean phasing = checkCollisionBox();

        speedX = 0;
        speedY = 0;
        speedZ = 0;

        if (mc.gameSettings.keyBindJump.isKeyDown() && (hDelay < 1 || (axisMode.getMode("Mutli").isToggled() && phasing))) {
            if (mc.player.ticksExisted % (type.getMode("Reset").isToggled() || type.getMode("Normal").isToggled() || limit.getMode("Strict").isToggled() ? 10 : 20) == 0) {
                speedY = (!antiKickMode.getMode("None").isToggled()) ? -0.032 : 0.062;
            } else {
                speedY = 0.062;
            }
            antiKickTicks = 0;
            vDelay = 5;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown() && (hDelay < 1 || (axisMode.getMode("Multi").isToggled() && phasing))) {
            speedY = -0.062;
            antiKickTicks = 0;
            vDelay = 5;
        }

        if ((axisMode.getMode("Multi").isToggled() && phasing) || !(mc.gameSettings.keyBindSneak.isKeyDown() && mc.gameSettings.keyBindJump.isKeyDown())) {
            if (MovementUtil.isMoving()) {

                double[] dir = MovementUtil.directionSpeed((phasing && phase.getMode("NCP").isToggled() ? (!slowDown.getValue() ? (axisMode.getMode("Multi").isToggled() ? 0.0465 : 0.062) : 0.031) : 0.26) * speed.getValue());
                if ((dir[0] != 0 || dir[1] != 0) && (vDelay < 1 || (axisMode.getMode("Multi").isToggled() && phasing))) {
                    speedX = dir[0];
                    speedZ = dir[1];
                    hDelay = 5;
                }
            }
            // WE CANNOT DO ANTIKICK AFTER FLYING UP OR DOWN!!! THIS CAN MESS UP SO MUCH STUFF
            if (antiKickMode.getMode("None").isToggled() && (limit.getMode("None").isToggled() || limitTicks != 0)) {
                if (antiKickTicks < (packetMode.getMode("Bypass").isToggled() && !bounds.getValue() ? 1 : 3)) {
                    antiKickTicks++;
                } else {
                    antiKickTicks = 0;
                    if (!antiKickMode.getMode("Limit").isToggled() || !phasing) {
                        speedY = antiKickMode.getMode("Stricter").isToggled() ? -0.08 : -0.04;
                    }
                }
            }
        }

        if (phasing) {
            if (phase.getMode("NoCheatPlus").isToggled() && (double) mc.player.moveForward != 0.0 || (double) mc.player.moveStrafing != 0.0 && speedY != 0) {
                speedY /= 2.5;
            }
        }

        if (!limit.getMode("None").isToggled()) {
            if (limitTicks == 0) {
                speedX = 0;
                speedY = 0;
                speedZ = 0;
            } else if (limitTicks == 2 && jitter.getValue()) {
                if (oddJitter) {
                    speedX = 0;
                    speedY = 0;
                    speedZ = 0;
                }
                oddJitter = !oddJitter;
            }
        } else if (jitter.getValue() && jitterTicks == 7) {
            speedX = 0;
            speedY = 0;
            speedZ = 0;
        }

        // switch (type.getValue()) {
        if (type.getMode("Fast").isToggled()) {
            mc.player.setVelocity(speedX, speedY, speedZ);
            sendPackets(speedX, speedY, speedZ, packetMode.getActiveMode(), true, false);
        }
        if (type.getMode("Normal").isToggled()) {
            sendPackets(speedX, speedY, speedZ, packetMode.getActiveMode(), true, false);
        }
        if (type.getMode("Reset").isToggled()) {
            mc.player.setVelocity(speedX, speedY, speedZ);
            sendPackets(speedX, speedY, speedZ, packetMode.getActiveMode(), false, false);
        }
        if (type.getMode("Factor").isToggled() || type.getMode("Desynced").isToggled()) {
            float rawFactor = factor.getValue().floatValue();

//                if (PlayerUtils.isKeyDown(facrotize.getValue().getKeyCode()) && intervalTimer.hasPassed(3500)) {
//                    intervalTimer.reset();
//                    rawFactor = motion.getValue();
//                }
            int factorInt = (int) Math.floor(rawFactor);
            factorCounter++;
            if (factorCounter > (int) (20D / ((rawFactor - (double) factorInt) * 20D))) {
                factorInt += 1;
                factorCounter = 0;
            }
            for (int i = 1; i <= factorInt; ++i) {
                mc.player.setVelocity(speedX * i, speedY * i, speedZ * i);
                sendPackets(speedX * i, speedY * i, speedZ * i, packetMode.getValue(), true, false);
            }
            speedX = mc.player.motionX;
            speedY = mc.player.motionY;
            speedZ = mc.player.motionZ;
        }
        vDelay--;
        hDelay--;

        if (restrict.getValue() && (limit.getMode("None").isToggled() || limitTicks > 1)) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
        }

        limitTicks++;
        jitterTicks++;

        if (limitTicks > ((limit.getMode("Strict").isToggled()) ? (limitStrict ? 1 : 2) : 3)) {
            limitTicks = 0;
            limitStrict = !limitStrict;
        }

        if (jitterTicks > 7) {
            jitterTicks = 0;
        }
    });

    private void sendPackets(double x, double y, double z, Mode mode, boolean sendConfirmTeleport, boolean sendExtraCT) {
        Vec3d nextPos = new Vec3d(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
        Vec3d bounds = getBoundsVec(x, y, z, mode);

        CPacketPlayer nextPosPacket = new CPacketPlayer.Position(nextPos.x, nextPos.y, nextPos.z, mc.player.onGround);
        packets.add(nextPosPacket);
        mc.player.connection.sendPacket(nextPosPacket);

        if (!limit.getMode("None").isToggled() && limitTicks == 0) return;

        CPacketPlayer boundsPacket = new CPacketPlayer.Position(bounds.x, bounds.y, bounds.z, mc.player.onGround);
        packets.add(boundsPacket);
        mc.player.connection.sendPacket(boundsPacket);

        if (sendConfirmTeleport) {
            teleportId++;

            if (sendExtraCT) {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportId - 1));
            }

            mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportId));

            posLooks.put(teleportId, new TimeVec3D(nextPos.x, nextPos.y, nextPos.z, System.currentTimeMillis()));

            if (sendExtraCT) {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportId + 1));
            }
        }

        /*
        if (type.getValue() != Type.FACTOR && type.getValue() != Type.NOJITTER && packetMode.getValue() != Mode.BYPASS) {
            CPacketPlayer currentPos = new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false);
            packets.add(currentPos);
            mc.player.connection.sendPacket(currentPos);
        }
         */
    }

    private Vec3d getBoundsVec(double x, double y, double z, Mode mode) {
        //  switch (mode) {
        if (mode.getName().equals("Up")) {
            return new Vec3d(mc.player.posX + x, bounds.getValue() ? (strict.getValue() ? 255 : 256) : mc.player.posY + 420, mc.player.posZ + z);
        } else if (mode.getName().equals("Keep")) {
            return new Vec3d(bounds.getValue() ? mc.player.posX + randomHorizontal() : randomHorizontal(), strict.getValue() ? (Math.max(mc.player.posY, 2D)) : mc.player.posY, bounds.getValue() ? mc.player.posZ + randomHorizontal() : randomHorizontal());
        } else if (mode.getName().equals("Jitter")) {
            return new Vec3d(mc.player.posX + (strict.getValue() ? x : randomLimitedHorizontal()), mc.player.posY + randomLimitedVertical(), mc.player.posZ + (strict.getValue() ? z : randomLimitedHorizontal()));
        } else if (mode.getName().equals("Bypass")) {
            if (bounds.getValue()) {
                double rawY = y * 510;
                return new Vec3d(mc.player.posX + x, mc.player.posY + ((rawY > ((mc.player.dimension == -1) ? 127 : 255)) ? -rawY : (rawY < 1) ? -rawY : rawY), mc.player.posZ + z);
            } else {
                return new Vec3d(mc.player.posX + (x == 0D ? (random.nextBoolean() ? -10 : 10) : x * 38), mc.player.posY + y, mc.player.posX + (z == 0D ? (random.nextBoolean() ? -10 : 10) : z * 38));
            }
        } else if (mode.getName().equals("Random")) {
            return new Vec3d(mc.player.posX + randomHorizontal(), Math.max(1.5D, Math.min(mc.player.posY + y, 253.5D)), mc.player.posZ + randomHorizontal());
        } else {
            return new Vec3d(mc.player.posX + x, bounds.getValue() ? (strict.getValue() ? 1 : 0) : mc.player.posY - 1337, mc.player.posZ + z);
        }
        // }
    }

    public static double randomHorizontal() {
        int randomValue = random.nextInt(bounds.getValue() ? 80 : (packetMode.getMode("Random").isToggled() ? (mc.player.ticksExisted % 2 == 0 ? 480 : 100) : 29000000)) + (bounds.getValue() ? 5 : 500);
        if (random.nextBoolean()) {
            return randomValue;
        }
        return -randomValue;
    }

    public static double randomLimitedVertical() {
        int randomValue = random.nextInt(22);
        randomValue += 70;
        if (random.nextBoolean()) {
            return randomValue;
        }
        return -randomValue;
    }

    public static double randomLimitedHorizontal() {
        int randomValue = random.nextInt(10);
        if (random.nextBoolean()) {
            return randomValue;
        }
        return -randomValue;
    }

    private void cleanPosLooks() {
        posLooks.forEach((tp, timeVec3d) -> {
            if (System.currentTimeMillis() - timeVec3d.getTime() > TimeUnit.SECONDS.toMillis(30L)) {
                posLooks.remove(tp);
            }
        });
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }
        packets.clear();
        posLooks.clear();
        teleportId = 0;
        vDelay = 0;
        hDelay = 0;
        postYaw = -400F;
        postPitch = -400F;
        antiKickTicks = 0;
        limitTicks = 0;
        jitterTicks = 0;
        speedX = 0;
        speedY = 0;
        speedZ = 0;
        oddJitter = false;
        startingOutOfBoundsPos = null;
        startingOutOfBoundsPos = new CPacketPlayer.Position(randomHorizontal(), 1, randomHorizontal(), mc.player.onGround);
        packets.add(startingOutOfBoundsPos);
        mc.player.connection.sendPacket(startingOutOfBoundsPos);
        entryID = TimerManager.addTimerMultiplier(1, 3);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.setVelocity(0, 0, 0);
        }
        intervalTimer.reset();
        TimerManager.removeTimerMultiplier(entryID);
        super.onDisable();
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(event -> {
        if(event.getSide() == PacketEvent.Side.OUT)return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            if (!(mc.currentScreen instanceof GuiDownloadTerrain)) {
                SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                if (mc.player.isEntityAlive()) {
                    if (this.teleportId <= 0) {
                        this.teleportId = ((SPacketPlayerPosLook) event.getPacket()).getTeleportId();
                    } else {
                        if (mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false) &&
                            !type.getMode("Reset").isToggled()) {
                            if (type.getMode("Desynced").isToggled()) {
                                posLooks.remove(packet.getTeleportId());
                                event.setCancelled(true);
                                if (type.getMode("Normal").isToggled()) {
                                    mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
                                }
                                return;
                            } else if (posLooks.containsKey(packet.getTeleportId())) {
                                TimeVec3D vec = posLooks.get(packet.getTeleportId());
                                if (vec.x == packet.getX() && vec.y == packet.getY() && vec.z == packet.getZ()) {
                                    posLooks.remove(packet.getTeleportId());
                                    event.setCancelled(true);
                                    if (type.getMode("Normal").isToggled()) {
                                        mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
                packet.yaw = mc.player.rotationYaw;
                packet.pitch = mc.player.rotationPitch;
                packet.getFlags().remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
                packet.getFlags().remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
                teleportId = packet.getTeleportId();
            } else {
                teleportId = 0;
            }
        }

    });

    @EventHandler
    private final EventListener<PlayerMoveEvent> onPlayerMove = new EventListener<>(event -> {
        if (!type.getMode("Reset").isToggled() && teleportId <= 0) {
            return;
        }

        if (type.getMode("Normal").isToggled()) {
            event.x = (float) speedX;
            event.y = (float) speedY;
            event.z = (float) speedZ;
        }

        if (!phase.getMode("None").isToggled() && phase.getMode("Vanilla").isToggled()|| checkCollisionBox()) {
            mc.player.noClip = true;
        }
    });

    private boolean checkCollisionBox() {
        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(0.0, 0.0, 0.0)).isEmpty()) {
            return true;
        }
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, 2.0, 0.0).contract(0.0, 1.99, 0.0)).isEmpty();
    }

    @EventHandler
    private final EventListener<PacketEvent> onPacketSend = new EventListener<>(event -> {
        if(event.getSide() == PacketEvent.Side.IN)return;
        if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            if (this.packets.contains(packet)) {
                this.packets.remove(packet);
                return;
            }
            event.setCancelled(true);
        }
    });

//    @Subscriber
//    public void onBlockPushOut(BlockPushOutEvent event) {
//        event.setCancelled(true);
//    }

}
