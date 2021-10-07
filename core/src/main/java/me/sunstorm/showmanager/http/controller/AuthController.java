package me.sunstorm.showmanager.http.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpResponseException;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.http.HttpHandler;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class AuthController implements Handler, InjectRecipient {
    @Inject
    private HttpHandler httpHandler;

    public AuthController() {
        inject();
    }

    @Override
    public void handle(@NotNull Context ctx) {
        String value = ctx.header(httpHandler.getHeader());
        if (!httpHandler.getSecret().equals(value))
            throw new HttpResponseException(403, "Invalid or missing auth credentials!", Collections.emptyMap());
    }
}
