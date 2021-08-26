package me.sunstorm.showmanager.project;

import lombok.Getter;
import me.sunstorm.showmanager.settings.config.Config;

@Getter
public class Project {
    private String name = "Untitled Project";
    private Config config;
}
