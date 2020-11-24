package me.mrexplode.timecode;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.*;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.val;
import me.mrexplode.timecode.gui.ServerGUI;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

@Log
public class Bootstrap {

    public static void main(String... args) {
        initLogger();
        String ver = ManagementFactory.getRuntimeMXBean().getVmVersion();
        if (Integer.parseInt(ver.substring(0, 2)) < 11) {
            log.severe("Outdated Java JVM: " + ver);
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
                log.warning("Less than 3 GB memory!");
            }
            try {
                Runtime.getRuntime().exec(new String [] {"java" ,"-Xmx3G", "-jar", URLDecoder.decode(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6), StandardCharsets.UTF_8), "--withRAM"});
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed to start with increased heap", e);
            }
        }
    }

    @SneakyThrows
    private static void initLogger() {
        Logger logger = Logger.getGlobal();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                Date date = new Date(record.getMillis());
                String threadName = ManagementFactory.getThreadMXBean().getThreadInfo(record.getThreadID()).getThreadName();
                StringBuilder builder = new StringBuilder("[").append(format.format(date)).append("] [").append(threadName).append("] [").append(record.getLevel().getName()).append("] ").append(record.getMessage()).append("\n");
                if (record.getThrown() != null)
                    builder.append("\n").append(record.getThrown().toString());
                return builder.toString();
            }
        };
        logger.setUseParentHandlers(false);

        ConsoleHandler consoleManager = new ConsoleHandler() {
            @Override
            protected synchronized void setOutputStream(OutputStream out) throws SecurityException {
                super.setOutputStream(System.out);
            }
        };

        consoleManager.setFormatter(formatter);
        logger.addHandler(consoleManager);

        //file logging
        File logFolder = new File("logs/");
        if (!logFolder.exists())
            logFolder.mkdirs();

        FileHandler fileHandler = new FileHandler(logFolder.getAbsolutePath() + "/log-%g-%u.log", 1024 * 1024 * 1024, 100);
        fileHandler.setFormatter(formatter);
        fileHandler.setLevel(Level.INFO);
        logger.addHandler(fileHandler);
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
