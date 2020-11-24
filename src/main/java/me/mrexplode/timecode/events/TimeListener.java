package me.mrexplode.timecode.events;


import me.mrexplode.timecode.events.impl.MarkerEvent;
import me.mrexplode.timecode.events.impl.music.MusicEvent;
import me.mrexplode.timecode.events.impl.osc.OscEvent;
import me.mrexplode.timecode.events.impl.time.TimeChangeEvent;
import me.mrexplode.timecode.events.impl.time.TimeEvent;

public interface TimeListener {
    
    void onTimeChangeEvent(TimeChangeEvent e);
    
    void onTimeEvent(TimeEvent e);
    
    void onOscEvent(OscEvent e);
    
    void onMarkerEvent(MarkerEvent e);
    
    void onMusicEvent(MusicEvent e);

}
