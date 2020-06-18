package me.mrexplode.timecode;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;

import me.mrexplode.timecode.gui.ClientGUI;
import me.mrexplode.timecode.gui.ServerGUI;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;

public class Bootstrap {

    public static void main(String[] args) {
        String ver = ManagementFactory.getRuntimeMXBean().getVmVersion();
        int versionNumber = Integer.valueOf(ver.substring(0, 2));
        if (versionNumber < 11) {
            showVersionError(ver);
            return;
        }
        ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(args));
        if (arguments.contains("--client")) {
            EventQueue.invokeLater(() -> {
                try {
                    ClientGUI gui = new ClientGUI();
                    gui.setVisible(true);
                } catch (SocketException e) {
                    showError(e);
                    e.printStackTrace();
                }
            });
        } else {
            if (arguments.contains("--withRAM") || arguments.contains("--debugStart")) {
                EventQueue.invokeLater(() ->{
                    try {
                        ServerGUI gui = new ServerGUI();
                        gui.setVisible(true);
                    } catch (SocketException e) {
                        showError(e);
                        e.printStackTrace();
                    }
                });
            } else {
                SystemInfo si = new SystemInfo();
                HardwareAbstractionLayer h = si.getHardware();
                GlobalMemory globalMem = h.getMemory();
                long freeSysMem = globalMem.getAvailable();
                if (freeSysMem < 3000000000L) {
                    JOptionPane.showConfirmDialog(null, "Less than 3 GB free memory! Please free the memory if you wish to use this program", "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
                    return;
                } else {
                    try {
                        Runtime.getRuntime().exec(new String [] {"java" ,"-Xmx3G", "-jar", URLDecoder.decode(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6), "UTF-8"), "--withRAM"});
                    } catch (IOException e) {
                        JOptionPane.showConfirmDialog(null, "Failed to start the process!\n " + e.getMessage(), "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }
    
    private static void showError(Exception e) {
        JOptionPane.showMessageDialog(null, "Failed to initialize:\n" + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE, null);
    }
    
    private static void showVersionError(String currentVer) {
        JLabel label = new JLabel();
        Font font = label.getFont();
        
        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");
        
        JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">Required Java version: JDK 11<br>Current: " + currentVer + "<br><a href=\"https://adoptopenjdk.net/index.html?variant=openjdk14&jvmVariant=hotspot\">Recommended download</a>");
        ep.setEditable(false);
        ep.setBackground(label.getBackground());
        ep.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JOptionPane.showConfirmDialog(null, ep, "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
    }
    
}
