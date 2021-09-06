package me.sunstorm.showmanager.terminable;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Terminables {
    private static final Set<Terminable> terminables = ConcurrentHashMap.newKeySet();

    protected static void addTerminable(Terminable terminable) {
        if (terminables.contains(terminable))
            throw new IllegalArgumentException("Terminable already registered");
        terminables.add(terminable);
    }

    public static void shutdownAll() {
        log.info("Shutdown initiated...");
        terminables.forEach(t -> {
            try {
                t.shutdown();
            } catch (Exception e) {
                log.error("Failed to terminate " + t, e);
            }
        });
    }
}
