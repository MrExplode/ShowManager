package me.mrexplode.timecode.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.mrexplode.timecode.WorkerThread;
import me.mrexplode.timecode.util.DmxAddress;

@Getter
public class DmxRemoteControl {
    private static final int TOLERANCE = 5;
    @Setter private boolean enabled = false;
    private RemoteState state;
    private RemoteState previousState;
    @Setter private DmxAddress address;

    public void handleData(byte[] dmxData) {
        if (!enabled) {
            state = RemoteState.DISABLED;
            return;
        }
        byte value = dmxData[address.getAddress() - 1];
        if (inToleratedRange(25, value)) {
            state = RemoteState.FORCE_IDLE;
            if (state != previousState)
                previousState = state;
        } else if (inToleratedRange(51, value)) {
            state = RemoteState.PLAYING;
            if (state != previousState) {
                previousState = state;
                WorkerThread.getInstance().play();
            }
        } else if (inToleratedRange(76, value)) {
            state = RemoteState.PAUSE;
            if (state != previousState) {
                previousState = state;
                WorkerThread.getInstance().pause();
            }
        } else if (inToleratedRange(102, value)) {
            state = RemoteState.STOPPED;
            if (state != previousState) {
                previousState = state;
                WorkerThread.getInstance().stop();
            }
        } else {
            state = RemoteState.IDLE;
            if (state != previousState)
                previousState = state;
        }
    }

    private boolean inToleratedRange(int origin, byte value) {
        return value <= origin + TOLERANCE && value >= origin - TOLERANCE;
    }
}
