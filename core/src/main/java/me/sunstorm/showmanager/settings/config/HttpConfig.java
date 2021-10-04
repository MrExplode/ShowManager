package me.sunstorm.showmanager.settings.config;

import lombok.Data;

@Data
public class HttpConfig {
    private int port = 7000;
    private String header = "secret";
    private String value = "xxxxxxxxxx";
}
