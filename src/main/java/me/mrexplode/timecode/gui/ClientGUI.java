package me.mrexplode.timecode.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import me.mrexplode.timecode.ArraySegment;
import me.mrexplode.timecode.Networking;
import me.mrexplode.timecode.Sequencer;
import me.mrexplode.timecode.Timecode;
import me.mrexplode.timecode.events.EventHandler;
import me.mrexplode.timecode.events.EventType;
import me.mrexplode.timecode.events.MarkerEvent;
import me.mrexplode.timecode.events.MusicEvent;
import me.mrexplode.timecode.events.OscEvent;
import me.mrexplode.timecode.events.TimeChangeEvent;
import me.mrexplode.timecode.events.TimeEvent;
import me.mrexplode.timecode.events.TimeListener;
import me.mrexplode.timecode.fileio.ClientSettingsProvider;


public class ClientGUI extends JFrame implements TimeListener {

    private static final long serialVersionUID = -6578357892309795731L;
    private JPanel contentPane;
    private Animator animator;
    private EventHandler eventHandler;
    private TimeMonitor monitor;
    private Networking net;
    private OSCPortIn oscIn;
    private ArrayList<ArraySegment> segments;
    private Parser parser;
    private HtmlRenderer renderer;
    private ClientSettingsProvider settingsProvider;
    private JPanel animPanel;
    private JPanel timePanel;
    private JLabel timeLabel;
    private JTextPane textPane;
    private JPanel controlPanel;
    private JLabel lblPort;
    public JTextField portField1;
    private JButton btnSet;
    private JButton btnTimeMonitor;
    private JScrollPane scrollPane;
    private JPanel musicPanel;
    private JLabel trackLabel;
    private TrackPanel trackPanel;
    private JLabel lblCom2Port;
    public JTextField portField2;
    public JComboBox<NetEntry> interfaceBox;
    

