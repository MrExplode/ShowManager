package me.mrexplode.timecode;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import me.mrexplode.timecode.events.EventHandler;
import me.mrexplode.timecode.events.EventType;
import me.mrexplode.timecode.events.TimeChangeEvent;
import me.mrexplode.timecode.events.TimeEvent;
import me.mrexplode.timecode.events.TimecodeEventAdapter;
import me.mrexplode.timecode.gui.ServerGUI;
import me.mrexplode.timecode.gui.SchedulerTableModel;

public class DataGrabber implements Runnable {
    
    private static EventHandler eventHandler;
    
    private WorkerThread worker;
    private ServerGUI gui;
    private boolean running;
    private RemoteState previousState;
    private ArrayList<Integer> prevDispatched;
    private Timecode currentTime = new Timecode(0, 0, 0, 0);
    
    private Object dataLock = new Object();
    
    public DataGrabber(ServerGUI guiInstance, int networkPort) {
        this.gui = guiInstance;
        this.running = true;
        this.previousState = RemoteState.DISABLED;
        this.prevDispatched = new ArrayList<Integer>();
        eventHandler = new EventHandler();
        try {
            eventHandler.startNetworking(networkPort);
        } catch (IOException e) {
            err("Failed to start network event handling!");
            e.printStackTrace();
        }
    }
    
    public Object getLock() {
        return dataLock;
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
        
        eventHandler.addListener(new TimecodeEventAdapter() {
            @Override
            public void onTimeEvent(TimeEvent e) {
                if (e.getType() == EventType.TC_START) {
                    prevDispatched = new ArrayList<Integer>();
                    if (gui.timeMonitor.isVisible())
                        gui.timeMonitor.getAnimator().startFlash(Color.RED);
                }
                if (e.getType() == EventType.TC_PAUSE || e.getType() == EventType.TC_STOP) {
                    if (gui.timeMonitor.isVisible())
                        gui.timeMonitor.getAnimator().stopFlash();
                }
            }
        });
        
        while (running) {
            if (running) {
                try {
                    synchronized (dataLock) {
                        //sleeping until the next iteration
                        dataLock.wait();
                    }
                } catch (InterruptedException e) {
                    err("DataGrabber got interrupted! Restart is strongly adviced since gui won't work anymore!");
                    e.printStackTrace();
                    shutdown();
                    throw new RuntimeException("Thread got interrupted while trying to wait.", e);
                }
            }
        }
    }
    
