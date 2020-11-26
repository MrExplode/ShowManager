package me.mrexplode.timecode.gui.general;

import java.awt.Color;

import javax.swing.JComponent;

public class Animator implements Runnable {
    
    private final JComponent component;
    private Color color;
    private Color base;
    private boolean running = false;
    private boolean value = true;

    public Animator(JComponent component) {
        this.component = component;
    }
    
    @Override
    public void run() {
        Thread.currentThread().setName("Display Animator");
        long time = 0;
        running = true;
        while (running) {
            if (System.currentTimeMillis() > time + 750) {
                time = System.currentTimeMillis();
                if (value) {
                    component.setBackground(color);
                } else {
                    component.setBackground(base);
                }
                value = !value;
            }
        }
    }
    
    public void startFlash(Color color) {
        this.color = color;
        this.base = component.getBackground();
        Thread animThread = new Thread(this);
        animThread.start();
    }
    
    public void stopFlash() {
        running = false;
        component.setBackground(base);
    }
    
    public boolean isRunning() {
        return running;
    }
    
}