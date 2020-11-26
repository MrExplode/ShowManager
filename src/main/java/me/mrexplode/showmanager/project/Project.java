package me.mrexplode.showmanager.project;

import lombok.Getter;
import me.mrexplode.showmanager.config.server.ServerConfig;

@Getter
public class Project {
    private String name = "Untitled Project";
    private ServerConfig config;
}
