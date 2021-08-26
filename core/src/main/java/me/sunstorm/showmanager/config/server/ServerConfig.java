package me.sunstorm.showmanager.config.server;

import lombok.Getter;

@Getter
public class ServerConfig {
    private ArtNetConfig artNetConfig = new ArtNetConfig();
    private LtcConfig ltcConfig = new LtcConfig();
    private DmxRemoteConfig dmxRemoteConfig = new DmxRemoteConfig();
    private OscDispatchConfig oscDispatchConfig = new OscDispatchConfig();
    private AudioPlayerConfig audioPlayerConfig = new AudioPlayerConfig();
}
