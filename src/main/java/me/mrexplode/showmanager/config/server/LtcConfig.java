package me.mrexplode.showmanager.config.server;

import lombok.Data;

@Data
public class LtcConfig {
    private boolean enabled = false;
    private String ltcOutput = "";
}
