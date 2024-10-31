package me.sunstorm.showmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.illposed.osc.OSCMessage;
import me.sunstorm.showmanager.modules.audio.AudioTrack;
import me.sunstorm.showmanager.modules.scheduler.ScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;
import me.sunstorm.showmanager.util.serialize.AudioTrackSerializer;
import me.sunstorm.showmanager.util.serialize.OscMessageSerializer;
import me.sunstorm.showmanager.util.serialize.ScheduledEventSerializer;
import me.sunstorm.showmanager.util.serialize.TimecodeSerializer;

import java.io.File;

public interface Constants {
    Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(OSCMessage.class, new OscMessageSerializer())
            .registerTypeAdapter(Timecode.class, new TimecodeSerializer())
            .registerTypeAdapter(ScheduledEvent.class, new ScheduledEventSerializer())
            .registerTypeAdapter(AudioTrack.class, new AudioTrackSerializer())
            .create();
    //yeah I don't care about cross compat ATM.
    File BASE_DIRECTORY = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming", "ShowManager");
}
