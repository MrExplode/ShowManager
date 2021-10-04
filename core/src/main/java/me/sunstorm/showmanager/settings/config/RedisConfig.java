package me.sunstorm.showmanager.settings.config;

import lombok.Data;
import me.sunstorm.showmanager.redis.RedisCredentials;

@Data
public class RedisConfig {
    private boolean enabled = false;
    private RedisCredentials credentials = new RedisCredentials("127.0.0.1", 6379, "");
}
