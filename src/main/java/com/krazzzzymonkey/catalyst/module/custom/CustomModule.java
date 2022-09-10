package com.krazzzzymonkey.catalyst.module.custom;

import com.krazzzzymonkey.catalyst.events.ClientEvent;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.CustomModuleUtils;
import com.krazzzzymonkey.catalyst.value.Value;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CustomModule extends Modules {

    private HashSet<String> eventsToListenFor = new HashSet<>();


    @EventHandler
    private final EventListener<ClientEvent> eventListener = new EventListener<>(n -> handleEvents(n));

    public final HashMap<String, Value> values = new HashMap<>();


    private LuaValue pcall = null;
    private LuaValue chunk = null;

    private Globals globals = null;

    public CustomModule(String name, ModuleCategory category, String description, File file) {
        super(name, category, description);
        globals = JsePlatform.standardGlobals();
        pcall = globals.get("pcall");
        try {
            LuaValue mc = CoerceJavaToLua.coerce(Minecraft.getMinecraft());
            LuaValue instance = CoerceJavaToLua.coerce(this);
            LuaValue client = CoerceJavaToLua.coerce(new CustomModuleUtils());
            globals.set("instance", instance);
            globals.set("mc", mc);
            globals.set("client", client);
            String devEnvCode = Files.lines(file.toPath()).collect(Collectors.joining(System.lineSeparator()));
            chunk = globals.load(devEnvCode);
            // chunk = globals.load();
            chunk.call();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        addValue(values.values().toArray(new Value[0]));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(eventsToListenFor.contains("onEnable"))globals.get("onEnable").call();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(eventsToListenFor.contains("onDisable"))globals.get("onDisable").call();
    }

    public void addListener(String str){
        eventsToListenFor.add(str);
    }

    public void handleEvents(ClientEvent event){
        //In order for this to work outside of the dev env, we either need mappings for the event classes or keep the method names the same
        if(eventsToListenFor.contains("on" + event.getName()))globals.get("on" + event.getName()).call(CoerceJavaToLua.coerce(event));
    }

    public void addBooleanSetting(String name, boolean defaultValue, String description){
        values.put(name, new BooleanValue(name, defaultValue, description));
    }

    public void addIntegerSetting(String name, int defaultValue, int min, int max, String description){
        values.put(name, new IntegerValue(name, defaultValue, min, max, description));
    }

    public void addDoubleSetting(String name, double defaultValue, double min, double max, String description){
        values.put(name, new DoubleValue(name, defaultValue, min, max, description));
    }

    public void addColorSetting(String name, int defaultValue, String description){
        values.put(name, new ColorValue(name, defaultValue, description));
    }

    public Value getSetting(String name){
        return values.get(name);
    }



}
