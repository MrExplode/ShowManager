package me.mrexplode.timecode.fileio;

import me.mrexplode.timecode.schedule.ScheduledEvent;
import me.mrexplode.timecode.schedule.ScheduledOSC;

public class ServerSettings {
    
    public String netInterface;
    public String ltcAudioOutput;
    public String musicAudioOutput;
    public int framerate;
    public int dmxAddress;
    public int dmxUniverse;
    public int dmxSubnet;
    public Music[] musicTracks;
    
    //osc
    public String oscTargetIP;
    public int oscPort;
    public ScheduledEvent[] genericEvents;
    public ScheduledOSC[] oscEvents;

}
