package me.sunstorm.showmanager.scheduler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.illposed.osc.OSCMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.EventPriority;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.scheduler.EventAddEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.scheduler.impl.ScheduledJumpEvent;
import me.sunstorm.showmanager.scheduler.impl.ScheduledOscEvent;
import me.sunstorm.showmanager.scheduler.impl.ScheduledPauseEvent;
import me.sunstorm.showmanager.scheduler.impl.ScheduledStopEvent;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Getter
public class EventScheduler extends SettingsHolder implements Listener, InjectRecipient {
    private final List<ScheduledEvent> scheduledEvents = new CopyOnWriteArrayList<>();
    @Inject
    private EventBus eventBus;
    @Setter private boolean enabled = false;
    private int lastIndex = -1;
    private Timecode lastTime = new Timecode(-1);

    public EventScheduler() {
        super("scheduler");
        inject();
        load();
        eventBus.register(this);
    }

    public void addEvent(ScheduledEvent event) {
        scheduledEvents.add(event);
        new EventAddEvent(event).call(eventBus);
        //only sort if the last event's time is bigger than the added event
        if (scheduledEvents.size() != 0 && event.getExecuteTime().compareTo(scheduledEvents.get(scheduledEvents.size() - 1).getExecuteTime()) < 0)
            scheduledEvents.sort(Comparator.comparing(ScheduledEvent::getExecuteTime));
    }

    public JsonArray getEvents() {
        JsonArray array = new JsonArray();
        scheduledEvents.forEach(e -> array.add(e.getData()));
        return array;
    }

    @EventCall(EventPriority.LOWEST)
    public void onTimeChange(TimecodeChangeEvent e) {
        if (!enabled || scheduledEvents.size() == 0 || lastIndex + 1 == scheduledEvents.size()) return;
        Timecode current = e.getTime();
        //no exec yet or time was reset
        int i = 0;
        if (lastIndex != -1 || current.compareTo(lastTime) >= 0) {
            //iterating from the last executed index
            i = lastIndex + 1;
        }
        for (; i < scheduledEvents.size(); i++) {
            val event = scheduledEvents.get(i);
            //not there yet, break
            if (current.compareTo(event.getExecuteTime()) < 0) {
                break;
            //we are there, exec and update indexes
            } else if (current.compareTo(event.getExecuteTime()) == 0) {
                event.execute();
                lastIndex = i;
                lastTime = current;
            //already past the relevant things, break
            } else if (current.compareTo(event.getExecuteTime()) > 0) {
                break;
            }
        }
    }

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        JsonArray eventArray = new JsonArray();
        scheduledEvents.forEach(e -> eventArray.add(e.getData()));
        data.add("events", eventArray);
        return data;
    }

    @Override
    public void onLoad(JsonObject object) {
        enabled = object.get("enabled").getAsBoolean();
        JsonArray eventArray = object.get("events").getAsJsonArray();
        eventArray.forEach(e -> {
            JsonObject element = e.getAsJsonObject();
            String type = element.get("type").getAsString();
            switch (type) {
                case "jump": {
                    scheduledEvents.add(new ScheduledJumpEvent(Constants.GSON.fromJson(element.get("time"), Timecode.class), Constants.GSON.fromJson(element.get("jumpTime"), Timecode.class)));
                    break;
                }
                case "osc": {
                    scheduledEvents.add(new ScheduledOscEvent(Constants.GSON.fromJson(element.get("time"), Timecode.class), Constants.GSON.fromJson(element.get("packet"), OSCMessage.class)));
                    break;
                }
                case "pause": {
                    scheduledEvents.add(new ScheduledPauseEvent(Constants.GSON.fromJson(element.get("time"), Timecode.class)));
                    break;
                }
                case "stop": {
                    scheduledEvents.add(new ScheduledStopEvent(Constants.GSON.fromJson(element.get("time"), Timecode.class)));
                    break;
                }
                default: {
                    log.warn("Found unknown scheduled event type: {}", type);
                    break;
                }
            }
        });
    }
}
