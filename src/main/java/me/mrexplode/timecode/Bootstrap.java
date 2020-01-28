package me.mrexplode.timecode;

import java.awt.EventQueue;
import java.net.SocketException;

public class Bootstrap {

    public static void main(String[] args) {
        System.out.println("all: " + Runtime.getRuntime().maxMemory() / 1000000);
        try {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        } catch (SocketException e) {
            e.printStackTrace();
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
