package com.krazzzzymonkey.catalyst.brainfuck;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Input implements Iterator<Byte> {
    private final byte[] bytes;
    private int pointer = 0;

    public Input(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Input of(String str) {
        return new Input(str.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean hasNext() {
        return pointer < bytes.length;
    }

    @Override
    public Byte next() {
        if (!hasNext()) return 0;
        return bytes[pointer++];
    }
}
