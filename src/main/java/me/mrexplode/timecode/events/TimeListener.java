package me.mrexplode.timecode.events;


public interface TimeListener {
    
    public void onTimeChangeEvent(TimeChangeEvent e);
    
    public void onTimeEvent(TimeEvent e);
    
    public void onOscEvent(OscEvent e);
    
    public void onMarkerEvent(MarkerEvent e);
    
    public void onMusicEvent(MusicEvent e);

}
