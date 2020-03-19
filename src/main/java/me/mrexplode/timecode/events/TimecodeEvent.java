package me.mrexplode.timecode.events;

/**
 * Superclass
 * @author <a href="https://mrexplode.github.io">MrExplode</a>
 *
 */
public class TimecodeEvent {
    
    private EventType type;
    private String name;
    
    public TimecodeEvent(EventType type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public EventType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
}
