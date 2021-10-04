package me.sunstorm.showmanager.settings.config;

import lombok.Data;

@Data
public class ArtNetConfig {
    private boolean enabled = false;
    private String artNetInterface = "127.0.0.1";
}
