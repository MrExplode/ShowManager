package me.sunstorm.showmanager.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public class SilentClose {
    private static final Logger log = LoggerFactory.getLogger(SilentClose.class);

    public static void close(@NotNull Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            log.warn("Failed to close closeable", e);
        }
    }
}
