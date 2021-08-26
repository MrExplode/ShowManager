package me.sunstorm.showmanager.remote;

import com.illposed.osc.OSCMessage;
import lombok.Data;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;

@Data
public class OscRemoteControl {
    public static final String OSC_PLAY = "/timecode/remote/play";
    public static final String OSC_PAUSE = "/timecode/remote/pause";
    public static final String OSC_STOP = "/timecode/remote/stop";

    private boolean enabled = false;

    @EventCall
    public void onOscReceive(OscReceiveEvent e) {
        if (!enabled)
            return;
        if (e.getOscPacket() instanceof OSCMessage) {
            OSCMessage message = (OSCMessage) e.getOscPacket();

            switch (message.getAddress()) {
                case OSC_PLAY:
                    Worker.getInstance().play();
                    break;
                case OSC_PAUSE:
                    Worker.getInstance().pause();
                    break;
                case OSC_STOP:
                    Worker.getInstance().stop();
                    break;
            }
        }
    }
}
