package me.mrexplode.showmanager;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import me.mrexplode.showmanager.gui.ServerGUI;
import me.mrexplode.showmanager.gui.general.SchedulerTableModel;
import me.mrexplode.showmanager.remote.DmxRemoteState;
import me.mrexplode.showmanager.util.Timecode;

@Deprecated
public class DataGrabber implements Runnable {
    
    private WorkerThread worker;
    private final ServerGUI gui;
    private boolean running;
    private DmxRemoteState previousState;
    private ArrayList<Integer> prevDispatched;
    private Timecode currentTime = new Timecode(0);
    
    private final Object dataLock = new Object();
    
    public DataGrabber() {
        this.gui = ServerGUI.getInstance();
        this.running = true;
        this.previousState = DmxRemoteState.DISABLED;
        this.prevDispatched = new ArrayList<>();
    }
    
    public Object getLock() {
        return dataLock;
    }
    
    public void setWorkerInstance(WorkerThread instance) {
        this.worker = instance;
    }

    @Override
    public void run() {
        //log("Starting thread...");
        Thread.currentThread().setName("DataGrabber Thread");
        
//        eventHandler.addListener(new TimecodeEventAdapter() {
//            @Override
//            public void onTimeEvent(TimeEvent e) {
//                if (e.getType() == EventType.TC_START) {
//                    prevDispatched = new ArrayList<>();
//                    if (gui.getTimeMonitor().isVisible())
//                        gui.getTimeMonitor().getAnimator().startFlash(Color.RED);
//                }
//                if (e.getType() == EventType.TC_PAUSE || e.getType() == EventType.TC_STOP) {
//                    if (gui.getTimeMonitor().isVisible())
//                        gui.getTimeMonitor().getAnimator().stopFlash();
//                }
//            }
//        });
        
        while (running) {
            if (running) {
                try {
                    synchronized (dataLock) {
                        //sleeping until the next iteration
                        dataLock.wait();
                    }
                } catch (InterruptedException e) {
                    //err("DataGrabber got interrupted! Restart is strongly adviced since gui won't work anymore!");
                    e.printStackTrace();
                    shutdown();
                    throw new RuntimeException("Thread got interrupted while trying to wait.", e);
                }
            }
        }
    }
    
