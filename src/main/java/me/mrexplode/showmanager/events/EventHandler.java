package me.mrexplode.showmanager.events;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.transport.udp.OSCPortOut;
import me.mrexplode.showmanager.events.impl.music.MusicEvent;
import me.mrexplode.showmanager.events.impl.osc.OscEvent;
import me.mrexplode.showmanager.events.impl.time.TimeChangeEvent;
import me.mrexplode.showmanager.events.impl.time.TimeEvent;

public class EventHandler {
    
    private List<TimeListener> listeners;
    private Gson gson;
    private OSCPortOut oscOut;
    
    public EventHandler() {
        this.listeners = new ArrayList<>();
        this.gson = new Gson();
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
    
    public void startNetworking(int eventPort) throws IOException {
        this.oscOut = new OSCPortOut(InetAddress.getByName("255.255.255.255"), eventPort);
    }
    
    public void shutdown() {
        try {
            this.oscOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public OSCPortOut getSystemOSC() {
        if (oscOut != null) {
            return oscOut;
        } else {
            throw new IllegalArgumentException("Networking not initialized");
        }
    }
    
    /**
     * Handle the event coming over OSC, and call on local listeners.
     * @param msg the packet
     */
    public void fromNetwork(OSCMessage msg) {
        if (!msg.getAddress().startsWith("/timecode/events/call/")) {
            System.err.println("[NetEventHandler] Not Timecode Event packet! ignoring...");
            return;
        }
        if (msg.getArguments().size() == 0) {
            System.err.println("[NetEventHandler] No event body supplied in packet! ignoring...");
            return;
        }
        
        String eventName = msg.getAddress().substring(22);
        
        try {
            Class<?> clazz = Class.forName("me.mrexplode.timecode.events." + eventName);
            Object event = gson.fromJson((String) msg.getArguments().get(0), clazz);
            //double safe
            if (event instanceof TimecodeEvent) {
                callEvent((TimecodeEvent) event, true);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[NetEventHandler] Not existing event type " + eventName + ", ignoring...");
        }
        
    }
    
    /**
     * Calls the event, and broadcasts it to all listening clients.
     * Every listener listening on the same network will be notified for it's event.
     * @param e the event to send
     */
    public void callEvent(TimecodeEvent e) {
        callEvent(e, false);
    }
    
    /*
     * recieved indicates that we are recieved the event over network, so we don't have to send it out again.
     * The event happened on an other client.
     */
    private void callEvent(TimecodeEvent e, boolean recieved) {
        switch (e.getName()) {
            default:
                System.err.println("[EventHandler] Unknown event type: " + e.getName());
            break;
            case "TimeChangeEvent":
                for (TimeListener listener : listeners) {
                    listener.onTimeChangeEvent((TimeChangeEvent) e);
                }
            break;
            case"TimeEvent":
                for (TimeListener listener : listeners) {
                    listener.onTimeEvent((TimeEvent) e);
                }
            break;
            case "OscEvent":
                for (TimeListener listener : listeners) {
                    listener.onOscEvent((OscEvent) e);
                }
            break;
            case "MusicEvent":
                for (TimeListener listener : listeners) {
                    listener.onMusicEvent((MusicEvent) e);
                }
            break;
        }
        if (!recieved)
            if (oscOut != null) {
                send(e);
            } else {
                System.err.println("[NetEventHandler] Could not dispatch event over network, because networking isn't started");
            }
    }
    
    private void send(TimecodeEvent e) {
        String path = "/timecode/events/call/" + e.getName();
        String value = gson.toJson(e);
        try {
            oscOut.send(new OSCMessage(path, Collections.singletonList(value)));
        } catch (IOException e1) {
            System.err.println("[NetEventHandler] Failed to send event over network");
            e1.printStackTrace();
        } catch (OSCSerializeException e1) {
            System.out.println("[NetEventHandler] Failed to serialize OSC message");
            e1.printStackTrace();
        }
    }

}
