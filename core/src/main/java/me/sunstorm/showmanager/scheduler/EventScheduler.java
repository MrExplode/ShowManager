package me.sunstorm.showmanager.scheduler;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.EventPriority;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class EventScheduler implements Terminable, Listener {
    private final List<ScheduledEvent> scheduledEvents = new CopyOnWriteArrayList<>();
    @Setter private boolean enabled = false;
    private int lastIndex = -1;
    private Timecode lastTime = new Timecode(-1);

    public EventScheduler() {
        register();
        ShowManager.getInstance().getEventBus().register(this);
    }

    public void addEvent(ScheduledEvent event) {
        scheduledEvents.add(event);
        //only sort if the last event's time is bigger than the added event
        if (scheduledEvents.size() != 0 && event.getExecuteTime().compareTo(scheduledEvents.get(scheduledEvents.size() - 1).getExecuteTime()) < 0)
            scheduledEvents.sort(Comparator.comparing(ScheduledEvent::getExecuteTime));
    }

    @EventCall(EventPriority.LOWEST)
    private void onTimeChange(TimecodeChangeEvent e) {
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
    public void shutdown() throws Exception {
        //save
    }
}
