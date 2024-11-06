package me.sunstorm.showmanager.util;

import com.google.common.hash.Hashing;
import me.sunstorm.showmanager.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WaveformRunner {
    private static final Logger log = LoggerFactory.getLogger(WaveformRunner.class);
    private static final File CACHE = new File(Constants.BASE_DIRECTORY, "wavecache");
    private final Path executablePath;

    public WaveformRunner(@NotNull Path executablePath) {
        this.executablePath = executablePath;
    }

    public byte[] sample(@NotNull Path wav) throws IOException {
        log.info("Sampling {}...", wav);
        var cached = tryCaching(wav);
        if (cached != null) {
            log.info("Cached samples found.");
            return cached;
        }

        var process = new ProcessBuilder()
                .command(executablePath.toString(), "-i", wav.toString(), "--output-format", "json", "-q", "-b", "8")
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .start();
        var buf = new ByteArrayOutputStream();
        process.getInputStream().transferTo(buf);
        try {
            var result = process.waitFor();
            if (result == 0) {
                var data = buf.toByteArray();
                saveCache(data, wav.toString());
                return data;
            } else {
                throw new IOException("waveform process exited with non zero code");
            }
        } catch (InterruptedException e) {
            Exceptions.sneaky(e);
            return null;
        }
    }

    @Nullable
    private byte[] tryCaching(@NotNull Path wav) throws IOException {
        var name = Hashing.sha256().hashString(wav.toString(), StandardCharsets.UTF_8).toString();
        var cached = CACHE.toPath().resolve(name);
        if (Files.exists(cached)) {
            return Files.readAllBytes(cached);
        }
        return null;
    }

    private void saveCache(byte[] data, String file) throws IOException {
        if (!CACHE.isDirectory())
            CACHE.mkdirs();
        var name = Hashing.sha256().hashString(file, StandardCharsets.UTF_8).toString();
        Files.write(CACHE.toPath().resolve(name), data);
    }
}
