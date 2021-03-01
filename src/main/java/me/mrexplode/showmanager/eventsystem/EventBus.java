package me.mrexplode.showmanager.eventsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.mrexplode.showmanager.eventsystem.events.CancellableEvent;
import me.mrexplode.showmanager.eventsystem.events.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class EventBus {
    private final Map<Class<?>, List<ListenerContainer>> listeners = new HashMap<>();
    private final Predicate<Method> methodPredicate = method -> method.isAnnotationPresent(EventCall.class) && method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void call(Event event) {
        call(false, event);
    }

    public void call(boolean async, Event event) {
        if (async) {
            if (event instanceof CancellableEvent) {
                log.error("[EventHandler] Called cancellable event (" + event.getClass().getSimpleName() + ") asynchronously");
            } else {
                executor.execute(() -> executeEvent(event));
            }
        } else {
            executeEvent(event);
        }
    }

    private void executeEvent(Event event) {
        List<ListenerContainer> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null)
            return;

        log.debug("[EventHandler] Invoking " + event.getClass().getSimpleName());
        eventListeners.forEach(eventContainer -> {
            try {
                eventContainer.getMethod().setAccessible(true);
                eventContainer.getMethod().invoke(eventContainer.getInstance(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Failed to invoke event: " + event.getClass().getSimpleName(), e);
            }
        });
    }

    public void register(Listener listener) {
        log.debug("Registering listener: " + listener.getClass().getSimpleName());
        for (Method method : Arrays.stream(listener.getClass().getDeclaredMethods()).filter(methodPredicate).collect(Collectors.toList())) {
            Class<?> eventType = method.getParameterTypes()[0];
            EventPriority priority = method.getAnnotation(EventCall.class).priority();
            listeners.computeIfAbsent(eventType, typeList -> new CopyOnWriteArrayList<>()).add(new ListenerContainer(method, listener, priority));
            listeners.get(eventType).sort(Comparator.comparingInt(o -> o.getPriority().getPriority()));
            log.debug("Registering method: " + method.getName() + " type: " + eventType.getSimpleName());
        }
    }

    public void unregister(Listener listener) {
        listeners.values().forEach(list -> list.removeIf(container -> container.getInstance().equals(listener)));
    }

    @Getter
    @AllArgsConstructor
    static class ListenerContainer {
        private final Method method;
        private final Listener instance;
        private final EventPriority priority;
    }
}
