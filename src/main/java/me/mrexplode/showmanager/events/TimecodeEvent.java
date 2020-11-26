package me.mrexplode.showmanager.events;

/**
 * Superclass
 * @author <a href="https://mrexplode.github.io">MrExplode</a>
 *
 */
public class TimecodeEvent {
    
    private EventType type;
    private String name;
    
    /**
     * 
     * @param type the type of the event
     * @param name MUST BE the same as the classname!
     */
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
