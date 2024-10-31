package me.sunstorm.showmanager.settings.config;

public class Config {
    private int framerate = 25;
    private LtcConfig ltcConfig = new LtcConfig();
    private RedisConfig redisConfig = new RedisConfig();

    public int getFramerate() {
        return framerate;
    }

    public LtcConfig getLtcConfig() {
        return ltcConfig;
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }
}
