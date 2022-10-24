package me.sunstorm.showmanager.modules.http.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpResponseException;
import me.sunstorm.showmanager.modules.http.HttpModule;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class AuthController implements Handler, InjectRecipient {
    @Inject
    private HttpModule httpModule;

    public AuthController() {
        inject();
    }

    @Override
    public void handle(@NotNull Context ctx) {
        String value = ctx.header(httpModule.getHeader());
        if (!httpModule.getSecret().equals(value))
            throw new HttpResponseException(403, "Invalid or missing auth credentials!", Collections.emptyMap());
    }
}
