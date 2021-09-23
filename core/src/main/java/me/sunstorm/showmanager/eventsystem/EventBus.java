package me.sunstorm.showmanager.eventsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.eventsystem.registry.EventConverter;
import me.sunstorm.showmanager.eventsystem.registry.EventWrapper;
import me.sunstorm.showmanager.redis.AbstractMessageHandler;
import me.sunstorm.showmanager.redis.converter.Converter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class EventBus extends AbstractMessageHandler<EventWrapper> {
    private final Map<Class<?>, List<ListenerContainer>> listeners = new HashMap<>();
    private final Predicate<Method> methodPredicate = method -> method.isAnnotationPresent(EventCall.class) && method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0]);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExecutorFactory executorFactory = new ExecutorFactory();

    public EventBus() {
        super("eventbus");
        log.info("Loading EventBus...");
    }

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
        //todo send redis
    }

    private void executeEvent(Event event) {
        List<ListenerContainer> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null)
            return;

        eventListeners.forEach(eventContainer -> {
            try {
                eventContainer.execute(event);
            } catch (Throwable e) {
                log.error("Failed to invoke event: " + event.getClass().getSimpleName(), e);
            }
        });
    }

    public void register(Listener listener) {
        log.debug("Registering listener: " + listener.getClass().getSimpleName());
        for (Method method : Arrays.stream(listener.getClass().getDeclaredMethods()).filter(methodPredicate).collect(Collectors.toList())) {
            try {
                Class<?> eventType = method.getParameterTypes()[0];
                EventPriority priority = method.getAnnotation(EventCall.class).value();
                method.setAccessible(true);
                EventExecutor executor = executorFactory.create(listener, method);
                listeners.computeIfAbsent(eventType, typeList -> new CopyOnWriteArrayList<>()).add(new ListenerContainer(executor, listener, priority));
                listeners.get(eventType).sort(Comparator.comparingInt(o -> o.getPriority().getPriority()));
                log.debug("Registering method: " + method.getName() + " type: " + eventType.getSimpleName());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Failed to create executor for '{}' method", method.getName(), e);
            }
        }
    }

    public void unregister(Listener listener) {
        listeners.values().forEach(list -> list.removeIf(container -> container.getInstance().equals(listener)));
    }

    @Override
    public void handleMessage(EventWrapper message) {
        if (message.isAsync()) {
            if (message.getEvent() instanceof CancellableEvent) {
                log.error("[EventHandler] Called cancellable event (" + message.getEvent().getClass().getSimpleName() + ") asynchronously");
            } else {
                executor.execute(() -> executeEvent(message.getEvent()));
            }
        } else {
            executeEvent(message.getEvent());
        }
    }

    @Override
    public Converter<EventWrapper> getConverter() {
        return new EventConverter();
    }

    @Getter
    @AllArgsConstructor
    static class ListenerContainer {
        private final EventExecutor executor;
        private final Listener instance;
        private final EventPriority priority;

        public void execute(Event event) {
            executor.execute(event, instance);
        }
    }
}
