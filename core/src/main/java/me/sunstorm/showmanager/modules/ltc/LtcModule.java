package me.sunstorm.showmanager.modules.ltc;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.modules.Module;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.*;

@Slf4j
@Getter
public class LtcModule extends Module {
    private final File ltcFile = new File(Constants.BASE_DIRECTORY, "LTC_00000000_10mins_25fps_48000x8.wav");
    private Mixer mixer;
    private AudioInputStream stream;
    private Clip clip;
    @Inject private SettingsStore store;
    @Getter
    private boolean enabled = false;
    private boolean playing = false;
    private int offset = 0;

    public LtcModule() {
        super("ltc-timecode");
    }

    public void init() {
        if (!ltcFile.exists()) {
            try (var in = LtcModule.class.getResourceAsStream("/LTC_00000000_10mins_25fps_48000x8.wav"); var out = new FileOutputStream(ltcFile)) {
                in.transferTo(out);
            } catch (Exception e) {
                log.error("Failed to extract LTC sound file", e);
            }
        }
        try (InputStream in = new BufferedInputStream(new FileInputStream(ltcFile))) {
            stream = AudioSystem.getAudioInputStream(in);
            AudioFormat format = stream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits() * 2, format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(), true);
                stream = AudioSystem.getAudioInputStream(format, stream);
            }
            val sourceInfo = new DataLine.Info(Clip.class, format, ((int) stream.getFrameLength() * format.getFrameSize()));
            clip = (Clip) mixer.getLine(sourceInfo);
            clip.flush();
            clip.open(stream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            log.error("Failed to open LTC sound file", e);
        }
    }

    public void start() {
        playing = true;
        if (clip != null && enabled)
            clip.start();
    }

    public void stop() {
        if (clip != null && enabled)
            clip.stop();
    }

    public void setTime(Timecode time) {
        clip.setMicrosecondPosition(time.getMillisecLength() * 1000 + offset * 1000L);
    }

    public void setEnabled(boolean value) {
        if (enabled && !value && clip != null)
            clip.stop();
        enabled = value;
    }

    @Override
    public void shutdown() throws IOException {
        log.info("Shutting down LTC...");
        clip.close();
        stream.close();
        mixer.close();
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        data.addProperty("offset", offset);
        data.addProperty("mixer", mixer != null ? mixer.getMixerInfo().getName() : store.getMixerByName("").getMixerInfo().getName());
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonObject object) {
        enabled = object.get("enabled").getAsBoolean();
        offset = object.get("offset").getAsInt();
        mixer = store.getMixerByName(object.get("mixer").getAsString());
    }
}
