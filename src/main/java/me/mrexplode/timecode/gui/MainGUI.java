package me.mrexplode.timecode.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import javafx.application.Platform;
import me.mrexplode.timecode.DataGrabber;
import me.mrexplode.timecode.SettingsProvider;
import me.mrexplode.timecode.WorkerThread;


public class MainGUI extends JFrame {
    
    /**
     * THe path of the base directory for the program
     */
    public final String PROG_HOME = System.getProperty("user.home") + "\\AppData\\Roaming\\TimecodeGenerator";
    
    private WorkerThread workThread;
    private DataGrabber dataGrabber;
    private HashMap<Integer, String> ltcSources = new HashMap<Integer, String>();
    private SettingsProvider settingsProvider;

    private JPanel contentPane;
    private JPanel timePanel;
    public JLabel timeDisplay;
    public JLabel remoteControl;
    private JPanel controlPanel;
    public JButton btnPlay;
    public JButton btnPause;
    public JButton btnStop;
    private JPanel settingsPanel;
    private JCheckBox artnetCheckBox;
    private JCheckBox ltcCheckBox;
    private JCheckBox remoteCheckBox;
    public JButton btnSetTime;
    public JTextField dmxField;
    private JLabel lblDmxAddress;
    public JTextField universeField;
    private JLabel lblUniverse;
    public JTextField subnetField;
    private JLabel lblSubnet;
    public JComboBox framerateBox;
    private JLabel lblFramerate;
    private JButton btnSetDmx;
    public JComboBox ltcOutputBox;
    private JPanel panel;
    private JTextField hourField;
    private JTextField minField;
    private JTextField secField;
    private JTextField frameField;
    private JPanel dmxSettingsPanel;
    public JComboBox addressBox;
    public JButton btnRestart;
    private JPanel playerPanel;
    private JCheckBox musicCheckBox;
    private JComboBox audioOutputBox;
    private JLabel lblTrack;
    private JComboBox comboBox;
    private JPanel jfxPanel;

