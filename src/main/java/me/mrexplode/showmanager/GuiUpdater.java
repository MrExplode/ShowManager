package me.mrexplode.showmanager;

import me.mrexplode.showmanager.eventsystem.EventCall;
import me.mrexplode.showmanager.eventsystem.Listener;
import me.mrexplode.showmanager.eventsystem.events.remote.DmxRemoteStateEvent;
import me.mrexplode.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.mrexplode.showmanager.eventsystem.events.time.TimecodeStartEvent;
import me.mrexplode.showmanager.eventsystem.events.time.TimecodeStopEvent;
import me.mrexplode.showmanager.gui.ServerGUI;
import me.mrexplode.showmanager.gui.general.SchedulerTableModel;

import javax.swing.*;
import java.awt.*;

//this class is responsible for updating the gui
public class GuiUpdater implements Listener {
    private final ServerGUI gui;

    public GuiUpdater() {
        WorkerThread.getInstance().getEventBus().register(this);
        gui = ServerGUI.getInstance();
    }

    @EventCall
    void onTimeChange(TimecodeChangeEvent e) {
        onEventDispatch(() -> {
            gui.getTimeDisplay().setText(e.getTime().guiFormatted());
            if (gui.getTimeMonitor().isVisible())
                gui.getTimeMonitor().timeDisplay.setText(e.getTime().guiFormatted());
        });
    }

    @EventCall
    void onTimeStart(TimecodeStartEvent e) {
        onEventDispatch(() -> {
            gui.getBtnSetTime().setEnabled(false);
            gui.getFramerateBox().setEnabled(false);
            gui.getBtnRestart().setEnabled(false);
            gui.getMusicCheckBox().setEnabled(false);
            ((SchedulerTableModel) gui.getTable().getModel()).setEditable(false);
        });
    }

    @EventCall
    void onTimeStop(TimecodeStopEvent e) {
        onEventDispatch(() -> {
            gui.getBtnSetTime().setEnabled(true);
            gui.getFramerateBox().setEnabled(true);
            gui.getBtnRestart().setEnabled(true);
            gui.getMusicCheckBox().setEnabled(true);
            ((SchedulerTableModel) gui.getTable().getModel()).setEditable(true);
        });
    }

    @EventCall
    void onDmxRemoteStateChange(DmxRemoteStateEvent e) {
        onEventDispatch(() -> {
            Color defColor = UIManager.getColor("Button.background");
            switch (e.getState()) {
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
        });
    }

    private void onEventDispatch(Runnable r) {
        SwingUtilities.invokeLater(r);
    }
}
