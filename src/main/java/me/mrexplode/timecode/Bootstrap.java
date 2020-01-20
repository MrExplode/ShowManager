package me.mrexplode.timecode;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

public class Bootstrap {

    public static void main(String[] args) {
        for (String a : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            System.out.println(a);
        }
        //magic starts...
        EventQueue.invokeLater(() ->{
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }

}
