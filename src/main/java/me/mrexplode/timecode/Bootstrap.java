package me.mrexplode.timecode;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.SocketException;
import java.net.URLDecoder;
import java.time.LocalTime;
import java.util.Date;

import javax.swing.JOptionPane;

import me.mrexplode.timecode.gui.MainGUI;

public class Bootstrap {

    public static void main(String[] args) {/*
        boolean running = true;
        long elapsed = 0;
        int[] conv = new int[4];
        long start = System.currentTimeMillis();
        long time = start;
        
        long timer = System.currentTimeMillis();
        while (running) {
            long current = System.currentTimeMillis();
            if (current >= time + (1000 / 25)) {
                time = current;
                
                elapsed = time - start;
                long frameNumbers = elapsed / (1000 / 25);
                conv = decode(frameNumbers, 1);
                System.out.println(conv[0] + "h " + conv[1] + "m " + conv[2] + "s " + conv[3] + "   ==   " + new Date(current));
                
                if (current >= start + 60 * 1000) {
                    System.out.println("calculated: " + conv[0] + "h " + conv[1] + "m " + conv[2] + "s " + conv[3]);
                    System.out.println("elapsed: " + elapsed);
                    System.out.println("calculated elapsed: " + (System.currentTimeMillis() - timer));
                    break;
                }
            }
        }*/
        
    	if (args.length == 0) {
    		//long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
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
    
    /**
     * Convert the separate values into one long value, for easy increment/decrement
     * 
     * @param hour number of hours
     * @param min number of minutes
     * @param sec number of seconds
     * @param frame number of frames
     * @param frameType the type of the timecode
     * @return the encoded time value
     */
    public static long encode(int hour, int min, int sec, int frame, int frameType) {
        int framerate = 30;
        switch (frameType) {
            case 0:
                //film
                framerate = 24;
                break;
            case 1:
                //ebu
                framerate = 25;
                break;
            case 2:
                //df
                throw new IllegalArgumentException("DF type not implemented! Do you wanna implement it yourself?");
            case 3:
                //smtpe
                framerate = 30;
                break;
            default:
                framerate = 25;
                break;
        }
        
        int hour_fr = hour * 60 * 60 * framerate;
        int min_fr = min * 60 * framerate;
        int sec_fr = sec * framerate;
        
        return hour_fr + min_fr + sec_fr + frame;
    }
    
    /**
     * Decodes the encoded timecode value.<br>
     * Elements of the returning int array:<br>
     * 0: hour<br>
     * 1: minute<br>
     * 2: second<br>
     * 3: frame<br>
     * 
     * @param frames the encoded time data
     * @param frameType the type of the timecode
     * @return
     */
    public static int[] decode(long frames, int frameType) {
        int framerate = 30;
        switch (frameType) {
            case 0:
                framerate = 24;
                break;
            case 1:
                framerate = 25;
                break;
            case 2:
                throw new IllegalArgumentException("DF type not implemented! Do you wanna implement it yourself?");
            case 3:
                framerate = 30;
                break;
            default:
                framerate = 25;
                break;
        }
        
        int[] dec = new int[4];
        
        int hour = ((int) frames / 60 / 60 / framerate);
        frames = frames - (hour * 60 * 60 * framerate);
        dec[0] = hour;
        
        int min = ((int) frames / 60 / framerate);
        frames = frames - (min * 60 * framerate);
        dec[1] = min;
        
        int sec = ((int) frames / framerate);
        frames = frames - (sec * framerate);
        dec[2] = sec;
        
        int frame = (int) frames;
        dec[3] = frame;
        
        return dec;
    }

}
