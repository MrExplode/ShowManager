package me.sunstorm.showmanager.settings.config;

public class Config {
    private int framerate = 25;
    private LtcConfig ltcConfig = new LtcConfig();

    public int getFramerate() {
        return framerate;
    }

    public LtcConfig getLtcConfig() {
        return ltcConfig;
    }
}
