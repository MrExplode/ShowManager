package me.mrexplode.showmanager.config.server;

import lombok.Getter;

@Getter
public class ServerConfig {
    private final ArtNetConfig artNetConfig = new ArtNetConfig();
    private final LtcConfig ltcConfig = new LtcConfig();
    private final DmxRemoteConfig dmxRemoteConfig = new DmxRemoteConfig();
    private final OscDispatchConfig oscDispatchConfig = new OscDispatchConfig();
    private final AudioPlayerConfig audioPlayerConfig = new AudioPlayerConfig();
}
