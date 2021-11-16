package me.sunstorm.showmanager.event.impl;

import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;

public class DummyListener implements Listener {

    @EventCall
    public void onDummyEvent(DummyEvent e) {

    }
}
