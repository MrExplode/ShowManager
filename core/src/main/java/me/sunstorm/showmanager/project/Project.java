package me.sunstorm.showmanager.project;

import lombok.Getter;
import me.sunstorm.showmanager.config.server.ServerConfig;

@Getter
public class Project {
    private String name = "Untitled Project";
    private ServerConfig config;
}
