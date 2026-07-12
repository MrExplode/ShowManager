package me.sunstorm.showmanager.modules.http.controller;

import io.javalin.http.Context;
import me.sunstorm.showmanager.cluster.ClusterNodes;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.PathPrefix;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@PathPrefix("/cluster")
public class ClusterController {
    private final ClusterNodes nodes;

    @Inject
    public ClusterController(ClusterNodes nodes) {
        this.nodes = nodes;
    }

    @Get("/state")
    public void getState(@NotNull Context ctx) {
        ctx.json(nodes.state());
    }
}
