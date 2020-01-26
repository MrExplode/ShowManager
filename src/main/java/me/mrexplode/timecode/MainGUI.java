package me.mrexplode.timecode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
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


public class MainGUI extends JFrame {
    
    private WorkerThread workThread;
    private DataGrabber dataGrabber;

    private JPanel contentPane;
    private JPanel timePanel;
    public JLabel timeDisplay;
    public JLabel remoteControl;
    private JPanel controlPanel;
    public JButton btnPlay;
    public JButton btnPause;
    public JButton btnStop;
    private JTextField hourField;
    private JTextField minField;
    private JTextField secField;
    private JTextField frameField;
    private JPanel settingsPanel;
    private JCheckBox artnetBox;
    private JCheckBox ltcBox;
    private JCheckBox remoteBox;
    public JButton btnSetTime;
    private JTextField dmxField;
    private JLabel lblDmxAddress;
    private JTextField universeField;
    private JLabel lblUniverse;
    private JTextField subnetField;
    private JLabel lblSubnet;
    public JComboBox framerateBox;
    private JLabel lblFramerate;
    private JButton btnSetDmx;
    private JComboBox outputBox;

    /**
     * Create the frame.
     */
    public MainGUI() {
        setResizable(false);
        setTitle("Timecode Generator");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImages(getIcons());
        setBounds(100, 100, 450, 300);
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
        
        hourField = new JTextField();
        hourField.setColumns(10);
        
        minField = new JTextField();
        minField.setColumns(10);
        
        secField = new JTextField();
        secField.setColumns(10);
        
        frameField = new JTextField();
        frameField.setColumns(10);
        
        settingsPanel = new JPanel();
        
        btnSetTime = new JButton("Set time");
        btnSetTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("implement time set");
            }
        });
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addComponent(timePanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                                .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
                                    .addGap(29)
                                    .addComponent(hourField, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18)
                                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(btnSetTime, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                            .addComponent(minField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                            .addComponent(secField, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)))
                                    .addGap(18)
                                    .addComponent(frameField, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                    .addGap(39)))
                            .addGap(0))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addComponent(remoteControl, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)))
                    .addComponent(settingsPanel, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addComponent(settingsPanel, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addComponent(timePanel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                .addComponent(hourField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(minField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(secField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(frameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnSetTime)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(controlPanel, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                            .addGap(18)
                            .addComponent(remoteControl)))
                    .addContainerGap())
        );
        
        artnetBox = new JCheckBox("ArtNet timecode");
        artnetBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println((artnetBox.isSelected() ? "Enabled" : "Disabled") + " ArtNet timecode");
                workThread.setBroadcast(artnetBox.isSelected());
            }
        });
        artnetBox.setSelected(true);
        artnetBox.setToolTipText("Toggles the ArtNet timecode broadcasting");
        
        ltcBox = new JCheckBox("LTC timecode");
        ltcBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = ltcBox.isSelected();
                if (selected) {
                    MixerEntry mixEntry = (MixerEntry) outputBox.getSelectedItem();
                    Mixer mixer = AudioSystem.getMixer(mixEntry.getMixerInfo());
                    //SourceDataLine.Info info = new DataLine.Info(Clip.class, format, bufferSize);
                } else {
                    workThread.setLTC(false);
                }
            }
        });
        ltcBox.setToolTipText("Toggles the LTC output");
        
        remoteBox = new JCheckBox("DMX remote control");
        remoteBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = remoteBox.isSelected();
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
        remoteBox.setToolTipText("Toggles the remote control via DMX");
        
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
        
        framerateBox = new JComboBox();
        framerateBox.setToolTipText("Currently no effect!");
        framerateBox.setModel(new DefaultComboBoxModel(new String[] {"24", "25", "30"}));
        framerateBox.setSelectedIndex(1);
        
        lblFramerate = new JLabel("Framerate");
        
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
        
        outputBox = new JComboBox();
        outputBox.setToolTipText("Select the output for LTC. Carefully! LTC should never go out on speakers!");
        //INIT list available outputs
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            MixerEntry entry = new MixerEntry(info.getName(), info);
            outputBox.addItem(entry);
        }
        
        GroupLayout gl_settingsPanel = new GroupLayout(settingsPanel);
        gl_settingsPanel.setHorizontalGroup(
            gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_settingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(outputBox, 0, 148, Short.MAX_VALUE)
                        .addGroup(gl_settingsPanel.createSequentialGroup()
                            .addGroup(gl_settingsPanel.createParallelGroup(Alignment.TRAILING)
                                .addComponent(artnetBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                .addGroup(gl_settingsPanel.createSequentialGroup()
                                    .addGap(21)
                                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_settingsPanel.createSequentialGroup()
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(dmxField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(lblDmxAddress))
                                        .addGroup(gl_settingsPanel.createSequentialGroup()
                                            .addComponent(subnetField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(lblSubnet, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                                        .addComponent(btnSetDmx, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                        .addGroup(gl_settingsPanel.createSequentialGroup()
                                            .addComponent(universeField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(lblUniverse, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))))
                                .addGroup(gl_settingsPanel.createSequentialGroup()
                                    .addComponent(framerateBox, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addComponent(lblFramerate, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                                .addComponent(ltcBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                .addComponent(remoteBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                            .addGap(18))))
        );
        gl_settingsPanel.setVerticalGroup(
            gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_settingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(artnetBox)
                    .addGap(3)
                    .addComponent(ltcBox)
                    .addGap(3)
                    .addComponent(outputBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(remoteBox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblDmxAddress)
                        .addComponent(dmxField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(universeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblUniverse))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(subnetField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSubnet))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnSetDmx)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(framerateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFramerate))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(btnPlay, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(btnPause, GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnStop, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addGap(11))
        );
        gl_controlPanel.setVerticalGroup(
            gl_controlPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_controlPanel.createSequentialGroup()
                    .addGroup(gl_controlPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnPlay, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPause)
                        .addComponent(btnStop))
                    .addContainerGap(11, Short.MAX_VALUE))
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
        
        this.dataGrabber = new DataGrabber(this);
        Thread dThreadInstance = new Thread(dataGrabber);
        
        this.workThread = new WorkerThread(dThreadInstance);
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
