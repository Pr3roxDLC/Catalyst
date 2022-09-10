package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.managers.ModuleManager;
import com.krazzzzymonkey.catalyst.utils.system.Wrapper;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import joptsimple.internal.Strings;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketTabComplete;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// TODO: bruteforce alphabetically

public class PluginsGetter extends Command {

    @EventHandler
    private final EventListener<PacketEvent> onPacket = new EventListener<>(e -> {

        if (!(e.getPacket() instanceof SPacketTabComplete)) return;

        SPacketTabComplete packet = (SPacketTabComplete) e.getPacket();

        List<String> plugins = new CopyOnWriteArrayList<>();
        String[] commands = packet.getMatches();

        for (String s : commands) {
            String[] command = s.split(":");

            if (command.length > 1) {
                String pluginName = command[0].replace("/", "");
                if (!plugins.contains(pluginName)) {
                    plugins.add(pluginName);
                }
            }
        }

        if (!plugins.isEmpty()) {
            // TODO: is this weird?
            // plugins.sort(String.CASE_INSENSITIVE_ORDER);
            ChatUtils.message("Plugins \u00a77(\u00a78" + plugins.size() + "\u00a77): \u00a79" + Strings.join(plugins.toArray(new String[0]), "\u00a77, \u00a79"));
        } else {
            ChatUtils.error("No plugins found.");
        }

        mc.addScheduledTask(() -> ModuleManager.EVENT_MANAGER.unregister(this));

    });

    public PluginsGetter() {
        super("pluginsgetter");
    }

    @Override
    public void runCommand(String s, String[] args) {
        ModuleManager.EVENT_MANAGER.register(this);
        Wrapper.INSTANCE.sendPacket(new CPacketTabComplete("/", null, false));
    }

    @Override
    public String getDescription() {
        return "Gets server plugins";
    }

    @Override
    public String getSyntax() {
        return getName();
    }

}
