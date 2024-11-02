package me.sunstorm.showmanager.modules.remote;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.illposed.osc.OSCMessage;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;
import me.sunstorm.showmanager.modules.ToggleableModule;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class handles the OSC remote capabilities.
 */
@Singleton
public class OscRemoteModule extends ToggleableModule {
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

    private final Worker worker;

    @Inject
    public OscRemoteModule(EventBus bus, Worker worker) {
        super(bus);
        this.worker = worker;
        eventBus.register(this);
    }

    @EventCall
    public void onOscReceive(OscReceiveEvent e) {
        if (!isEnabled())
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

    @NotNull
    @Override
    public JsonElement getData() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void onLoad(@NotNull JsonElement element) {
        // unused
    }

    @Override
    public String getName() {
        return "osc-remote";
    }
}