    /**
     * Create the frame.
     * @throws SocketException 
     */
    @SuppressWarnings({ "resource" , "rawtypes", "unchecked" })
    public MainGUI() throws SocketException {
        ltcSources.put(24, "ltc/LTC_00_00_00_00__90mins_24.wav");
        ltcSources.put(25, "ltc/LTC_00_00_00_00__91mins_25.wav");
        ltcSources.put(30, "ltc/LTC_00_00_00_00__90mins_30.wav");
        
        this.settingsProvider = new SettingsProvider(new File(PROG_HOME + "\\settings.json"), this);
        
        setTitle("Timecode Generator");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImages(getIcons());
        setBounds(100, 100, 597, 469);
        setMinimumSize(new Dimension(597, 413));
        contentPane = new JPanel();
        contentPane.setBorder(null);
        setContentPane(contentPane);
        
        timePanel = new JPanel();
        timePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Current time", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        
        remoteControl = new JLabel("Remote control: Waiting");
        remoteControl.setHorizontalAlignment(SwingConstants.CENTER);
        remoteControl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        remoteControl.setForeground(Color.RED);
        
        controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder(null, "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        settingsPanel = new JPanel();
        
        btnSetTime = new JButton("Set time");
        btnSetTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("implement time set");
            }
        });
        
        panel = new JPanel();
        
        dmxSettingsPanel = new JPanel();
        
        playerPanel = new JPanel();
        playerPanel.setToolTipText("All sound files must be in .wav format.");
        playerPanel.setBorder(new TitledBorder(null, "Music player", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addComponent(playerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(10))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_contentPane.createSequentialGroup()
                                    .addComponent(timePanel, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                    .addGap(3))
                                .addGroup(gl_contentPane.createSequentialGroup()
                                    .addGap(81)
                                    .addComponent(btnSetTime, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(91))
                                .addGroup(gl_contentPane.createSequentialGroup()
                                    .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                    .addGap(3))
                                .addGroup(gl_contentPane.createSequentialGroup()
                                    .addComponent(remoteControl, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                                    .addGap(0))
                                .addGroup(gl_contentPane.createSequentialGroup()
                                    .addComponent(panel, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                                    .addGap(5)))
                            .addGap(1)
                            .addComponent(settingsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(dmxSettingsPanel, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))))
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(dmxSettingsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(settingsPanel, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addComponent(timePanel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(panel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnSetTime)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(controlPanel, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                            .addGap(18)
                            .addComponent(remoteControl)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(playerPanel, GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .addContainerGap())
        );
        
        lblTrack = new JLabel("Current track");
        
        comboBox = new JComboBox();
        
        jfxPanel = new JPanel();
        GroupLayout gl_playerPanel = new GroupLayout(playerPanel);
        gl_playerPanel.setHorizontalGroup(
            gl_playerPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_playerPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(jfxPanel, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTrack, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        gl_playerPanel.setVerticalGroup(
            gl_playerPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_playerPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblTrack)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    .addComponent(jfxPanel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        playerPanel.setLayout(gl_playerPanel);
        
        remoteCheckBox = new JCheckBox("DMX remote control");
        remoteCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = remoteCheckBox.isSelected();
                System.out.println((selected ? "Enabled" : "Disabled") + " remote control");
                workThread.setRemoteControl(selected);
                dmxField.setEnabled(selected);
                lblDmxAddress.setEnabled(selected);
                universeField.setEnabled(selected);
                lblUniverse.setEnabled(selected);
                subnetField.setEnabled(selected);
                lblSubnet.setEnabled(selected);
                btnSetDmx.setEnabled(selected);
            }
        });
        remoteCheckBox.setToolTipText("Toggles the remote control via DMX");
        
        dmxField = new JTextField();
        dmxField.setEnabled(false);
        dmxField.setColumns(10);
        
        lblDmxAddress = new JLabel("DMX address");
        lblDmxAddress.setEnabled(false);
        
        universeField = new JTextField();
        universeField.setEnabled(false);
        universeField.setColumns(10);
        
        lblUniverse = new JLabel("Universe");
        lblUniverse.setEnabled(false);
        
        subnetField = new JTextField();
        subnetField.setEnabled(false);
        subnetField.setColumns(10);
        
        lblSubnet = new JLabel("Subnet");
        lblSubnet.setEnabled(false);
        
        btnSetDmx = new JButton("Set dmx");
        btnSetDmx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workThread.setDmxAddress(Integer.valueOf(dmxField.getText()));
                workThread.setUniverse(Integer.valueOf(universeField.getText()));
                workThread.setSubnet(Integer.valueOf(subnetField.getText()));
            }
        });
        btnSetDmx.setEnabled(false);
        GroupLayout gl_dmxSettingsPanel = new GroupLayout(dmxSettingsPanel);
        gl_dmxSettingsPanel.setHorizontalGroup(
            gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(remoteCheckBox, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                            .addGap(21)
                            .addComponent(dmxField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                            .addGap(4)
                            .addComponent(lblDmxAddress))
                        .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                            .addGap(21)
                            .addComponent(universeField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                            .addGap(4)
                            .addComponent(lblUniverse, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                            .addGap(21)
                            .addComponent(subnetField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                            .addGap(4)
                            .addComponent(lblSubnet, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                            .addGap(21)
                            .addComponent(btnSetDmx, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_dmxSettingsPanel.setVerticalGroup(
            gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGap(1)
                    .addComponent(remoteCheckBox)
                    .addGap(2)
                    .addGroup(gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(dmxField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                            .addGap(3)
                            .addComponent(lblDmxAddress)))
                    .addGap(6)
                    .addGroup(gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(universeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                            .addGap(3)
                            .addComponent(lblUniverse)))
                    .addGap(6)
                    .addGroup(gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(subnetField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                            .addGap(3)
                            .addComponent(lblSubnet)))
                    .addGap(6)
                    .addComponent(btnSetDmx)
                    .addContainerGap(116, Short.MAX_VALUE))
        );
        dmxSettingsPanel.setLayout(gl_dmxSettingsPanel);
        
        hourField = new JTextField();
        hourField.setColumns(10);
        
        minField = new JTextField();
        minField.setColumns(10);
        
        secField = new JTextField();
        secField.setColumns(10);
        
        frameField = new JTextField();
        frameField.setColumns(10);
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addGap(32)
                    .addComponent(hourField, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addGap(26)
                    .addComponent(minField, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addGap(26)
                    .addComponent(secField, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addGap(26)
                    .addComponent(frameField, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addGap(24))
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(hourField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(minField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(secField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(frameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel.setLayout(gl_panel);
        
        artnetCheckBox = new JCheckBox("ArtNet timecode");
        artnetCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println((artnetCheckBox.isSelected() ? "Enabled" : "Disabled") + " ArtNet timecode");
                workThread.setBroadcast(artnetCheckBox.isSelected());
            }
        });
        artnetCheckBox.setToolTipText("Toggles the ArtNet timecode broadcasting");
        
        ltcCheckBox = new JCheckBox("LTC timecode");
        ltcCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = ltcCheckBox.isSelected();
                workThread.setLTC(selected);
            }
        });
        ltcCheckBox.setToolTipText("Toggles the LTC output");
        
        framerateBox = new JComboBox();
        framerateBox.setToolTipText("Timecode framerate");
        framerateBox.setModel(new DefaultComboBoxModel(new String[] {"24", "25", "30"}));
        framerateBox.setSelectedIndex(1);
        
        lblFramerate = new JLabel("Framerate");
        
        ltcOutputBox = new JComboBox();
        ltcOutputBox.setToolTipText("Select the output for LTC. Carefully! LTC should never go out on speakers!");
        //INIT list available outputs
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            MixerEntry entry = new MixerEntry(info.getName(), info);
            ltcOutputBox.addItem(entry);
        }
        ltcOutputBox.setSelectedIndex(0);
        
        addressBox = new JComboBox();
        addressBox.setToolTipText("Network to broadcast ArtNet Timecode.");
        //INIT list available network interfaces
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netInterface = interfaces.nextElement();
            if (netInterface.isUp()) {
                InetAddress addr = netInterface.getInetAddresses().nextElement();
                addressBox.addItem(new NetEntry(addr, netInterface.getName() + " " + addr.getHostAddress()));
            }
        }
        addressBox.setSelectedIndex(0);
        
        btnRestart = new JButton("Restart internals");
        btnRestart.setToolTipText("In order to your changes take effect, you have to restart the internal implementation.");
        btnRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //just in case
                workThread.stop();
                restartInternals();
            }
        });
        
        musicCheckBox = new JCheckBox("Audio player");
        musicCheckBox.setToolTipText("Toggles audio player output");
        
        audioOutputBox = new JComboBox();
        audioOutputBox.setToolTipText("Select the output for the audio player.");
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            MixerEntry entry = new MixerEntry(info.getName(), info);
            audioOutputBox.addItem(entry);
        }
        
        GroupLayout gl_settingsPanel = new GroupLayout(settingsPanel);
        gl_settingsPanel.setHorizontalGroup(
            gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_settingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_settingsPanel.createSequentialGroup()
                            .addComponent(btnRestart, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(Alignment.TRAILING, gl_settingsPanel.createSequentialGroup()
                            .addComponent(musicCheckBox, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(Alignment.TRAILING, gl_settingsPanel.createSequentialGroup()
                            .addComponent(artnetCheckBox, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                            .addGap(18))
                        .addGroup(Alignment.TRAILING, gl_settingsPanel.createSequentialGroup()
                            .addGroup(gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(addressBox, 0, 151, Short.MAX_VALUE)
                                .addComponent(ltcOutputBox, 0, 151, Short.MAX_VALUE)
                                .addGroup(gl_settingsPanel.createSequentialGroup()
                                    .addComponent(ltcCheckBox, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                    .addGap(28)))
                            .addGap(0))
                        .addGroup(Alignment.TRAILING, gl_settingsPanel.createSequentialGroup()
                            .addComponent(framerateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblFramerate, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .addGap(24))
                        .addComponent(audioOutputBox, Alignment.TRAILING, 0, 151, Short.MAX_VALUE)))
        );
        gl_settingsPanel.setVerticalGroup(
            gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_settingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(artnetCheckBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(addressBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(ltcCheckBox)
                    .addGap(3)
                    .addComponent(ltcOutputBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(musicCheckBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(audioOutputBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(13)
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(framerateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFramerate))
                    .addPreferredGap(ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                    .addComponent(btnRestart)
                    .addContainerGap())
        );
        settingsPanel.setLayout(gl_settingsPanel);
        
        btnPlay = new JButton("Play");
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workThread.play();
            }
        });
        
        btnPause = new JButton("Pause");
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workThread.pause();
            }
        });
        
        btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workThread.stop();
            }
        });
        GroupLayout gl_controlPanel = new GroupLayout(controlPanel);
        gl_controlPanel.setHorizontalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addGap(6)
                    .addComponent(btnPlay, GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(btnPause, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnStop, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addGap(11))
        );
        gl_controlPanel.setVerticalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnPlay, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPause)
                        .addComponent(btnStop))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        controlPanel.setLayout(gl_controlPanel);
        
        timeDisplay = new JLabel("00 : 00 : 00 / 00");
        timeDisplay.setFont(new Font("Tahoma", Font.BOLD, 24));
        timeDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        GroupLayout gl_timePanel = new GroupLayout(timePanel);
        gl_timePanel.setHorizontalGroup(
            gl_timePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_timePanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(timeDisplay, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_timePanel.setVerticalGroup(
            gl_timePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_timePanel.createSequentialGroup()
                    .addGap(7)
                    .addComponent(timeDisplay, GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE)
                    .addContainerGap())
        );
        timePanel.setLayout(gl_timePanel);
        contentPane.setLayout(gl_contentPane);
        
        //setting up threading
        this.dataGrabber = new DataGrabber(this);
        Thread dThreadInstance = new Thread(dataGrabber);
        
        AudioInputStream stream = null;
        try {
        	InputStream bufferedStream = new BufferedInputStream(this.getClass().getResourceAsStream("/" + ltcSources.get(Integer.valueOf((String) framerateBox.getSelectedItem()))));
            stream = AudioSystem.getAudioInputStream(bufferedStream);
        } catch (UnsupportedAudioFileException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Mixer mixer = AudioSystem.getMixer(((MixerEntry) ltcOutputBox.getSelectedItem()).getMixerInfo());
        InetAddress address = ((NetEntry) addressBox.getSelectedItem()).getNetworkAddress();
        this.workThread = new WorkerThread(stream, mixer, address, dThreadInstance);
        this.workThread.setFramerate(Integer.valueOf((String) framerateBox.getSelectedItem()));
        Thread wThreadInstance = new Thread(workThread);
        
        this.dataGrabber.setWorkerInstance(workThread);
        
        wThreadInstance.start();
        dThreadInstance.start();
        
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
    
    @SuppressWarnings("resource")
    public void restartInternals() {
        //stop
        this.dataGrabber.stop();
        this.workThread.shutdown();
        this.dataGrabber = null;
        this.workThread = null;
        System.gc();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //start
        this.dataGrabber = new DataGrabber(this);
        Thread dThreadInstance = new Thread(dataGrabber);
        
        AudioInputStream stream = null;
        try {
        	InputStream bufferedStream = new BufferedInputStream(this.getClass().getResourceAsStream("/" + ltcSources.get(Integer.valueOf((String) framerateBox.getSelectedItem()))));
            stream = AudioSystem.getAudioInputStream(bufferedStream);
        } catch (UnsupportedAudioFileException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Mixer mixer = AudioSystem.getMixer(((MixerEntry) ltcOutputBox.getSelectedItem()).getMixerInfo());
        InetAddress address = ((NetEntry) addressBox.getSelectedItem()).getNetworkAddress();
        this.workThread = new WorkerThread(stream, mixer, address, dThreadInstance);
        this.workThread.setFramerate(Integer.valueOf((String) framerateBox.getSelectedItem()));
        Thread wThreadInstance = new Thread(workThread);
        
        this.dataGrabber.setWorkerInstance(workThread);
        
        wThreadInstance.start();
        dThreadInstance.start();
    }
    
    private List<Image> getIcons() {
        List<Image> list = new ArrayList<Image>();
        int[] sizes = new int[] {32, 64, 256};
        for (int i : sizes) {
            try {
                list.add(ImageIO.read(this.getClass().getResource("/icons/icon" + i + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
