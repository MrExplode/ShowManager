package me.sunstorm.showmanager.ltc;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Getter
public class LtcHandler extends SettingsHolder implements Terminable, InjectRecipient, Runnable {
    private Mixer mixer;
    private SourceDataLine line;
    private LibLTCWrapper wrapper;
    @Inject private SettingsStore store;
    @Getter
    private boolean enabled = false;
    private boolean playing = false;
    private boolean workLoop = true;
    private int frameRate = 25;
    private int sampleRate = 48000;

    public LtcHandler() {
        super("ltc-timecode");
        register();
        inject();
        load();

    }

    public void init() throws LineUnavailableException {
        log.info("Starting LtcHandler...");
        try {
            File libFile = new File(Constants.BASE_DIRECTORY, "libltc.dll");
            try (var in = LtcHandler.class.getResourceAsStream("/libltc.dll"); var out = new FileOutputStream(libFile)) {
                in.transferTo(out);
            }
            System.load(libFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("failed to load libltc dll", e);
        }
        AudioFormat format = new AudioFormat(sampleRate, 8, 1, false, true);
        SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        line = (SourceDataLine) mixer.getLine(info);
        line.start();
        wrapper = new LibLTCWrapper();
        wrapper.init(sampleRate, frameRate);
        //new Thread(this).start();
    }

    public void start() {
        playing = true;
    }

    public void stop() {
        playing = false;
    }

    public void setTime(Timecode time) {
        wrapper.setTime(time.getHour(), time.getMin(), time.getSec(), time.getFrame());
    }

    public void setEnabled(boolean value) {
        if (enabled && !value)
            playing = false;
        enabled = value;
    }

    @Override
    public void shutdown() {
        log.info("Shutting down LTC...");
        workLoop = false;
        wrapper.free();
        line.close();
        mixer.close();
    }

    @Override
    public void run() {
        while (workLoop) {
            if (playing) {
                byte[] data = wrapper.getData();
                line.write(data, 0, data.length);
            }
        }
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        data.addProperty("framerate", frameRate);
        data.addProperty("sample-rate", sampleRate);
        data.addProperty("mixer", mixer != null ? mixer.getMixerInfo().getName() : store.getMixerByName("").getMixerInfo().getName());
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonObject object) {
        enabled = object.get("enabled").getAsBoolean();
        frameRate = object.get("framerate").getAsInt();
        sampleRate = object.get("sample-rate").getAsInt();
        mixer = store.getMixerByName(object.get("mixer").getAsString());
    }
}
