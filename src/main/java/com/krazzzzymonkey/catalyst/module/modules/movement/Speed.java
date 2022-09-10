package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.MotionEvent;
import com.krazzzzymonkey.catalyst.events.ReachEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.managers.TimerManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.world.Timer;
import com.krazzzzymonkey.catalyst.utils.MovementUtil;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

//TODO MORE SPEED MODULES
public class Speed extends Modules {

    private DoubleValue onGroundSpeedGroundSpeed;
    private DoubleValue onGroundSpeedAirSpeed;
    public ModeValue Mode;


    public Speed() {
        super("Speed", ModuleCategory.MOVEMENT, "Makes you BHop");
        this.onGroundSpeedGroundSpeed = new DoubleValue("GroundSpeed", 1.199d, 0.1d, 2d, "The speed setting for the OnGround mode");
        this.onGroundSpeedAirSpeed = new DoubleValue("AirSpeed", 1.199d, 0.1d, 2d, "The air speed setting for OnGround mode");
        Mode = new ModeValue("Mode", new Mode("Strafe", false),new Mode("StrafeStrict", true), new Mode("NCPBHop", false), new Mode("NCPBHopAlt", false), new Mode("OnGround", false), new Mode("LowHop", false), new Mode("YPort", false));
        this.addValue(Mode, onGroundSpeedGroundSpeed, onGroundSpeedAirSpeed);
    }

    int delay = 0;
    double defaultTimer;
    public static boolean move;
    public static boolean hop;
    public static Double prevY;
    public static float mul = 0;
    private static int entryID = -1;


    @Override
    public void onEnable() {
        entryID = TimerManager.addTimerMultiplier(1, 2, false);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        TimerManager.removeTimerMultiplier(entryID);
        super.onDisable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Mode.getMode("NCPBHop").isToggled()) {
            TimerManager.getMultiplier(entryID).setMultiplier(1.0865d);
            TimerManager.getMultiplier(entryID).setEnabled(true);
            if (mc.player == null) return;
            if (MovementUtil.isMoving()) {
                if (mc.player.onGround) {
                    mc.player.jump();
                    mc.player.moveForward = 0.0223f;
                }
                MovementUtil.strafe();
            } else {
                mc.player.motionZ = 0.0;
                mc.player.motionX = 0.0;
            }
        }

        if (Mode.getMode("NCPBHopAlt").isToggled()) {
            if (mc.player == null) return;
            if (MovementUtil.isMoving()) {
                if (mc.player.onGround) {
                    mc.player.jump();
                    mc.player.motionX *= 1.01;
                    mc.player.motionZ *= 1.01;
                    mc.player.moveForward = 0.0223f;
                }
                mc.player.motionY -= 0.00099999;
                MovementUtil.strafe();
            } else {
                mc.player.motionX = 0.0;
                mc.player.motionZ = 0.0;
            }
        }
        if (Mode.getMode("Strafe").isToggled()) {
            if (mc.player == null) return;
            TimerManager.getMultiplier(entryID).setMultiplier(1.17d);
            TimerManager.getMultiplier(entryID).setEnabled(true);
            delay++;
            if (delay >= 2) {
                this.a(0.405, 0.22f, 1.0064f );
                delay = 0;
            }
        }

