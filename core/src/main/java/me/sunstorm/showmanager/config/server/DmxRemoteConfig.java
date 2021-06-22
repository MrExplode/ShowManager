package me.sunstorm.showmanager.config.server;

import lombok.Data;

@Data
public class DmxRemoteConfig {
    private boolean enabled = false;
    private int address = 0;
    private int universe = 0;
    private int subnet = 0;
}
