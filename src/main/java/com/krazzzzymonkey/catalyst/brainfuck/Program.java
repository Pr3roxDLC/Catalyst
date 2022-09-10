package com.krazzzzymonkey.catalyst.brainfuck;

public class Program {
    public final char[] instructions;
    public int pointer;

    public Program(String instructions) {
        this.instructions = instructions.replaceAll("[^><+\\-.,\\[\\]]", "").toCharArray();
        this.pointer = 0;
    }

    public char instruction() {
        return instructions[pointer];
    }

    public char instruction(int pointer) {
        return instructions[pointer];
    }

    public boolean pointerValid() {
        return pointer >= 0 && pointer < instructions.length;
    }

    public void step() {
        pointer++;
    }
}
