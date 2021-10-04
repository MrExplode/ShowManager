package me.sunstorm.showmanager.settings.config;

import lombok.Data;

@Data
public class OscDispatchConfig {
    private boolean enabled = false;
    private int port = 7000;
    private boolean broadcast = false;
    private String target = "127.0.0.1";
}
