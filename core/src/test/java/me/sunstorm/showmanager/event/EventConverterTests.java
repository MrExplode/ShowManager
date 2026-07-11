package me.sunstorm.showmanager.event;

import me.sunstorm.showmanager.cluster.serial.EventConverter;
import me.sunstorm.showmanager.cluster.serial.EventWrapper;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.eventsystem.events.transport.TransportCommandEvent;
import me.sunstorm.showmanager.util.Timecode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class EventConverterTests {

    private final EventConverter converter = new EventConverter();

    @Test
    void roundTripsTimecodeChange() {
        EventWrapper wrapper = new EventWrapper(11, false, "node1", new TimecodeChangeEvent(new Timecode(0, 1, 30, 0)));
        EventWrapper result = converter.decode(converter.encode(wrapper));
        assertThat(result.id()).isEqualTo(11);
        assertThat(result.async()).isFalse();
        assertThat(result.origin()).isEqualTo("node1");
        assertThat(result.event()).isInstanceOf(TimecodeChangeEvent.class);
        assertThat(((TimecodeChangeEvent) result.event()).getTime()).isEqualTo(new Timecode(0, 1, 30, 0));
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