        if (Mode.getMode("StrafeStrict").isToggled()) {
            if (mc.player == null) return;
            delay++;
            if (delay >= 2) {
                this.a(0.405, 0.22f, 1.0064f );
                delay = 0;
            }
        }
    });


    @EventHandler
    private final EventListener<MotionEvent.PRE> onMotion = new EventListener<>(event -> {

        if (Mode.getMode("OnGround").isToggled()) {

            TimerManager.getMultiplier(entryID).setEnabled(true);

            if (mc.player == null || !MovementUtil.isMoving())
                return;

            if (mc.player.fallDistance > 3.994)
                return;
            if (mc.player.isInWater() || mc.player.isOnLadder() || mc.player.collidedHorizontally)
                return;

            mc.player.posY -= 0.3993000090122223;
            mc.player.motionY = -1000.0;
            mc.player.cameraPitch = 0.3f;
            mc.player.distanceWalkedModified = 44.0f;
            TimerManager.getMultiplier(entryID).setMultiplier(onGroundSpeedAirSpeed.getValue());

            if (mc.player.onGround) {
                mc.player.posY += 0.3993000090122223;
                mc.player.motionY = 0.3993000090122223;
                mc.player.distanceWalkedOnStepModified = 44.0f;
                mc.player.motionX *= 1.590000033378601;
                mc.player.motionZ *= 1.590000033378601;
                mc.player.cameraPitch = 0.0f;
                TimerManager.getMultiplier(entryID).setMultiplier(onGroundSpeedGroundSpeed.getValue());
            }

        }

        if (Mode.getMode("YPort").isToggled()) {

            if (MovementUtil.isMoving()) {
                TimerManager.getMultiplier(entryID).setEnabled(true);
                TimerManager.getMultiplier(entryID).setMultiplier((1.0F + mul));
                mul += 0.01F;
                if (mul > 0.5F) {
                    mul = -0.1F;
                }
            }
            doYPortSpeed();
        }

        if (Mode.getMode("LowHop").isToggled()) {

            if (MovementUtil.isMoving()) {
                TimerManager.getMultiplier(entryID).setEnabled(true);
                if (mc.player.onGround) {
                    TimerManager.getMultiplier(entryID).setMultiplier(1.09d);
                    mc.player.onGround = false;
                    mc.player.motionY = 0.2F;

                } else {
                    mc.player.motionY = Math.max(mc.player.motionY, -0.08);
                    TimerManager.getMultiplier(entryID).setMultiplier(1.5d);
                }
            } else {
                TimerManager.getMultiplier(entryID).setEnabled(false);

            }
        }
    });

    public static void doYPortSpeed() {
        if ((hop) && (mc.player.posY >= prevY + 0.399994D)) {
            mc.player.motionY = -10000.0D;
            mc.player.posY = prevY;
            hop = false;
        }
        if ((mc.player.moveForward != 0.0F) && (!mc.player.collidedHorizontally)) {
            if ((mc.player.moveForward == 0.0F) && (mc.player.moveStrafing == 0.0F)) {
                mc.player.motionX = 0.0D;
                mc.player.motionZ = 0.0D;
                if (mc.player.collidedVertically) {
                    mc.player.jump();
                    move = true;
                }
                if ((move) && (mc.player.collidedVertically)) {
                    move = false;
                }
            }
            if (mc.player.collidedVertically) {
                mc.player.motionX *= 0.5079D;
                mc.player.motionZ *= 0.5079D;
                doMiniHop();
            }
            if ((hop) && (!move) && (mc.player.posY >= prevY + 0.399994D)) {
                mc.player.motionY = -100.0D;
                mc.player.posY = prevY;
                hop = false;
            }
        }
    }

    public static void doMiniHop() {
        hop = true;
        prevY = mc.player.posY;
        mc.player.jump();
    }


    private void a(final double motionY, final float n, final double n2) {
        final boolean v2 = (Minecraft.getMinecraft().player.moveForward != 0.0f) || Minecraft.getMinecraft().player.moveForward > 0.0f;
        if (v2 || Minecraft.getMinecraft().player.moveStrafing != 0.0f) {
            Minecraft.getMinecraft().player.setSprinting(true);
            if (Minecraft.getMinecraft().player.onGround) {
                Minecraft.getMinecraft().player.motionY = motionY;
                final float a = ny();
                final EntityPlayerSP player = Minecraft.getMinecraft().player;
                player.motionX -= MathHelper.sin(a) * n;
                final EntityPlayerSP player2 = Minecraft.getMinecraft().player;
                player2.motionZ += MathHelper.cos(a) * n;
            } else {
                final double sqrt = Math.sqrt(Minecraft.getMinecraft().player.motionX * Minecraft.getMinecraft().player.motionX + Minecraft.getMinecraft().player.motionZ * Minecraft.getMinecraft().player.motionZ);
                final double n3 = ny();
                Minecraft.getMinecraft().player.motionX = -Math.sin(n3) * n2 * sqrt;
                Minecraft.getMinecraft().player.motionZ = Math.cos(n3) * n2 * sqrt;
            }
        }
    }

    public void a(final double n, final double n2, final EntityPlayerSP entityPlayerSP) {
        final MovementInput movementInput = Minecraft.getMinecraft().player.movementInput;
        float moveForward = movementInput.moveForward;
        float moveStrafe = movementInput.moveStrafe;
        float rotationYaw = Minecraft.getMinecraft().player.rotationYaw;
        if (moveForward != 0.0) {
            if (moveStrafe > 0.0) {
                rotationYaw += ((moveForward > 0.0) ? -45 : 45);
            } else if (moveStrafe < 0.0) {
                rotationYaw += ((moveForward > 0.0) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0) {
                moveForward = -1.0f;
            }
        }
        if (moveStrafe > 0.0) {
            moveStrafe = 1.0f;
        } else if (moveStrafe < 0.0) {
            moveStrafe = -1.0f;
        }
        entityPlayerSP.motionX = n + (moveForward * 0.2 * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + moveStrafe * 0.2 * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
        entityPlayerSP.motionZ = n2 + (moveForward * 0.2 * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - moveStrafe * 0.2 * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
    }

    public static float ny() {
        float v = Minecraft.getMinecraft().player.rotationYaw;
        if (Minecraft.getMinecraft().player.moveForward < 0.0f) {
            v += 180.0f;
        }
        float v2 = 1.0f;
        if (Minecraft.getMinecraft().player.moveForward < 0.0f) {
            v2 = -0.5f;
        } else if (Minecraft.getMinecraft().player.moveForward > 0.0f) {
            v2 = 0.5f;
        }
        if (Minecraft.getMinecraft().player.moveStrafing > 0.0f) {
            v -= 90.0f * v2;
        }
        if (Minecraft.getMinecraft().player.moveStrafing < 0.0f) {
            v += 90.0f * v2;
        }
        v *= 0.017453292f;
        return v;
    }
}





