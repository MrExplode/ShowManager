package me.sunstorm.showmanager.scheduler;

import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class Scheduler implements Listener {
    private final List<ScheduledEvent> events = new ArrayList<>();

    public void addEvent(ScheduledEvent event) {

    }

    @EventCall
    public void onTimecodeChange(TimecodeChangeEvent event) {

    }
}
