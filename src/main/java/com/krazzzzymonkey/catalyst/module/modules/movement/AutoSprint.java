package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.Utils;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//TODO MUTLI DIR
public class AutoSprint extends Modules {
    public static BooleanValue multiDirection;
    public AutoSprint() {
        super("AutoSprint", ModuleCategory.MOVEMENT, "Automatically sprints for you");
        multiDirection = new BooleanValue("AllDirections", true, "Allows you to sprint in all directions instead to just forward");
        this.addValue(multiDirection);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (this.canSprint()) {
            Wrapper.INSTANCE.player().setSprinting(Utils.isMoving(Wrapper.INSTANCE.player()));
        }

    });


    boolean canSprint() {
        if (!Wrapper.INSTANCE.player().onGround) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isSprinting()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isOnLadder()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isInWater()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isInLava()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().collidedHorizontally) {
            return false;
        }
        if (Wrapper.INSTANCE.player().moveForward < 0.1F) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isSneaking()) {
            return false;
        }
        if (Wrapper.INSTANCE.player().getFoodStats().getFoodLevel() < 6) {
            return false;
        }
        if (Wrapper.INSTANCE.player().isRiding()) {
            return false;
        }
        return !Wrapper.INSTANCE.player().isPotionActive(MobEffects.BLINDNESS);
    }
}