    public void update() {
        currentTime = worker.getTimecode();
        String timeString = worker.getTimecode().guiFormatted();
        DmxRemoteState dmxRemoteState = DmxRemoteState.DISABLED;//worker.getRemoteState();
        
        boolean playing = worker.isPlaying();
        
//        if (playing) {
//            TimeChangeEvent event = new TimeChangeEvent(currentTime);
//            eventHandler.callEvent(event);
//        }
        
        onEDT(() -> {
            if (gui.getTimeMonitor().isVisible()) {
                gui.getTimeMonitor().timeDisplay.setText(timeString);
            }
            gui.getTimeDisplay().setText(timeString);
            gui.getBtnSetTime().setEnabled(!playing);
            gui.getFramerateBox().setEnabled(!playing);
            gui.getBtnRestart().setEnabled(!playing);
            gui.getMusicCheckBox().setEnabled(!playing);
            //OSC
            ((SchedulerTableModel) gui.getTable().getModel()).setEditable(!playing);
            if (!((SchedulerTableModel) gui.getTable().getModel()).getLatestDispatched().equals(this.prevDispatched)) {
                this.prevDispatched =  new ArrayList<>(((SchedulerTableModel) gui.getTable().getModel()).getLatestDispatched());
                gui.getTable().clearSelection();
                gui.getTable().setRowSelectionInterval(prevDispatched.get(0), prevDispatched.get(prevDispatched.size() - 1));
            }
            Color defColor = UIManager.getColor("Button.background");
            gui.getBtnPlay().setBackground(playing ? Color.GREEN : defColor);

            
            if (dmxRemoteState != previousState) {
                previousState = dmxRemoteState;
                
                switch (dmxRemoteState) {
                    case DISABLED:
                        gui.getRemoteControl().setText("");
                        gui.getBtnPlay().setEnabled(true);
                        gui.getBtnPause().setEnabled(true);
                        gui.getBtnStop().setEnabled(true);
                        gui.getBtnNow().setEnabled(true);
                        gui.getBtnSort().setEnabled(true);
                        gui.getBtnInsert().setEnabled(true);
                        gui.getBtnInsertTime().setEnabled(true);
                        gui.getBtnAdd().setEnabled(true);
                        gui.getBtnRemove().setEnabled(true);
                        break;
                    case FORCE_IDLE:
                        gui.getRemoteControl().setText("Remote control: Force takeover");
                        gui.getBtnPlay().setEnabled(false);
                        gui.getBtnPause().setEnabled(false);
                        gui.getBtnStop().setEnabled(false);
                        gui.getBtnNow().setEnabled(false);
                        gui.getBtnSort().setEnabled(false);
                        gui.getBtnInsert().setEnabled(false);
                        gui.getBtnInsertTime().setEnabled(false);
                        gui.getBtnAdd().setEnabled(false);
                        gui.getBtnRemove().setEnabled(false);
                        break;
                    case IDLE:
                        gui.getRemoteControl().setText("Remote control: Waiting");
                        gui.getBtnPlay().setBackground(defColor);
                        gui.getBtnPause().setBackground(defColor);
                        gui.getBtnStop().setBackground(defColor);
                        gui.getBtnPlay().setEnabled(true);
                        gui.getBtnPause().setEnabled(true);
                        gui.getBtnStop().setEnabled(true);
                        gui.getBtnNow().setEnabled(true);
                        gui.getBtnSort().setEnabled(true);
                        gui.getBtnInsert().setEnabled(true);
                        gui.getBtnInsertTime().setEnabled(true);
                        gui.getBtnAdd().setEnabled(true);
                        gui.getBtnRemove().setEnabled(true);
                        break;
                    case PAUSE:
                        gui.getRemoteControl().setText("Remote control: Paused");
                        gui.getBtnPlay().setBackground(defColor);
                        gui.getBtnPause().setBackground(Color.ORANGE);
                        gui.getBtnStop().setBackground(defColor);
                        gui.getBtnPlay().setEnabled(false);
                        gui.getBtnPause().setEnabled(false);
                        gui.getBtnStop().setEnabled(false);
                        gui.getBtnNow().setEnabled(false);
                        gui.getBtnSort().setEnabled(false);
                        gui.getBtnInsert().setEnabled(false);
                        gui.getBtnInsertTime().setEnabled(false);
                        gui.getBtnAdd().setEnabled(false);
                        gui.getBtnRemove().setEnabled(false);
                        break;
                    case PLAYING:
                        gui.getRemoteControl().setText("Remote control: Playing");
                        gui.getBtnPlay().setBackground(Color.GREEN);
                        gui.getBtnPause().setBackground(defColor);
                        gui.getBtnStop().setBackground(defColor);
                        gui.getBtnPlay().setEnabled(false);
                        gui.getBtnPause().setEnabled(false);
                        gui.getBtnStop().setEnabled(false);
                        gui.getBtnNow().setEnabled(false);
                        gui.getBtnSort().setEnabled(false);
                        gui.getBtnInsert().setEnabled(false);
                        gui.getBtnInsertTime().setEnabled(false);
                        gui.getBtnAdd().setEnabled(false);
                        gui.getBtnRemove().setEnabled(false);
                        break;
                    case STOPPED:
                        gui.getRemoteControl().setText("Remote control: Stopped");
                        gui.getBtnPlay().setBackground(defColor);
                        gui.getBtnPause().setBackground(defColor);
                        gui.getBtnStop().setBackground(Color.RED);
                        gui.getBtnPlay().setEnabled(false);
                        gui.getBtnPause().setEnabled(false);
                        gui.getBtnStop().setEnabled(false);
                        gui.getBtnNow().setEnabled(false);
                        gui.getBtnSort().setEnabled(false);
                        gui.getBtnInsert().setEnabled(false);
                        gui.getBtnInsertTime().setEnabled(false);
                        gui.getBtnAdd().setEnabled(false);
                        gui.getBtnRemove().setEnabled(false);
                        break;
                    default:
                        break;
                    
                }
            }
        });
    }
    
    public void shutdown() {
        //log("Shutting down...");
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

}
