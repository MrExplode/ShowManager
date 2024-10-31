package me.sunstorm.showmanager.settings.config;

import me.sunstorm.showmanager.redis.RedisCredentials;

public class RedisConfig {
    private boolean enabled = false;
    private RedisCredentials credentials = new RedisCredentials("127.0.0.1", 6379, "");

    public boolean isEnabled() {
        return enabled;
    }

    public RedisCredentials getCredentials() {
        return credentials;
    }
}
