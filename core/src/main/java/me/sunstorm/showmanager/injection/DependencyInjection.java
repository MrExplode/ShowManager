package me.sunstorm.showmanager.injection;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.sunstorm.showmanager.terminable.Terminables;
import me.sunstorm.showmanager.terminable.statics.StaticTerminable;
import me.sunstorm.showmanager.terminable.statics.Termination;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Simple dependency injection system. This doesn't handle instance creation,
 * injection is done by explicitly invoking {@link InjectRecipient#inject()}<br>
 * Instance providers can be registered for injection by {@link #registerProvider(Class, Supplier)}.
 * Provided instances can be updated by {@link #updateProvider(Class, Supplier)}
 */
@Slf4j
public class DependencyInjection implements StaticTerminable {
    private static final Map<Class<?>, Supplier<?>> providerMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Set<InjectRecipient>> injectMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Field>> fieldCache = new ConcurrentHashMap<>();

    public static <T> void registerProvider(@NotNull Class<T> type, Supplier<T> provider) {
        log.debug("Registering provider for {}", type.getSimpleName());
        providerMap.put(type, provider);
    }

    public static <T> void updateProvider(Class<T> type, Supplier<T> provider) {
        providerMap.put(type, provider);
        if (injectMap.get(type) != null)
            injectMap.get(type).forEach(recipient -> injectSpecific(type, recipient));
    }

    protected static void performInjection(@NotNull InjectRecipient recipient, boolean watchUpdate) {
        log.debug("Injecting dependencies into {}", recipient.getClass().getSimpleName());
        val clazz = recipient.getClass();
        if (clazz.isAnnotationPresent(Inject.class)) {
            for (Field field : allFields(clazz)) {
                if (!providerMap.containsKey(field.getType()))
                    continue;
                injectField(field, recipient, watchUpdate);
            }
        } else {
            allFields(clazz).stream().filter(f -> f.isAnnotationPresent(Inject.class)).forEach(f -> {
                if (!providerMap.containsKey(f.getType())) {
                    log.error("Found @Inject annotated field ({}#{}) without known provider type {}", clazz.getSimpleName(), f.getName(), f.getType().getSimpleName());
                    return;
                }
                injectField(f, recipient, watchUpdate);
            });
        }
    }

    private static void injectSpecific(Class<?> type, @NotNull InjectRecipient recipient) {
        allFields(recipient.getClass()).stream().filter(f -> f.getType().equals(type) && (f.isAnnotationPresent(Inject.class) || recipient.getClass().isAnnotationPresent(Inject.class))).forEach(f -> injectField(f, recipient, false));
    }

    private static void injectField(@NotNull Field f, InjectRecipient recipient, boolean watchUpdate) {
        try {
            if (providerMap.get(f.getType()) == null) return;
            f.setAccessible(true);
            f.set(recipient, providerMap.get(f.getType()).get());
            if (watchUpdate)
                injectMap.computeIfAbsent(f.getType(), __ -> Collections.newSetFromMap(new WeakHashMap<>())).add(recipient);
        } catch (IllegalAccessException e) {
            log.error("Failed to inject value to field (" + f.getName() + " - " + f.getType().getSimpleName() + ")", e);
        }
    }

    public static List<Field> allFields(Class<?> clazz) {
        if (fieldCache.get(clazz) != null) return fieldCache.get(clazz);
        List<Field> fields = new ArrayList<>();
        if (clazz == null || clazz.equals(Object.class)) return fields;

        try {
            fields.addAll(List.of(clazz.getDeclaredFields()));
        } catch (Exception ignored) {}

        if (clazz.getSuperclass() != null) {
            fields.addAll(allFields(clazz.getSuperclass()));
        }

        fieldCache.put(clazz, fields);
        return fields;
    }

    @Termination
    public static void shutdownStatic() {
        providerMap.clear();
        injectMap.clear();
    }

    static {
        Terminables.addTerminable(DependencyInjection.class);
    }
}
