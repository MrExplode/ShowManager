package me.sunstorm.showmanager.settings.config;

import lombok.Data;

@Data
public class OscDispatchConfig {
    private boolean enabled = false;
    private int port = 0;
    private boolean broadcast = false;
    private String target = "";
}
