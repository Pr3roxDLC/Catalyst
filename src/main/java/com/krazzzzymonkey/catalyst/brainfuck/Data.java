package com.krazzzzymonkey.catalyst.brainfuck;

import java.util.HashMap;
import java.util.Map;

public class Data {

    private final Map<Long, Byte> data = new HashMap<>();
    public int pointer;

    public byte get(long dataPointer) {
        return data.getOrDefault(dataPointer, (byte) 0);
    }

    public void put(long dataPointer, byte b) {
        if (b == 0) {
            data.remove(dataPointer);
            return;
        }
        data.put(dataPointer, b);
    }

    public void inc(long dataPointer) {
        put(dataPointer, (byte) (get(dataPointer) + 1));
    }

    public void dec(long dataPointer) {
        put(dataPointer, (byte) (get(dataPointer) - 1));
    }
}
