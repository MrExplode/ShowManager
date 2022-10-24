package me.sunstorm.showmanager.modules.http.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.scheduler.EventDeleteEvent;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.PathPrefix;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.modules.osc.OscModule;
import me.sunstorm.showmanager.modules.scheduler.SchedulerModule;
import me.sunstorm.showmanager.modules.scheduler.ScheduledEvent;
import org.jetbrains.annotations.NotNull;

@Slf4j
@PathPrefix("/scheduler")
public class SchedulerController implements InjectRecipient {
    @Inject
    private EventBus eventBus;
    @Inject
    private SchedulerModule scheduler;
    @Inject
    private OscModule oscModule;

    public SchedulerController() {
        inject();
    }

    @Get("/record")
    public void getRecording(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("recording", oscModule.isRecording());
        ctx.json(data);
    }

    @Post("/record")
    public void postRecording(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("enabled"))
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("OSC recording {}", value ? "started" : "stopped");
        oscModule.setRecording(value);
    }

    @Get("/events")
    public void getEvents(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        data.add("events", scheduler.getEvents());
        ctx.json(data);
    }

    @Post("/events/add")
    public void addEvent(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("event"))
            throw new BadRequestResponse();
        log.info("Adding scheduled event: {}", data.get("event"));
        scheduler.addEvent(Constants.GSON.fromJson(data.get("event"), ScheduledEvent.class));
    }

    @Post("/events/delete")
    public void deleteEvents(@NotNull Context ctx) {
        JsonArray data = JsonParser.parseString(ctx.body()).getAsJsonArray();
        boolean success = scheduler.getScheduledEvents().removeIf(e -> data.contains(e.getData()));
        if (success)
            log.info("Deleted some events duh");
        else
            log.warn("Didn't find any matching events to delete");
        new EventDeleteEvent().call(eventBus);
    }
}
