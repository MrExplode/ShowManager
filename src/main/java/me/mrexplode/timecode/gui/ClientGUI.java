package me.mrexplode.timecode.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCPacketListener;
import com.illposed.osc.transport.udp.OSCPortIn;

import me.mrexplode.timecode.Timecode;
import me.mrexplode.timecode.events.EventHandler;
import me.mrexplode.timecode.events.EventType;
import me.mrexplode.timecode.events.MarkerEvent;
import me.mrexplode.timecode.events.OscEvent;
import me.mrexplode.timecode.events.TimeChangeEvent;
import me.mrexplode.timecode.events.TimeEvent;
import me.mrexplode.timecode.events.TimeListener;


public class ClientGUI extends JFrame implements TimeListener {

    private static final long serialVersionUID = -6578357892309795731L;
    private JPanel contentPane;
    private Animator animator;
    private EventHandler eventHandler;
    private TimeMonitor monitor;
    private OSCPortIn oscIn;
    private JPanel animPanel;
    private JPanel timePanel;
    private JLabel timeLabel;
    private JTextPane textPane;
    private JPanel controlPanel;
    private JLabel lblPort;
    private JTextField portField;
    private JButton btnSet;
    private JButton btnTimeMonitor;
    

    /**
     * Create the frame.
     */
    public ClientGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setTitle("Timecode Generator - Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImages(ServerGUI.getIcons());
        setBounds(100, 100, 681, 429);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        monitor = new TimeMonitor();
        monitor.setIconImages(ServerGUI.getIcons());
        eventHandler = new EventHandler();
        
        animPanel = new JPanel();
        animPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        animPanel.setBackground(SystemColor.menu);
        
        animator = new Animator(animPanel);
        
        timePanel = new JPanel();
        timePanel.setBorder(new TitledBorder(null, "Current time", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        timeLabel = new JLabel("00 : 00 : 00 / 00");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setFont(new Font("Tahoma", Font.BOLD, 50));
        GroupLayout gl_timePanel = new GroupLayout(timePanel);
        gl_timePanel.setHorizontalGroup(
            gl_timePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_timePanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(timeLabel, GroupLayout.PREFERRED_SIZE, 426, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(61, Short.MAX_VALUE))
        );
        gl_timePanel.setVerticalGroup(
            gl_timePanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, gl_timePanel.createSequentialGroup()
                    .addComponent(timeLabel, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(28, Short.MAX_VALUE))
        );
        timePanel.setLayout(gl_timePanel);
        GroupLayout gl_animPanel = new GroupLayout(animPanel);
        gl_animPanel.setHorizontalGroup(
            gl_animPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(timePanel, GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
        );
        gl_animPanel.setVerticalGroup(
            gl_animPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(timePanel, GroupLayout.PREFERRED_SIZE, 121, Short.MAX_VALUE)
        );
        animPanel.setLayout(gl_animPanel);
        
        textPane = new JTextPane();
        
        controlPanel = new JPanel();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGap(10)
                            .addComponent(textPane, GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addComponent(animPanel, GroupLayout.PREFERRED_SIZE, 472, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addComponent(animPanel, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGap(11)
                            .addComponent(controlPanel, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(textPane, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                    .addContainerGap())
        );
        
        lblPort = new JLabel("Communication port");
        
        portField = new JTextField();
        portField.setText("7100");
        portField.setColumns(10);
        
        btnSet = new JButton("Set");
        btnSet.addActionListener(e -> {
            restart();
        });
        btnSet.setToolTipText("Set the port");
        
        btnTimeMonitor = new JButton("Time Monitor");
        btnTimeMonitor.addActionListener(e -> {
            monitor.setVisible(!monitor.isVisible());
        });
        btnTimeMonitor.setToolTipText("Toggles the Time Monitor visibility");
        GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
        gl_controlPanel.setHorizontalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_controlPanel.createSequentialGroup()
                            .addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(portField, GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
                        .addComponent(btnSet)
                        .addComponent(btnTimeMonitor))
                    .addContainerGap())
        );
        gl_controlPanel.setVerticalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblPort)
                        .addComponent(portField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnSet)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnTimeMonitor)
                    .addContainerGap(26, Short.MAX_VALUE))
        );
        controlPanel.setLayout(gl_controlPanel);
        contentPane.setLayout(gl_contentPane);
        
        start();
        
    }
    
    private void restart() {
        stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        start();
    }
    
    private void start() {
        try {
            eventHandler.startNetworking(Integer.valueOf(portField.getText()));
            eventHandler.addListener(this);
            oscIn = new OSCPortIn(Integer.valueOf(portField.getText()));
            oscIn.startListening();
            oscIn.addPacketListener(new OSCPacketListener() {

                @Override
                public void handlePacket(OSCPacketEvent e) {
                    if (e.getPacket() instanceof OSCMessage) {
                        OSCMessage msg = (OSCMessage) e.getPacket();
                        if (msg.getAddress().startsWith("/timecode/events/call/")) {
                            eventHandler.fromNetwork(msg);
                        }
                    }
                }

                @Override
                public void handleBadData(OSCBadDataEvent e) {
                }
                
            });
        } catch (NumberFormatException e) {
            displayError("Try to write a valid port next time!");
            e.printStackTrace();
        } catch (IOException e) {
            displayError("Failed to start network event handling!\nPlease set settings again!");
            e.printStackTrace();
        }
    }
    
    private void stop() {
        oscIn.stopListening();
    }
    
    private static void displayError(String errorMessage) {
        Thread t = new Thread(() -> {
            JOptionPane.showConfirmDialog(null, errorMessage, "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
        });
        t.setName("Error display thread");
        t.start();
    }

    @Override
    public void onTimeChangeEvent(TimeChangeEvent e) {
        System.out.println("timechange");
        Timecode time = e.getTime();
        String timeString = (time.getHour() < 10 ? "0" + time.getHour() : time.getHour()) + " : " + (time.getMin() < 10 ? "0" + time.getMin() : time.getMin()) + " : " + (time.getSec() < 10 ? "0" + time.getSec() : time.getSec()) + " / " + (time.getFrame() < 10 ? "0" + time.getFrame() : time.getFrame());
        timeLabel.setText(timeString);
        if (monitor.isVisible()) {
            monitor.timeDisplay.setText(timeString);
        }
    }

    @Override
    public void onTimeEvent(TimeEvent e) {
        System.out.println("timeevent");
        if (e.getType() == EventType.TC_PAUSE || e.getType() == EventType.TC_STOP) {
            animator.stopFlash();
            if (monitor.isVisible())
                monitor.getAnimator().stopFlash();
        }
        if (e.getType() == EventType.TC_START) {
            animator.startFlash(Color.RED);
            if (monitor.isVisible())
                monitor.getAnimator().startFlash(Color.RED);
        }
    }

    @Override
    public void onOscEvent(OscEvent e) {
    }

    @Override
    public void onMarkerEvent(MarkerEvent e) {
    }
}
