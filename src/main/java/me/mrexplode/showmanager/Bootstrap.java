package me.mrexplode.showmanager;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.mrexplode.showmanager.gui.ServerGUI;
import oshi.SystemInfo;

@Slf4j
public class Bootstrap {

    public static void main(String... args) {
        String ver = ManagementFactory.getRuntimeMXBean().getVmVersion();
        if (Integer.parseInt(ver.substring(0, 2)) < 11) {
            log.error("Outdated Java JVM: " + ver);
            showVersionError(ver);
            return;
        }

        OptionParser parser = new OptionParser();
        parser.accepts("debug");
        parser.accepts("withRAM");
        parser.accepts("client");
        OptionSpec<String> ignored = parser.nonOptions();
        OptionSet optionSet = parser.parse(args);
        if (!optionSet.valuesOf(ignored).isEmpty())
            System.out.println("Ignored arguments: " + Arrays.toString(optionSet.valuesOf(ignored).toArray()));

        if (optionSet.has("client")) {
            //client
            return;
        }
        if (optionSet.has("debug") || optionSet.has("withRAM")) {
            EventQueue.invokeLater(() -> {
                try {
                    ServerGUI gui = new ServerGUI();
                    gui.setVisible(true);
                } catch (Exception e) {
                    showError(e);
                }
            });
        } else {
            val memory = new SystemInfo().getHardware().getMemory();
            if (memory.getAvailable() < 3L * 1000 * 1000 * 1000) {
                log.warn("Less than 3 GB memory!");
            }
            try {
                Runtime.getRuntime().exec(new String [] {"java" ,"-Xmx3G", "-jar", URLDecoder.decode(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6), StandardCharsets.UTF_8), "--withRAM"});
            } catch (IOException e) {
                log.error("Failed to start with increased heap", e);
            }
        }
    }

    private static void showError(Exception e) {
        JOptionPane.showMessageDialog(null, "Failed to initialize:\n" + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE, null);
    }
    
    private static void showVersionError(String currentVer) {
        JLabel label = new JLabel();
        Font font = label.getFont();

        String style = "font-family:" + font.getFamily() + ";" + "font-weight:" + (font.isBold() ? "bold" : "normal") + ";" +
                "font-size:" + font.getSize() + "pt;";
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
