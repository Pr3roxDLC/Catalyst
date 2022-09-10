package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.events.ClientTickEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomFOV extends Modules {

    public IntegerValue Fov;
    public float normalFOV = 80f;

    public CustomFOV() {
        super("CustomFOV", ModuleCategory.MISC, "Set a custom field of view");

        Fov = new IntegerValue("FOV", 60, 1, 155, "The custom field of view you would like set");

        this.addValue(Fov);
    }

    @Override
    public void onEnable(){
       normalFOV = Minecraft.getMinecraft().gameSettings.fovSetting;
       super.onEnable();
    }

    @EventHandler
    private final EventListener<ClientTickEvent> onClientTick = new EventListener<>(e -> {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)return;

        double fov = Fov.getValue();
        Minecraft.getMinecraft().gameSettings.fovSetting = (float) fov;

    });

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.fovSetting = normalFOV;
        super.onDisable();
    }


}