    /**
     * Create the frame.
     * @throws SocketException 
     */
    public ClientGUI() throws SocketException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setTitle("Timecode Generator - Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImages(ServerGUI.getIcons());
        setBounds(100, 100, 681, 655);
        setMinimumSize(new Dimension(681, 655));
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        monitor = new TimeMonitor();
        monitor.setIconImages(ServerGUI.getIcons());
        eventHandler = new EventHandler();
        segments = new ArrayList<ArraySegment>();
        parser = Parser.builder().build();
        renderer = HtmlRenderer.builder().build();
        
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
        
        
        controlPanel = new JPanel();
        
        scrollPane = new JScrollPane();
        
        musicPanel = new JPanel();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                        .addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE))
                        .addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(musicPanel, GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE))
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
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(musicPanel, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 362, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        trackLabel = new JLabel("Current track:");
        
        trackPanel = new TrackPanel();
        GroupLayout gl_musicPanel = new GroupLayout(musicPanel);
        gl_musicPanel.setHorizontalGroup(
            gl_musicPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(trackLabel, GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                .addComponent(trackPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
        );
        gl_musicPanel.setVerticalGroup(
            gl_musicPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_musicPanel.createSequentialGroup()
                    .addComponent(trackLabel)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(trackPanel, GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
        );
        trackPanel.setLayout(null);
        musicPanel.setLayout(gl_musicPanel);
        
        textPane = new JTextPane();
        textPane.setEditable(false);
        scrollPane.setViewportView(textPane);
        textPane.setContentType("text/html");
        
        lblPort = new JLabel("Communication port 1:");
        
        portField1 = new JTextField();
        portField1.setText("7100");
        portField1.setColumns(10);
        
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
        
        lblCom2Port = new JLabel("Communication port 2:");
        
        portField2 = new JTextField();
        portField2.setText("7007");
        portField2.setColumns(10);
        
        interfaceBox = new JComboBox<NetEntry>();
        GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
        gl_controlPanel.setHorizontalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_controlPanel.createSequentialGroup()
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(interfaceBox, 0, 163, Short.MAX_VALUE)
                        .addGroup(gl_controlPanel.createSequentialGroup()
                            .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblPort)
                                .addGroup(gl_controlPanel.createParallelGroup(Alignment.TRAILING, false)
                                    .addComponent(lblCom2Port, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnTimeMonitor, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGap(2)
                            .addGroup(gl_controlPanel.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_controlPanel.createSequentialGroup()
                                    .addGap(4)
                                    .addComponent(btnSet))
                                .addComponent(portField1, GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                                .addComponent(portField2, GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        gl_controlPanel.setVerticalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblPort)
                        .addComponent(portField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblCom2Port)
                        .addComponent(portField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(interfaceBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnTimeMonitor)
                        .addComponent(btnSet)))
        );
        controlPanel.setLayout(gl_controlPanel);
        contentPane.setLayout(gl_contentPane);
        
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netInterface = interfaces.nextElement();
            if (netInterface.isUp()) {
                InetAddress addr = netInterface.getInetAddresses().nextElement();
                NetEntry netEntry = new NetEntry(addr, netInterface.getName() + " " + addr.getHostAddress());
                interfaceBox.addItem(netEntry);
            }
        }
        interfaceBox.setSelectedIndex(0);
        
        settingsProvider = new ClientSettingsProvider(new File(ServerGUI.PROG_HOME + "\\clientSettings.json"), this);
        
        start();
        
        try {
            Thread.sleep(15);
            settingsProvider.load();
        } catch (IOException e1) {
            System.err.println("An error occured while loading settings: ");
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            System.err.println("FUCK");
            e1.printStackTrace();
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                settingsProvider.save();
            } catch (IOException e1) {
                System.out.println("An error occured while saving settings: ");
                e1.printStackTrace();
            }
        }));
        
    }
    
    public void restart() {
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
            if (eventHandler == null)
                eventHandler = new EventHandler();
            net = new Networking(((NetEntry) interfaceBox.getSelectedItem()).getNetworkAddress(), Integer.valueOf(portField2.getText()));
            eventHandler.startNetworking(Integer.valueOf(portField1.getText()));
            eventHandler.addListener(this);
            oscIn = new OSCPortIn(Integer.valueOf(portField1.getText()));
            oscIn.startListening();
            oscIn.addPacketListener(new OSCPacketListener() {

                @Override
                public void handlePacket(OSCPacketEvent e) {
                    if (e.getPacket() instanceof OSCMessage) {
                        OSCMessage msg = (OSCMessage) e.getPacket();
                        if (msg.getAddress().startsWith("/timecode/events/call/")) {
                            eventHandler.fromNetwork(msg);
                        }
                        
                        if (msg.getAddress().equals("/timecode/network/displayinfo")) {
                            System.out.println("[Client] Recieved display info");
                            String html = null;
                            html = renderer.render(parser.parse((String) msg.getArguments().get(0)));
                            textPane.setText("<html>" + html + "</html>");
                        }
                        
                        if (msg.getAddress().equals("/timecode/musicplayer/progress")) {
                            trackPanel.setValue(Integer.valueOf((String) msg.getArguments().get(0)));
                        }
                    }
                }

                @Override
                public void handleBadData(OSCBadDataEvent e) {
                    //unused event
                }
                
            });
        } catch (NumberFormatException e) {
            displayError("Try to write a valid port next time, dumbass!");
            e.printStackTrace();
        } catch (IOException e) {
            displayError("Failed to start network event handling!\nPlease set settings again!");
            e.printStackTrace();
        }
        
        net.startListening((data) -> {
            float[] wave = new float[data.length - 2];
            System.arraycopy(data, 2, wave, 0, data.length -2);
            segments.add(new ArraySegment((int) data[0], (int) data[1], wave));
            System.out.println("recieved " + data[0] + " of " + data[1] + ", length: " + wave.length);
            if (data[0] == data[1] - 1)
                System.out.println("Last packet");
        });
    }
    
    private void stop() {
        oscIn.stopListening();
        net.shutdown();
        eventHandler.removeAllListeners();
        eventHandler.shutdown();
        eventHandler = null;
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
        Timecode time = e.getTime();
        String timeString = (time.getHour() < 10 ? "0" + time.getHour() : time.getHour()) + " : " + (time.getMin() < 10 ? "0" + time.getMin() : time.getMin()) + " : " + (time.getSec() < 10 ? "0" + time.getSec() : time.getSec()) + " / " + (time.getFrame() < 10 ? "0" + time.getFrame() : time.getFrame());
        timeLabel.setText(timeString);
        if (monitor.isVisible()) {
            monitor.timeDisplay.setText(timeString);
        }
    }

    @Override
    public void onTimeEvent(TimeEvent e) {
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
    public void onMusicEvent(MusicEvent e) {
        if (e.getType() == EventType.MUSIC_LOAD) {
            System.out.println("load event occured");
            trackLabel.setText("Next track: "+ e.getMusic());
            float[] data = Sequencer.merge(segments);
            segments.clear();
            trackPanel.setSamples(data);
            System.out.println("merged data length: " + data.length);
        }
        if (e.getType() == EventType.MUSIC_START) {
            trackLabel.setText("Current track: "+ e.getMusic());
            trackPanel.setColor(TrackPanel.playColor);
        }
        if (e.getType() == EventType.MUSIC_PAUSE || e.getType() == EventType.MUSIC_STOP) {
            //trackLabel.setText("Current track: "+ e.getMusic());
            trackPanel.setColor(TrackPanel.pauseColor);
        }
    }

    @Override
    public void onOscEvent(OscEvent e) {
        //unused event
    }

    @Override
    public void onMarkerEvent(MarkerEvent e) {
        //unused event
    }
}
