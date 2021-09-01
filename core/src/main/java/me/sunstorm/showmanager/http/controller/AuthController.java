package me.sunstorm.showmanager.http.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpResponseException;
import me.sunstorm.showmanager.ShowManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class AuthController implements Handler {

    @Override
    public void handle(@NotNull Context ctx) {
        String value = ctx.header(ShowManager.getInstance().getConfig().getHttpConfig().getHeader());
        if (!ShowManager.getInstance().getConfig().getHttpConfig().getValue().equals(value))
            throw new HttpResponseException(403, "Invalid or missing auth credentials!", Collections.emptyMap());
    }
}
