package com.krazzzzymonkey.catalyst.managers;

import com.krazzzzymonkey.catalyst.command.*;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandManager {
    public static ArrayList<Command> commands = new ArrayList<>();
    private volatile static CommandManager instance;

    public static String prefix = "~";

    public CommandManager() {
        FileManager.loadPrefix();
        addCommands();
    }

    public void addCommands() {
       // addCommand(new Goto());
        addCommand(new Panic());
        addCommand(new Profile());
        addCommand(new Toggle());
        addCommand(new EntityDesync());
        addCommand(new Drawn());
        addCommand(new NameHistory());
        addCommand(new MsgAll());
        addCommand(new Help());
        addCommand(new Module());
        addCommand(new Bind());
        addCommand(new VClip());
        addCommand(new ChatMention());
        addCommand(new DebugInfo());
        addCommand(new Login());
        addCommand(new Friend());
        addCommand(new Enemy());
        addCommand(new Prefix());
        addCommand(new BreadcrumbsCommand());
        addCommand(new Peek());
        addCommand(new PeekBook());
        addCommand(new Font());
        addCommand(new AutoGG());
        addCommand(new FaceTowards());/*
        addCommand(new MapDump());
        addCommand(new NbtDump());*/
        addCommand(new PluginsGetter());
        addCommand(new InventoryCleaner());
        commands.sort(Comparator.comparing(Command::getCommand));
    }


    public void runCommands(String s) {
        String[] split = s.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // 086 / KAMI
        String commandName = split[0];
        String args = s.substring(commandName.length()).trim();
        AtomicBoolean commandResolved = new AtomicBoolean(false);

        commands.forEach(c -> {
            if (c.getCommand().equalsIgnoreCase(commandName)) {
                commandResolved.set(true);
                try {
                    c.runCommand(args, args.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                } catch (Exception e) {
                    ChatUtils.error(c.getSyntax());
                }
            }

        });
        {

        }
        if (!commandResolved.get()) {
            ChatUtils.error("Cannot resolve internal command: \u00a7c" + commandName);
        }
    }

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    public static void addCommand(Command c) {
        commands.add(c);
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
