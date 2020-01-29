package me.mrexplode.timecode;

import java.lang.management.ManagementFactory;
import java.net.SocketException;

import javax.swing.JOptionPane;

public class Bootstrap {

    @SuppressWarnings("restriction")
	public static void main(String[] args) {
    	long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    	long freeSysMem = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
    	if (freeSysMem < 1000000000) {
    		JOptionPane.showConfirmDialog(null, "Less than 1 GB free memory! Please free the memory if you wish to use this program", "Timecode Generator", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null);
    	}
        /*
        if (args.length == 0) {
            System.out.println("all: " + Runtime.getRuntime().maxMemory() / 1000000);
            System.out.println("free: " + Runtime.getRuntime().freeMemory() / 1000000);
        } else {
            if (args[0].equals("89e87d777f3364470b35e9644848738c")) {
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
        }*/
    }

}
