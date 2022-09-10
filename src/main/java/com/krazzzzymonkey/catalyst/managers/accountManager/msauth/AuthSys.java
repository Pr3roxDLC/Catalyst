package com.krazzzzymonkey.catalyst.managers.accountManager.msauth;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.krazzzzymonkey.catalyst.managers.accountManager.AccountManager;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.HttpTools;
import org.lwjgl.Sys;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;

public class AuthSys {
    private static final Gson gson = new Gson();
    private static volatile HttpServer srv;
    public static void start(MSAuthScreen gui) {
        String done = "<head><title>Catalyst | Account linked</title><link rel=\"stylesheet\" href=\"https://catalyst.sexy/css/main.css\"><link rel=\"stylesheet\" href=\"https://catalyst.sexy/css/waves.css\"><link rel=\"stylesheet\" href=\"https://catalyst.sexy/css/nav.css\"><link rel=\"stylesheet\" href=\"https://catalyst.sexy/css/contact.css\"></head><body><header><span id=\"bg-animation\"><canvas class=\"particles-js-canvas-el\" style=\"width: 100%; height: 64%;\" width=\"1910\" height=\"307\"></canvas></span><svg class=\"waves\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" viewBox=\"0 24 150 28\" preserveAspectRatio=\"none\" shape-rendering=\"auto\"><defs><path id=\"gentle-wave\" d=\"M-160 44c30 0 58-18 88-18s 58 18 88 18 58-18 88-18 58 18 88 18 v44h-352z\"></path></defs><g class=\"parallax\"><use xlink:href=\"#gentle-wave\" x=\"48\" y=\"0\" fill=\"rgba(61,61,61,0.7\"></use><use xlink:href=\"#gentle-wave\" x=\"48\" y=\"3\" fill=\"rgba(56,186,148,0.5)\"></use><use xlink:href=\"#gentle-wave\" x=\"48\" y=\"5\" fill=\"rgba(61,61,61,0.3)\"></use><use xlink:href=\"#gentle-wave\" x=\"48\" y=\"7\" fill=\"#3e3e3e\"></use></g></svg><div class=\"inner\"><a href=\"https://catalyst.sexy/\"><img src=\"https://catalyst.sexy/images/logo.png\" class=\"fadein\" alt=\"Catalyst Client\"></a></div></header><main class=\"fadein\"><h1 class=\"spacer\"><br></h1><div class=\"topnav\"></div><h1 class=\"spacer\"><br><br></h1><div class=\"container center\" style=\"width: 50%\"><h1>Authenticated</h1><h3>You can now close this window</h3></div><br><br><br><br><br></main><footer><p class=\"copyright fadein\">Copyright © <span class=\"copyright-year\"></span> Catalyst Development<br><span class=\"lower\"><a href=\"/tos\" target=\"_blank\">TOS</a> &amp; <a href=\"https://discord.catalyst.sexy\" target=\"_blank\">Discord</a></span></p></footer><script src=\"https://catalyst.sexy/lib/jquery-3.4.1.min.js\"></script><script src=\"https://catalyst.sexy/lib/particles.min.js\"></script><script async=\"\" src=\"https://catalyst.sexy/js/particleInit.min.js\"></script></body>";
        new Thread(() -> {
            try {
                if (srv != null) return;
                gui.setState("Waiting...");
                if (!HttpTools.ping("http://minecraft.net")) throw new MicrosoftAuthException("No internet connection");
                srv = HttpServer.create(new InetSocketAddress(59125), 0);
                srv.createContext("/", new HttpHandler() {
                    public void handle(HttpExchange exchange) throws IOException {
                        try {
                            gui.setState("Fetching Token...");
                            byte[] b = done.getBytes(StandardCharsets.UTF_8);
                            exchange.getResponseHeaders().put("Content-Type", Arrays.asList("text/html; charset=UTF-8"));
                            exchange.sendResponseHeaders(200, b.length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(b);
                            os.flush();
                            os.close();
                            String s = exchange.getRequestURI().getQuery();
                            if (s == null) {
                                gui.error("query=null");
                            } else if (s.startsWith("code=")) {
                                accessTokenStep(s.replace("code=", ""), gui);
                            } else if (s.equals("error=access_denied&error_description=The user has denied access to the scope requested by the client application.")) {
                                gui.error("Authentication was cancelled");
                            } else {
                                gui.error(s);
                            }
                        } catch (Throwable t) {
                            if (t instanceof MicrosoftAuthException) {
                                gui.error(t.getLocalizedMessage());
                            } else {
                                t.printStackTrace();
                                gui.error("Unexpected error: " + t.toString());
                            }
                        }
                        stop();
                    }
                });
                srv.start();
                Sys.openURL("https://login.live.com/oauth20_authorize.srf" +
                    "?client_id=54fd49e4-2103-4044-9603-2b028c814ec3" +
                    "&response_type=code" +
                    "&scope=XboxLive.signin%20XboxLive.offline_access" +
                    "&redirect_uri=http://localhost:59125" +
                    "&prompt=consent");
            } catch (Throwable t) {
                if (t instanceof MicrosoftAuthException) {
                    gui.error(t.getLocalizedMessage());
                } else {
                    gui.error("Unexpected error: " + t.toString());
                    t.printStackTrace();
                }
                stop();
            }
        }, "Auth Thread").start();
    }

    public static void stop() {
        try {
            if (srv != null) {
                srv.stop(0);
                srv = null;
            }
        } catch (Throwable ignored) {}
    }

    private static void accessTokenStep(String code, MSAuthScreen gui) throws Throwable {
        PostRequest pr = new PostRequest("https://login.live.com/oauth20_token.srf").header("Content-Type", "application/x-www-form-urlencoded");
        Map<Object, Object> data = new HashMap<>();
        data.put("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3");
        data.put("code", code);
        data.put("grant_type", "authorization_code");
        data.put("redirect_uri", "http://localhost:59125");
        data.put("scope", "XboxLive.signin XboxLive.offline_access");
        pr.post(data);
        if (pr.response() != 200) throw new MicrosoftAuthException("accessToken response: " + pr.response());
        xblStep(gson.fromJson(pr.body(), JsonObject.class).get("access_token").getAsString(), gui);
    }

    private static void xblStep(String token, MSAuthScreen gui) throws Throwable {
        gui.setState("Authenticating...");
        PostRequest pr = new PostRequest("https://user.auth.xboxlive.com/user/authenticate").header("Content-Type", "application/json").header("Accept", "application/json");
        HashMap<Object, Object> map = new HashMap<>();
        HashMap<Object, Object> sub = new HashMap<>();
        sub.put("AuthMethod", "RPS");
        sub.put("SiteName", "user.auth.xboxlive.com");
        sub.put("RpsTicket", "d=" + token);
        map.put("Properties", sub);
        map.put("RelyingParty", "http://auth.xboxlive.com");
        map.put("TokenType", "JWT");
        pr.post(gson.toJson(map));
        if (pr.response() != 200) throw new MicrosoftAuthException("xbl response: " + pr.response());
        xstsStep(gson.fromJson(pr.body(), JsonObject.class).get("Token").getAsString(), gui);
    }

    private static void xstsStep(String xbl, MSAuthScreen gui) throws Throwable {
        PostRequest pr = new PostRequest("https://xsts.auth.xboxlive.com/xsts/authorize").header("Content-Type", "application/json").header("Accept", "application/json");
        HashMap<Object, Object> map = new HashMap<>();
        HashMap<Object, Object> sub = new HashMap<>();
        sub.put("SandboxId", "RETAIL");
        sub.put("UserTokens", Arrays.asList(xbl));
        map.put("Properties", sub);
        map.put("RelyingParty", "rp://api.minecraftservices.com/");
        map.put("TokenType", "JWT");
        pr.post(gson.toJson(map));
        if (pr.response() == 401) throw new MicrosoftAuthException("This account doesn't have Minecraft account linked to it.");
        if (pr.response() != 200) throw new MicrosoftAuthException("xsts response: " + pr.response());
        JsonObject jo = gson.fromJson(pr.body(), JsonObject.class);
        minecraftTokenStep(jo.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0)
            .getAsJsonObject().get("uhs").getAsString(), jo.get("Token").getAsString(), gui);
    }

