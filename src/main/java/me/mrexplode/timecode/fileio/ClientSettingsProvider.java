package me.mrexplode.timecode.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

import me.mrexplode.timecode.gui.ClientGUI;
import me.mrexplode.timecode.gui.NetEntry;

public class ClientSettingsProvider {
    
    private File saveFile;
    private ClientSettings settings;
    private ClientGUI gui;
    
    private Gson gson;
    
    public ClientSettingsProvider(File saveFile, ClientGUI gui) {
        this.saveFile = saveFile;
        this.gui = gui;
        this.settings = new ClientSettings();
        this.gson = new Gson();
    }
    
    public void load() throws IOException {
        if (saveFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(saveFile));
            settings = gson.fromJson(reader, ClientSettings.class);
            reader.close();
            
            gui.portField1.setText(Integer.toString(settings.com1Port));
            gui.portField2.setText(Integer.toString(settings.com2Port));
            for (int i = 0; i < gui.interfaceBox.getItemCount(); i++) {
                if (gui.interfaceBox.getItemAt(i).getNetworkAddress().getHostAddress().equals(settings.com2Interface)) {
                    gui.interfaceBox.setSelectedIndex(i);
                    break;
                }
            }
            
            gui.restart();
        }
    }
    
    public void save() throws IOException {
        try {
            settings.com1Port = Integer.valueOf(gui.portField1.getText());
        } catch (NumberFormatException e) {
            settings.com1Port = 7100;
        }
        try {
            settings.com2Port = Integer.valueOf(gui.portField2.getText());
        } catch (NumberFormatException e) {
            settings.com2Port = 7007;
        }
        
        settings.com2Interface = ((NetEntry) gui.interfaceBox.getSelectedItem()).getNetworkAddress().getHostAddress();
        
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        PrintWriter writer = new PrintWriter(new FileOutputStream(saveFile), false);
        gson.toJson(settings, writer);
        writer.flush();
        writer.close();
    }
}
