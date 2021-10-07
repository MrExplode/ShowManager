package me.sunstorm.showmanager.http.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.osc.OscHandler;
import me.sunstorm.showmanager.scheduler.EventScheduler;

@Slf4j
public class SchedulerController implements InjectRecipient {
    @Inject
    private EventScheduler scheduler;
    @Inject
    private OscHandler oscHandler;

    public SchedulerController() {
        inject();
    }

    public void handleRecording(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("enabled"))
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("OSC recording {}", value ? "started" : "stopped");
        oscHandler.setRecording(value);
    }
}
