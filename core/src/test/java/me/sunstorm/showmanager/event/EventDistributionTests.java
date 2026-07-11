package me.sunstorm.showmanager.event;

import me.sunstorm.showmanager.cluster.ClusterService;
import me.sunstorm.showmanager.cluster.serial.EventConverter;
import me.sunstorm.showmanager.cluster.serial.EventWrapper;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioVolumeChangeEvent;
import me.sunstorm.showmanager.settings.config.ClusterConfig;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class EventDistributionTests {

    static class CapturingCluster extends ClusterService {
        byte[] sent;

        CapturingCluster(String id) {
            super(new ClusterConfig());
            this.id = id;
        }

        String id;

        @Override
        public void send(byte[] payload) {
            sent = payload;
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        @Override
        public String selfId() {
            return id;
        }
    }

    public static class VolumeListener implements Listener {
        final AtomicInteger last = new AtomicInteger(-1);

        @EventCall
        public void onVolume(AudioVolumeChangeEvent event) {
            last.set(event.getVolume());
        }
    }

    @Test
    void distributesToRemoteBus() {
        CapturingCluster clusterA = new CapturingCluster("A");
        EventBus busA = new EventBus();
        busA.setCluster(clusterA);

        EventBus busB = new EventBus();
        busB.setCluster(new CapturingCluster("B"));
        VolumeListener listener = new VolumeListener();
        busB.register(listener);

        busA.call(new AudioVolumeChangeEvent(15));
        assertThat(clusterA.sent).isNotNull();

        busB.onClusterMessage(clusterA.sent);
        assertThat(listener.last).hasValue(15);
    }

    @Test
    void suppressesOwnEcho() {
        EventBus bus = new EventBus();
        bus.setCluster(new CapturingCluster("B"));
        VolumeListener listener = new VolumeListener();
        bus.register(listener);

        byte[] echo = new EventConverter().encode(new EventWrapper(5, false, "B", new AudioVolumeChangeEvent(7)));
        bus.onClusterMessage(echo);
        assertThat(listener.last).hasValue(-1);
    }
}
