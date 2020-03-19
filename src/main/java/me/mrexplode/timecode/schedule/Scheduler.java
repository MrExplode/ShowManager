package me.mrexplode.timecode.schedule;

import java.io.IOException;
import java.net.InetAddress;

import com.illposed.osc.transport.udp.OSCPortOut;

import me.mrexplode.timecode.DataGrabber;
import me.mrexplode.timecode.Timecode;
import me.mrexplode.timecode.events.TimeChangeEvent;
import me.mrexplode.timecode.events.TimecodeEventAdapter;

public class Scheduler implements Runnable {
    
    private boolean running;
    private Object lock = new Object();
    
    private OSCPortOut oscPort;
    private InetAddress targetAddress;
    int port;
    
    public Scheduler(InetAddress address, int port) {
        this.targetAddress = address;
        this.port = port;
        try {
            oscPort = new OSCPortOut(targetAddress, port);
        } catch (IOException e) {
            err("Failed to initialize thread!");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        log("Starting thread..");
        DataGrabber.getEventHandler().addListener(new TimecodeEventAdapter() {
            @Override
            public void onTimeChangeEvent(TimeChangeEvent e) {
                Timecode time = e.getTime();
                
                
                lock.notify();
            }
        });
        while (running) {
            //loop
            
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void shutdown() {
        log("Shutting down thread...");
        this.running = false;
        //escape waiting
        lock.notify();
        try {
            oscPort.close();
        } catch (IOException e) {
            err("Error while shutting down thread. You can ignore, as it is already shutting down.");
            e.printStackTrace();
        }
    }
    
    private static void log(String message) {
        System.out.println("[Scheduler] " + message);
    }
    
    private static void err(String errorMessage) {
        System.err.println("[Scheduler] " + errorMessage);
    }

}
