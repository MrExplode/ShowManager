package me.sunstorm.showmanager.modules.remote;

import com.illposed.osc.OSCMessage;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;

/**
 * This class handles the OSC remote capabilities.
 */
public class OscRemoteModule implements Listener, InjectRecipient {
    /**
     * OSC packets coming on this address will be considered as PLAY triggers
     */
    public static final String OSC_PLAY = "/showmanager/remote/play";
    /**
     * OSC packets coming on this address will be considered as PAUSE triggers
     */
    public static final String OSC_PAUSE = "/showmanager/remote/pause";
    /**
     * OSC packets coming on this address will be considered as STOP triggers
     */
    public static final String OSC_STOP = "/showmanager/remote/stop";

    @Inject
    private EventBus eventBus;
    @Inject
    private Worker worker;

    private boolean enabled = false;

    public OscRemoteModule() {
        inject();
        eventBus.register(this);
    }

    @EventCall
    public void onOscReceive(OscReceiveEvent e) {
        if (!enabled)
            return;
        if (e.getOscPacket() instanceof OSCMessage message) {
            switch (message.getAddress()) {
                case OSC_PLAY -> worker.play();
                case OSC_PAUSE -> worker.pause();
                case OSC_STOP -> worker.stop();
                default -> {
                }
            }
        }
    }
}
