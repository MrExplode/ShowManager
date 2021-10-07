package me.sunstorm.showmanager.settings.config;

import lombok.Getter;

@Getter
public class Config {
    private int framerate = 25;
    private LtcConfig ltcConfig = new LtcConfig();
    private RedisConfig redisConfig = new RedisConfig();
}
