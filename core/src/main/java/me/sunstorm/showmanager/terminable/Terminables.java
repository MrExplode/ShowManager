package me.sunstorm.showmanager.terminable;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.sunstorm.showmanager.terminable.statics.StaticTerminable;
import me.sunstorm.showmanager.terminable.statics.Termination;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class Terminables {
    private static final Set<TerminateAction> terminations = ConcurrentHashMap.newKeySet();
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    protected static void addTerminable(@NotNull Terminable terminable) {
        terminations.add(terminable::shutdown);
    }

    public static void addTerminable(@NotNull Class<? extends StaticTerminable> staticTerminable) {
        val list = Arrays.stream(staticTerminable.getDeclaredMethods())
                .filter(m -> Modifier.isStatic(m.getModifiers()) && m.isAnnotationPresent(Termination.class))
                .map(m -> {
                    try {
                        m.setAccessible(true);
                        return lookup.unreflect(m);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
        terminations.add(() -> {
            for (MethodHandle methodHandle : list) {
                methodHandle.invokeExact();
            }
        });
    }

    public static void shutdownAll() {
        log.info("Shutdown initiated...");
        terminations.forEach(t -> {
            try {
                t.terminate();
            } catch (Throwable e) {
                log.error("Failed to terminate " + t, e);
            }
        });
        terminations.clear();
    }
}
