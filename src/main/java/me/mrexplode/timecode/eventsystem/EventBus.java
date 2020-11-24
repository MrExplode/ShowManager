package me.mrexplode.timecode.eventsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.timecode.eventsystem.events.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventBus {
    private final Map<Class<?>, List<ListenerContainer>> listeners = new HashMap<>();
    private final Predicate<Method> methodPredicate = method -> method.isAnnotationPresent(EventCall.class) && method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(Event.class);

    public void call(Event event) {
        List<ListenerContainer> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null)
            return;

        eventListeners.forEach(eventContainer -> {
            try {
                eventContainer.getMethod().invoke(eventContainer.getInstance(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public void register(Listener listener) {
        for (Method method : Arrays.stream(listener.getClass().getMethods()).filter(methodPredicate).collect(Collectors.toList())) {
            Class<?> eventType = method.getParameterTypes()[0];
            EventPriority priority = method.getAnnotation(EventCall.class).priority();
            listeners.computeIfAbsent(eventType, typeList -> new CopyOnWriteArrayList<>()).add(new ListenerContainer(method, listener, priority));
            listeners.get(eventType).sort(Comparator.comparingInt(o -> o.getPriority().getPriority()));
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
