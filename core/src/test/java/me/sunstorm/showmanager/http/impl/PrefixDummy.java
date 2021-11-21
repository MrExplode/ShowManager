package me.sunstorm.showmanager.http.impl;

import io.javalin.http.Context;
import me.sunstorm.showmanager.http.routing.annotate.Get;
import me.sunstorm.showmanager.http.routing.annotate.PathPrefix;

@PathPrefix("/prefix")
public class PrefixDummy {

    @Get("/test/path")
    public void testPrefix(Context ctx) {

    }
}
