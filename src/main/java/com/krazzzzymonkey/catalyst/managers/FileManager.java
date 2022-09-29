package com.krazzzzymonkey.catalyst.managers;

import com.google.gson.*;
import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.gui.click.ClickGuiScreen;
import com.krazzzzymonkey.catalyst.gui.click.elements.Frame;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.module.modules.misc.InventoryCleaner;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.Value;
import com.krazzzzymonkey.catalyst.value.sliders.DoubleValue;
import com.krazzzzymonkey.catalyst.value.sliders.IntegerValue;
import com.krazzzzymonkey.catalyst.value.types.BooleanValue;
import com.krazzzzymonkey.catalyst.value.types.ColorValue;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import com.krazzzzymonkey.catalyst.value.types.SubMenu;
import com.krazzzzymonkey.catalyst.xray.XRayData;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileManager {

    public static final File CATALYST_DIR = new File(String.format("%s%s%s%s",
                                                                   Wrapper.INSTANCE.mc().gameDir,
                                                                   File.separator,
                                                                   Main.NAME,
                                                                   File.separator));
    public static final File ASSET_DIR = new File(CATALYST_DIR + "/Assets/");
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static final File ALT_DIR = new File(CATALYST_DIR + "/Catalyst Account Manager/");
    public static final File PROFILES_DIR = new File(CATALYST_DIR + "/Profiles/");
    private static final File HACKS = new File(PROFILES_DIR, "default.json");
    public static final File CLICKGUI = new File(CATALYST_DIR, "clickgui.json");
    private static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser jsonParser = new JsonParser();
    private static final File CHATMENTION = new File(CATALYST_DIR, "chatmention.json");
    private static final File AUTOGGMESSAGES = new File(CATALYST_DIR, "ggmessages.txt");
    private static final File XRAYDATA = new File(CATALYST_DIR, "xraydata.json");
    private static final File FRIENDS = new File(CATALYST_DIR, "friends.json");
    private static final File ENEMYS = new File(CATALYST_DIR, "enemys.json");
    private static final File PREFIX = new File(CATALYST_DIR, "prefix.json");
    private static final File CURRENTPROFILE = new File(CATALYST_DIR, "currentprofile.json");
    private static final File FONT = new File(CATALYST_DIR, "font.json");
    private static final File INVENTORY_CLEANER = new File(CATALYST_DIR, "inventorycleaner.json");

    public static @Nullable File getAssetFile(@Nonnull String path) {
        File file = new File(ASSET_DIR + path);
        Main.logger.info("Providing asset: " + file.getPath());
        if (file.exists()) return file;
        try {
            Main.logger.info("Creating asset from default resource: " + path);
            file.getParentFile().mkdirs();
            FileUtils.copyInputStreamToFile(resourceAsStream(path), file);
        } catch (IOException | SecurityException e) {
            Main.logger.info(e.getMessage());
            return null;
        }
        return file;
    }

    private static InputStream resourceAsStream(String path) {
        return FileManager.class.getClassLoader().getResourceAsStream("assets/catalyst/" + path);
    }

    private static void loadInventoryCleaner() {
        BufferedReader loadJson;
        try {
            loadJson = new BufferedReader(new FileReader(INVENTORY_CLEANER));
            JsonObject jsonObject = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();
            jsonObject.get("items").getAsJsonArray().iterator().forEachRemaining(n -> {
                InventoryCleaner.listItems.add(Item.getByNameOrId(n.getAsString()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void saveInventoryCleaner() {
        try {
            JsonObject jsonObject = new JsonObject();
            JsonArray array = new JsonArray();
            InventoryCleaner.listItems.stream()
                                      .filter(Objects::nonNull)
                                      .forEach(n -> array.add(n.getItemStackDisplayName(n.getDefaultInstance())));
            jsonObject.add("items", array);

            PrintWriter saveJson = new PrintWriter(new FileWriter(INVENTORY_CLEANER));
            saveJson.println(gsonPretty.toJson(jsonObject));
            saveJson.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCurrentProfile() {
        BufferedReader loadJson;
        try {
            loadJson = new BufferedReader(new FileReader(CURRENTPROFILE));
            JsonObject moduleJason = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            ProfileManager.currentProfile = moduleJason.get("ActiveProfile").getAsString();
            Main.logger.info("Loaded CurrentProfile: " + ProfileManager.currentProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void saveCurrentProfile() {
        try {
            JsonObject json = new JsonObject();

            json.addProperty("ActiveProfile", ProfileManager.currentProfile);

            PrintWriter saveJson = new PrintWriter(new FileWriter(CURRENTPROFILE));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
            Main.logger.info("Saved CurrentProfile: " + ProfileManager.currentProfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFont() {
        BufferedReader loadJson;
        try {
            loadJson = new BufferedReader(new FileReader(FONT));
            JsonObject fontJason = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            FontManager.font = fontJason.get("Font").getAsString();
            Main.logger.info("Loaded font: " + FontManager.font);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void saveFont() {
        try {
            JsonObject json = new JsonObject();

            json.addProperty("Font", FontManager.font);

            PrintWriter saveJson = new PrintWriter(new FileWriter(FONT));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
            Main.logger.info("Saved Font: " + FontManager.font);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadPrefix() {
        BufferedReader loadJson = null;
        try {
            loadJson = new BufferedReader(new FileReader(PREFIX));
            JsonObject moduleJason = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            CommandManager.prefix = moduleJason.get("prefix").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void savePrefix() {
        try {
            JsonObject json = new JsonObject();

            json.addProperty("prefix", CommandManager.prefix);

            PrintWriter saveJson = new PrintWriter(new FileWriter(PREFIX));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadModules(String profile) {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(HACKS));
            if (!profile.equals("")) {
                if (new File(PROFILES_DIR, profile + ".json").exists()) {
                    loadJson = new BufferedReader(new FileReader(new File(PROFILES_DIR, profile + ".json")));
                }
            }

            JsonObject moduleJason = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            for (Map.Entry<String, JsonElement> entry : moduleJason.entrySet()) {
                Modules mods = ModuleManager.getModule(entry.getKey());

                if (mods != null) {
                    JsonObject jsonMod = (JsonObject) entry.getValue();
                    boolean enabled = jsonMod.get("toggled").getAsBoolean();

                    mods.eventToggle(enabled);

                    if (!mods.getValues().isEmpty()) {
                        for (Value value : mods.getValues()) {
                            try {
                                if (value instanceof BooleanValue) {
                                    boolean bvalue = jsonMod.get(value.getName()).getAsBoolean();
                                    value.setValue(bvalue);
                                }
                                if (value instanceof DoubleValue) {
                                    value.setValue(jsonMod.get(value.getName()).getAsDouble());
                                }

                                if (value instanceof IntegerValue) {
                                    try {
                                        value.setValue(jsonMod.get(value.getName()).getAsBigInteger());
                                    } catch (NumberFormatException e) {
                                        double doubleValue = jsonMod.get(value.getName()).getAsDouble();
                                        value.setValue((int) doubleValue);
                                    }
                                }
                                if (value instanceof ColorValue) {
                                    value.setValue(jsonMod.get(value.getName())
                                                          .getAsJsonObject()
                                                          .get("color")
                                                          .getAsInt());
                                    ((ColorValue) value).setLineColor(jsonMod.get(value.getName())
                                                                             .getAsJsonObject()
                                                                             .get("lineColor")
                                                                             .getAsInt());
                                    ((ColorValue) value).setSelColorY(jsonMod.get(value.getName())
                                                                             .getAsJsonObject()
                                                                             .get("selColorY")
                                                                             .getAsInt());
                                    ((ColorValue) value).setSelOpacityY(jsonMod.get(value.getName())
                                                                               .getAsJsonObject()
                                                                               .get("selOpacityY")
                                                                               .getAsInt());
                                    ((ColorValue) value).setTriPos(jsonMod.get(value.getName())
                                                                          .getAsJsonObject()
                                                                          .get("triX")
                                                                          .getAsInt(),
                                                                   jsonMod.get(value.getName())
                                                                          .getAsJsonObject()
                                                                          .get("triY")
                                                                          .getAsInt());
                                }
                                if (value instanceof com.krazzzzymonkey.catalyst.value.types.Number) {
                                    value.setValue(jsonMod.get(value.getName()).getAsDouble());
                                }
                                if (value instanceof ModeValue) {
                                    ModeValue modeValue = (ModeValue) value;
                                    for (Mode mode : modeValue.getModes()) {
                                        mode.setToggled(jsonMod.get(modeValue.getName())
                                                               .getAsJsonObject()
                                                               .get(mode.getName())
                                                               .getAsBoolean());
                                    }
                                }
                                if (value instanceof SubMenu) {
                                    SubMenu subMenu = (SubMenu) value;
                                    for (Value value1 : subMenu.getValues()) {
                                        if (value1 instanceof BooleanValue) {
                                            boolean bvalue = jsonMod.get(subMenu.getName())
                                                                    .getAsJsonObject()
                                                                    .get(value1.getName())
                                                                    .getAsBoolean();
                                            value1.setValue(bvalue);
                                        }
                                        if (value1 instanceof DoubleValue) {
                                            value1.setValue(jsonMod.get(subMenu.getName())
                                                                   .getAsJsonObject()
                                                                   .get(value1.getName())
                                                                   .getAsDouble());
                                        }

                                        if (value1 instanceof IntegerValue) {
                                            try {
                                                value1.setValue(jsonMod.get(subMenu.getName())
                                                                       .getAsJsonObject()
                                                                       .get(value1.getName())
                                                                       .getAsBigInteger());
                                            } catch (NumberFormatException e) {
                                                double doubleValue = jsonMod.get(subMenu.getName())
                                                                            .getAsJsonObject()
                                                                            .get(value1.getName())
                                                                            .getAsDouble();
                                                value1.setValue((int) doubleValue);
                                            }
                                        }
                                        if (value1 instanceof ColorValue) {
                                            value1.setValue(jsonMod.get(subMenu.getName())
                                                                   .getAsJsonObject()
                                                                   .get(value1.getName())
                                                                   .getAsJsonObject()
                                                                   .get("color")
                                                                   .getAsInt());
                                            ((ColorValue) value1).setLineColor(jsonMod.get(subMenu.getName())
                                                                                      .getAsJsonObject()
                                                                                      .get(value1.getName())
                                                                                      .getAsJsonObject()
                                                                                      .get("lineColor")
                                                                                      .getAsInt());
                                            ((ColorValue) value1).setSelColorY(jsonMod.get(subMenu.getName())
                                                                                      .getAsJsonObject()
                                                                                      .get(value1.getName())
                                                                                      .getAsJsonObject()
                                                                                      .get("selColorY")
                                                                                      .getAsInt());
                                            ((ColorValue) value1).setSelOpacityY(jsonMod.get(subMenu.getName())
                                                                                        .getAsJsonObject()
                                                                                        .get(value1.getName())
                                                                                        .getAsJsonObject()
                                                                                        .get("selOpacityY")
                                                                                        .getAsInt());
                                            ((ColorValue) value1).setTriPos(jsonMod.get(subMenu.getName())
                                                                                   .getAsJsonObject()
                                                                                   .get(value1.getName())
                                                                                   .getAsJsonObject()
                                                                                   .get("triX")
                                                                                   .getAsInt(),
                                                                            jsonMod.get(subMenu.getName())
                                                                                   .getAsJsonObject()
                                                                                   .get(value1.getName())
                                                                                   .getAsJsonObject()
                                                                                   .get("triY")
                                                                                   .getAsInt());
                                        }
                                        if (value1 instanceof com.krazzzzymonkey.catalyst.value.types.Number) {
                                            value1.setValue(jsonMod.get(subMenu.getName())
                                                                   .getAsJsonObject()
                                                                   .get(value1.getName())
                                                                   .getAsDouble());
                                        }
                                        if (value1 instanceof ModeValue) {
                                            ModeValue modeValue = (ModeValue) value1;
                                            for (Mode mode : modeValue.getModes()) {
                                                mode.setToggled(jsonMod.get(subMenu.getName())
                                                                       .getAsJsonObject()
                                                                       .get(modeValue.getName())
                                                                       .getAsJsonObject()
                                                                       .get(mode.getName())
                                                                       .getAsBoolean());
                                            }
                                        }
                                    }
                                }


                            } catch (NullPointerException e) {
                                Main.logger.warn("Unknown Config for: " + mods.getModuleName() + ". Setting Default config!");
                                if (new File(PROFILES_DIR, profile + ".json").exists()) {
                                    saveModules(profile);
                                } else saveModules("default");

                            }
                        }
                    }
                    mods.setKey(jsonMod.get("bind").getAsJsonObject().get("key").getAsInt());
                    mods.setBindHold(jsonMod.get("bind").getAsJsonObject().get("held").getAsBoolean());
                    if (new File(PROFILES_DIR, profile + ".json").exists()) {
                        ProfileManager.currentProfile = profile;
                    } else ProfileManager.currentProfile = "default";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFriends() {
        final List<String> friends = read(FRIENDS);
        for (String name : friends) {
            FriendManager.addFriend(name);
        }
    }

    public static void loadChatMentions() {
        final List<String> word = read(CHATMENTION);
        for (String Word : word) {
            ChatMentionManager.addMention(Word);
        }
    }

    public static void loadMessages() {
        final List<String> messages = read(AUTOGGMESSAGES);
        for (String message : messages) {
            AutoGGManager.addMessage(message);
        }
    }

    public static void loadEnemys() {
        final List<String> enemys = read(ENEMYS);
        for (String name : enemys) {
            EnemyManager.addEnemy(name);
        }
    }

    public static void loadXRayData() {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(XRAYDATA));
            JsonObject json = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                JsonObject jsonData = (JsonObject) entry.getValue();

                String[] split = entry.getKey().split(":");

                int id = Integer.parseInt(split[0]);
                int meta = Integer.parseInt(split[1]);

                int red = jsonData.get("red").getAsInt();
                int green = jsonData.get("green").getAsInt();
                int blue = jsonData.get("blue").getAsInt();

                XRayManager.addData(new XRayData(id, meta, red, green, blue));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveXRayData() {
        try {
            JsonObject json = new JsonObject();

            for (XRayData data : XRayManager.xrayList) {
                JsonObject jsonData = new JsonObject();

                jsonData.addProperty("red", data.getRed());
                jsonData.addProperty("green", data.getGreen());
                jsonData.addProperty("blue", data.getBlue());

                json.add("" + data.getId() + ":" + data.getMeta(), jsonData);
            }

            PrintWriter saveJson = new PrintWriter(new FileWriter(XRAYDATA));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadClickGui() {
        try {
            BufferedReader loadJson = new BufferedReader(new FileReader(CLICKGUI));
            JsonObject json = (JsonObject) jsonParser.parse(loadJson);
            loadJson.close();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                JsonObject jsonData = (JsonObject) entry.getValue();

                String text = entry.getKey();

                int posX = jsonData.get("posX").getAsInt();
                int posY = jsonData.get("posY").getAsInt();
                boolean maximized = jsonData.get("maximized").getAsBoolean();

                for (Frame frame : ClickGuiScreen.clickGui.getFrames()) {
                    if (frame.getText().equals(text)) {
                        frame.setxPos(posX);
                        frame.setyPos(posY);
                        frame.setMaximized(maximized);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveClickGui() {
        try {
            JsonObject json = new JsonObject();
            for (Frame frame : ClickGuiScreen.clickGui.getFrames()) {
                JsonObject jsonData = new JsonObject();

                jsonData.addProperty("posX", frame.getX());
                jsonData.addProperty("posY", frame.getY());
                jsonData.addProperty("maximized", frame.isMaximized());

                json.add(frame.getText(), jsonData);
            }

            PrintWriter saveJson = new PrintWriter(new FileWriter(CLICKGUI));
            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveFriends() {
        write(FRIENDS, FriendManager.friendsList, true, true);
    }

    public static void saveEnemys() {
        write(ENEMYS, EnemyManager.enemysList, true, true);
    }

    public static void saveChatMention() {
        write(CHATMENTION, ChatMentionManager.mentionList, true, true);
    }

    public static void saveMessages() {
        write(AUTOGGMESSAGES, AutoGGManager.messages, true, true);
    }

    public static void saveModules(String profile) {
        try {
            JsonObject json = new JsonObject();

            for (Modules module : ModuleManager.getModules()) {
                JsonObject jsonHack = new JsonObject();
                jsonHack.addProperty("toggled", module.isToggled());

                JsonObject keyBinds = new JsonObject();
                keyBinds.addProperty("key", module.getKey());
                keyBinds.addProperty("held", module.isBindHold());
                jsonHack.add("bind", keyBinds);

                if (!module.getValues().isEmpty()) {
                    for (Value value : module.getValues()) {
                        if (value instanceof BooleanValue) {
                            jsonHack.addProperty(value.getName(), (Boolean) value.getValue());
                        }
                        if (value instanceof IntegerValue) {
                            jsonHack.addProperty(value.getName(), (Integer) value.getValue());
                        }
                        if (value instanceof DoubleValue) {
                            jsonHack.addProperty(value.getName(), (Double) value.getValue());
                        }
                        if (value instanceof ColorValue) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("color", ((ColorValue) value).getColorInt());
                            jsonObject.addProperty("lineColor", ((ColorValue) value).getLineColor().getRGB());
                            jsonObject.addProperty("selColorY", ((ColorValue) value).getSelColorY());
                            jsonObject.addProperty("selOpacityY", ((ColorValue) value).getSelOpacityY());
                            jsonObject.addProperty("triX", ((ColorValue) value).getTriPos()[0]);
                            jsonObject.addProperty("triY", ((ColorValue) value).getTriPos()[1]);
                            jsonHack.add(value.getName(), jsonObject);
                        }
                        if (value instanceof com.krazzzzymonkey.catalyst.value.types.Number) {
                            jsonHack.addProperty(value.getName(), (Number) value.getValue());
                        }
                        if (value instanceof ModeValue) {
                            ModeValue modeValue = (ModeValue) value;
                            JsonObject jsonObject = new JsonObject();

                            for (Mode mode : modeValue.getModes()) {
                                jsonObject.addProperty(mode.getName(), mode.isToggled());
                            }
                            jsonHack.add(modeValue.getName(), jsonObject);
                        }

                        if (value instanceof SubMenu) {
                            SubMenu subMenu = (SubMenu) value;
                            JsonObject jsonObject = new JsonObject();

                            for (Value value1 : subMenu.getValues()) {

                                if (value1 instanceof BooleanValue) {
                                    jsonObject.addProperty(value1.getName(), (Boolean) value1.getValue());
                                }
                                if (value1 instanceof IntegerValue) {
                                    jsonObject.addProperty(value1.getName(), (Integer) value1.getValue());
                                }
                                if (value1 instanceof DoubleValue) {
                                    jsonObject.addProperty(value1.getName(), (Double) value1.getValue());
                                }
                                if (value1 instanceof ColorValue) {
                                    JsonObject jsonObject1 = new JsonObject();
                                    jsonObject1.addProperty("color", ((ColorValue) value1).getColorInt());
                                    jsonObject1.addProperty("lineColor", ((ColorValue) value1).getLineColor().getRGB());
                                    jsonObject1.addProperty("selColorY", ((ColorValue) value1).getSelColorY());
                                    jsonObject1.addProperty("selOpacityY", ((ColorValue) value1).getSelOpacityY());
                                    jsonObject1.addProperty("triX", ((ColorValue) value1).getTriPos()[0]);
                                    jsonObject1.addProperty("triY", ((ColorValue) value1).getTriPos()[1]);
                                    jsonObject.add(value1.getName(), jsonObject1);
                                }
                                if (value1 instanceof com.krazzzzymonkey.catalyst.value.types.Number) {
                                    jsonObject.addProperty(value1.getName(), (Number) value1.getValue());
                                }
                                if (value1 instanceof ModeValue) {
                                    ModeValue modeValue = (ModeValue) value1;
                                    JsonObject jsonObject1 = new JsonObject();

                                    for (Mode mode : modeValue.getModes()) {
                                        jsonObject1.addProperty(mode.getName(), mode.isToggled());
                                    }
                                    jsonObject.add(modeValue.getName(), jsonObject1);
                                }
                            }
                            jsonHack.add(subMenu.getName(), jsonObject);
                        }
                    }
                }
                json.add(module.getModuleName(), jsonHack);
            }
            PrintWriter saveJson = new PrintWriter(new FileWriter(new File(PROFILES_DIR, profile + ".json")));

            saveJson.println(gsonPretty.toJson(json));
            saveJson.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(File outputFile, List<String> writeContent, boolean newline, boolean overrideContent) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile, !overrideContent));
            for (final String outputLine : writeContent) {
                writer.write(outputLine);
                writer.flush();
                if (newline) {
                    writer.newLine();
                }
            }
        } catch (Exception ex) {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex2) {
            }
        }
    }

    public static List<String> read(File inputFile) {
        ArrayList<String> readContent = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = reader.readLine()) != null) {
                readContent.add(line);
            }
        } catch (Exception ex) {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex2) {
            }
        }
        return readContent;
    }

    public static void init() {
        reload();
    }

    public static void reload() {
        if (!CATALYST_DIR.exists()) {
            CATALYST_DIR.mkdir();
        }
        if (!ALT_DIR.exists()) {
            ALT_DIR.mkdir();
        }
        if (!PROFILES_DIR.exists()) {
            PROFILES_DIR.mkdir();
        }
        if (!CURRENTPROFILE.exists()) {
            saveCurrentProfile();
        } else {
            loadCurrentProfile();
        }
        if (!(new File(PROFILES_DIR, ProfileManager.currentProfile + ".json").exists())) {
            Main.logger.warn("Profile: " + ProfileManager.currentProfile + " does not exist! Setting default Profile");
            ProfileManager.currentProfile = "default";
            saveModules(ProfileManager.currentProfile);
        } else {
            loadModules(ProfileManager.currentProfile);
        }
        if (!CHATMENTION.exists()) {
            saveChatMention();
        } else {
            loadChatMentions();
        }
        if (!XRAYDATA.exists()) {
            saveXRayData();
        } else {
            loadXRayData();
        }
        if (!FRIENDS.exists()) {
            saveFriends();
        } else {
            loadFriends();
        }
        if (!ENEMYS.exists()) {
            saveEnemys();
        } else {
            loadEnemys();
        }
        if (!PREFIX.exists()) {
            savePrefix();
        } else {
            loadPrefix();
        }
        if (!FONT.exists()) {
            saveFont();
        } else {
            loadFont();
        }
        if (!AUTOGGMESSAGES.exists()) {
            AutoGGManager.messages.add("GG {name}! Catalyst ontop.");
            saveMessages();
        } else {
            loadMessages();
        }
        if (!INVENTORY_CLEANER.exists()) {
            saveInventoryCleaner();
        } else {
            loadInventoryCleaner();
        }
    }
}
