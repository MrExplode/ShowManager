package me.sunstorm.showmanager.settings.config;

import lombok.Getter;

@Getter
public class Config {
    private int framerate = 25;
    private ArtNetConfig artNetConfig = new ArtNetConfig();
    private AudioPlayerConfig audioPlayerConfig = new AudioPlayerConfig();
    private DmxRemoteConfig dmxRemoteConfig = new DmxRemoteConfig();
    private LtcConfig ltcConfig = new LtcConfig();
    private OscDispatchConfig oscDispatchConfig = new OscDispatchConfig();
    private RedisConfig redisConfig = new RedisConfig();
}
