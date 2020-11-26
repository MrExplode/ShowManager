package me.mrexplode.showmanager.ltc;

import lombok.Getter;
import lombok.val;
import me.mrexplode.ltc4j.Framerate;
import me.mrexplode.ltc4j.LTCGenerator;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import java.util.Arrays;

@Getter
public class LtcHandler {
    private final Mixer mixer;
    private final LTCGenerator generator;

    public LtcHandler(Mixer mixer, int framerate) {
        this.mixer = mixer;
        val opt = Arrays.stream(Framerate.values()).filter(f -> ((int) f.getFps()) == framerate).findFirst();
        if (opt.isEmpty()) throw new IllegalArgumentException("Invalid framerate");
        generator = new LTCGenerator(mixer, opt.get(), 48000);
        generator.setVolume(90);
    }

    //ew
    public void init() throws LineUnavailableException {
        generator.init();
    }

    public void shutdown() {
        generator.shutdown();
        mixer.close();
    }
}
