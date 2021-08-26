package me.sunstorm.showmanager.settings;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class SettingsStore {
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
            val interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                val netInterface = interfaces.nextElement();
                if (netInterface.isUp()) {
                    val address = netInterface.getInetAddresses().nextElement();
                    networkInterfaces.add(new InetData(address.getHostAddress(), address));
                }
            }
        } catch (SocketException e) {
            log.error("Failed to load network interfaces", e);
        }
    }

    public Mixer getMixerByName(@NotNull String name) {
        for (Mixer.Info output : audioOutputs) {
            if (output.getName().equals(name))
                return AudioSystem.getMixer(output);
        }
        return AudioSystem.getMixer(audioOutputs.get(0));
    }
}
