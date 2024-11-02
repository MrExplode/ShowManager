package me.sunstorm.showmanager.modules.remote;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.remote.DmxRemoteStateEvent;
import me.sunstorm.showmanager.modules.Module;
import me.sunstorm.showmanager.util.DmxAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * It's possible to remote control ShowManager via dmx signal.
 * This can be done by setting a single dmx channel to specific percentages. <br><br>
 * 10% - Force Idle <br>
 * 20% - Play <br>
 * 30% - Pause <br>
 * 40% - Stop <br>
 * Any other value: idle
 */
@Singleton
public class DmxRemoteModule extends Module {
    private static final int TOLERANCE = 5;
    @Nullable
    private Worker worker;
    private final Provider<Worker> workerProvider;
    private DmxAddress address = new DmxAddress(0, 0, 0);
    private boolean enabled = false;
    private DmxRemoteState state = DmxRemoteState.DISABLED;
    private DmxRemoteState previousState = DmxRemoteState.DISABLED;

    @Inject
    public DmxRemoteModule(EventBus bus, Provider<Worker> worker) {
        super(bus);
        this.workerProvider = worker;
        load();
    }

    public void handleData(byte[] dmxData) {
        if (!enabled) {
            state = DmxRemoteState.DISABLED;
            return;
        }
        if (worker == null) {
            worker = workerProvider.get();
        }
        byte value = dmxData[address.address() - 1];
        if (inToleratedRange(25, value)) {
            state = DmxRemoteState.FORCE_IDLE;
            if (state != previousState) {
                DmxRemoteStateEvent event = new DmxRemoteStateEvent(state, previousState);
                event.call(eventBus);
                previousState = state;
            }
        } else if (inToleratedRange(51, value)) {
            state = DmxRemoteState.PLAYING;
            if (state != previousState) {
                DmxRemoteStateEvent event = new DmxRemoteStateEvent(state, previousState);
                event.call(eventBus);
                previousState = state;
                worker.play();
            }
        } else if (inToleratedRange(76, value)) {
            state = DmxRemoteState.PAUSE;
            if (state != previousState) {
                DmxRemoteStateEvent event = new DmxRemoteStateEvent(state, previousState);
                event.call(eventBus);
                previousState = state;
                worker.pause();
            }
        } else if (inToleratedRange(102, value)) {
            state = DmxRemoteState.STOPPED;
            if (state != previousState) {
                DmxRemoteStateEvent event = new DmxRemoteStateEvent(state, previousState);
                event.call(eventBus);
                previousState = state;
                worker.stop();
            }
        } else {
            state = DmxRemoteState.IDLE;
            if (state != previousState) {
                DmxRemoteStateEvent event = new DmxRemoteStateEvent(state, previousState);
                event.call(eventBus);
                previousState = state;
            }
        }
    }

    private boolean inToleratedRange(int origin, byte value) {
        return value <= origin + TOLERANCE && value >= origin - TOLERANCE;
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        data.addProperty("address", address.address());
        data.addProperty("universe", address.universe());
        data.addProperty("subnet", address.subnet());
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonElement element) {
        var object = element.getAsJsonObject();
        enabled = object.get("enabled").getAsBoolean();
        address = new DmxAddress(object.get("address").getAsInt(), object.get("universe").getAsInt(), object.get("subnet").getAsInt());
    }

    @Override
    public String getName() {
        return "dmx-remote";
    }

    // generated

    public DmxAddress getAddress() {
        return address;
    }
}
