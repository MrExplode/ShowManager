package me.sunstorm.showmanager.ltc;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.sunstorm.ltc4j.Framerate;
import me.sunstorm.ltc4j.LTCGenerator;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.terminable.Terminable;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import java.util.Arrays;

@Slf4j
@Getter
public class LtcHandler extends SettingsHolder implements Terminable, InjectRecipient {
    private Mixer mixer;
    private final LTCGenerator generator;

    @Inject private SettingsStore store;
    @Getter
    private boolean enabled = false;
    private int framerate = 25;

    public LtcHandler() {
        super("ltc-timecode");
        register();
        inject();
        load();
        val opt = Arrays.stream(Framerate.values()).filter(f -> ((int) f.getFps()) == framerate).findFirst();
        if (opt.isEmpty()) throw new IllegalArgumentException("Invalid framerate");
        generator = new LTCGenerator(mixer, opt.get(), 48000);
        generator.setVolume(100);
    }

    public void init() throws LineUnavailableException {
        log.info("Starting LtcHandler...");
        generator.init();
    }

    public void start() {
        if (enabled)
            generator.start();
    }

    public void stop() {
        if (enabled)
            generator.stop();
    }

    public void setEnabled(boolean value) {
        if (enabled && !value)
            generator.stop();
        enabled = value;
    }

    @Override
    public void shutdown() {
        log.info("Shutting down LTC...");
        generator.shutdown();
        mixer.close();
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        data.addProperty("framerate", framerate);
        data.addProperty("mixer", mixer != null ? mixer.getMixerInfo().getName() : store.getMixerByName("").getMixerInfo().getName());
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonObject object) {
        enabled = object.get("enabled").getAsBoolean();
        framerate = object.get("framerate").getAsInt();
        mixer = store.getMixerByName(object.get("mixer").getAsString());
    }
}
