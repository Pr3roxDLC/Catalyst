package com.krazzzzymonkey.catalyst.command;

import com.google.gson.*;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class NameHistory extends Command implements Runnable {

    String name = "";
    Gson gson = new Gson();

    @Override
    public void runCommand(String s, String[] args) {

        name = args[0];
        Thread f = new Thread(this);
        f.start();

    }

    public NameHistory() {
        super("namehistory");
    }

    @Override
    public String getDescription() {
        return "Shows you the former names of a Player";
    }

    @Override
    public String getSyntax() {
        return "namehistory <Player>";
    }

    @Override
    public void run() {

        ChatUtils.message("Collecting Data, this may take some time");

        try {
            URL uuidUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            URLConnection connection = uuidUrl.openConnection();
            BufferedReader uuidIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = uuidIn.readLine()) != null) {
                stringBuilder.append(line);
            }

            if(stringBuilder.toString().equalsIgnoreCase("")){
                ChatUtils.error("Player Not Found");
                return;
            }

            String uuid = stringBuilder.toString().split(",")[1].split("\":\"")[1].split("\"")[0];

            ChatUtils.message("Fetching " + name + "'s UUID: " + uuid);

            URL nameurl = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
            URLConnection yc = nameurl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            StringBuilder str = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                str.append(inputLine);
            }

            StringBuilder output = new StringBuilder();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(str.toString());
            JsonArray jSonArray = jsonElement.getAsJsonArray();
            output.append("Player " + name + " used the following names in the past: ");
            for(int i = 0; i < jSonArray.size(); i++){

               output.append(jSonArray.get(i).getAsJsonObject().get("name").toString() + ", ");

            }

            ChatUtils.message(output.toString());

        }catch (Exception e){

            ChatUtils.error("Something went wrong");

            e.printStackTrace();
        }
//
//        URL nameurl = null;
//        try {
//            nameurl = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        URLConnection yc = null;
//        try {
//            yc = nameurl.openConnection();
//            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
//            String inputLine;
//            StringBuilder str = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) {
//                str.append(inputLine);
//            }
//            ChatUtils.message(str.toString());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//

        }
    }