    private static void minecraftTokenStep(String xbl, String xsts, MSAuthScreen gui) throws Throwable {
        PostRequest pr = new PostRequest("https://api.minecraftservices.com/authentication/login_with_xbox").header("Content-Type", "application/json").header("Accept", "application/json");
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("identityToken", "XBL3.0 x=" + xbl + ";" + xsts);
        pr.post(gson.toJson(map));
        if (pr.response() != 200) throw new MicrosoftAuthException("minecraftToken response: " + pr.response());
        minecraftStoreVerify(gson.fromJson(pr.body(), JsonObject.class).get("access_token").getAsString(), gui);
    }

    private static void minecraftStoreVerify(String token, MSAuthScreen gui) throws Throwable {
        gui.setState("Verifying...");
        GetRequest gr = new GetRequest("https://api.minecraftservices.com/entitlements/mcstore").header("Authorization", "Bearer " + token);
        gr.get();
        if (gr.response() != 200) throw new MicrosoftAuthException("minecraftStore response: " + gr.response());
        if (gson.fromJson(gr.body(), JsonObject.class).getAsJsonArray("items").size() == 0) throw new MicrosoftAuthException("This account does not own the game.");
        minecraftProfileVerify(token, gui);
    }

    private static void minecraftProfileVerify(String token, MSAuthScreen gui) throws Throwable {
        GetRequest gr = new GetRequest("https://api.minecraftservices.com/minecraft/profile").header("Authorization", "Bearer " + token);
        gr.get();
        if (gr.response() != 200) throw new MicrosoftAuthException("minecraftProfile response: " + gr.response());
        JsonObject jo = gson.fromJson(gr.body(), JsonObject.class);
        String name = (String) jo.get("name").getAsString();
        String uuid = (String) jo.get("id").getAsString();
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> {
            if (mc.currentScreen != gui) return;
            try {
                AccountManager.setSession(new Session(name, uuid, token, "mojang"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mc.displayGuiScreen(null);
        });
    }

    public static class MicrosoftAuthException extends Exception {
        private static final long serialVersionUID = 1L;
        public MicrosoftAuthException() {}
        public MicrosoftAuthException(String s) {
            super(s);
        }
    }
}
