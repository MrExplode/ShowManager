package me.sunstorm.showmanager.modules.scheduler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.EventPriority;
import me.sunstorm.showmanager.eventsystem.events.scheduler.EventAddEvent;
import me.sunstorm.showmanager.eventsystem.events.scheduler.SchedulerExecuteEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeSetEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeStopEvent;
import me.sunstorm.showmanager.modules.ToggleableModule;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class handles the scheduled events:<br>
 *  - OSC packet dispatch<br>
 *  - Internal actions (jump, pause, stop)
 */
@Singleton
public class SchedulerModule extends ToggleableModule {
    private static final Logger log = LoggerFactory.getLogger(SchedulerModule.class);

    private final List<ScheduledEvent> scheduledEvents = new CopyOnWriteArrayList<>();
    private int lastIndex = -1;

    @Inject
    public SchedulerModule(EventBus bus) {
        super(bus);
        init();
        scheduledEvents.sort(Comparator.comparing(ScheduledEvent::getExecuteTime));
    }

    public void addEvent(ScheduledEvent event) {
        scheduledEvents.add(event);
        new EventAddEvent(event).call(eventBus);
        //only sort if the last event's time is bigger than the added event
//        if (!scheduledEvents.isEmpty() && event.getExecuteTime().compareTo(scheduledEvents.getLast().getExecuteTime()) < 0)
            scheduledEvents.sort(Comparator.comparing(ScheduledEvent::getExecuteTime));
    }

    public JsonArray getEvents() {
        JsonArray array = new JsonArray();
        scheduledEvents.forEach(e -> array.add(e.getData()));
        return array;
    }

    @EventCall(EventPriority.LOWEST)
    public void onTimeChange(TimecodeChangeEvent e) {
        if (!isEnabled() || scheduledEvents.isEmpty() || lastIndex + 1 == scheduledEvents.size()) return;
        Timecode current = e.getTime();

        for (ScheduledEvent event : scheduledEvents) {
            if (event.getExecuteTime().equals(current)) {
                log.info("Executing scheduled event: {}", event.getType());
                new SchedulerExecuteEvent(event).call(eventBus);
                event.execute();
            }
        }
    }

    @EventCall
    public void onTimeStop(TimecodeStopEvent e) {
        lastIndex = -1;
    }

    @EventCall
    public void onTimeSet(TimecodeSetEvent e) {
        //rerun search by resetting index to default
        lastIndex = -1;
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", isEnabled());
        JsonArray eventArray = new JsonArray();
        scheduledEvents.forEach(e -> eventArray.add(e.getData()));
        data.add("events", eventArray);
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonElement element) {
        var object = element.getAsJsonObject();
        setEnabled(object.get("enabled").getAsBoolean());
        JsonArray eventArray = object.get("events").getAsJsonArray();
        eventArray.forEach(e -> {
            ScheduledEvent event = Constants.GSON.fromJson(e, ScheduledEvent.class);
            if (event != null)
                scheduledEvents.add(event);
            else
                log.warn("Found unknown event: {}", e);
        });
    }

    @Override
    public String getName() {
        return "scheduler";
    }

    // generated

    public List<ScheduledEvent> getScheduledEvents() {
        return scheduledEvents;
    }
}
