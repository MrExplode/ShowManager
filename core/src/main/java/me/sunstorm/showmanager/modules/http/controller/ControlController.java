package me.sunstorm.showmanager.modules.http.controller;

import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.PathPrefix;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.util.JsonBuilder;
import me.sunstorm.showmanager.util.Timecode;

@PathPrefix("/control")
public class ControlController implements InjectRecipient {
    @Inject
    private Worker worker;

    public ControlController() {
        inject();
    }

    @Get("/play")
    public void getPlay(Context ctx) {
        ctx.json(new JsonBuilder().addProperty("playing", worker.isPlaying()).build());
    }

    @Post("/play")
    public void postPlay(Context ctx) {
        worker.play();
    }

    @Post("/pause")
    public void postPause(Context ctx) {
        worker.pause();
    }

    @Post("/stop")
    public void postStop(Context ctx) {
        worker.stop();
    }

    @Post("/set")
    public void postSet(Context ctx) {
        var data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("hour") || !data.has("min") || !data.has("sec") || !data.has("frame"))
            throw new BadRequestResponse();
        worker.setTime(new Timecode(data.get("hour").getAsInt(), data.get("min").getAsInt(), data.get("sec").getAsInt(), data.get("frame").getAsInt()));
    }

    @Post("/quickjump")
    public void quickJump(Context ctx) {
        var data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("amount"))
            throw new BadRequestResponse();
        int amount = data.get("amount").getAsInt();
        if (amount > 0)
            worker.setTime(worker.getCurrentTime().add(new Timecode(0, 0, amount, 0)));
        else
            worker.setTime(worker.getCurrentTime().subtract(new Timecode(0, 0, Math.abs(amount), 0)));
    }
}
