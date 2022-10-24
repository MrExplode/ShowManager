package me.sunstorm.showmanager.http.impl;

import io.javalin.http.Context;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;

public class RouteDummy {

    @Get("/test/path/1")
    public void testGet(Context ctx) {

    }

    @Post("/test/path/2")
    public void testPost(Context ctx) {

    }
}
