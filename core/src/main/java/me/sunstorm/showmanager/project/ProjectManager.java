package me.sunstorm.showmanager.project;

import lombok.Getter;
import me.sunstorm.showmanager.config.server.ServerConfig;
import me.sunstorm.showmanager.gui.ServerGUI;
import me.sunstorm.showmanager.util.JsonLoader;

import java.io.File;

@Getter
public class ProjectManager {
    private Project project;
    private File projectFile;
    private boolean modified = false;

    public void newProject(String name) {
        File file = new File(ServerGUI.PROG_HOME, name);
        project = JsonLoader.loadOrDefault(file, Project.class);
        modified = false;
        updateGui();
    }

    public void loadProject(File file) {
        project = JsonLoader.loadConfig(file, Project.class);
        projectFile = file;
        modified = false;
        updateGui();
    }

    public void saveProject() {
        JsonLoader.saveConfig(projectFile, project);
        modified = false;
    }

    public void saveProjectAs(File file) {
        JsonLoader.saveConfig(file, project);
        projectFile = file;
        modified = false;
    }

    public void invalidate() {
        modified = true;
    }

    private void updateGui() {
        ServerGUI gui = ServerGUI.getInstance();
        gui.setTitle("ShowManager - Server - " + project.getName());
        ServerConfig config = project.getConfig();
        //artnet
        gui.getArtnetCheckBox().setSelected(config.getArtNetConfig().isEnabled());
        for (int i = 0; i < gui.getArtnetInterfaceBox().getItemCount(); i++) {
            if (gui.getArtnetInterfaceBox().getItemAt(i).getName().equals(config.getArtNetConfig().getArtNetInterface())) {
                gui.getArtnetInterfaceBox().setSelectedIndex(i);
                break;
            }
        }
        //ltc
        gui.getLtcCheckBox().setSelected(config.getLtcConfig().isEnabled());
        for (int i = 0; i < gui.getLtcOutputBox().getItemCount(); i++) {
            if (gui.getLtcOutputBox().getItemAt(i).getName().equals(config.getLtcConfig().getLtcOutput())) {
                gui.getLtcOutputBox().setSelectedIndex(i);
                break;
            }
        }
        //dmx remote
        gui.getRemoteCheckBox().setSelected(config.getDmxRemoteConfig().isEnabled());
        gui.getDmxField().setText(String.valueOf(config.getDmxRemoteConfig().getAddress()));
        gui.getUniverseField().setText(String.valueOf(config.getDmxRemoteConfig().getUniverse()));
        gui.getSubnetField().setText(String.valueOf(config.getDmxRemoteConfig().getSubnet()));
        //osc
        gui.getChckbxOsc().setSelected(config.getOscDispatchConfig().isEnabled());
        gui.getOscPortField().setText(String.valueOf(config.getOscDispatchConfig().getPort()));
        gui.getOscIPField().setText(String.valueOf(config.getOscDispatchConfig().getTarget()));
        //audioplayer
        gui.getMusicCheckBox().setSelected(config.getAudioPlayerConfig().isEnabled());
        for (int i = 0; i < gui.getAudioOutputBox().getItemCount(); i++) {
            if (gui.getAudioOutputBox().getItemAt(i).getName().equals(config.getAudioPlayerConfig().getAudioOutput())) {
                gui.getAudioOutputBox().setSelectedIndex(i);
                break;
            }
        }
        //todo tracklist loading
    }
}
