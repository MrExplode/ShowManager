package me.mrexplode.showmanager.events;


import me.mrexplode.showmanager.events.impl.MarkerEvent;
import me.mrexplode.showmanager.events.impl.music.MusicEvent;
import me.mrexplode.showmanager.events.impl.osc.OscEvent;
import me.mrexplode.showmanager.events.impl.time.TimeChangeEvent;
import me.mrexplode.showmanager.events.impl.time.TimeEvent;

public interface TimeListener {
    
    void onTimeChangeEvent(TimeChangeEvent e);
    
    void onTimeEvent(TimeEvent e);
    
    void onOscEvent(OscEvent e);
    
    void onMarkerEvent(MarkerEvent e);
    
    void onMusicEvent(MusicEvent e);

}
