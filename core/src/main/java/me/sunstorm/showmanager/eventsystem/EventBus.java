package me.sunstorm.showmanager.eventsystem;

import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.eventsystem.registry.EventConverter;
import me.sunstorm.showmanager.eventsystem.registry.EventWrapper;
import me.sunstorm.showmanager.redis.AbstractMessageHandler;
import me.sunstorm.showmanager.redis.converter.Converter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class EventBus extends AbstractMessageHandler<EventWrapper> {
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

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
                log.error("[EventHandler] Called cancellable event ({}) asynchronously", event.getClass().getSimpleName());
            } else {
                executor.execute(() -> executeEvent(event));
            }
        } else {
            executeEvent(event);
        }
        //todo send redis
    }

    private void executeEvent(@NotNull Event event) {
        List<ListenerContainer> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null)
            return;

        eventListeners.forEach(eventContainer -> {
            try {
                eventContainer.execute(event);
            } catch (Throwable e) {
                log.error("Failed to invoke event: {}", event.getClass().getSimpleName(), e);
            }
        });
    }

    public void register(@NotNull Listener listener) {
        log.debug("Registering listener: {}", listener.getClass().getSimpleName());
        for (Method method : Arrays.stream(listener.getClass().getDeclaredMethods()).filter(methodPredicate).toList()) {
            try {
                Class<?> eventType = method.getParameterTypes()[0];
                EventPriority priority = method.getAnnotation(EventCall.class).value();
                method.setAccessible(true);
                EventExecutor executor = executorFactory.create(listener, method);
                listeners.computeIfAbsent(eventType, typeList -> new CopyOnWriteArrayList<>()).add(new ListenerContainer(executor, listener, priority));
                listeners.get(eventType).sort(Comparator.comparingInt(o -> o.priority().getPriority()));
                log.debug("Registering method: {} type: {}", method.getName(), eventType.getSimpleName());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Failed to create executor for '{}' method", method.getName(), e);
            }
        }
    }

    public void unregister(Listener listener) {
        listeners.values().forEach(list -> list.removeIf(container -> container.instance().equals(listener)));
    }

    @Override
    public void handleMessage(EventWrapper message) {
        if (message.async()) {
            if (message.event() instanceof CancellableEvent) {
                log.error("[EventHandler] Called cancellable event ({}) asynchronously", message.event().getClass().getSimpleName());
            } else {
                executor.execute(() -> executeEvent(message.event()));
            }
        } else {
            executeEvent(message.event());
        }
    }

    @Override
    public Converter<EventWrapper> getConverter() {
        return new EventConverter();
    }

    record ListenerContainer(EventExecutor executor, Listener instance, EventPriority priority) {

        public void execute(Event event) {
            executor.execute(event, instance);
        }
    }
}
