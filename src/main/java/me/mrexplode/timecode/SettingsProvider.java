package me.mrexplode.timecode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

import me.mrexplode.timecode.gui.MainGUI;
import me.mrexplode.timecode.gui.MixerEntry;
import me.mrexplode.timecode.gui.NetEntry;

public class SettingsProvider {
    
    private File saveFile;
    private MainGUI gui;
    private Settings settings;
    
    private Gson gson;
    
    public SettingsProvider(File file, MainGUI gui) {
        this.saveFile = file;
        this.gui = gui;
        this.gson = new Gson();
        this.settings = new Settings();
    }
    
    public void load() throws IOException {
        if (saveFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(saveFile));
            settings = gson.fromJson(reader, Settings.class);
            reader.close();
            
            gui.dmxField.setText(String.valueOf(settings.dmxAddress));
            gui.universeField.setText(String.valueOf(settings.dmxUniverse));
            gui.subnetField.setText(String.valueOf(settings.dmxSubnet));
            
            int addrSize = gui.addressBox.getItemCount();
            for (int i = 0; i < addrSize; i++) {
                NetEntry entry = (NetEntry) gui.addressBox.getItemAt(i);
                if (entry.getNetworkAddress().getHostAddress().equals(settings.netInterface)) {
                    gui.addressBox.setSelectedIndex(i);
                    break;
                }
            }
            
            int mixSize = gui.outputBox.getItemCount();
            for (int i = 0; i < mixSize; i++) {
                MixerEntry entry = (MixerEntry) gui.outputBox.getItemAt(i);
                if (entry.getMixerInfo().getName().equals(settings.audioOutput)) {
                    gui.outputBox.setSelectedIndex(i);
                    break;
                }
            }
            
            int frameSize = gui.framerateBox.getItemCount();
            for (int i = 0; i < frameSize; i++) {
                String entry = (String) gui.framerateBox.getItemAt(i);
                if (entry.equals(String.valueOf(settings.framerate))) {
                    gui.framerateBox.setSelectedIndex(i);
                    break;
                }
            }
            
            //actualize the changes to the threads
            gui.restartInternals();
        }
    }
    
    public void save() throws IOException {
        settings.audioOutput = ((MixerEntry) gui.outputBox.getSelectedItem()).getMixerInfo().getName();
        settings.netInterface = ((NetEntry) gui.addressBox.getSelectedItem()).getNetworkAddress().getHostAddress();
        settings.dmxAddress = Integer.valueOf(gui.dmxField.getText());
        settings.dmxUniverse = Integer.valueOf(gui.universeField.getText());
        settings.dmxSubnet = Integer.valueOf(gui.subnetField.getText());
        settings.framerate = Integer.valueOf((String) gui.framerateBox.getSelectedItem());
        
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        PrintWriter writer = new PrintWriter(new FileOutputStream(saveFile), false);
        gson.toJson(settings, writer);
        writer.flush();
        writer.close();
    }

}
