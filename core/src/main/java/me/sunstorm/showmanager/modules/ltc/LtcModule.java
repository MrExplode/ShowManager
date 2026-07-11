package me.sunstorm.showmanager.modules.ltc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.sunstorm.showmanager.cluster.OutputType;
import me.sunstorm.showmanager.cluster.Ownership;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.EventPriority;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodePauseEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeSetEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeStartEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeStopEvent;
import me.sunstorm.showmanager.ltc.LtcEncoder;
import me.sunstorm.showmanager.modules.ToggleableModule;
import me.sunstorm.showmanager.settings.SettingsStore;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 * Generates LTC timecode audio on the fly. The blocking {@link SourceDataLine#write} paces the
 * writer thread, the encoder counts the frames, and the show clock only steers: each frame carries
 * the timecode that will be current once the sound card plays it, and the stream jams back into
 * place if it ends up more than {@link #JAM_THRESHOLD} frames off.
 */
@Singleton
public class LtcModule extends ToggleableModule implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(LtcModule.class);
    private static final int JAM_THRESHOLD = 2;
    private static final int SAMPLE_SIZE = 2;
    private static final int CHANNELS = 2;

    private final SettingsStore store;
    private final Ownership ownership;
    private final int framerate;

    private Mixer mixer;
    private int sampleRate = 48000;
    private double level = -6;
    private double riseTime = 40;
    private int channel = 0;
    private int bufferFrames = 6;
    private int offset = 0;

    private LtcEncoder encoder;
    private SourceDataLine line;
    private short[] samples;
    private byte[] buffer;
    private long framesWritten = 0;

    private volatile boolean running = true;
    private volatile boolean playing = false;
    private volatile boolean seeking = false;
    private volatile Anchor anchor = new Anchor(0, System.nanoTime());

    @Inject
    public LtcModule(EventBus bus, SettingsStore store, Ownership ownership, @Named("framerate") int framerate) {
        super(bus);
        this.store = store;
        this.ownership = ownership;
        this.framerate = framerate;
        init();
        open();
    }

    private void open() {
        if (mixer == null) {
            log.warn("No audio output for LTC, output disabled");
            return;
        }
        encoder = new LtcEncoder(sampleRate, framerate, level, riseTime);
        samples = new short[encoder.maxFrameSamples()];
        buffer = new byte[samples.length * SAMPLE_SIZE * CHANNELS];

        AudioFormat format = new AudioFormat(sampleRate, SAMPLE_SIZE * 8, CHANNELS, true, false);
        try {
            line = (SourceDataLine) mixer.getLine(new DataLine.Info(SourceDataLine.class, format));
            line.open(format, bufferFrames * (sampleRate / framerate) * SAMPLE_SIZE * CHANNELS);
        } catch (LineUnavailableException | IllegalArgumentException e) {
            log.error("Failed to open LTC output line on {}", mixer.getMixerInfo().getName(), e);
            line = null;
            return;
        }
        log.info("LTC output on {}, {} fps, {} Hz, {} dBFS", mixer.getMixerInfo().getName(), framerate, sampleRate, level);
        Thread thread = new Thread(this, "ltc-output");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        long current = -1;
        boolean started = false;
        while (running) {
            if (!playing || !isEnabled() || !ownership.owns(OutputType.LTC)) {
                if (started) {
                    idle();
                    started = false;
                }
                current = -1;
                sleep();
                continue;
            }
            if (!started) {
                line.start();
                rewind();
                started = true;
            }
            if (seeking) {
                seeking = false;
                line.flush();
                rewind();
                current = -1;
            }

            double target = targetFrame();
            long next = current + 1;
            if (current < 0 || Math.abs(target - next) >= JAM_THRESHOLD) {
                if (current >= 0)
                    log.debug("LTC is {} frames off the show clock, jamming", String.format("%.2f", target - next));
                next = Math.round(target);
            }
            current = Math.max(0, next);

            int written = encoder.encode(
                    (int) (current / framerate / 3600 % 24),
                    (int) (current / framerate / 60 % 60),
                    (int) (current / framerate % 60),
                    (int) (current % framerate),
                    samples);
            interleave(written);
            line.write(buffer, 0, written * SAMPLE_SIZE * CHANNELS);
            framesWritten += written;
        }
    }

    /**
     * Where the show clock lands, in frames, once the queued audio has played.
     * <p>
     * Keep it fractional: the line reports its position in period sized steps, and rounding that
     * noise to a whole frame makes the stream jam onto a dithered value and oscillate. Keep the
     * queue depth raw as well: a stalled writer drains exactly as much as the clock advances, so
     * the two cancel, which a smoothed depth would break.
     */
    private double targetFrame() {
        long queued = Math.max(0, framesWritten - line.getLongFramePosition());
        double millis = showMillis() + queued * 1000d / sampleRate + offset;
        return Math.max(0, millis * framerate / 1000);
    }

    // the show clock only ticks at the framerate, interpolate between the ticks
    private long showMillis() {
        Anchor current = anchor;
        return current.millis() + (System.nanoTime() - current.nanos()) / 1_000_000L;
    }

    private void interleave(int written) {
        for (int i = 0; i < written; i++) {
            int index = (i * CHANNELS + channel) * SAMPLE_SIZE;
            buffer[index] = (byte) (samples[i] & 0xFF);
            buffer[index + 1] = (byte) ((samples[i] >> 8) & 0xFF);
        }
    }

    private void idle() {
        line.stop();
        line.flush();
        rewind();
        encoder.reset();
    }

    private void rewind() {
        framesWritten = line.getLongFramePosition();
    }

    private void sleep() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }

    @EventCall(EventPriority.LOWEST)
    public void onTimeChange(TimecodeChangeEvent event) {
        anchor = new Anchor(event.getTime().millis(), System.nanoTime());
    }

    @EventCall(EventPriority.LOWEST)
    public void onStart(TimecodeStartEvent event) {
        if (event.isCancelled())
            return;
        anchor = new Anchor(event.getTime().millis(), System.nanoTime());
        playing = true;
    }

    @EventCall(EventPriority.LOWEST)
    public void onPause(TimecodePauseEvent event) {
        if (!event.isCancelled())
            playing = false;
    }

    @EventCall(EventPriority.LOWEST)
    public void onStop(TimecodeStopEvent event) {
        if (event.isCancelled())
            return;
        playing = false;
        anchor = new Anchor(0, System.nanoTime());
    }

    @EventCall(EventPriority.LOWEST)
    public void onSet(TimecodeSetEvent event) {
        if (event.isCancelled())
            return;
        anchor = new Anchor(event.getTime().millis(), System.nanoTime());
        seeking = true;
    }

    @Override
    public void shutdown() {
        log.info("Shutting down LTC...");
        running = false;
        if (line != null)
            line.close();
        if (mixer != null)
            mixer.close();
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", isEnabled());
        data.addProperty("offset", offset);
        data.addProperty("mixer", mixer != null ? mixer.getMixerInfo().getName() : store.getMixerByName("").getMixerInfo().getName());
        data.addProperty("sample-rate", sampleRate);
        data.addProperty("level-dbfs", level);
        data.addProperty("rise-time-us", riseTime);
        data.addProperty("channel", channel);
        data.addProperty("buffer-frames", bufferFrames);
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        setEnabled(object.get("enabled").getAsBoolean());
        offset = object.get("offset").getAsInt();
        mixer = store.getMixerByName(object.get("mixer").getAsString());
        // added with the generator, older projects don't have them
        if (object.has("sample-rate"))
            sampleRate = object.get("sample-rate").getAsInt();
        if (object.has("level-dbfs"))
            level = object.get("level-dbfs").getAsDouble();
        if (object.has("rise-time-us"))
            riseTime = object.get("rise-time-us").getAsDouble();
        if (object.has("channel"))
            channel = Math.clamp(object.get("channel").getAsInt(), 0, CHANNELS - 1);
        if (object.has("buffer-frames"))
            bufferFrames = Math.max(2, object.get("buffer-frames").getAsInt());
    }

    @Override
    public String getName() {
        return "ltc-timecode";
    }

    private record Anchor(long millis, long nanos) {
    }
}
