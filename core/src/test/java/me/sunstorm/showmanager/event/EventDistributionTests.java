package me.sunstorm.showmanager.event;

import me.sunstorm.showmanager.cluster.ClusterService;
import me.sunstorm.showmanager.cluster.serial.EventConverter;
import me.sunstorm.showmanager.cluster.serial.EventWrapper;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeSyncEvent;
import me.sunstorm.showmanager.settings.config.ClusterConfig;
import me.sunstorm.showmanager.util.Timecode;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

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
        public void send(byte[] payload, boolean oob) {
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

    public static class TimeListener implements Listener {
        final AtomicReference<Timecode> last = new AtomicReference<>();

        @EventCall
        public void onSync(TimecodeSyncEvent event) {
            last.set(event.getPosition());
        }
    }

    @Test
    void distributesToRemoteBus() {
        CapturingCluster clusterA = new CapturingCluster("A");
        EventBus busA = new EventBus();
        busA.setCluster(clusterA);

        EventBus busB = new EventBus();
        busB.setCluster(new CapturingCluster("B"));
        TimeListener listener = new TimeListener();
        busB.register(listener);

        busA.call(new TimecodeSyncEvent(new Timecode(0, 0, 5, 0), 12345L));
        assertThat(clusterA.sent).isNotNull();

        busB.onClusterMessage(clusterA.sent);
        assertThat(listener.last.get()).isEqualTo(new Timecode(0, 0, 5, 0));
    }

    @Test
    void suppressesOwnEcho() {
        EventBus bus = new EventBus();
        bus.setCluster(new CapturingCluster("B"));
        TimeListener listener = new TimeListener();
        bus.register(listener);

        byte[] echo = new EventConverter().encode(new EventWrapper(6, false, "B", new TimecodeSyncEvent(new Timecode(0, 0, 7, 0), 999L)));
        bus.onClusterMessage(echo);
        assertThat(listener.last.get()).isNull();
    }
}
