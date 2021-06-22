package me.sunstorm.showmanager.config.server;

import lombok.Data;

@Data
public class OscDispatchConfig {
    private boolean enabled = false;
    private int port = 0;
    private boolean broadcast = false;
    private String target = "";
}
