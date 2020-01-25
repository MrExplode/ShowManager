package me.mrexplode.timecode;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

public class Bootstrap {

    public static void main(String[] args) {
        //magic starts...
        EventQueue.invokeLater(() ->{
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }

}
