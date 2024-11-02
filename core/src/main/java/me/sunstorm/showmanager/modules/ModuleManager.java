package me.sunstorm.showmanager.modules;

import me.sunstorm.showmanager.modules.artnet.ArtNetModule;
import me.sunstorm.showmanager.modules.audio.AudioModule;
import me.sunstorm.showmanager.modules.http.HttpModule;
import me.sunstorm.showmanager.modules.ltc.LtcModule;
import me.sunstorm.showmanager.modules.osc.OscModule;
import me.sunstorm.showmanager.modules.remote.DmxRemoteModule;
import me.sunstorm.showmanager.modules.remote.OscRemoteModule;
import me.sunstorm.showmanager.modules.scheduler.SchedulerModule;
import org.codejargon.feather.Feather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
    private static final List<Class<? extends Module>> MODULES = List.of(
            ArtNetModule.class,
            AudioModule.class,
            HttpModule.class,
            LtcModule.class,
            OscModule.class,
            DmxRemoteModule.class,
            OscRemoteModule.class,
            SchedulerModule.class
    );

    private final Feather feather;

    public ModuleManager(Feather feather) {
        this.feather = feather;
        var m = new ArrayList<>(MODULES);
        Collections.shuffle(m);
        m.forEach(feather::instance);
    }

    public <T extends Module> T get(Class<T> module) {
        return feather.instance(module);
    }
}
