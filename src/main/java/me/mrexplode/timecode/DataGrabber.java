package me.mrexplode.timecode;

import java.awt.Color;

import javax.swing.UIManager;

public class DataGrabber implements Runnable {
    
    private WorkerThread worker;
    private MainGUI gui;
    private boolean running;
    
    public Object lock = new Object();
    
    public DataGrabber(MainGUI guiInstance) {
        this.gui = guiInstance;
        this.running = true;
    }
    
    public void setWorkerInstance(WorkerThread instance) {
        this.worker = instance;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("DataGrabber Thread");
        while (running) {
            String time = worker.getCurrentTime();
            boolean playing = worker.isPlaying();
            gui.timeDisplay.setText(time);
            gui.btnSetTime.setEnabled(!playing);
            gui.framerateBox.setEnabled(!playing);
            Color defColor = UIManager.getColor("Button.background");
            gui.btnPlay.setBackground(playing ? Color.GREEN : defColor);

            switch (worker.getRemoteState()) {
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
            
            try {
                synchronized (Thread.currentThread()) {
                    //sleeping until the next iteration
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException e) {
                System.out.println("DataGrabber got interrupted! Restart is strongly adviced since gui won't work anymore!");
                e.printStackTrace();
                stop();
            }
        }
    }
    
    public void stop() {
        this.running = false;
    }

}
