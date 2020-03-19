package me.mrexplode.timecode;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import me.mrexplode.timecode.events.EventHandler;
import me.mrexplode.timecode.events.TimeChangeEvent;
import me.mrexplode.timecode.events.TimeListener;
import me.mrexplode.timecode.gui.MainGUI;

public class DataGrabber implements Runnable {
    
    private static EventHandler eventHandler;
    
    private WorkerThread worker;
    private MainGUI gui;
    private boolean running;
    private RemoteState previousState;
    
    public DataGrabber(MainGUI guiInstance) {
        this.gui = guiInstance;
        this.running = true;
        this.previousState = RemoteState.DISABLED;
        eventHandler = new EventHandler();
    }
    
    public static EventHandler getEventHandler() {
        if (eventHandler != null) {
            return eventHandler;
        } else {
            throw new IllegalStateException("DataGrabber not initialized, but tried to get EventHandler instance");
        }
    }
    
    public void setWorkerInstance(WorkerThread instance) {
        this.worker = instance;
    }

    @Override
    public void run() {
        log("Starting thread...");
        Thread.currentThread().setName("DataGrabber Thread");
        while (running) {
            String timeString = worker.getCurrentTime();
            Timecode timecode = worker.getCurrentTimecode();
            RemoteState remoteState = worker.getRemoteState();
            
            boolean playing = worker.isPlaying();
            gui.timeDisplay.setText(timeString);
            gui.btnSetTime.setEnabled(!playing);
            gui.framerateBox.setEnabled(!playing);
            gui.btnRestart.setEnabled(!playing);
            Color defColor = UIManager.getColor("Button.background");
            gui.btnPlay.setBackground(playing ? Color.GREEN : defColor);

            
            if (remoteState != previousState) {
                previousState = remoteState;
                
                switch (remoteState) {
                    case DISABLED:
                        gui.remoteControl.setText("");
                        gui.btnPlay.setEnabled(true);
                        gui.btnPause.setEnabled(true);
                        gui.btnStop.setEnabled(true);
                        break;
                    case FORCE_IDLE:
                        gui.remoteControl.setText("Remote control: Force takeover");
                        gui.btnPlay.setEnabled(false);
                        gui.btnPause.setEnabled(false);
                        gui.btnStop.setEnabled(false);
                        break;
                    case IDLE:
                        gui.remoteControl.setText("Remote control: Waiting");
                        gui.btnPlay.setEnabled(true);
                        gui.btnPause.setEnabled(true);
                        gui.btnStop.setEnabled(true);
                        break;
                    case PAUSE:
                        gui.remoteControl.setText("Remote control: Paused");
                        gui.btnPlay.setBackground(defColor);
                        gui.btnPause.setBackground(Color.ORANGE);
                        gui.btnStop.setBackground(defColor);
                        gui.btnPlay.setEnabled(false);
                        gui.btnPause.setEnabled(false);
                        gui.btnStop.setEnabled(false);
                        break;
                    case PLAYING:
                        gui.remoteControl.setText("Remote control: Playing");
                        gui.btnPlay.setBackground(Color.GREEN);
                        gui.btnPause.setBackground(defColor);
                        gui.btnStop.setBackground(defColor);
                        gui.btnPlay.setEnabled(false);
                        gui.btnPause.setEnabled(false);
                        gui.btnStop.setEnabled(false);
                        break;
                    case STOPPED:
                        gui.remoteControl.setText("Remote control: Stopped");
                        gui.btnPlay.setBackground(defColor);
                        gui.btnPause.setBackground(defColor);
                        gui.btnStop.setBackground(Color.RED);
                        gui.btnPlay.setEnabled(false);
                        gui.btnPause.setEnabled(false);
                        gui.btnStop.setEnabled(false);
                        break;
                    default:
                        break;
                    
                }
            }
            
            TimeChangeEvent event = new TimeChangeEvent(timecode);
            eventHandler.callEvent(event);
            
            try {
                synchronized (Thread.currentThread()) {
                    //sleeping until the next iteration
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException e) {
                err("DataGrabber got interrupted! Restart is strongly adviced since gui won't work anymore!");
                e.printStackTrace();
                stop();
                throw new RuntimeException("Thread got interrupted while trying to wait.", e);
            }
        }
    }
    
    public void stop() {
        System.out.println("[DataGrabber] Shutting down...");
        this.running = false;
    }
    
    private void log(String message) {
        System.out.println("[DataGrabber] " + message);
    }
    
    private void err(String errorMessage) {
        System.err.println("[DataGrabber] " + errorMessage);
    }

}
