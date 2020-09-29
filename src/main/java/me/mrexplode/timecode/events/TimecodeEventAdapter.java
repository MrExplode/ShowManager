package me.mrexplode.timecode.events;


import me.mrexplode.timecode.events.impl.MarkerEvent;
import me.mrexplode.timecode.events.impl.music.MusicEvent;
import me.mrexplode.timecode.events.impl.osc.OscEvent;
import me.mrexplode.timecode.events.impl.time.TimeChangeEvent;
import me.mrexplode.timecode.events.impl.time.TimeEvent;

public class TimecodeEventAdapter implements TimeListener {

    @Override
    public void onTimeChangeEvent(TimeChangeEvent e) {
        // overridden in extending class
    }

    @Override
    public void onTimeEvent(TimeEvent e) {
        // overridden in extending class
    }

    @Override
    public void onOscEvent(OscEvent e) {
        // overridden in extending class
    }

    @Override
    public void onMarkerEvent(MarkerEvent e) {
        // overridden in extending class
    }
    
    @Override
    public void onMusicEvent(MusicEvent e) {
        // overridden in extending class
    }
    
}
