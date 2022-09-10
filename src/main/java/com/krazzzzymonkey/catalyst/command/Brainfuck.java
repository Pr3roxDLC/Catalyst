package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.brainfuck.Interpreter;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Brainfuck extends Command {
    public Brainfuck() {
        super("brainfuck");
    }

    @Override
    public void runCommand(String s, String[] args) {
        if (args.length < 2) {
            ChatUtils.error("Usage: " + getSyntax());
            return;
        }
        String input = "";
        if (args.length > 2) {
            input = String.join("", Arrays.copyOfRange(args, 2, args.length));
        }
        String code;
        if (args[0].equalsIgnoreCase("eval")) {
            code = args[1];
        } else if (args[0].equalsIgnoreCase("exec")) {
            try {
                code = String.join("", Files.readAllLines(new File(args[1]).toPath()));
            } catch (IOException e) {
                ChatUtils.error("Error: " + e.getMessage());
                ChatUtils.error("Usage: " + getSyntax());
                return;
            }
        } else {
            ChatUtils.error("Usage: " + getSyntax());
            return;
        }
        try {
            ChatUtils.normalMessage(Interpreter.run(code, input));
        } catch (Exception e) {
            ChatUtils.error("Error: " + e.getMessage());
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Brainfuck interpreter";
    }

    @Override
    public String getSyntax() {
        return "brainfuck <eval> <code> <input> / <exec> <file> <input>";
    }
}
