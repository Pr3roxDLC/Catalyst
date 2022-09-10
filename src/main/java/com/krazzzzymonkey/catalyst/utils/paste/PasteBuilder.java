package com.krazzzzymonkey.catalyst.utils.paste;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// we have unused fields and these other warnings and ignore them because this is a json model class
@SuppressWarnings({"unused", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
public class PasteBuilder {

    @Expose(serialize = false, deserialize = false)
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final String visibility = "unlisted";
    private final List<PasteFile> files = new ArrayList<>();

    private String name = "Catalyst Client";
    private String description = "Get Catalyst at: catalyst.sexy";
    private String expires = ZonedDateTime.now(ZoneOffset.UTC).plusDays(7).toString();

    private static Data upload(String output) throws IOException {
        URL url = new URL("https://api.paste.gg/v1/pastes");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept", "application/json");
        OutputStream os = connection.getOutputStream();
        byte[] input = output.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
        return parseResponse(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
    }

    private static Data parseResponse(InputStreamReader s) {
        JsonObject root = new JsonParser().parse(s).getAsJsonObject();
        String status = root.get("status").getAsString();
        if (!status.equals("success")) return null;
        JsonObject result = root.get("result").getAsJsonObject();
        return new Data(result.get("id").getAsString(), result.get("deletion_key").getAsString(), result.get("expires").getAsString());
    }

    public PasteBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PasteBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public PasteBuilder setExpiration(int hours) {
        this.expires = ZonedDateTime.now(ZoneOffset.UTC).plusHours(hours).toString();
        return this;
    }

    /**
     * Adds a new item to the paste.
     * Paste item name extension (think filename extensions) automatically sets syntax highlighting in the ui.
     *
     * @param name    Name of paste item
     * @param content Content of paste item
     */
    public void addContent(String name, String content) {
        if (!content.endsWith("\n") && !content.endsWith("\r"))
            content = content + "\n";
        files.add(new PasteFile(name, content));
    }

    /**
     * Upload paste to paste.gg
     */
    public void post() {

        Optional<Data> uploadData;
        try {
            uploadData = Optional.ofNullable(upload(GSON.toJson(this)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            uploadData = Optional.empty();
        }

        if (!uploadData.isPresent()) {
            ChatUtils.normalMessage("Pasted upload failed :(");
        } else {
            final PasteBuilder.Data data = uploadData.get();
            Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(data.getUrl()), null);
            ChatUtils.normalMessage("URL copied to clipboard, deletion key: " + data.deleteKey);
        }

    }

    public static class Data {
        public final String id;
        public final String deleteKey;
        public final String expires;

        private Data(String id, String deleteKey, String expires) {
            this.id = id;
            this.deleteKey = deleteKey;
            this.expires = expires;
        }

        public String getUrl() {
            return "https://paste.gg/" + this.id;
        }
    }

    private static class PasteFile {
        private final String name;
        private final PasteContent content;

        private PasteFile(String name, String content) {
            this.name = name;
            this.content = new PasteContent(content);
        }

        private static class PasteContent {
            private final String format = "text";
            private final String value;

            private PasteContent(String value) {
                this.value = value;
            }
        }
    }

}
