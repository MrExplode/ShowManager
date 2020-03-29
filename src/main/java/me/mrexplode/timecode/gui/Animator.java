package me.mrexplode.timecode.gui;

import java.awt.Color;

public class Animator implements Runnable {
    
    private TimeMonitor instance;
    private Color color;
    private Color base;
    private boolean running = false;
    private boolean value = true;
    private Thread animThread;
    
    public Animator(TimeMonitor instance) {
        this.instance = instance;
    }
    
    @Override
    public void run() {
        Thread.currentThread().setName("Display Animator");
        System.out.println("started animator");
        long time = 0;
        running = true;
        while (running) {
            if (System.currentTimeMillis() > time + 750) {
                time = System.currentTimeMillis();
                if (value) {
                    instance.contentPane.setBackground(color);
                    value = !value;
                } else {
                    instance.contentPane.setBackground(base);
                    value = !value;
                }
            }
        }
    }
    
    public void startFlash(Color color) {
        this.color = color;
        this.base = instance.contentPane.getBackground();
        this.animThread = new Thread(this);
        this.animThread.start();
    }
    
    public void stopFlash() {
        running = false;
        instance.contentPane.setBackground(base);
    }
    
    public boolean isRunning() {
        return running;
    }
    
}