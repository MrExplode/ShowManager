package me.sunstorm.showmanager.event;

import me.sunstorm.showmanager.cluster.serial.EventConverter;
import me.sunstorm.showmanager.cluster.serial.EventWrapper;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeSyncEvent;
import me.sunstorm.showmanager.eventsystem.events.transport.TransportCommandEvent;
import me.sunstorm.showmanager.util.Timecode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class EventConverterTests {

    private final EventConverter converter = new EventConverter();

    @Test
    void roundTripsTimecodeSync() {
        EventWrapper wrapper = new EventWrapper(6, false, "node1", new TimecodeSyncEvent(new Timecode(0, 1, 30, 0), 42L));
        EventWrapper result = converter.decode(converter.encode(wrapper));
        assertThat(result.id()).isEqualTo(6);
        assertThat(result.async()).isFalse();
        assertThat(result.origin()).isEqualTo("node1");
        assertThat(result.event()).isInstanceOf(TimecodeSyncEvent.class);
        TimecodeSyncEvent sync = (TimecodeSyncEvent) result.event();
        assertThat(sync.getPosition()).isEqualTo(new Timecode(0, 1, 30, 0));
        assertThat(sync.getMasterTimestamp()).isEqualTo(42L);
    }

    @Test
    void roundTripsTransportCommandWithTime() {
        EventWrapper wrapper = new EventWrapper(5, false, "node2",
                new TransportCommandEvent(TransportCommandEvent.Action.SET, new Timecode(0, 0, 42, 0)));
        EventWrapper result = converter.decode(converter.encode(wrapper));
        assertThat(result.id()).isEqualTo(5);
        assertThat(result.origin()).isEqualTo("node2");
        assertThat(result.event()).isInstanceOf(TransportCommandEvent.class);
        TransportCommandEvent command = (TransportCommandEvent) result.event();
        assertThat(command.getAction()).isEqualTo(TransportCommandEvent.Action.SET);
        assertThat(command.getTime()).isEqualTo(new Timecode(0, 0, 42, 0));
    }
}
