package me.sunstorm.showmanager.settings.config;

import lombok.Data;

@Data
public class HttpConfig {
    private int port = 0;
    private String header = "";
    private String value = "";
}
