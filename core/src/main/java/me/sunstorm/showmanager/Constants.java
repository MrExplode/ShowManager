package me.sunstorm.showmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.illposed.osc.OSCMessage;
import lombok.experimental.UtilityClass;
import me.sunstorm.showmanager.scheduler.ScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;
import me.sunstorm.showmanager.util.serialize.OscMessageSerializer;
import me.sunstorm.showmanager.util.serialize.ScheduledEventSerializer;
import me.sunstorm.showmanager.util.serialize.TimecodeSerializer;

import java.io.File;

@UtilityClass
public class Constants {
    public final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(OSCMessage.class, new OscMessageSerializer())
            .registerTypeAdapter(Timecode.class, new TimecodeSerializer())
            .registerTypeAdapter(ScheduledEvent.class, new ScheduledEventSerializer())
            .create();
    //yeah I don't care about cross compat ATM.
    public File BASE_DIRECTORY = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming", "ShowManager");
}
