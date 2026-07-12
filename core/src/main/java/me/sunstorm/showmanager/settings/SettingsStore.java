package me.sunstorm.showmanager.settings;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class SettingsStore {
    private static final Logger log = LoggerFactory.getLogger(SettingsStore.class);

    private final List<Mixer.Info> audioOutputs = new ArrayList<>();
    private final List<InetData> networkInterfaces = new ArrayList<>();

    public void load() {
        log.info("Looking for possible outputs...");
        loadAudioOutputs();
        loadNetworkInterfaces();
    }

    private void loadAudioOutputs() {
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            if (!mixerInfo.getName().startsWith("Port")) {
                audioOutputs.add(mixerInfo);
            }
        }

        log.info("Found {} audio output(s) - copy a name into the project's 'mixer' field:", audioOutputs.size());
        for (Mixer.Info mixerInfo : audioOutputs) {
            log.info("    \"{}\"  ({})", mixerInfo.getName(), mixerInfo.getDescription());
        }
    }

    private void loadNetworkInterfaces() {
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            log.error("Failed to enumerate network interfaces", e);
            return;
        }
        if (interfaces == null) {
            log.warn("No network interfaces found");
            return;
        }

        log.info("Found network address(es) - copy one into 'interface', 'host' or 'bindAddress':");
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // isUp() and getInetAddresses() throw on an adapter that is down or was unplugged mid-enumeration,
            // and one bad adapter must not cost us the rest
            try {
                if (!iface.isUp()) {
                    log.debug("Skipping '{}': interface is down", iface.getName());
                    continue;
                }
                for (InetAddress address : Collections.list(iface.getInetAddresses())) {
                    networkInterfaces.add(new InetData(address.getHostAddress(), address));
                    log.info("    \"{}\"  ({})", address.getHostAddress(), iface.getName());
                }
            } catch (Exception e) {
                log.warn("Skipping network interface '{}': {}", iface.getName(), e.toString());
            }
        }
    }

    public Mixer getMixerByName(@NotNull String name) {
        for (Mixer.Info output : audioOutputs) {
            if (output.getName().equals(name))
                return AudioSystem.getMixer(output);
        }
        if (audioOutputs.isEmpty())
            throw new IllegalStateException("No audio outputs available, cannot resolve mixer '" + name + "'");
        Mixer.Info fallback = audioOutputs.getFirst();
        // "" means "give me the default", so only a real miss is worth warning about
        if (!name.isEmpty())
            log.warn("Audio output \"{}\" not found, falling back to \"{}\"", name, fallback.getName());
        return AudioSystem.getMixer(fallback);
    }

    // generated

    public List<Mixer.Info> getAudioOutputs() {
        return audioOutputs;
    }

    public List<InetData> getNetworkInterfaces() {
        return networkInterfaces;
    }
}
