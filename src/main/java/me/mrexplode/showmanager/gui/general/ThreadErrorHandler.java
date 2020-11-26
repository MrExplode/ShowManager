package me.mrexplode.showmanager.gui.general;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class ThreadErrorHandler implements UncaughtExceptionHandler {
    
    private final JPanel displayPanel;
    private final JButton restartBtn;
    
    public ThreadErrorHandler(JButton restart, JPanel errorPanel, String name) {
        this.displayPanel = errorPanel;
        this.restartBtn = restart;
        this.displayPanel.setBackground(Color.GREEN);
        this.displayPanel.setToolTipText(name + " is running");
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        restartBtn.setEnabled(true);
        displayPanel.setBackground(Color.RED);
        displayPanel.setToolTipText(e.getMessage());
        displayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter pw = new PrintWriter(stringWriter);
                e.printStackTrace(pw);
                JOptionPane.showConfirmDialog(null, stringWriter.toString(), "Error info", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
            }
        });
    }

}
