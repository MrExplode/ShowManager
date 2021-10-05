package me.sunstorm.showmanager.remote;

import com.illposed.osc.OSCMessage;
import lombok.Data;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;

@Data
public class OscRemoteControl implements Listener, InjectRecipient {
    public static final String OSC_PLAY = "/timecode/remote/play";
    public static final String OSC_PAUSE = "/timecode/remote/pause";
    public static final String OSC_STOP = "/timecode/remote/stop";

    @Inject
    private EventBus eventBus;
    @Inject
    private Worker worker;

    private boolean enabled = false;

    public OscRemoteControl() {
        inject();
        eventBus.register(this);
    }

    @EventCall
    public void onOscReceive(OscReceiveEvent e) {
        if (!enabled)
            return;
        if (e.getOscPacket() instanceof OSCMessage) {
            OSCMessage message = (OSCMessage) e.getOscPacket();

            switch (message.getAddress()) {
                case OSC_PLAY:
                    worker.play();
                    break;
                case OSC_PAUSE:
                    worker.pause();
                    break;
                case OSC_STOP:
                    worker.stop();
                    break;
                default:
                    break;
            }
        }
    }
}
