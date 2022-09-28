package com.krazzzzymonkey.catalyst.utils;

import com.krazzzzymonkey.catalyst.managers.FileManager;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author SooStrator1136
 */
public class Mapper {


    /**
     * First String is the class, the inner Maps first String is the Srg Name and the second is the obfed Name
     */
    private final Map<String, Map<String, String>> FIELD_MAPPINGS = new HashMap<>();
    private final Map<String, Map<String, String>> METHOD_MAPPINGS = new HashMap<>();


    public String unmapField(String clazz, String srgName) {
        if (FIELD_MAPPINGS.containsKey(clazz)) {
            if(FIELD_MAPPINGS.get(clazz).containsKey(srgName)) {
                return FIELD_MAPPINGS.get(clazz).getOrDefault(srgName, srgName);
            }else{
                //Field was not found in this class, searching superclasses for it and merging their HashMaps so we dont have to travel up the
                //tree next time we try to find this method
                try {
                    Class clazz2 = Class.forName(clazz).getSuperclass();
                    do{
                        if(FIELD_MAPPINGS.get(clazz2.getName()).containsKey(srgName)){
                            FIELD_MAPPINGS.get(clazz).putAll(FIELD_MAPPINGS.get(clazz2.getName()));
                            return FIELD_MAPPINGS.get(clazz2.getName()).getOrDefault(srgName, srgName);
                        }
                        clazz2 = clazz2.getSuperclass();
                    }while(clazz2 != Object.class);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return srgName;
    }

    public String unmapMethod(String clazz, String srgName) {
        if (METHOD_MAPPINGS.containsKey(clazz)) {
            //Method is found in this class
            if (METHOD_MAPPINGS.get(clazz).containsKey(srgName)) {
                return METHOD_MAPPINGS.get(clazz).getOrDefault(srgName, srgName);
            }else{
                //Method was not found in this class, searching superclasses for it and merging their HashMaps so we dont have to travel up the
                //tree next time we try to find this method
                try {
                    Class clazz2 = Class.forName(clazz).getSuperclass();
                    do{
                        if(METHOD_MAPPINGS.get(clazz2.getName()).containsKey(srgName)){
                            METHOD_MAPPINGS.get(clazz).putAll(METHOD_MAPPINGS.get(clazz2.getName()));
                            return METHOD_MAPPINGS.get(clazz2.getName()).getOrDefault(srgName, srgName);
                        }
                        clazz2 = clazz2.getSuperclass();
                    }while(clazz2 != Object.class);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return srgName;
    }

    public Mapper() {
        File file = FileManager.getAssetFile("lua/mappings.srg");
        try {
            Files.lines(file.toPath()).forEach(line -> {
                if (line.startsWith("FD:")) {
                    line = line.substring(4);
                    final String firstPart = line.substring(0, line.indexOf(" "));
                    final String className = firstPart.substring(0, firstPart.lastIndexOf("/")).replaceAll("/", "\\.");
                    if (FIELD_MAPPINGS.containsKey(className))
                        FIELD_MAPPINGS.get(className).put(Optional.of(line.substring(line.lastIndexOf(" ") + 1)).map(str -> str.substring(str.lastIndexOf("/") + 1)).get(), firstPart.substring(firstPart.lastIndexOf("/") + 1));
                    else {
                        String finalLine = line;
                        FIELD_MAPPINGS.put(className, new HashMap<String, String>() {{
                            put(Optional.of(finalLine.substring(finalLine.lastIndexOf(" ") + 1)).map(str -> str.substring(str.lastIndexOf("/") + 1)).get(), firstPart.substring(firstPart.lastIndexOf("/") + 1));
                        }});
                    }
                } else if (line.startsWith("MD:")) {
                    line = line.substring(4);
                    final String firstPart = line.substring(0, line.indexOf(" "));
                    final String className = firstPart.substring(0, firstPart.lastIndexOf("/")).replaceAll("/", "\\.");
                    if (METHOD_MAPPINGS.containsKey(className))
                        METHOD_MAPPINGS.get(className).put(Optional.of(line.substring(line.indexOf(" ") + 1, line.lastIndexOf(" "))).map(str -> str.substring(str.lastIndexOf("/") + 1)).get(), firstPart.substring(firstPart.lastIndexOf("/") + 1));
                    else {
                        final String finalLine = line;
                        METHOD_MAPPINGS.put(className, new HashMap<String, String>() {{
                            put(Optional.of(finalLine.substring(finalLine.indexOf(" ") + 1, finalLine.lastIndexOf(" "))).map(str -> str.substring(str.lastIndexOf("/") + 1)).get(), firstPart.substring(firstPart.lastIndexOf("/") + 1));
                        }});
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
