package com.krazzzzymonkey.catalyst.configuration;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.krazzzzymonkey.catalyst.gui.GuiCustom;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

import static com.krazzzzymonkey.catalyst.managers.FileManager.CATALYST_DIR;
import static com.krazzzzymonkey.catalyst.managers.FileManager.getAssetFile;

public class ConfigurationLoader {
    Config config;

    public ConfigurationLoader(Config config) {
        this.config = config;
    }

    public void load() throws Exception {
        JsonParser jsonParser = new JsonParser();

        File configFolder = new File(CATALYST_DIR, "CatalystMainMenu");
        if (!configFolder.exists()) {
            configFolder.mkdir();
        }

        FileUtils.copyFile(getAssetFile("mainmenu/config.json"), new File(configFolder, "mainmenu.json"));


        File[] jsonFiles = configFolder.listFiles();


        // Preload Main Menu so that other menus can rely on it

        for (File guiFile : jsonFiles) {
            if (guiFile.getName().equals("mainmenu.json")) {
                GuiConfig guiConfig = new GuiConfig();
                String name = guiFile.getName().replace(".json", "");

                JsonReader reader = null;
                try {
                    reader = new JsonReader(new FileReader(guiFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    JsonElement jsonElement = jsonParser.parse(reader);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    guiConfig.load(name, jsonObject);
                } catch (Exception e) {
                    try {
                        reader.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    throw e;
                }

                try {
                    reader.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }

                this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
            }
        }

        for (File guiFile : jsonFiles) {
            if (!guiFile.getName().equals("mainmenu.json") && guiFile.getName().endsWith(".json")) {
                GuiConfig guiConfig = new GuiConfig();
                String name = guiFile.getName().replace(".json", "");

                JsonReader reader = null;
                try {
                    reader = new JsonReader(new FileReader(guiFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    JsonElement jsonElement = jsonParser.parse(reader);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    guiConfig.load(name, jsonObject);
                } catch (Exception e) {
                    try {
                        reader.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    throw e;
                }

                try {
                    reader.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }

                this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
            }
        }
    }
}
