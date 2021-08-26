package me.sunstorm.showmanager.settings.config;

import lombok.Data;

@Data
public class LtcConfig {
    private boolean enabled = false;
    private String ltcOutput = "";
}
