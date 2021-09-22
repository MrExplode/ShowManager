package me.sunstorm.showmanager.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

@Slf4j
public class SilentClose {

    public static void close(@NotNull Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            log.warn("Failed to close closeable", e);
        }
    }
}
