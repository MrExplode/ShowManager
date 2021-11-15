package me.sunstorm.showmanager.gui;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * Deprecated and disgusting swing gui.
 */
@Slf4j
@Getter
public class ServerGUI extends JFrame {/*


    public static final String PROG_HOME = System.getProperty("user.home") + "\\AppData\\Roaming\\ShowManager";

    @Getter private static ServerGUI instance;

    private Worker workThread;
    private MusicThread musicThread;
    private ProjectManager projectManager;
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new ThreadFactoryBuilder().setNameFormat("Executor Thread #%d").setDaemon(true).build());
    private final ArrayList<JComponent> inputComponents = Lists.newArrayList();
    private final TimeMonitor timeMonitor;
    private JComboBox<NetEntry> com2InterfaceBox = new JComboBox<>();

    private final JPanel contentPane;
    private final JPanel timePanel;
    private final JLabel timeDisplay;
    private final JLabel remoteControl;
    private final JPanel controlPanel;
    private final JButton btnPlay;
    private final JButton btnPause;
    private final JButton btnStop;
    private final JPanel settingsPanel;
    private final JCheckBox artnetCheckBox;
    private final JCheckBox ltcCheckBox;
    private final JCheckBox remoteCheckBox;
    private final JButton btnSetTime;
    private JTextField dmxField;
    private JLabel lblDmxAddress;
    private JTextField universeField;
    private JLabel lblUniverse;
    private JTextField subnetField;
    private JLabel lblSubnet;
    private final JComboBox<Integer> framerateBox;
    private final JLabel lblFramerate;
    private JButton btnSetDmx;
    private final JComboBox<MixerEntry> ltcOutputBox;
    private final JPanel setTimePanel;
    private final JTextField hourField;
    private final JTextField minField;
    private final JTextField secField;
    private final JTextField frameField;
    private final JPanel dmxSettingsPanel;
    private final JComboBox<NetEntry> artnetInterfaceBox;
    private final JButton btnRestart;
    private final JPanel playerPanel;
    private final JCheckBox musicCheckBox;
    private final JComboBox<MixerEntry> audioOutputBox;
    private final JLabel lblTrackInfo;
    private final JComboBox<Music> musicListBox;
    private final TrackPanel trackPanel;
    private final JButton btnRemove;
    private final JButton btnAdd;
    private final JSlider volumeSlider;
    private final JButton btnNetworkSettings;
    private final JPanel modulePane;
    private final JButton btnOscVis;
    private final JPanel oscPanel;
    private final JPanel mainPanel;
    private final JCheckBox chckbxOsc;
    private final JScrollPane scrollPane;
    private JTable table;
    private final JLabel lblTargetIp;
    private final JTextField oscIPField;
    private final JLabel lblOSCPort;
    private final JTextField oscPortField;
    private final JButton btnNow;
    private final JButton btnInsert;
    private final JButton btnInsertTime;
    private final JButton btnSort;

    private Thread wThreadInstance;
    private Thread mThreadInstance;
    
    private final JButton btnTimeMonitor;
    private final JButton btnRemoveOSC;
    private final JButton btnImport;
    private final JButton btnExport;

    public ServerGUI() throws SocketException {
        instance = this;
        timeMonitor = new TimeMonitor();
        timeMonitor.setIconImages(getIcons());

        //this.settingsProvider = new ServerSettingsProvider(new File(PROG_HOME + "\\serverSettings.json"), this);
        
        setTitle("ShowManager - Server");
        LafManager.install();
        projectManager = new ProjectManager();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(ServerGUI.this,
                        (projectManager.isModified() ? "You have unsaved changes!\nDo you really want to quit?" : "Are you sure want to quit?"),
                        "ShowManager", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        setIconImages(getIcons());
        setBounds(100, 100, 1150, 650);
        setMinimumSize(new Dimension(1150, 650));
        contentPane = new JPanel();
        contentPane.setBorder(null);
        setContentPane(contentPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu project = new JMenu("Project");
        JMenuItem newProject = new JMenuItem("New project");
        newProject.addActionListener(l -> {
            log.info("Creating new project...");
        });
        JMenuItem load = new JMenuItem("Load project");
        load.addActionListener(l -> {
            log.info("Loading project...");
        });
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(l -> executor.execute(() -> {
            log.info("Saving project...");
            projectManager.saveProject();
        }));
        JMenuItem saveAs = new JMenuItem("Save as...");
        saveAs.addActionListener(l -> {
            log.info("Saving project with different name...");
        });
        project.add(newProject);
        project.add(load);
        project.add(save);
        project.add(saveAs);
        menuBar.add(project);

        JMenu help = new JMenu("Help");
        JMenuItem laf = new JMenuItem("Look and feel settings");
        laf.setIcon(ThemeSettings.getIcon());
        laf.addActionListener(l -> ThemeSettings.showSettingsDialog(this));
        help.add(laf);
        JMenuItem about = new JMenuItem("About");
        JEditorPane pane = new JEditorPane("text/html", "<html><h2>ShowManager by MrExplode, 2020</h2><br>" +
                "A miniaturized show controller, capable of outputting ArtNet and LTC timecode,<br>" +
                "playing soundtrack along with it, broadcasting timed osc messages<br>" +
                "and sending various information through Redis in a client-server context.<br>" +
                "Remote control is possible with DMX or OSC.<br>" +
                "Not intended for commercial use by any mean.<br>" +
                "I made it for fun, for my needs.<br><br>" +
                "Credits to <a href=\"https://github.com/cansik\">Florian Bruggisser</a> for <a href=\"https://github.com/cansik/artnet4j\">artnet4j</a>" +
                "<br><html>");
        pane.setEditable(false);
        pane.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        about.addActionListener(l -> JOptionPane.showMessageDialog(this,
                pane, "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);
        menuBar.add(help);
        setJMenuBar(menuBar);

        oscPanel = new JPanel();
        oscPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "OSC controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        mainPanel = new JPanel();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGap(2)
                    .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, 613, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(oscPanel, GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(oscPanel, GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                    .addContainerGap())
                .addComponent(mainPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
        );
        
        chckbxOsc = new JCheckBox("OSC control");
        inputComponents.add(chckbxOsc);
        chckbxOsc.addActionListener(e -> {
            //@gui action osc toggle
            log.info("Toggled OSC control " + (chckbxOsc.isSelected() ? "on" : "off"));
        });
        
        scrollPane = new JScrollPane();
        
        lblTargetIp = new JLabel("Target IP");
        
        oscIPField = new JTextField();
        oscIPField.setColumns(10);
        
        lblOSCPort = new JLabel("Port");
        
        oscPortField = new JTextField();
        oscPortField.setColumns(10);
        
        btnNow = new JButton("Now");
        inputComponents.add(btnNow);
        btnNow.setToolTipText("Set the selected event's time to the current time");
        btnNow.addActionListener(e -> {
            //@gui action scheduler insert now
            //((SchedulerTableModel) table.getModel()).getEvent(table.getSelectedRow()).setExecTime(dataGrabber.getCurrentTime());
        });

        btnInsert = new JButton("Insert");
        inputComponents.add(btnInsert);
        btnInsert.addActionListener(e -> {
            //@gui action insert
            ((SchedulerTableModel) table.getModel()).insertEmptyRow(table.getSelectedRow());
        });
        btnInsert.setToolTipText("Insert an empty element befor the currently selected element");
        
        btnInsertTime = new JButton("Insert with time");
        inputComponents.add(btnInsertTime);
        btnInsertTime.addActionListener(e -> {
            //@gui action insert with time
            //((SchedulerTableModel) table.getModel()).insertRow(table.getSelectedRow(), new ScheduledEvent(null, dataGrabber.getCurrentTime()));
        });
        btnInsertTime.setToolTipText("Insert an empty element, with the current timecode");
        
        btnSort = new JButton("Sort");
        inputComponents.add(btnSort);
        btnSort.setToolTipText("Sort the table with the time values");
        btnSort.addActionListener(e-> {
            //@gui action sort
            ((SchedulerTableModel) table.getModel()).sort();
        });
        
        btnRemoveOSC = new JButton("Remove");
        inputComponents.add(btnRemoveOSC);
        btnRemoveOSC.addActionListener(e -> {
            //@gui action remove scheduled entry
            ((SchedulerTableModel) table.getModel()).removeRow(table.getSelectedRow());
        });
        btnRemoveOSC.setToolTipText("Remove the selected event");
        
        btnImport = new JButton("Import...");
        inputComponents.add(btnImport);
        btnImport.addActionListener(e -> {
            //@gui action import button
            FileIOPrompt prompt = new FileIOPrompt(true, (SchedulerTableModel) table.getModel());
            prompt.setVisible(true);
        });
        btnImport.setToolTipText("Import data structure from an external source");
        
        btnExport = new JButton("Export...");
        btnExport.addActionListener(e -> {
            //@gui action export
            FileIOPrompt prompt = new FileIOPrompt(false, (SchedulerTableModel) table.getModel());
            prompt.setVisible(true);
        });
        inputComponents.add(btnExport);
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
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(lblTargetIp)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(oscIPField, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18)
                                    .addComponent(lblOSCPort)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(oscPortField, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
                                .addGroup(gl_oscPanel.createSequentialGroup()
                                    .addComponent(btnNow)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnSort)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnInsert)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnInsertTime)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnRemoveOSC)))
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
                        .addComponent(btnImport)
                        .addComponent(lblTargetIp)
                        .addComponent(oscIPField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblOSCPort)
                        .addComponent(oscPortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_oscPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnNow)
                        .addComponent(btnSort)
                        .addComponent(btnInsert)
                        .addComponent(btnInsertTime)
                        .addComponent(btnRemoveOSC)
                        .addComponent(btnExport))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
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
        timePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Current time", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
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
        inputComponents.add(artnetCheckBox);
        artnetCheckBox.addActionListener(e -> {
            //@gui action artnet checkbox
            log.info("ArtNet broadcast " + (artnetCheckBox.isSelected() ? "on" : "off"));
            //workThread.setBroadcastArtNet(artnetCheckBox.isSelected());
        });
        artnetCheckBox.setToolTipText("Toggles the ArtNet timecode broadcasting");
        
        ltcCheckBox = new JCheckBox("LTC timecode");
        inputComponents.add(ltcCheckBox);
        ltcCheckBox.addActionListener(e -> {
            //@gui action ltc checkbox
            log.info("LTC output " + (ltcCheckBox.isSelected() ? "on" : "off"));
            //workThread.setLTC(selected);
        });
        ltcCheckBox.setToolTipText("Toggles the LTC output");
        
        framerateBox = new JComboBox<>();
        framerateBox.setToolTipText("Timecode framerate");
        framerateBox.addItem(24);
        framerateBox.addItem(25);
        framerateBox.addItem(30);
        framerateBox.setSelectedIndex(1);
        
        lblFramerate = new JLabel("Framerate");
        
        ltcOutputBox = new JComboBox<>();
        ltcOutputBox.setToolTipText("Select the output for LTC. Carefully! LTC should never go out on speakers!");
        
        artnetInterfaceBox = new JComboBox<>();
        artnetInterfaceBox.setToolTipText("Network to broadcast ArtNet Timecode.");
        
        btnRestart = new JButton("Restart internals");
        inputComponents.add(btnRestart);
        btnRestart.setToolTipText("In order to your changes take effect, you have to restart the internal implementation.");
        btnRestart.addActionListener(e -> {
            //@gui action restart
            workThread.stop();
            restartInternals();
        });
        
        musicCheckBox = new JCheckBox("Audio player");
        musicCheckBox.addActionListener(e -> {
            //@gui action music checkbox
            log.info("AudioPlayer turned " + (musicCheckBox.isSelected() ? "on" : "off"));
            musicThread.setEnabled(musicCheckBox.isSelected());
        });
        inputComponents.add(musicCheckBox);
        musicCheckBox.setToolTipText("Toggles the audio output");
        
        audioOutputBox = new JComboBox<>();
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
                                .addComponent(artnetInterfaceBox, 0, 151, Short.MAX_VALUE)
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
                    .addComponent(artnetInterfaceBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
        inputComponents.add(remoteCheckBox);
        remoteCheckBox.addActionListener(e -> {
            //@gui action dmx remote checkbox
            boolean selected = remoteCheckBox.isSelected();
            log.info("DMX remote control " + (selected ? "on" : "off"));
            //workThread.setRemoteControl(selected);
            dmxField.setEnabled(selected);
            lblDmxAddress.setEnabled(selected);
            universeField.setEnabled(selected);
            lblUniverse.setEnabled(selected);
            subnetField.setEnabled(selected);
            lblSubnet.setEnabled(selected);
            btnSetDmx.setEnabled(selected);
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
        inputComponents.add(btnSetDmx);
        btnSetDmx.addActionListener(e -> {
            //@gui action dmx set button
            //workThread.setDmxAddress(Integer.valueOf(dmxField.getText()));
            //workThread.setUniverse(Integer.valueOf(universeField.getText()));
            //workThread.setSubnet(Integer.valueOf(subnetField.getText()));
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
        inputComponents.add(btnOscVis);
        btnOscVis.addActionListener(e -> {
            //@gui action cue pilot unused
        });
        
        btnTimeMonitor = new JButton("Time monitor");
        inputComponents.add(btnTimeMonitor);
        btnTimeMonitor.addActionListener(e -> {
            //@gui action time monitor show
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
            //@gui action network settings (unused)
//            NetworkingGUI netGui = new NetworkingGUI(com1Port, com2Port, com2InterfaceBox, packetSize, this);
//            netGui.setVisible(true);
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
                .addGroup(Alignment.TRAILING, gl_setTimePanel.createSequentialGroup()
                    .addGap(33)
                    .addComponent(hourField, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addGap(26)
                    .addComponent(minField, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addGap(26)
                    .addComponent(secField, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addGap(26)
                    .addComponent(frameField, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addGap(23))
        );
        gl_setTimePanel.setVerticalGroup(
            gl_setTimePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_setTimePanel.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(gl_setTimePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(hourField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(minField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(secField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(frameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        setTimePanel.setLayout(gl_setTimePanel);
        
        btnSetTime = new JButton("Set time");
        inputComponents.add(btnSetTime);
        btnSetTime.addActionListener(e -> {
            //@gui action set time button
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
                hour = Integer.parseInt(hourField.getText());
            }
            if (minField.getText().equals("") || !m.reset(minField.getText()).matches()) {
                //empty or not number
                minField.setBackground(Color.RED);
                wrong = true;
            } else {
                min = Integer.parseInt(minField.getText());
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
                sec = Integer.parseInt(secField.getText());
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
                frame = Integer.parseInt(frameField.getText());
                if (frame < 0 || frame > (int) (framerateBox.getSelectedItem())) {
                    frameField.setBackground(Color.RED);
                    wrong = true;
                }
            }

            if (!wrong) {
                //Timecode time = new Timecode(hour, min, sec, frame, WorkerThread.getFramerate());
                //workThread.setTime(time);
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

        });
        
        controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
        inputComponents.add(btnPlay);
        btnPlay.addActionListener(e -> {
            //@gui action play button
            workThread.play();
            table.clearSelection();
        });
        
        btnPause = new JButton("Pause");
        inputComponents.add(btnPause);
        btnPause.addActionListener(e -> {
            //@gui action pause button
            workThread.pause();
        });
        
        btnStop = new JButton("Stop");
        inputComponents.add(btnStop);
        btnStop.addActionListener(e -> {
            //@gui action stop button
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
        playerPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Music Player", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        lblTrackInfo = new JLabel("Current track");
        
        musicListBox = new JComboBox<>();
        
        trackPanel = new TrackPanel();
        
        btnRemove = new JButton("Remove");
        btnRemove.addActionListener(e -> {
            //@gui action music remove
            musicListBox.removeItemAt(musicListBox.getSelectedIndex());
        });
        btnRemove.setToolTipText("Remove the current track");
        inputComponents.add(btnRemove);
        
        btnAdd = new JButton("Add");
        btnAdd.setToolTipText("Requres a restart after adding.");
        inputComponents.add(btnAdd);
        btnAdd.addActionListener(e -> {
            //@gui action music add
            MusicAdder musicPrompt = new MusicAdder(musicListBox);
            musicPrompt.setVisible(true);
        });
        
        volumeSlider = new JSlider();
        volumeSlider.addChangeListener(e -> {
            //@gui action volume slider
            if (musicThread != null)
                musicThread.setVolume((float) volumeSlider.getValue() / 100);
        });
        
        volumeSlider.setValue(75);
        inputComponents.add(volumeSlider);
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
                            .addGap(59))
                        .addGroup(gl_playerPanel.createSequentialGroup()
                            .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnRemove)))
                    .addContainerGap())
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
                    .addGap(10)
                    .addComponent(trackPanel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        trackPanel.setLayout(null);
        playerPanel.setLayout(gl_playerPanel);
        GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
        gl_mainPanel.setHorizontalGroup(
            gl_mainPanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_mainPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_mainPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(playerPanel, GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                        .addGroup(gl_mainPanel.createSequentialGroup()
                            .addGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(remoteControl, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                .addComponent(controlPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                                .addGroup(gl_mainPanel.createSequentialGroup()
                                    .addGap(44)
                                    .addComponent(btnSetTime, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE))
                                .addGroup(Alignment.TRAILING, gl_mainPanel.createSequentialGroup()
                                    .addGap(2)
                                    .addComponent(setTimePanel, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                                    .addGap(3))
                                .addComponent(timePanel, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(settingsPanel, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(dmxSettingsPanel, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)))
                    .addGap(0))
        );
        gl_mainPanel.setVerticalGroup(
            gl_mainPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_mainPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(dmxSettingsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_mainPanel.createSequentialGroup()
                            .addComponent(timePanel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(setTimePanel, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnSetTime)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(controlPanel, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(remoteControl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(settingsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 271, GroupLayout.PREFERRED_SIZE)
                    .addGap(19))
        );
        mainPanel.setLayout(gl_mainPanel);

        //INIT ltc and audio player outputs
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            //port prefixed mixers don't seem to work
            if (!info.getName().startsWith("Port")) {
                MixerEntry entry = new MixerEntry(info, info.getName());
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
                artnetInterfaceBox.addItem(netEntry);
                com2InterfaceBox.addItem(netEntry);
            }
        }
        artnetInterfaceBox.setSelectedIndex(0);
        com2InterfaceBox.setSelectedIndex(0);
        
        contentPane.setLayout(gl_contentPane);
        
        //overriding default space actions
        for (JComponent component : inputComponents) {
            InputMap im = component.getInputMap();
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        }
        
        //setting up threading
        start();
    }

    private void start() {
        Mixer ltcMixer = AudioSystem.getMixer(((MixerEntry) ltcOutputBox.getSelectedItem()).getMixerInfo());
        InetAddress artnetAddress = ((NetEntry) artnetInterfaceBox.getSelectedItem()).getNetworkAddress();
        InetAddress oscAddress = null;

        try {
            oscAddress = InetAddress.getByName(oscIPField.getText());
        } catch (UnknownHostException e) {
            JOptionPane.showConfirmDialog(null, "Unknown host exception, try another valid ip", "OSC target ip", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
            e.printStackTrace();
        }
        int oscPort = 0;
        try {
            oscPort = Integer.parseInt(oscPortField.getText());
        } catch (NumberFormatException e) {
            oscPortField.setText("0");
        }
        OscHandler oscHandler = new OscHandler(null, 0, 0);
        LtcHandler ltcHandler = new LtcHandler(ltcMixer, 30);
        workThread = new Worker(oscHandler, ltcHandler, ((SchedulerTableModel)table.getModel()), null, artnetAddress, 30);
        wThreadInstance = new Thread(workThread);
        
        //musicthread setup
        Mixer audioMixer = AudioSystem.getMixer(((MixerEntry) audioOutputBox.getSelectedItem()).getMixerInfo());
        ArrayList<Music> musicList = new ArrayList<>();
        for (int i = 0; i < musicListBox.getModel().getSize(); i++) {
            musicList.add(musicListBox.getItemAt(i));
        }
        InetAddress com2addr = ((NetEntry) com2InterfaceBox.getSelectedItem()).getNetworkAddress();
        musicThread = new MusicThread(audioMixer, trackPanel, lblTrackInfo, musicList);
        mThreadInstance = new Thread(musicThread);
        trackPanel.dependencies(musicThread, workThread);
        
        //actual start
        wThreadInstance.start();
        mThreadInstance.start();
    }
    
    private void stop() {
        this.workThread.shutdown();
        this.musicThread.shutdown();
        this.workThread = null;
        this.musicThread = null;
        try {
            this.wThreadInstance.join();
            this.wThreadInstance = null;
            this.mThreadInstance = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.gc();
    }

    @SneakyThrows(value = {InterruptedException.class})
    public void restartInternals() {
        stop();
        Thread.sleep(1000);
        start();
    }

    @SneakyThrows
    public static List<Image> getIcons() {
        List<Image> list = new ArrayList<>();
        int[] sizes = new int[] {32, 64, 256};
        for (int i : sizes) {
            list.add(ImageIO.read(ServerGUI.class.getResource("/icons/icon" + i + ".png")));
        }
        return list;
    }*/
}
