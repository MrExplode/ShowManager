package me.sunstorm.showmanager.config.server;

import lombok.Data;

@Data
public class ArtNetConfig {
    private boolean enabled = false;
    private String artNetInterface = "";
}