    public void update() {
        currentTime = worker.getCurrentTimecode();
        String timeString = worker.getCurrentTime();
        RemoteState remoteState = worker.getRemoteState();
        
        boolean playing = worker.isPlaying();
        
        if (playing) {
            TimeChangeEvent event = new TimeChangeEvent(currentTime);
            eventHandler.callEvent(event);
        }
        
        onEDT(() -> {
            if (gui.timeMonitor.isVisible()) {
                gui.timeMonitor.timeDisplay.setText(timeString);
            }
            gui.timeDisplay.setText(timeString);
            gui.btnSetTime.setEnabled(!playing);
            gui.framerateBox.setEnabled(!playing);
            gui.btnRestart.setEnabled(!playing);
            gui.musicCheckBox.setEnabled(!playing);
            //OSC
            ((SchedulerTableModel) gui.table.getModel()).setEditable(!playing);
            if (!((SchedulerTableModel) gui.table.getModel()).getLatestDispatched().equals(this.prevDispatched)) {
                this.prevDispatched =  new ArrayList<Integer>(((SchedulerTableModel) gui.table.getModel()).getLatestDispatched());
                gui.table.clearSelection();
                gui.table.setRowSelectionInterval(prevDispatched.get(0), prevDispatched.get(prevDispatched.size() - 1));
            }
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
                        gui.btnNow.setEnabled(true);
                        gui.btnSort.setEnabled(true);
                        gui.btnInsert.setEnabled(true);
                        gui.btnInsertTime.setEnabled(true);
                        gui.btnAdd.setEnabled(true);
                        gui.btnRemove.setEnabled(true);
                        break;
                    case FORCE_IDLE:
                        gui.remoteControl.setText("Remote control: Force takeover");
                        gui.btnPlay.setEnabled(false);
                        gui.btnPause.setEnabled(false);
                        gui.btnStop.setEnabled(false);
                        gui.btnNow.setEnabled(false);
                        gui.btnSort.setEnabled(false);
                        gui.btnInsert.setEnabled(false);
                        gui.btnInsertTime.setEnabled(false);
                        gui.btnAdd.setEnabled(false);
                        gui.btnRemove.setEnabled(false);
                        break;
                    case IDLE:
                        gui.remoteControl.setText("Remote control: Waiting");
                        gui.btnPlay.setEnabled(true);
                        gui.btnPause.setEnabled(true);
                        gui.btnStop.setEnabled(true);
                        gui.btnStop.setEnabled(true);
                        gui.btnNow.setEnabled(true);
                        gui.btnSort.setEnabled(true);
                        gui.btnInsert.setEnabled(true);
                        gui.btnInsertTime.setEnabled(true);
                        gui.btnAdd.setEnabled(true);
                        gui.btnRemove.setEnabled(true);
                        break;
                    case PAUSE:
                        gui.remoteControl.setText("Remote control: Paused");
                        gui.btnPlay.setBackground(defColor);
                        gui.btnPause.setBackground(Color.ORANGE);
                        gui.btnStop.setBackground(defColor);
                        gui.btnPlay.setEnabled(false);
                        gui.btnPause.setEnabled(false);
                        gui.btnStop.setEnabled(false);
                        gui.btnNow.setEnabled(false);
                        gui.btnSort.setEnabled(false);
                        gui.btnInsert.setEnabled(false);
                        gui.btnInsertTime.setEnabled(false);
                        gui.btnAdd.setEnabled(false);
                        gui.btnRemove.setEnabled(false);
                        break;
                    case PLAYING:
                        gui.remoteControl.setText("Remote control: Playing");
                        gui.btnPlay.setBackground(Color.GREEN);
                        gui.btnPause.setBackground(defColor);
                        gui.btnStop.setBackground(defColor);
                        gui.btnPlay.setEnabled(false);
                        gui.btnPause.setEnabled(false);
                        gui.btnStop.setEnabled(false);
                        gui.btnNow.setEnabled(false);
                        gui.btnSort.setEnabled(false);
                        gui.btnInsert.setEnabled(false);
                        gui.btnInsertTime.setEnabled(false);
                        gui.btnAdd.setEnabled(false);
                        gui.btnRemove.setEnabled(false);
                        break;
                    case STOPPED:
                        gui.remoteControl.setText("Remote control: Stopped");
                        gui.btnPlay.setBackground(defColor);
                        gui.btnPause.setBackground(defColor);
                        gui.btnStop.setBackground(Color.RED);
                        gui.btnPlay.setEnabled(false);
                        gui.btnPause.setEnabled(false);
                        gui.btnStop.setEnabled(false);
                        gui.btnNow.setEnabled(false);
                        gui.btnSort.setEnabled(false);
                        gui.btnInsert.setEnabled(false);
                        gui.btnInsertTime.setEnabled(false);
                        gui.btnAdd.setEnabled(false);
                        gui.btnRemove.setEnabled(false);
                        break;
                    default:
                        break;
                    
                }
            }
        });
    }
    
    public void shutdown() {
        log("Shutting down...");
        this.running = false;
        synchronized (dataLock) {
            this.dataLock.notify();
        }
    }
    
    public Timecode getCurrentTime() {
        return currentTime;
    }
    
    private static void onEDT(Runnable task) {
        SwingUtilities.invokeLater(task);
    }
    
    private static void log(String message) {
        System.out.println("[DataGrabber] " + message);
    }
    
    private static void err(String errorMessage) {
        System.err.println("[DataGrabber] " + errorMessage);
    }

}
