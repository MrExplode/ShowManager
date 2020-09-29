package me.mrexplode.timecode.events;


import me.mrexplode.timecode.events.impl.MarkerEvent;
import me.mrexplode.timecode.events.impl.music.MusicEvent;
import me.mrexplode.timecode.events.impl.osc.OscEvent;
import me.mrexplode.timecode.events.impl.time.TimeChangeEvent;
import me.mrexplode.timecode.events.impl.time.TimeEvent;

public interface TimeListener {
    
    public void onTimeChangeEvent(TimeChangeEvent e);
    
    public void onTimeEvent(TimeEvent e);
    
    public void onOscEvent(OscEvent e);
    
    public void onMarkerEvent(MarkerEvent e);
    
    public void onMusicEvent(MusicEvent e);

}
