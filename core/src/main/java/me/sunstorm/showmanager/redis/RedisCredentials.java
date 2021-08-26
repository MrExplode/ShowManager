package me.sunstorm.showmanager.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RedisCredentials {
    private final String address;
    private final int port;
    private final String password;
}
