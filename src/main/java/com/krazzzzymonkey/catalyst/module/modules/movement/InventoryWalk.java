package com.krazzzzymonkey.catalyst.module.modules.movement;

import com.krazzzzymonkey.catalyst.events.RenderGameOverlayEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;

public class InventoryWalk extends Modules {

    BooleanValue strict;

    public InventoryWalk() {
        super("InventoryWalk", ModuleCategory.MOVEMENT, "Allows you to move around while in a GUI screen");

        strict = new BooleanValue("Strict", false, "Prevents you from sprinting when in a gui");
        this.addValue(strict);

    }

    boolean autoSprint = false;

    @EventHandler
    private final EventListener<RenderGameOverlayEvent.Text> onRenderGameOverlay = new EventListener<>(e -> {

        if (strict.getValue() && mc.currentScreen != null) {

            if (ModuleManager.getModule("AutoSprint").isToggled()) {
                ModuleManager.getModule("AutoSprint").toggle();
                autoSprint = true;
            }

            mc.player.setSprinting(false);

        } else if (autoSprint) {
            ModuleManager.getModule("AutoSprint").setToggled(true);
            autoSprint = false;
        }


        if ((mc.currentScreen != null) && (!(mc.currentScreen instanceof GuiChat))) {
            if (Keyboard.isKeyDown(200)) {
                if (!(mc.player.rotationPitch <= -90.0F))
                    pitch(mc.player.rotationPitch - 2.0F);
            }
            if (Keyboard.isKeyDown(208)) {
                if (!(mc.player.rotationPitch >= 90.0F))
                    pitch(mc.player.rotationPitch + 2.0F);
            }
            if (Keyboard.isKeyDown(203)) {
                yaw(mc.player.rotationYaw - 3.0F);
            }
            if (Keyboard.isKeyDown(205)) {
                yaw(mc.player.rotationYaw + 3.0F);
            }
        }
    });

    private void pitch(float pitch) {
        mc.player.rotationPitch = pitch;
    }

    private void yaw(float yaw) {
        mc.player.rotationYaw = yaw;
    }
}


