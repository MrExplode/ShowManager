package me.mrexplode.timecode;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.SocketException;
import java.net.URLDecoder;

import javax.swing.JOptionPane;

import me.mrexplode.timecode.gui.MainGUI;

public class Bootstrap {

    @SuppressWarnings("restriction")
	public static void main(String[] args) {
    	if (args.length == 0) {
    		//long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        	long freeSysMem = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
        	if (freeSysMem < 3000000000L) {
        		JOptionPane.showConfirmDialog(null, "Less than 3 GB free memory! Please free the memory if you wish to use this program", "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
        		return;
        	} else {
        		try {
        			JOptionPane.showConfirmDialog(null, "Starting main app...", "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
					Runtime.getRuntime().exec(new String [] {"java" ,"-Xmx3G", "-jar", URLDecoder.decode(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6), "UTF-8"), "withRAM"});
				} catch (IOException e) {
					JOptionPane.showConfirmDialog(null, "Failed to start the process!\n " + e.getMessage(), "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
					e.printStackTrace();
					return;
				}
        	}
    	} else {
    		if (args[0].equals("withRAM") || args[0].equals("debugStart")) {
                //magic starts...
                  EventQueue.invokeLater(() ->{
                      try {
                          MainGUI gui = new MainGUI();
                          gui.setVisible(true);
                      } catch (SocketException e) {
                          e.printStackTrace();
                      }
                  });
              }
    	}
    }

}
