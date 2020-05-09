package me.mrexplode.timecode.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import me.mrexplode.timecode.DataGrabber;
import me.mrexplode.timecode.MusicThread;
import me.mrexplode.timecode.Timecode;
import me.mrexplode.timecode.WorkerThread;
import me.mrexplode.timecode.fileio.Music;
import me.mrexplode.timecode.fileio.ServerSettingsProvider;
import me.mrexplode.timecode.schedule.ScheduledEvent;


public class ServerGUI extends JFrame {

    /**
     * THe path of the base directory for the program
     */
    public static final String PROG_HOME = System.getProperty("user.home") + "\\AppData\\Roaming\\TimecodeGenerator";
    
    private static final long serialVersionUID = -7342971032020137377L;
    private WorkerThread workThread;
    private DataGrabber dataGrabber;
    private MusicThread musicThread;
    private HashMap<Integer, String> ltcSources = new HashMap<Integer, String>();
    private ServerSettingsProvider settingsProvider;
    public static ServerGUI guiInstance;
    private ArrayList<JComponent> components = new ArrayList<JComponent>();
    private ArrayList<JPanel> threadIndicators = new ArrayList<JPanel>();
    public TimeMonitor timeMonitor;
    public int com1Port = 7100;
    public int com2Port = 7007;
    public JComboBox<NetEntry> com2InterfaceBox = new JComboBox<NetEntry>();

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
    public JComboBox<Integer> framerateBox;
    private JLabel lblFramerate;
    private JButton btnSetDmx;
    public JComboBox<MixerEntry> ltcOutputBox;
    private JPanel setTimePanel;
    private JTextField hourField;
    private JTextField minField;
    private JTextField secField;
    private JTextField frameField;
    private JPanel dmxSettingsPanel;
    public JComboBox<NetEntry> addressBox;
    public JButton btnRestart;
    private JPanel playerPanel;
    public JCheckBox musicCheckBox;
    public JComboBox<MixerEntry> audioOutputBox;
    public JLabel lblTrackInfo;
    public JComboBox<Music> musicListBox;
    private TrackPanel trackPanel;
    public JButton btnRemove;
    public JButton btnAdd;
    private JSlider volumeSlider;
    private JButton btnNetworkSettings;
    private JPanel modulePane;
    private JButton btnOscVis;
    private JPanel oscPanel;
    private JPanel mainPanel;
    private JPanel threadIndicator1;
    private JPanel threadIndicator2;
    private JPanel threadIndicator3;
    private JPanel threadIndicator4;
    private JPanel threadIndicator5;
    private JCheckBox chckbxOsc;
    private JScrollPane scrollPane;
    public JTable table;
    private JLabel lblTargetIp;
    public JTextField oscIPField;
    private JLabel lblOSCPort;
    public JTextField oscPortField;
    public JButton btnNow;
    public JButton btnInsert;
    public JButton btnInsertTime;
    public JButton btnSort;

    private Thread dThreadInstance;
    private Thread wThreadInstance;
    private Thread mThreadInstance;
    
    private JButton btnTimeMonitor;
    private JButton btnRemoveOSC;
    private JButton btnImport;
    private JButton btnExport;

