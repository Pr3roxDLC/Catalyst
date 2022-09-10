package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class    ReverseStep extends Modules {

    public IntegerValue blocks;

    public ReverseStep() {
        super("ReverseStep", ModuleCategory.MOVEMENT, "Allows you to instantly step down specified height of blocks");

        blocks = new IntegerValue("Height", 3, 1, 100, "The height at which reverse step activates ");

        this.addValue(blocks);
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.getUniqueID() == null)
            return;

        if (Wrapper.INSTANCE.player() == null || Wrapper.INSTANCE.world() == null || Wrapper.INSTANCE.player().isInWater() || Wrapper.INSTANCE.player().isInLava() || ModuleManager.getModule("Freecam").isToggled()) {
            return;
        }

        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()){
            for (double y = 0.0; y < this.blocks.getValue() + 0.5; y += 0.01){
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()){
                    mc.player.motionY = -10.0;
                    break;
                }
            }
        }


    });
}
