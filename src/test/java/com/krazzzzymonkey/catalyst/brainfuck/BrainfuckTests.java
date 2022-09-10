package com.krazzzzymonkey.catalyst.brainfuck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

class BrainfuckTests {
    private static String loadProgram(String filename) {
        URL resource = Objects.requireNonNull(BrainfuckTests.class.getClassLoader().getResource("brainfuck/" + filename));
        Path path = new File(resource.getFile()).toPath();
        try {
            return Files.readAllLines(path).stream().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    @Test
    void hello_world() {
        Assertions.assertEquals("Hello World!\n", Interpreter.run(loadProgram("hello_world.txt")));
    }

    @Test
    void adding_two_values() {
        Assertions.assertEquals("7", Interpreter.run(loadProgram("adding_two_values.txt")));
    }

    @Test
    void echo() {
        Assertions.assertEquals("test", Interpreter.run(loadProgram("echo.txt"), Input.of("test")));
    }

    @Test
    void ascii_3d() {
        Assertions.assertEquals("  ___                                      _____  ___     ___     ___\n" +
            " /\\  \\                      ___           /  ___\\_\\  \\__ _\\  \\__ _\\  \\__ \n" +
            " \\ \\  \\____ ___  ___ ______/\\__\\  __  ____\\  \\__/\\__   _\\\\__   _\\\\__   _\\\n" +
            "  \\ \\   __ \\\\  \\/ __\\\\___   \\ _/_/  \\/ _  \\_   __\\_/\\__\\//_/\\__\\//_/\\__\\/\n" +
            "   \\ \\  \\/\\ \\\\   /__//___\\   \\\\  \\\\   / \\  \\\\  \\_/ \\/__/   \\/__/   \\/__/  \n" +
            "    \\ \\  \\_\\ \\\\  \\   /\\  _    \\\\  \\\\  \\\\ \\  \\\\  \\\n" +
            "     \\ \\______\\\\__\\  \\ \\___/\\__\\\\__\\\\__\\\\ \\__\\\\__\\\n" +
            "      \\/______//__/   \\/__/\\/__//__//__/ \\/__//__/\n", Interpreter.run(loadProgram("ascii_3d.txt")));
    }

    @Test
    void sierpinski() {
        Assertions.assertEquals(
            "###########################\n" +
                "# ## ## ## ## ## ## ## ## #\n" +
                "###########################\n" +
                "###   ######   ######   ###\n" +
                "# #   # ## #   # ## #   # #\n" +
                "###   ######   ######   ###\n" +
                "###########################\n" +
                "# ## ## ## ## ## ## ## ## #\n" +
                "###########################\n" +
                "#########         #########\n" +
                "# ## ## #         # ## ## #\n" +
                "#########         #########\n" +
                "###   ###         ###   ###\n" +
                "# #   # #         # #   # #\n" +
                "###   ###         ###   ###\n" +
                "#########         #########\n" +
                "# ## ## #         # ## ## #\n" +
                "#########         #########\n" +
                "###########################\n" +
                "# ## ## ## ## ## ## ## ## #\n" +
                "###########################\n" +
                "###   ######   ######   ###\n" +
                "# #   # ## #   # ## #   # #\n" +
                "###   ######   ######   ###\n" +
                "###########################\n" +
                "# ## ## ## ## ## ## ## ## #\n" +
                "###########################\n",
            Interpreter.run(loadProgram("sierpinski.txt"), Input.of("3")));
    }
}
