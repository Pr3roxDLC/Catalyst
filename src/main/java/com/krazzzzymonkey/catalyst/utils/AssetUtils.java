package com.krazzzzymonkey.catalyst.utils;

import com.krazzzzymonkey.catalyst.Main;
import org.apache.commons.io.FileUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AssetUtils {
    private Thread thread;

    public AssetUtils() {
        this.thread = new Thread(() -> {
            File assetsDir = new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator);
            if (!assetsDir.exists()) {
                assetsDir.mkdirs();
            }
            try {
                Files.copy(getUrlStream("https://auth.catalyst.sexy/get/assets"), new File(assetsDir.getAbsolutePath() + File.separator + "temp").toPath(), StandardCopyOption.REPLACE_EXISTING);
                unzip(assetsDir.getAbsolutePath() + File.separator + "temp", new File(System.getProperty("user.home") + File.separator + "Catalyst" + File.separator));
            } catch (Exception e) {
                Main.logger.error("Unable to fetch Catalyst Assets!");

            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                new File(assetsDir.getAbsolutePath() + File.separator + "temp").delete();
                try {
                    FileUtils.deleteDirectory(new File(assetsDir + File.separator + "assets"));
                } catch (IOException ignored) {
                }
            }));
        });
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    private static InputStream getUrlStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.addRequestProperty("User-Agent", "Catalyst Capes");

        return con.getInputStream();
    }

    private static void unzip(String zipFilePath, File destDir) {
        // create output directory if it doesn't exist
        if (!destDir.exists()) destDir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir.getAbsolutePath() + File.separator + fileName);
                //create directories for sub directories in zip
                String name = newFile.getAbsolutePath().substring(newFile.getAbsolutePath().lastIndexOf(File.separator) + 1);
                if (name.contains(".")) {
                    new File(newFile.getAbsolutePath().replace(name, "")).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }


                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (Exception e) {
            Main.logger.error("Could not download Catalyst textures!");
        }

    }
}
