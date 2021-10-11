package me.sunstorm.showmanager.http.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.scheduler.EventDeleteEvent;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.osc.OscHandler;
import me.sunstorm.showmanager.scheduler.EventScheduler;

import java.util.function.Predicate;

@Slf4j
public class SchedulerController implements InjectRecipient {
    @Inject
    private EventBus eventBus;
    @Inject
    private EventScheduler scheduler;
    @Inject
    private OscHandler oscHandler;

    public SchedulerController() {
        inject();
    }

    public void getRecording(Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("recording", oscHandler.isRecording());
        ctx.json(data);
    }

    public void postRecording(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("enabled"))
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("OSC recording {}", value ? "started" : "stopped");
        oscHandler.setRecording(value);
    }

    public void getEvents(Context ctx) {
        JsonObject data = new JsonObject();
        data.add("events", scheduler.getEvents());
        ctx.json(data);
    }

    public void deleteEvents(Context ctx) {
        JsonArray data = JsonParser.parseString(ctx.body()).getAsJsonArray();
        scheduler.getScheduledEvents().removeIf(e -> data.contains(e.getData()));
        new EventDeleteEvent().call(eventBus);
    }
}
