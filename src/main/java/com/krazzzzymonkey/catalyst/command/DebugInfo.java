package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.utils.paste.PasteBuilder;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DebugInfo extends Command {

    public DebugInfo() {
        super("debuginfo");
    }

    @Override
    public void runCommand(String s, String[] args) {

        final PasteBuilder paste = new PasteBuilder()
            .setName("Catalyst Debug Infos");

        paste.addContent("mods.csv", Loader
            .instance()
            .getIndexedModList()
            .values()
            .stream()
            .map(c -> c.getName() + ", " + c.getModId() + ", " + c.getVersion())
            .collect(Collectors.joining(System.lineSeparator())));

        CompletableFuture.runAsync(() -> {

            List<String> environment = new ArrayList<>();
            environment.add("java vendor, " + System.getProperty("java.vendor"));
            environment.add("java spec vendor, " + System.getProperty("java.specification.vendor"));
            environment.add("java runtime, " + System.getProperty("java.runtime.name"));
            environment.add("java vm, " + System.getProperty("java.vm.name"));
            environment.add("java version, " + System.getProperty("java.version"));
            environment.add("java runtime version, " + System.getProperty("java.runtime.version"));
            environment.add("os arch, " + System.getProperty("os.arch"));
            environment.add("os name, " + System.getProperty("os.name"));
            environment.add("os version, " + System.getProperty("os.version"));
            environment.add("encoding, " + System.getProperty("sun.jnu.encoding"));
            environment.add("cores, " + Runtime.getRuntime().availableProcessors());
            environment.add("memory, " + Math.round(Runtime.getRuntime().freeMemory() / 1024f / 1024f) + "mb / " + Math.round(Runtime.getRuntime().maxMemory() / 1024f / 1024f) + "mb (init " + Math.round(Runtime.getRuntime().totalMemory() / 1024f / 1024f) + "mb)");
            paste.addContent("environment.csv", environment.stream().collect(Collectors.joining(System.lineSeparator())));

            File gameOptions = new File(Wrapper.INSTANCE.mc().gameDir, "options.txt");
            if (gameOptions.isFile() && gameOptions.canRead()) {
                try {
                    paste.addContent("options.txt", Files
                        .lines(gameOptions.toPath())
                        .collect(Collectors.joining(System.lineSeparator())));
                } catch (IOException e) {
                    paste.addContent("options.txt", e.getMessage());
                }
            } else {
                paste.addContent("options.txt", "options.txt not a file or not readable");
            }

            File hacksConfig = new File(FileManager.CATALYST_DIR, "hacks.json");
            if (hacksConfig.isFile() && hacksConfig.canRead()) {
                try {
                    paste.addContent("hacks.json", Files
                        .lines(hacksConfig.toPath())
                        .collect(Collectors.joining(System.lineSeparator())));
                } catch (IOException e) {
                    paste.addContent("hacks.json", e.getMessage());
                }
            } else {
                paste.addContent("hacks.json", "hacks.json not a file or not readable");
            }

            paste.post();

        });

    }

    @Override
    public String getDescription() {
        return "Upload debug info to a paste.";
    }

    @Override
    public String getSyntax() {
        return "debuginfo";
    }

}
