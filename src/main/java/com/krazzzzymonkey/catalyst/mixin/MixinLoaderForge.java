package com.krazzzzymonkey.catalyst.mixin;

import com.krazzzzymonkey.catalyst.Main;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class MixinLoaderForge implements IFMLLoadingPlugin {

    private static boolean isObfuscatedEnvironment = false;

    public MixinLoaderForge() {

        try {
            isObfuscatedEnvironment = Launch.classLoader.getClassBytes("net.minecraft.world.World") == null;
        }catch (Throwable e){
            e.printStackTrace();
        }

        Main.logger.info("Catalyst Mixins Loaded!");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.catalyst.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        Main.logger.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (boolean) (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
