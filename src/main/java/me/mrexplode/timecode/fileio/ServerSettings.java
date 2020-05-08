package me.mrexplode.timecode.fileio;

import me.mrexplode.timecode.schedule.ScheduledEvent;
import me.mrexplode.timecode.schedule.ScheduledOSC;

public class ServerSettings {
    
    //general timecode stuff
    public String artnetInterface;
    public String ltcAudioOutput;
    public int framerate;
    
    //dmx remote
    public int dmxAddress;
    public int dmxUniverse;
    public int dmxSubnet;
    
    //music
    public String musicAudioOutput;
    public Music[] musicTracks;
    
    //osc
    public String oscTargetIP;
    public int oscPort;
    public ScheduledEvent[] genericEvents;
    public ScheduledOSC[] oscEvents;
    
    //networking
    public int com1Port;
    public int com2Port;
    public String com2Interface;

}
