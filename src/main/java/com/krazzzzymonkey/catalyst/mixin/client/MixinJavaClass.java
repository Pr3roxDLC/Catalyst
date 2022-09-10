package com.krazzzzymonkey.catalyst.mixin.client;

import com.krazzzzymonkey.catalyst.managers.LuaManager;
import org.luaj.vm2.LuaValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.Map;


//Remaps any method calls/field accesses to Srg Names
//This could get very bad for the clients performance, the getMethod and getField  methods could potentially be called multiple hundred times per tick,
//resulting in alot of reflection usage, maybe caching the remapField and remapMethod methods from the MixinProxy class would be better then getting them everytime
//through reflections
@Mixin(targets = "org.luaj.vm2.lib.jse.JavaClass")
public class MixinJavaClass {

    @Shadow
    Map fields;

    @Shadow
    Map methods;

    private Class clazz = null;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructorInject(Class clazz, CallbackInfo ci) {
        this.clazz = clazz;
    }


    @Inject(method = "getMethod", at = @At("RETURN"), remap = false, cancellable = true)
    public void getMethodInject(LuaValue key, CallbackInfoReturnable<LuaValue> callbackInfoReturnable) {
        if (!LuaManager.isInDevEnv) {
            callbackInfoReturnable.setReturnValue((LuaValue) methods.get(LuaValue.valueOf(LuaManager.MAPPER.unmapMethod(clazz.getName(), key.toString()))));
        } else {
            callbackInfoReturnable.setReturnValue((LuaValue) methods.get(key));
        }
        //System.out.println("Method: " + key + " from class: " + clazz.getSimpleName() + " remapped to " + LuaManager.MAPPER.unmapMethod(clazz.getName(), key.toString()));
    }

    @Inject(method = "getField", at = @At("RETURN"), remap = false, cancellable = true)
    public void getFieldInject(LuaValue key, CallbackInfoReturnable<Field> callbackInfoReturnable) {
        if (!LuaManager.isInDevEnv) {
            callbackInfoReturnable.setReturnValue((Field) fields.get(LuaValue.valueOf(LuaManager.MAPPER.unmapField(clazz.getName(), key.toString()))));
        } else {
            callbackInfoReturnable.setReturnValue((Field) fields.get(key));
        }
        //System.out.println("Field: " + key + " from class: " + clazz.getSimpleName() + " remapped to: " + LuaManager.MAPPER.unmapField(clazz.getName(), key.toString()));
    }

}
