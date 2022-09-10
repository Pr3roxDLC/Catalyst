package com.krazzzzymonkey.catalyst.module.modules.combat;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.managers.FriendManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.EntityUtils;
import com.krazzzzymonkey.catalyst.utils.TimerUtils;
import com.krazzzzymonkey.catalyst.utils.Utils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;


//TODO FIX IT, MAKE IT BETTER
public class KillAura extends Modules {

    public BooleanValue players;
    public BooleanValue passiveMobs;
    public BooleanValue hostileMobs;
    public BooleanValue neutralMobs;
    public BooleanValue invisibles;
    public BooleanValue enemies;
    public ModeValue priority;

    public BooleanValue walls;
    public BooleanValue autoDelay;

    public DoubleValue packetRange;
    public DoubleValue range;

    public IntegerValue tickDelay;
    public IntegerValue fov;
    public TimerUtils timer;
    public static EntityLivingBase target;
    public static float[] facingCam = null;

    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT, "Automatically attacks entities for you");
        players = new BooleanValue("Players", true, "Attack players");
        passiveMobs = new BooleanValue("PassiveMobs", false, "Attack passive mobs");
        hostileMobs = new BooleanValue("HostileMobs", false, "Attack hostile mobs");
        neutralMobs = new BooleanValue("NeutralMobs", false, "Attack neutral mobs like enderman or pigman");
        invisibles = new BooleanValue("Invisibles", true, "Attack invisible entities");
        enemies = new BooleanValue("OnlyEnemies", false, "Only attack enemies");
        this.priority = new ModeValue("Priority", new Mode("Closest", true), new Mode("Health", false));
        tickDelay = new IntegerValue("TickDelay", 3, 0, 30, "The delay between attacks");
        fov = new IntegerValue("FOV", 3, 0, 30, "Check if target is in a specified field of view");
        walls = new BooleanValue("ThroughWalls", true, "Attacks people through walls");
        autoDelay = new BooleanValue("AutoDelay", true, "Automatically delays each attack to the cooldown of the weapon");
        packetRange = new DoubleValue("PacketRange", 10.0D, 1.0D, 100D, "The max range of the target to send the attack packet ");
        range = new DoubleValue("Range", 3.4D, 1.0D, 7D, "The max range to attack the target");

        this.addValue(players, passiveMobs, hostileMobs, neutralMobs, invisibles, enemies, priority, tickDelay, fov, walls, autoDelay, packetRange, range);

        this.timer = new TimerUtils();
    }

    private int waitCounter;

    @Override
    public void onEnable() {
        facingCam = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        facingCam = null;
        target = null;
        super.onDisable();
    }


    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
            return;
        }

        for (Object object : Wrapper.INSTANCE.world().loadedEntityList) {
            if (object instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) object;
                //     System.out.println("Found Target: " + entity.getName());
                if (check(entity)) {
                    //       System.out.println("Found Target: " + entity.getName() + " After Checks");
                    if (isClosest(mc.player, entity) && priority.getMode("Closest").isToggled()) {
                        //         System.out.println("Found Closest Entity");
                        target = entity;
                    }
                    if (isLowHealth(mc.player, entity) && priority.getMode("Health").isToggled()) {
                        target = entity;
                    }

                }
            }
        }
        if (target != null) {
            if (target.isDead || target.getDistance(mc.player) > 7 || target.getHealth() <= 0) {
                this.setExtraInfo("");
            } else {
                this.setExtraInfo(target.getName());
            }
        } else {
            this.setExtraInfo("");
        }

        if (target == null || target.getHealth() <= 0f) return;
        if (this.autoDelay.getValue()) {
            //  System.out.println("Attacking Phase");
            if (Wrapper.INSTANCE.player().getCooledAttackStrength(0) == 1) {
                if (!isInAttackRange(target) || !isInAttackFOV(target)) {
                    return;
                }
                EntityPlayerSP player = Wrapper.INSTANCE.player();
                float sharpLevel = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), target.getCreatureAttribute());

                if (autoDelay.getValue()) {
                    Wrapper.INSTANCE.mc().playerController.attackEntity(player, target);
                } else {
                    Wrapper.INSTANCE.sendPacket(new CPacketUseEntity(target));
                }

                player.swingArm(EnumHand.MAIN_HAND);
                if (sharpLevel > 0.0f) {
                    player.onEnchantmentCritical(target);
                }
                Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
                target = null;
            }
        } else {
            if (tickDelay.getValue() > 0) {
                if (waitCounter < tickDelay.getValue()) {
                    waitCounter++;
                } else {
                    waitCounter = 0;
                    if (Wrapper.INSTANCE.player().getCooledAttackStrength(0) == 1) {
                        if (!isInAttackRange(target) || !isInAttackFOV(target)) {
                            return;
                        }
                        EntityPlayerSP player = Wrapper.INSTANCE.player();
                        float sharpLevel = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), target.getCreatureAttribute());

                        if (autoDelay.getValue()) {
                            Wrapper.INSTANCE.mc().playerController.attackEntity(player, target);
                        } else {
                            Wrapper.INSTANCE.sendPacket(new CPacketUseEntity(target));
                        }

                        player.swingArm(EnumHand.MAIN_HAND);
                        if (sharpLevel > 0.0f) {
                            player.onEnchantmentCritical(target);
                        }
                        Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
                        this.setExtraInfo(target.getName());
                        target = null;
                    }
                }
            }
        }
    });


    public boolean check(EntityLivingBase entity) {

        //    System.out.println("Running Attack Checks on " + entity.getName());

        if (entity instanceof EntityArmorStand) {
            //    System.out.println("Failed ArmorStand Check");
            return false;
        }
        if (entity == mc.player || entity.getName().equals(mc.player.getName())) {
            //   System.out.println("Failed Self Check");
            return false;
        }
        if (entity.isDead) {
            //   System.out.println("Failed Death Check");
            return false;
        }
        if (entity.deathTime > 0) {
            //   System.out.println("Failed DeathTime Check");
            return false;
        }
        if (EntityUtils.isPlayer(entity) && !players.getValue()) {
            //   System.out.println("Failed PlayerValueCheck Check");
            return false;
        }

        if (EntityUtils.isPlayer(entity) && FriendManager.friendsList.contains(entity.getName())) {
            //   System.out.println("Failed FriendCheck Check");
            return false;
        }
        if (entity.isInvisible() && !invisibles.getValue()) {
            //   System.out.println("Failed Invis Check");
            return false;
        }
        if ((EntityUtils.isPassive(entity)) && !passiveMobs.getValue()) {
            //   System.out.println("Failed Passive Check");
            return false;
        }
        if ((EntityUtils.isHostileMob(entity)) && !hostileMobs.getValue()) {
            //    System.out.println("Failed Hostile Check");
            return false;
        }
        if (EntityUtils.isNeutralMob(entity) && !EntityUtils.isMobAggressive(entity) && !neutralMobs.getValue()) {
            //    System.out.println("Failed Neutral Check");
            return false;
        }

//        if (!(EntityUtils.isMobAggressive(entity) && hostileMobs.getValue())) {
//            System.out.println("Failed aggresive Check");
//            return false;
//        }

        if (!isInAttackFOV(entity)) {
            //  System.out.println("Failed FOV Check");
            return false;
        }
        if (!isInAttackRange(entity)) {
            //  System.out.println("Failed Range Check");
            return false;
        }

        if (!this.walls.getValue()) {
            if (!mc.player.canEntityBeSeen(entity)) {
                //    System.out.println("Failed Wall Check");
                return false;
            }
        }

        // System.out.println("Passed All Checks, Running Priority Check");

        return true;
    }


    boolean isPriority(EntityLivingBase entity) {
        //  System.out.println("prio Check: " + ((priority.getMode("Closest").isToggled() && isClosest(entity, target)) || (priority.getMode("Health").isToggled() && isLowHealth(entity, target))));
        return (priority.getMode("Closest").isToggled() && isClosest(entity, target)) || (priority.getMode("Health").isToggled() && isLowHealth(entity, target));
    }

    boolean isLowHealth(EntityLivingBase entity, EntityLivingBase entityPriority) {
        return entityPriority == null || entity.getHealth() < entityPriority.getHealth();
    }

    boolean isClosest(EntityLivingBase entity, EntityLivingBase entityPriority) {
        return entityPriority == null || Wrapper.INSTANCE.player().getDistance(entity) < Wrapper.INSTANCE.player().getDistance(entityPriority);
    }

    public boolean isInAttackFOV(EntityLivingBase entity) {
        return Utils.getDistanceFromMouse(entity) <= fov.getValue();
    }

    public boolean isInAttackRange(EntityLivingBase entity) {
        return entity.getDistance(Wrapper.INSTANCE.player()) <= range.getValue().floatValue();
    }
}
