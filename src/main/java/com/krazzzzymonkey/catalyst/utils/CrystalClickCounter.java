package com.krazzzzymonkey.catalyst.utils;

import java.util.LinkedList;
import java.util.Queue;


public class CrystalClickCounter {

    private final Queue<Long> crystals = new LinkedList<>();


    public void onBreak() {
        crystals.add(System.currentTimeMillis() + 1000L);
    }


    public int getCps() {
        long time = System.currentTimeMillis();
    try {
        while (!crystals.isEmpty() && crystals.peek() < time) {
            crystals.remove();
        }
    }catch (Exception e){
        // empty catch block
    }
        return crystals.size();
    }

}
