package me.mrexplode.timecode.eventsystem;

import me.mrexplode.timecode.eventsystem.events.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventBus {
    private final Map<Class<?>, List<Method>> listeners = new HashMap<>();
    private final Predicate<Method> methodPredicate = method -> method.isAnnotationPresent(EventCall.class) && method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(Event.class);

    public void call(Event event) {
        List<Method> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null)
            return;

        eventListeners.forEach(eventMethod -> {
            try {
                eventMethod.invoke(EventBus.this, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public void register(Listener listener) {
        for (Method method : Arrays.stream(listener.getClass().getMethods()).filter(methodPredicate).collect(Collectors.toList())) {
            Class<?> eventType = method.getParameterTypes()[0];
            listeners.computeIfAbsent(eventType, typeList -> new CopyOnWriteArrayList<>()).add(method);
        }
    }

    public void unregister(Listener listener) {
        listeners.values().forEach(list -> list.removeAll(Arrays.stream(listener.getClass().getMethods()).filter(methodPredicate).collect(Collectors.toList())));
    }
}