    /**
     * Create the frame.
     * @throws SocketException 
     */
    public ServerGUI() throws SocketException {
        guiInstance = this;
        timeMonitor = new TimeMonitor();
        timeMonitor.setIconImages(getIcons());
        ltcSources.put(24, "ltc/LTC_00_00_00_00__90mins_24.wav");
        ltcSources.put(25, "ltc/LTC_00_00_00_00__91mins_25.wav");
        ltcSources.put(30, "ltc/LTC_00_00_00_00__90mins_30.wav");
        
        this.settingsProvider = new ServerSettingsProvider(new File(PROG_HOME + "\\serverSettings.json"), this);
        
        setTitle("Timecode Generator - Server  (experimental LTC implementation)");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImages(getIcons());
        setBounds(100, 100, 1150, 513);
        setMinimumSize(new Dimension(1150, 513));
        contentPane = new JPanel();
        contentPane.setBorder(null);
        setContentPane(contentPane);
        
        oscPanel = new JPanel();
        oscPanel.setBorder(new TitledBorder(null, "OSC controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        mainPanel = new JPanel();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGap(2)
                    .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, 609, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(oscPanel, GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                        .addComponent(oscPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                        .addComponent(mainPanel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 455, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        
        chckbxOsc = new JCheckBox("OSC control");
        components.add(chckbxOsc);
        chckbxOsc.addActionListener(e -> {
            System.out.println((chckbxOsc.isSelected() ? "Enabled" : "Disabled") + " OSC message dispatch");
            workThread.setOSC(chckbxOsc.isSelected());
        });
        
        scrollPane = new JScrollPane();
        
        lblTargetIp = new JLabel("Target IP");
        
        oscIPField = new JTextField();
        oscIPField.setColumns(10);
        
        lblOSCPort = new JLabel("Port");
        
        oscPortField = new JTextField();
        oscPortField.setColumns(10);
        
        btnNow = new JButton("Now");
        components.add(btnNow);
        btnNow.setToolTipText("Set the selected event's time to the current time");
        btnNow.addActionListener(e -> {
            ((SchedulerTableModel) table.getModel()).getEvent(table.getSelectedRow()).setExecTime(dataGrabber.getCurrentTime());
        });
        
        btnInsert = new JButton("Insert");
        components.add(btnInsert);
        btnInsert.addActionListener(e -> {
            ((SchedulerTableModel) table.getModel()).insertEmptyRow(table.getSelectedRow());
        });
        btnInsert.setToolTipText("Insert an empty element befor the currently selected element");
        
        btnInsertTime = new JButton("Insert with time");
        components.add(btnInsertTime);
        btnInsertTime.addActionListener(e -> {
            ((SchedulerTableModel) table.getModel()).insertRow(table.getSelectedRow(), new ScheduledEvent(null, dataGrabber.getCurrentTime()));
        });
        btnInsertTime.setToolTipText("Insert an empty element, with the current timecode");
        
        btnSort = new JButton("Sort");
        components.add(btnSort);
        btnSort.setToolTipText("Sort the table with the time values");
        btnSort.addActionListener(e-> {
            ((SchedulerTableModel) table.getModel()).sort();
        });
        
        btnRemoveOSC = new JButton("Remove");
        components.add(btnRemoveOSC);
        btnRemoveOSC.addActionListener(e -> {
            ((SchedulerTableModel) table.getModel()).removeRow(table.getSelectedRow());
        });
        btnRemoveOSC.setToolTipText("Remove the selected event");
        
        btnImport = new JButton("Import...");
        components.add(btnImport);
        btnImport.addActionListener(e -> {
            FileIOPrompt prompt = new FileIOPrompt(true, (SchedulerTableModel) table.getModel());
            prompt.setVisible(true);
        });
        btnImport.setToolTipText("Import data structure from an external source");
        
        btnExport = new JButton("Export...");
        btnExport.addActionListener(e -> {
            FileIOPrompt prompt = new FileIOPrompt(false, (SchedulerTableModel) table.getModel());
            prompt.setVisible(true);
        });
        components.add(btnExport);
        btnExport.setToolTipText("Export current data");
        GroupLayout gl_oscPanel = new GroupLayout(oscPanel);
        gl_oscPanel.setHorizontalGroup(
            gl_oscPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_oscPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_oscPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                        .addGroup(gl_oscPanel.createSequentialGroup()
                            .addGroup(gl_oscPanel.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_oscPanel.createSequentialGroup()
                                    .addComponent(chckbxOsc)
                                    .addGap(18)
                                    .addComponent(lblTargetIp)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(oscIPField, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(lblOSCPort)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(oscPortField))
                                .addGroup(gl_oscPanel.createSequentialGroup()
                                    .addComponent(btnNow)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnSort)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnInsert)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnInsertTime)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnRemoveOSC, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_oscPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(btnExport, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                                .addComponent(btnImport, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        gl_oscPanel.setVerticalGroup(
            gl_oscPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_oscPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_oscPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(chckbxOsc)
                        .addComponent(lblTargetIp)
                        .addComponent(oscIPField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblOSCPort)
                        .addComponent(oscPortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnImport))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_oscPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnNow)
                        .addComponent(btnSort)
                        .addComponent(btnInsert)
                        .addComponent(btnInsertTime)
                        .addComponent(btnRemoveOSC)
                        .addComponent(btnExport))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                    .addContainerGap())
        );
        
        //creating scheduler table
        table = new JTable();
        table.setModel(new SchedulerTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        table.getColumnModel().getColumns().asIterator().forEachRemaining(e -> e.setCellEditor(new CustomCellEditor()));
        table.getColumnModel().getColumn(0).setCellEditor(new TimecodeCellEditor());
        //table.getColumnModel().getColumns().asIterator().forEachRemaining(e -> e.setCellRenderer(cellRenderer));
        
        scrollPane.setViewportView(table);
        oscPanel.setLayout(gl_oscPanel);
        
        timePanel = new JPanel();
        timePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Current time", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        
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
        
        settingsPanel = new JPanel();
        
        artnetCheckBox = new JCheckBox("ArtNet timecode");
        components.add(artnetCheckBox);
        artnetCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println((artnetCheckBox.isSelected() ? "Enabled" : "Disabled") + " ArtNet timecode");
                workThread.setBroadcast(artnetCheckBox.isSelected());
            }
        });
        artnetCheckBox.setToolTipText("Toggles the ArtNet timecode broadcasting");
        
        ltcCheckBox = new JCheckBox("LTC timecode");
        components.add(ltcCheckBox);
        ltcCheckBox.addActionListener(e -> {
            boolean selected = ltcCheckBox.isSelected();
            System.out.println((selected ? "Enabled" : "Disabled") + " LTC timecode");
            workThread.setLTC(selected);
        });
        ltcCheckBox.setToolTipText("Toggles the LTC output");
        
        framerateBox = new JComboBox<Integer>();
        framerateBox.setToolTipText("Timecode framerate");
        framerateBox.addItem(24);
        framerateBox.addItem(25);
        framerateBox.addItem(30);
        framerateBox.setSelectedIndex(1);
        
        lblFramerate = new JLabel("Framerate");
        
        ltcOutputBox = new JComboBox<MixerEntry>();
        ltcOutputBox.setToolTipText("Select the output for LTC. Carefully! LTC should never go out on speakers!");
        
        addressBox = new JComboBox<NetEntry>();
        addressBox.setToolTipText("Network to broadcast ArtNet Timecode.");
        
        btnRestart = new JButton("Restart internals");
        components.add(btnRestart);
        btnRestart.setToolTipText("In order to your changes take effect, you have to restart the internal implementation.");
        btnRestart.addActionListener(e -> {
            //just in case
            workThread.stop();
            restartInternals();
        });
        
        musicCheckBox = new JCheckBox("Audio player");
        musicCheckBox.addActionListener(e -> {
            System.out.println((musicCheckBox.isSelected() ? "Enabled" : "Disabled") + " music player");
            musicThread.setEnabled(musicCheckBox.isSelected());
        });
        components.add(musicCheckBox);
        musicCheckBox.setToolTipText("Toggles the audio output");
        
        audioOutputBox = new JComboBox<MixerEntry>();
        audioOutputBox.setToolTipText("Select the output for the audio player");
        
        GroupLayout gl_settingsPanel = new GroupLayout(settingsPanel);
        gl_settingsPanel.setHorizontalGroup(
            gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_settingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_settingsPanel.createSequentialGroup()
                            .addComponent(btnRestart, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(gl_settingsPanel.createSequentialGroup()
                            .addComponent(musicCheckBox, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(gl_settingsPanel.createSequentialGroup()
                            .addComponent(artnetCheckBox, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(18, Short.MAX_VALUE))
                        .addGroup(gl_settingsPanel.createSequentialGroup()
                            .addGroup(gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(addressBox, 0, 151, Short.MAX_VALUE)
                                .addComponent(ltcOutputBox, 0, 151, Short.MAX_VALUE)
                                .addGroup(gl_settingsPanel.createSequentialGroup()
                                    .addComponent(ltcCheckBox, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                    .addGap(28)))
                            .addGap(0))
                        .addGroup(gl_settingsPanel.createSequentialGroup()
                            .addComponent(framerateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblFramerate, GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                            .addGap(24))
                        .addComponent(audioOutputBox, 0, 151, Short.MAX_VALUE)))
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
                    .addPreferredGap(ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                    .addComponent(btnRestart)
                    .addContainerGap())
        );
        settingsPanel.setLayout(gl_settingsPanel);
        
        dmxSettingsPanel = new JPanel();
        
        remoteCheckBox = new JCheckBox("DMX remote control");
        components.add(remoteCheckBox);
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
        btnSetDmx.setToolTipText("(doesn't need restart)");
        components.add(btnSetDmx);
        btnSetDmx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workThread.setDmxAddress(Integer.valueOf(dmxField.getText()));
                workThread.setUniverse(Integer.valueOf(universeField.getText()));
                workThread.setSubnet(Integer.valueOf(subnetField.getText()));
            }
        });
        btnSetDmx.setEnabled(false);
        
        modulePane = new JPanel();
        modulePane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Additional windows", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        GroupLayout gl_dmxSettingsPanel = new GroupLayout(dmxSettingsPanel);
        gl_dmxSettingsPanel.setHorizontalGroup(
            gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(modulePane, GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
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
                    .addContainerGap())
        );
        gl_dmxSettingsPanel.setVerticalGroup(
            gl_dmxSettingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_dmxSettingsPanel.createSequentialGroup()
                    .addContainerGap()
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
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(modulePane, GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
        );
        
        btnNetworkSettings = new JButton("Networking settings");
        btnNetworkSettings.setToolTipText("Settings affecting client-server networking");
        
        btnOscVis = new JButton("Cue Pilot");
        btnOscVis.setToolTipText("Under development");
        btnOscVis.setEnabled(false);
        components.add(btnOscVis);
        btnOscVis.addActionListener(e -> {
            
        });
        
        btnTimeMonitor = new JButton("Time monitor");
        components.add(btnTimeMonitor);
        btnTimeMonitor.addActionListener(e -> {
            timeMonitor.setVisible(true);
        });
        GroupLayout gl_modulePane = new GroupLayout(modulePane);
        gl_modulePane.setHorizontalGroup(
            gl_modulePane.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_modulePane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_modulePane.createParallelGroup(Alignment.LEADING)
                        .addComponent(btnNetworkSettings, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                        .addComponent(btnOscVis, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                        .addComponent(btnTimeMonitor, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_modulePane.setVerticalGroup(
            gl_modulePane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_modulePane.createSequentialGroup()
                    .addGap(4)
                    .addComponent(btnNetworkSettings)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnOscVis)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnTimeMonitor)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        modulePane.setLayout(gl_modulePane);
        btnNetworkSettings.addActionListener(e -> {
            NetworkingGUI netGui = new NetworkingGUI(com1Port, com2Port, com2InterfaceBox, this);
            netGui.setVisible(true);
        });
        dmxSettingsPanel.setLayout(gl_dmxSettingsPanel);
        
        setTimePanel = new JPanel();
        
        hourField = new JTextField();
        hourField.setColumns(10);
        
        minField = new JTextField();
        minField.setColumns(10);
        
        secField = new JTextField();
        secField.setColumns(10);
        
        frameField = new JTextField();
        frameField.setColumns(10);
        GroupLayout gl_setTimePanel = new GroupLayout(setTimePanel);
        gl_setTimePanel.setHorizontalGroup(
            gl_setTimePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_setTimePanel.createSequentialGroup()
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
        gl_setTimePanel.setVerticalGroup(
            gl_setTimePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_setTimePanel.createSequentialGroup()
                    .addGroup(gl_setTimePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(hourField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(minField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(secField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(frameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        setTimePanel.setLayout(gl_setTimePanel);
        
        btnSetTime = new JButton("Set time");
        components.add(btnSetTime);
        btnSetTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean wrong = false;
                Pattern p = Pattern.compile("\\b\\d+\\b");
                Matcher m = p.matcher("");
                
                int hour = 0;
                int min = 0;
                int sec = 0;
                int frame = 0;
                if (hourField.getText().equals("") || !m.reset(hourField.getText()).matches()) {
                    //empty or not number
                    hourField.setBackground(Color.RED);
                    wrong = true;
                } else {
                    hour = Integer.valueOf(hourField.getText());
                }
                if (minField.getText().equals("") || !m.reset(minField.getText()).matches()) {
                    //empty or not number
                    minField.setBackground(Color.RED);
                    wrong = true;
                } else {
                    min = Integer.valueOf(minField.getText());
                    if (min > 59 || min < 0) {
                        minField.setBackground(Color.RED);
                        wrong = true;
                    }
                }
                if (secField.getText().equals("") || !m.reset(secField.getText()).matches()) {
                    //empty or not number
                    secField.setBackground(Color.RED);
                    wrong = true;
                } else {
                    sec = Integer.valueOf(secField.getText());
                    if (sec > 59 || sec < 0) {
                        secField.setBackground(Color.RED);
                        wrong = true;
                    }
                }
                if (frameField.getText().equals("") || !m.reset(frameField.getText()).matches()) {
                    //empty or not number
                    frameField.setBackground(Color.RED);
                    wrong = true;
                } else {
                    frame = Integer.valueOf(frameField.getText());
                    if (frame < 0 || frame > (int) (framerateBox.getSelectedItem())) {
                        frameField.setBackground(Color.RED);
                        wrong = true;
                    }
                }
                
                if (!wrong) {
                    Timecode time = new Timecode(hour, min, sec, frame, WorkerThread.getFramerate());
                    workThread.setTime(time);
                    hourField.setText("");
                    hourField.setBackground(Color.WHITE);
                    minField.setText("");
                    minField.setBackground(Color.WHITE);
                    secField.setText("");
                    secField.setBackground(Color.WHITE);
                    frameField.setText("");
                    frameField.setBackground(Color.WHITE);
                    setTimePanel.setToolTipText("");
                } else {
                    setTimePanel.setToolTipText("All fields must be filled in with numbers, and match time values! (eg. min must be between 0 and 59)");
                }
                
            }
        });
        
        controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder(null, "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        controlPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "spaceTogglePlay");
        controlPanel.getActionMap().put("spaceTogglePlay", new AbstractAction() {
            private static final long serialVersionUID = -103271722918723118L;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (workThread.isPlaying()) {
                    btnPause.doClick();
                } else {
                    btnPlay.doClick();
                }
            }
            
        });
        
        btnPlay = new JButton("Play");
        components.add(btnPlay);
        btnPlay.addActionListener(e -> {
            workThread.play();
            table.clearSelection();
        });
        
        btnPause = new JButton("Pause");
        components.add(btnPause);
        btnPause.addActionListener(e -> {
            workThread.pause();
        });
        
        btnStop = new JButton("Stop");
        components.add(btnStop);
        btnStop.addActionListener(e -> {
            workThread.stop();
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
        
        remoteControl = new JLabel("Remote control: Waiting");
        remoteControl.setHorizontalAlignment(SwingConstants.CENTER);
        remoteControl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        remoteControl.setForeground(Color.RED);
        
        playerPanel = new JPanel();
        playerPanel.setBorder(new TitledBorder(null, "Music player", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        lblTrackInfo = new JLabel("Current track");
        
        musicListBox = new JComboBox<Music>();
        
        trackPanel = new TrackPanel();
        
        btnRemove = new JButton("Remove");
        btnRemove.addActionListener(e -> {
            musicListBox.removeItemAt(musicListBox.getSelectedIndex());
        });
        btnRemove.setToolTipText("Remove the current track");
        components.add(btnRemove);
        
        btnAdd = new JButton("Add");
        btnAdd.setToolTipText("Requres a restart after adding.");
        components.add(btnAdd);
        btnAdd.addActionListener(e -> {
            MusicAdder musicPrompt = new MusicAdder(musicListBox);
            musicPrompt.setVisible(true);
        });
        
        volumeSlider = new JSlider();
        volumeSlider.addChangeListener(e -> {
            if (musicThread != null)
                musicThread.setVolume((float) volumeSlider.getValue() / 100);
        });
        
        volumeSlider.setValue(75);
        components.add(volumeSlider);
        volumeSlider.setToolTipText("Volume");
        volumeSlider.setPaintLabels(true);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setMajorTickSpacing(25);
        GroupLayout gl_playerPanel = new GroupLayout(playerPanel);
        gl_playerPanel.setHorizontalGroup(
            gl_playerPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_playerPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_playerPanel.createSequentialGroup()
                            .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(trackPanel, GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                                .addGroup(gl_playerPanel.createSequentialGroup()
                                    .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_playerPanel.createSequentialGroup()
                                            .addComponent(lblTrackInfo, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                                            .addGap(18))
                                        .addGroup(gl_playerPanel.createSequentialGroup()
                                            .addComponent(musicListBox, 0, 327, Short.MAX_VALUE)
                                            .addPreferredGap(ComponentPlacement.RELATED)))
                                    .addComponent(volumeSlider, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                                    .addGap(59)))
                            .addContainerGap())
                        .addGroup(gl_playerPanel.createSequentialGroup()
                            .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnRemove)
                            .addContainerGap(419, Short.MAX_VALUE))))
        );
        gl_playerPanel.setVerticalGroup(
            gl_playerPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_playerPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_playerPanel.createSequentialGroup()
                            .addComponent(lblTrackInfo)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(musicListBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(volumeSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_playerPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnAdd)
                        .addComponent(btnRemove))
                    .addPreferredGap(ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                    .addComponent(trackPanel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        trackPanel.setLayout(null);
        playerPanel.setLayout(gl_playerPanel);
        
        threadIndicator1 = new JPanel();
        
        threadIndicator2 = new JPanel();
        
        threadIndicator3 = new JPanel();
        
        threadIndicator4 = new JPanel();
        
        threadIndicator5 = new JPanel();
        GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
        gl_mainPanel.setHorizontalGroup(
            gl_mainPanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_mainPanel.createSequentialGroup()
                    .addGroup(gl_mainPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(playerPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                        .addGroup(gl_mainPanel.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(gl_mainPanel.createParallelGroup(Alignment.TRAILING)
                                .addGroup(gl_mainPanel.createSequentialGroup()
                                    .addGroup(gl_mainPanel.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(gl_mainPanel.createSequentialGroup()
                                            .addGap(2)
                                            .addComponent(setTimePanel, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                                        .addComponent(timePanel, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
                                    .addGap(3))
                                .addGroup(gl_mainPanel.createSequentialGroup()
                                    .addGap(85)
                                    .addComponent(btnSetTime, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(87))
                                .addGroup(gl_mainPanel.createSequentialGroup()
                                    .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                    .addGap(3))
                                .addComponent(remoteControl, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                                .addGroup(gl_mainPanel.createSequentialGroup()
                                    .addComponent(threadIndicator1, GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(threadIndicator2, GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                                    .addGap(18)
                                    .addComponent(threadIndicator3, GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                                    .addGap(18)
                                    .addComponent(threadIndicator4, GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(threadIndicator5, GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                                    .addGap(2)))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(settingsPanel, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(dmxSettingsPanel, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)))
                    .addGap(0))
        );
        gl_mainPanel.setVerticalGroup(
            gl_mainPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_mainPanel.createSequentialGroup()
                    .addGroup(gl_mainPanel.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_mainPanel.createSequentialGroup()
                            .addComponent(timePanel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(setTimePanel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnSetTime)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(controlPanel, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(remoteControl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(gl_mainPanel.createParallelGroup(Alignment.TRAILING)
                                .addComponent(threadIndicator1, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                .addComponent(threadIndicator5, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                .addComponent(threadIndicator2, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                .addComponent(threadIndicator3, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                .addComponent(threadIndicator4, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)))
                        .addComponent(settingsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(dmxSettingsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(playerPanel, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
        );
        mainPanel.setLayout(gl_mainPanel);
        
        threadIndicators.add(threadIndicator1);
        threadIndicators.add(threadIndicator2);
        threadIndicators.add(threadIndicator3);
        threadIndicators.add(threadIndicator4);
        threadIndicators.add(threadIndicator5);
        //INIT ltc and audio player outputs
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            //port prefixed mixers don't seem to work
            if (!info.getName().startsWith("Port")) {
                MixerEntry entry = new MixerEntry(info.getName(), info);
                ltcOutputBox.addItem(entry);
                audioOutputBox.addItem(entry);
            }
        }
        ltcOutputBox.setSelectedIndex(0);
        audioOutputBox.setSelectedIndex(0);
        
        //INIT artnet and internal communication outputs
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netInterface = interfaces.nextElement();
            if (netInterface.isUp()) {
                InetAddress addr = netInterface.getInetAddresses().nextElement();
                NetEntry netEntry = new NetEntry(addr, netInterface.getName() + " " + addr.getHostAddress());
                addressBox.addItem(netEntry);
                com2InterfaceBox.addItem(netEntry);
            }
        }
        addressBox.setSelectedIndex(0);
        com2InterfaceBox.setSelectedIndex(0);
        
        contentPane.setLayout(gl_contentPane);
        
        //overriding default space actions
        for (int i = 0; i < components.size(); i++) {
            InputMap im = components.get(i).getInputMap();
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        }
        
        //setting up threading
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
    
    @SuppressWarnings("resource")
    private void start() {
        //datagrabber setup
        this.dataGrabber = new DataGrabber(this, com1Port);
        dThreadInstance = new Thread(dataGrabber);
        
        //workerthread setup
        Mixer ltcMixer = AudioSystem.getMixer(((MixerEntry) ltcOutputBox.getSelectedItem()).getMixerInfo());
        InetAddress address = ((NetEntry) addressBox.getSelectedItem()).getNetworkAddress();
        InetAddress oscAddress = null;
        try {
            oscAddress = InetAddress.getByName(oscIPField.getText());
        } catch (UnknownHostException e) {
            JOptionPane.showConfirmDialog(null, "Unknown host exception, try another valid ip", "OSC target ip", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
            e.printStackTrace();
        }
        int oscPort = 0;
        try {
            oscPort = Integer.valueOf(oscPortField.getText());
        } catch (NumberFormatException e) {
            oscPortField.setText("0");
        }
        this.workThread = new WorkerThread(ltcMixer, address, (SchedulerTableModel) table.getModel(), oscAddress, oscPort, dThreadInstance, dataGrabber, dataGrabber.getLock());
        this.workThread.setFramerate((int) (framerateBox.getSelectedItem()));
        wThreadInstance = new Thread(workThread);
        
        this.dataGrabber.setWorkerInstance(workThread);
        
        //musicthread setup
        Mixer audioMixer = AudioSystem.getMixer(((MixerEntry) audioOutputBox.getSelectedItem()).getMixerInfo());
        ArrayList<Music> musicList = new ArrayList<Music>();
        for (int i = 0; i < musicListBox.getModel().getSize(); i++) {
            musicList.add(musicListBox.getItemAt(i));
        }
        InetAddress com2addr = ((NetEntry) com2InterfaceBox.getSelectedItem()).getNetworkAddress();
        musicThread = new MusicThread(audioMixer, trackPanel, lblTrackInfo, musicList, DataGrabber.getEventHandler(), dataGrabber.getLock(), com1Port, com2Port, com2addr);
        mThreadInstance = new Thread(musicThread);
        trackPanel.dependencies(musicThread, workThread);
        
        //thread error handling
        ThreadErrorHandler wHandler = new ThreadErrorHandler(btnRestart, threadIndicator2, "WorkerThread");
        ThreadErrorHandler dHandler = new ThreadErrorHandler(btnRestart, threadIndicator3, "DataGrabber");
        ThreadErrorHandler mHandler = new ThreadErrorHandler(btnRestart, threadIndicator4, "MusicThread");
        
        wThreadInstance.setUncaughtExceptionHandler(wHandler);
        dThreadInstance.setUncaughtExceptionHandler(dHandler);
        mThreadInstance.setUncaughtExceptionHandler(mHandler);
        
        //actual start
        wThreadInstance.start();
        dThreadInstance.start();
        mThreadInstance.start();
    }
    
    private void stop() {
        this.dataGrabber.shutdown();
        this.workThread.shutdown();
        this.musicThread.shutdown();
        this.dataGrabber = null;
        this.workThread = null;
        this.musicThread = null;
        try {
            this.wThreadInstance.join();
            this.wThreadInstance = null;
            this.dThreadInstance.join();
            this.dThreadInstance = null;
            //this.mThreadInstance.join();
            this.mThreadInstance = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.gc();
    }
    
    public void restartInternals() {
        for (int i = 0; i < threadIndicators.size(); i++) {
            JPanel indicator = threadIndicators.get(i);
            indicator.setBackground(Color.YELLOW);
            indicator.setToolTipText("Restarting...");
        }
        repaint();
        //stop
        stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //start
        start();
    }
    
    public static List<Image> getIcons() {
        List<Image> list = new ArrayList<Image>();
        int[] sizes = new int[] {32, 64, 256};
        for (int i : sizes) {
            try {
                list.add(ImageIO.read(ServerGUI.class.getResource("/icons/icon" + i + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
