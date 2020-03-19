package me.mrexplode.timecode.events;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    
    private List<TimeListener> listeners;
    
    public EventHandler()  {
        this.listeners = new ArrayList<TimeListener>();
    }
    
    public void addListener(TimeListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeListener(TimeListener listener) {
        this.listeners.remove(listener);
    }
    
    public void removeAllListeners() {
        this.listeners.clear();
    }
    
    public void callEvent(TimecodeEvent e) {
        switch (e.getName()) {
            default:
                System.err.println("[EventHandler] Unknown event type: " + e.getName());
            break;
            case "TimeChangeEvent":
                
            break;
            case"TimeEvent":
                
            break;
            case "OscEvent":
                
            break;
        }
    }

}
