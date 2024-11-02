package me.sunstorm.showmanager.settings;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class SettingsStore {
    private static final Logger log = LoggerFactory.getLogger(SettingsStore.class);

    private final List<Mixer.Info> audioOutputs = new ArrayList<>();
    private final List<InetData> networkInterfaces = new ArrayList<>();

    public void load() {
        log.info("Looking for possible outputs...");
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            if (!mixerInfo.getName().startsWith("Port")) {
                audioOutputs.add(mixerInfo);
            }
        }

        try {
            networkInterfaces.addAll(NetworkInterface.networkInterfaces()
                    .flatMap(NetworkInterface::inetAddresses)
                    .map(addr -> new InetData(addr.getHostAddress(), addr))
                    .toList()
            );
        } catch (SocketException e) {
            log.error("Failed to load network interfaces", e);
        }
    }

    public Mixer getMixerByName(@NotNull String name) {
        for (Mixer.Info output : audioOutputs) {
            if (output.getName().equals(name))
                return AudioSystem.getMixer(output);
        }
        return AudioSystem.getMixer(audioOutputs.getFirst());
    }

    // generated

    public List<Mixer.Info> getAudioOutputs() {
        return audioOutputs;
    }

    public List<InetData> getNetworkInterfaces() {
        return networkInterfaces;
    }
}
