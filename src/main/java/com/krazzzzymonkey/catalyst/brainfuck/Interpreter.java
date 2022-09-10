package com.krazzzzymonkey.catalyst.brainfuck;

import java.util.Collections;
import java.util.Iterator;


/**
 * https://gist.github.com/roachhd/dce54bec8ba55fb17d3a
 * https://docs.google.com/document/d/1M51AYmDR1Q9UBsoTrGysvuzar2_Hx69Hz14tsQXWV6M/edit
 */
public class Interpreter {

    public static String run(String code, Iterator<Byte> input) {
        Program prog = new Program(code);
        Data data = new Data();
        StringBuilder out = new StringBuilder();
        while (prog.pointerValid()) {
            switch (prog.instruction()) {
                case '>':
                    // Increment the data pointer (to point to the next cell to the right).
                    data.pointer++;
                    break;
                case '<':
                    // Decrement the data pointer (to point to the next cell to the left).
                    data.pointer--;
                    break;
                case '+':
                    // Increment (increase by one) the byte at the data pointer.
                    data.inc(data.pointer);
                    break;
                case '-':
                    // Decrement (decrease by one) the byte at the data pointer.
                    data.dec(data.pointer);
                    break;
                case '.':
                    // Output the byte at the data pointer.
                    out.append((char) data.get(data.pointer));
                    break;
                case ',':
                    // Accept one byte of input, storing its value in the byte at the data pointer.
                    data.put(data.pointer, input.next());
                    break;
                case '[':
                    // If the byte at the data pointer is zero,
                    // then instead of moving the instruction pointer forward to the next command,
                    // jump it forward to the command after the matching ] command.
                    if (data.get(data.pointer) == 0) prog.pointer = jmpF(prog);
                    break;
                case ']':
                    // If the byte at the data pointer is nonzero,
                    // then instead of moving the instruction pointer forward to the next command,
                    // jump it back to the command after the matching [ command.
                    if (data.get(data.pointer) != 0) prog.pointer = jmpB(prog);
                    break;
            }
            prog.step();
        }
        return out.toString();
    }

    public static String run(String code, String input) {
        return run(code, Input.of(input));
    }

    public static String run(String code) {
        return run(code, Collections.emptyIterator());
    }

    private static int jmpF(Program prog) {
        int level = 0;
        for (int i = prog.pointer; i < prog.instructions.length; i++) {
            if (prog.instruction(i) == '[') level++;
            else if (prog.instruction(i) == ']') {
                level--;
                if (level == 0) {
                    return i - 1;
                }
            }
        }
        throw new BrainfuckException("Missing Bracket");
    }

    private static int jmpB(Program prog) {
        int level = 0;
        for (int i = prog.pointer; i >= 0; i--) {
            if (prog.instruction(i) == ']') level++;
            else if (prog.instruction(i) == '[') {
                level--;
                if (level == 0) {
                    return i - 1;
                }
            }
        }
        throw new BrainfuckException("Missing Bracket");
    }
}
