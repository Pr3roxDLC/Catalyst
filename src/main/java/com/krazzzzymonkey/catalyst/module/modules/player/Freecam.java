package com.krazzzzymonkey.catalyst.module.modules.player;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


//TODO MAKE SPEED INSTANT
public class Freecam extends Modules {

    public IntegerValue speed;
    private BooleanValue cancelPackets;

    public Freecam() {
        super("Freecam", ModuleCategory.PLAYER, "Allows you to move the camera freely around the player");

        speed = new IntegerValue("Speed", 5, 1, 100, "The speed of the freecam");
        this.cancelPackets = new BooleanValue("CancelPackets", true, "Should it cancel the movement packets when in freecam");
        this.addValue(cancelPackets, speed);
    }

    private boolean isRidingEntity;
    private Entity ridingEntity;
    private EntityOtherPlayerMP clonedPlayer;
    private double posX, posY, posZ;
    private float pitch, yaw;

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {
        if(e.getSide() == PacketEvent.Side.OUT) {

            Packet packet = e.getPacket();
            if ((packet instanceof CPacketPlayer || packet instanceof CPacketPlayer.Position || packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation) && cancelPackets.getValue()) {
                e.setCancelled(true);
            }
        }
    });

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (mc.player == null) this.toggle();
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        Minecraft.getMinecraft().player.capabilities.isFlying = true;
        Minecraft.getMinecraft().player.capabilities.setFlySpeed(speed.getValue().intValue() / 100f);
        Minecraft.getMinecraft().player.noClip = true;
        Minecraft.getMinecraft().player.onGround = false;
        Minecraft.getMinecraft().player.fallDistance = 0;

    });

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;
        Wrapper.INSTANCE.player().noClip = true;
    }

    @Override
    public void onEnable() {
        if (Minecraft.getMinecraft().player != null) {
            mc.player.motionY = 0D;
            isRidingEntity = Minecraft.getMinecraft().player.getRidingEntity() != null;

            if (Minecraft.getMinecraft().player.getRidingEntity() == null) {
                Minecraft.getMinecraft().player.noClip = true;
                posX = Minecraft.getMinecraft().player.posX;
                posY = Minecraft.getMinecraft().player.posY;
                posZ = Minecraft.getMinecraft().player.posZ;
            } else {
                ridingEntity = Minecraft.getMinecraft().player.getRidingEntity();
                Minecraft.getMinecraft().player.dismountRidingEntity();
            }

            pitch = Minecraft.getMinecraft().player.rotationPitch;
            yaw = Minecraft.getMinecraft().player.rotationYaw;

            Minecraft.getMinecraft().player.noClip = true;
            clonedPlayer = new EntityOtherPlayerMP(Minecraft.getMinecraft().world, Minecraft.getMinecraft().getSession().getProfile());
            clonedPlayer.copyLocationAndAnglesFrom(Minecraft.getMinecraft().player);
            clonedPlayer.rotationYawHead = Minecraft.getMinecraft().player.rotationYawHead;
            Minecraft.getMinecraft().world.addEntityToWorld(-100, clonedPlayer);
            Minecraft.getMinecraft().player.capabilities.isFlying = true;
            clonedPlayer.inventory = mc.player.inventory;
            Minecraft.getMinecraft().player.capabilities.setFlySpeed(speed.getValue().intValue() / 100f);

        }
        super.onEnable();
    }


    public void onDisable() {
        {
            EntityPlayer localPlayer = Minecraft.getMinecraft().player;
            if (localPlayer != null) {
                Minecraft.getMinecraft().player.setPositionAndRotation(posX, posY, posZ, yaw, pitch);
                Minecraft.getMinecraft().world.removeEntityFromWorld(-100);
                posX = posY = posZ = 0.D;
                pitch = yaw = 0.f;
                Minecraft.getMinecraft().player.capabilities.isFlying = false; //getModManager().getMod("ElytraFlight").isEnabled();
                Minecraft.getMinecraft().player.capabilities.setFlySpeed(0.05f);
                Minecraft.getMinecraft().player.noClip = false;
                Minecraft.getMinecraft().player.motionX = Minecraft.getMinecraft().player.motionY = Minecraft.getMinecraft().player.motionZ = 0.f;

                if (isRidingEntity) {
                    Minecraft.getMinecraft().player.startRiding(ridingEntity, true);
                }
            }
        }

        super.onDisable();
    }

}
