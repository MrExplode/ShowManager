package me.mrexplode.showmanager.fileio;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;

import me.mrexplode.showmanager.gui.ServerGUI;
import me.mrexplode.showmanager.gui.general.SchedulerTableModel;

public class ServerSettingsProvider {
    
    private File saveFile;
    private ServerGUI gui;
    private ServerSettings settings;
    private SchedulerTableModel tableModel;
    
    private Gson gson;
    
    public ServerSettingsProvider(File file, ServerGUI gui) {
        this.saveFile = file;
        this.gui = gui;
        this.gson = new Gson();
        this.settings = new ServerSettings();
    }
    
    public void load() throws IOException {
//        if (tableModel == null) {
//            tableModel = (SchedulerTableModel) gui.table.getModel();
//        }
//        if (saveFile.exists()) {
//            BufferedReader reader = new BufferedReader(new FileReader(saveFile));
//            settings = gson.fromJson(reader, ServerSettings.class);
//            reader.close();
//
//            //dmx
//            gui.dmxField.setText(String.valueOf(settings.dmxAddress));
//            gui.universeField.setText(String.valueOf(settings.dmxUniverse));
//            gui.subnetField.setText(String.valueOf(settings.dmxSubnet));
//
//            //artnet interface
//            for (int i = 0; i < gui.addressBox.getItemCount(); i++) {
//                NetEntry entry = gui.addressBox.getItemAt(i);
//                if (entry.getNetworkAddress().getHostAddress().equals(settings.artnetInterface)) {
//                    gui.addressBox.setSelectedIndex(i);
//                    break;
//                }
//            }
//
//            //ltc output mixer
//            for (int i = 0; i < gui.ltcOutputBox.getItemCount(); i++) {
//                MixerEntry entry = gui.ltcOutputBox.getItemAt(i);
//                if (entry.getMixerInfo().getName().equals(settings.ltcAudioOutput)) {
//                    gui.ltcOutputBox.setSelectedIndex(i);
//                    break;
//                }
//            }
//
//            //audio output mixer
//            for (int i = 0; i < gui.audioOutputBox.getItemCount(); i++) {
//                MixerEntry entry = gui.audioOutputBox.getItemAt(i);
//                if (entry.getMixerInfo().getName().equals(settings.musicAudioOutput)) {
//                    gui.audioOutputBox.setSelectedIndex(i);
//                    break;
//                }
//            }
//
//            //framerate
//            for (int i = 0; i < gui.framerateBox.getItemCount(); i++) {
//                Integer entry = gui.framerateBox.getItemAt(i);
//                if (entry == settings.framerate) {
//                    gui.framerateBox.setSelectedIndex(i);
//                    break;
//                }
//            }
//
//            //music list
//            for (Music m : settings.musicTracks) {
//                gui.musicListBox.addItem(m);
//            }
//
//            //OSC
//            gui.oscIPField.setText(settings.oscTargetIP);
//            gui.oscPortField.setText(String.valueOf(settings.oscPort));
//
//            ArrayList<ScheduledEvent> events = new ArrayList<>();
//            events.addAll(Arrays.asList(settings.genericEvents));
//            events.addAll(Arrays.asList(settings.oscEvents));
//            tableModel.setData(events);
//            tableModel.sort();
//
//            //networking settings
//            gui.com1Port = settings.com1Port;
//            gui.com2Port = settings.com2Port;
//            for (int i = 0; i < gui.com2InterfaceBox.getItemCount(); i++) {
//                NetEntry entry = gui.com2InterfaceBox.getItemAt(i);
//                if (entry.getNetworkAddress().getHostAddress().equals(settings.com2Interface)) {
//                    gui.com2InterfaceBox.setSelectedIndex(i);
//                    break;
//                }
//            }
//            gui.packetSize = settings.packetSize;
//
//            //actualize the changes to the threads
//            gui.restartInternals();
//        }
    }
    
    public void save() throws IOException {
//        if (tableModel == null) {
//            tableModel = (SchedulerTableModel) gui.table.getModel();
//        }
//        settings.ltcAudioOutput = ((MixerEntry) gui.ltcOutputBox.getSelectedItem()).getMixerInfo().getName();
//        settings.artnetInterface = ((NetEntry) gui.addressBox.getSelectedItem()).getNetworkAddress().getHostAddress();
//        settings.musicAudioOutput = ((MixerEntry) gui.audioOutputBox.getSelectedItem()).getMixerInfo().getName();
//
//        //music list
//        ArrayList<Music> mList = new ArrayList<>();
//        for (int i = 0; i < gui.musicListBox.getItemCount(); i++) {
//            mList.add(gui.musicListBox.getItemAt(i));
//        }
//        settings.musicTracks = mList.toArray(new Music[mList.size()]);
//
//        //dmx
//        if (!gui.dmxField.getText().equals("")) {
//            settings.dmxAddress = Integer.parseInt(gui.dmxField.getText());
//        } else {
//            settings.dmxAddress = 0;
//        }
//
//        if (!gui.universeField.getText().equals("")) {
//            settings.dmxUniverse = Integer.parseInt(gui.universeField.getText());
//        } else {
//            settings.dmxUniverse = 0;
//        }
//
//        if (!gui.subnetField.getText().equals("")) {
//            settings.dmxSubnet = Integer.parseInt(gui.subnetField.getText());
//        } else {
//            settings.dmxSubnet = 0;
//        }
//
//        //framerate
//        settings.framerate = (int) (gui.framerateBox.getSelectedItem());
//
//        //OSC
//        if (!gui.oscIPField.getText().equals("")) {
//            settings.oscTargetIP = gui.oscIPField.getText();
//        }
//
//        if (!gui.oscPortField.getText().equals("")) {
//            settings.oscPort = Integer.parseInt(gui.oscPortField.getText());
//        }
//
//        ArrayList<ScheduledEvent> genericList = tableModel.getData();
//        ArrayList<ScheduledOSC> oscList = new ArrayList<>();
//
//        for (ScheduledEvent scheduledEvent : genericList) {
//            if (scheduledEvent instanceof ScheduledOSC) {
//                oscList.add((ScheduledOSC) scheduledEvent);
//            }
//        }
//        genericList.removeAll(oscList);
//
//        settings.genericEvents = genericList.toArray(new ScheduledEvent[genericList.size()]);
//        settings.oscEvents = oscList.toArray(new ScheduledOSC[oscList.size()]);
//
//        //networking
//        settings.com1Port = gui.com1Port;
//        settings.com2Port = gui.com2Port;
//        settings.com2Interface = ((NetEntry) gui.com2InterfaceBox.getSelectedItem()).getNetworkAddress().getHostAddress();
//        settings.packetSize = gui.packetSize;
//
//        //saving file
//        if (!saveFile.getParentFile().exists()) {
//            saveFile.getParentFile().mkdirs();
//        }
//        PrintWriter writer = new PrintWriter(new FileOutputStream(saveFile), false);
//        gson.toJson(settings, writer);
//        writer.flush();
//        writer.close();
    }

}
