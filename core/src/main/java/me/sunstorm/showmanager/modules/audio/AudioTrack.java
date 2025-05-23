package me.sunstorm.showmanager.modules.audio;

import com.google.common.base.Stopwatch;
import me.sunstorm.showmanager.modules.audio.marker.Marker;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.audio.*;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.sunstorm.showmanager.util.SilentClose.close;

public class AudioTrack {
    private static final Logger log = LoggerFactory.getLogger(AudioTrack.class);

    @Inject
    private transient EventBus eventBus;
    private Timecode startTime;
    private final File file;
    private transient boolean loaded = false;
    private transient boolean paused = false;
    private float volume = 1.0f;
    @Nullable private Timecode endTime;
    private final List<Marker> markers = new ArrayList<>();
    @Nullable private transient Clip clip;
    @Nullable private transient float[] samples;
    @Nullable private transient AudioInputStream stream;

    public AudioTrack(Timecode startTime, File file) {
        this.startTime = startTime;
        this.file = file;
    }

    public AudioTrack(Timecode startTime, File file, List<Marker> markers) {
        this(startTime, file);
        this.markers.addAll(markers);
    }

    public AudioTrack loadTrack(Mixer mixer) {
        discard();
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("Loading track {}...", file.getName());
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            stream = AudioSystem.getAudioInputStream(in);
            AudioFormat format = stream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits() * 2, format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(), true);
                stream = AudioSystem.getAudioInputStream(format, stream);
            }

            var sourceInfo = new DataLine.Info(Clip.class, format, ((int) stream.getFrameLength() * format.getFrameSize()));
            clip = (Clip) mixer.getLine(sourceInfo);
            clip.flush();
            clip.open(stream);
            clip.addLineListener(lineEvent -> {
                if (lineEvent.getType() == LineEvent.Type.STOP && !paused) {
                    AudioStopEvent event = new AudioStopEvent(this);
                    event.call(eventBus);
                }
            });
            endTime = startTime.add(new Timecode(clip.getMicrosecondLength() / 1000));
            loaded = true;
            AudioLoadEvent event = new AudioLoadEvent(this);
            event.call(eventBus);
            setVolume((int) (volume * 100));
            log.info("Loaded track {} in {} ms", file.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (IOException e) {
            log.error("Failed to load track", e);
        } catch (UnsupportedAudioFileException e) {
            log.error("Track audio type not supported", e);
        } catch (LineUnavailableException e) {
            log.error("Failed to open mixer line", e);
        }
        return this;
    }

    public void play() {
        if (clip != null) {
            AudioStartEvent event = new AudioStartEvent(this);
            event.call(eventBus);
            if (!event.isCancelled()) {
                log.info("Playing track {}", file.getName());
                clip.start();
                paused = false;
            }
        } else {
            log.warn("Tried to play track {} without loading", file.getName());
        }
    }

    public void pause() {
        if (clip != null) {
            AudioPauseEvent event = new AudioPauseEvent(this);
            event.call(eventBus);
            if (!event.isCancelled()) {
                paused = true;
                clip.stop();
            }
        } else {
            log.warn("Tried to pause track {} without loading", file.getName());
        }
    }

    public void stop() {
        if (clip != null) {
            AudioStopEvent event = new AudioStopEvent(this);
            event.call(eventBus);
            if (!event.isCancelled())
                clip.stop();
        } else {
            log.warn("Tried to stop track {} without loading", file.getName());
        }
    }

    public void discard() {
        stop();
        if (clip != null) {
            clip.close();
        }
        if (stream != null) {
            close(stream);
        }
        loaded = false;
    }

    public void setStartTime(Timecode time) {
        if (clip != null && clip.isRunning()) {
            log.warn("Moving a track on the timeline while it's playing not allowed");
            return;
        }
        startTime = time;
        endTime = startTime.add(new Timecode(clip.getMicrosecondLength() / 1000));
    }

    public void setVolume(int volume) {
        this.volume = volume / 100f;
        if (loaded) {
            log.info("Set volume on track {} to {}%", getName(), volume);
            AudioVolumeChangeEvent event = new AudioVolumeChangeEvent(volume);
            event.call(eventBus);
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(20f * (float) Math.log10(this.volume));
        }
    }

    public String getName() {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }

    // generated

    @Nullable
    public Timecode getEndTime() {
        return endTime;
    }

    public Timecode getStartTime() {
        return startTime;
    }

    @Nullable
    public Clip getClip() {
        return clip;
    }

    public float getVolume() {
        return volume;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public File getFile() {
        return file;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
