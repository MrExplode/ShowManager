package me.sunstorm.showmanager.settings.config;

import lombok.Data;

@Data
public class DmxRemoteConfig {
    private boolean enabled = false;
    private int address = 0;
    private int universe = 0;
    private int subnet = 0;
}
