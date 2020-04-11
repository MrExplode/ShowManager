package me.mrexplode.timecode;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import me.mrexplode.timecode.gui.ClientGUI;
import me.mrexplode.timecode.gui.ServerGUI;

public class Bootstrap {

    public static void main(String[] args) {
        ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(args));
        if (arguments.contains("--client")) {
            EventQueue.invokeLater(() -> {
                ClientGUI gui = new ClientGUI();
                gui.setVisible(true);
            });
        } else {
            if (arguments.contains("withRAM") || arguments.contains("debugStart")) {
                EventQueue.invokeLater(() ->{
                    try {
                        ServerGUI gui = new ServerGUI();
                        gui.setVisible(true);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                long freeSysMem = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
                if (freeSysMem < 3000000000L) {
                    JOptionPane.showConfirmDialog(null, "Less than 3 GB free memory! Please free the memory if you wish to use this program", "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
                    return;
                } else {
                    try {
                        //JOptionPane.showConfirmDialog(null, "Starting main app...", "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
                        Runtime.getRuntime().exec(new String [] {"java" ,"-Xmx3G", "-jar", URLDecoder.decode(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6), "UTF-8"), "withRAM"});
                    } catch (IOException e) {
                        JOptionPane.showConfirmDialog(null, "Failed to start the process!\n " + e.getMessage(), "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }
    
}
