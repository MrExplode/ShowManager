package me.sunstorm.showmanager.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Framerate {
    private static final AtomicInteger framerate = new AtomicInteger(25);

    public static void set(int value) {
        if (value != 24 && value != 25 && value != 30)
            throw new IllegalArgumentException("Invalid framerate: " + value);
        framerate.set(value);
    }

    public static int get() {
        return framerate.get();
    }
}
