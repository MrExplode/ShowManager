package me.sunstorm.showmanager.audio;

import com.google.common.base.Stopwatch;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.util.Sampler;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

import static me.sunstorm.showmanager.util.SilentClose.close;

@Slf4j
@Getter
@RequiredArgsConstructor
public class AudioTrack {
    @Setter @NonNull
    private Timecode startTime;
    private final File file;
    @Nullable private transient Clip clip;
    @Nullable private transient float[] samples;
    @Nullable private transient AudioInputStream stream;

    public void loadTrack(Mixer mixer) {
        discard();
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.debug("Loading track {}...", file.getName());
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            stream = AudioSystem.getAudioInputStream(in);
            AudioFormat format = stream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits() * 2, format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(), true);
                stream = AudioSystem.getAudioInputStream(format, stream);
            }
            samples = Sampler.sample(stream);
            val sourceInfo = new DataLine.Info(Clip.class, format, ((int) stream.getFrameLength() * format.getFrameSize()));
            clip = (Clip) mixer.getLine(sourceInfo);
            clip.flush();
            clip.open(stream);
            log.info("Loaded track {} in {} ms", file.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (IOException e) {
            log.error("Failed to load track", e);
        } catch (UnsupportedAudioFileException e) {
            log.error("Track audio type not supported", e);
        } catch (LineUnavailableException e) {
            log.error("Failed to open mixer line", e);
        }
    }

    public void discard() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
        if (stream != null) {
            close(stream);
        }
    }
}
