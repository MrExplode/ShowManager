package me.mrexplode.showmanager.gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import me.mrexplode.showmanager.util.Timecode;
import me.mrexplode.showmanager.WorkerThread;
import me.mrexplode.showmanager.fileio.Music;


public class MusicAdder extends JFrame {
    
    private static final long serialVersionUID = 8052480444363260810L;
    private File file;
    private JComboBox<Music> comboBox;
    
    private JPanel contentPane;
    private JLabel lblStartTime;
    private JTextField timeField;
    private JButton btnFileChooser;
    private JButton btnAdd;

    /**
     * Create the frame.
     */
    public MusicAdder(JComboBox<Music> comboBox) {
        this.comboBox = comboBox;
        setTitle("Add music");
        setResizable(false);
        setIconImages(ServerGUI.getIcons());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 390, 87);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        lblStartTime = new JLabel("Starting time");
        
        timeField = new JTextField();
        timeField.setToolTipText("format: hour/min/sec/frame");
        timeField.setColumns(10);
        
        btnFileChooser = new JButton("Choose file...");
        btnFileChooser.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileHidingEnabled(false);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory() || f.getName().endsWith(".wav")) {
                        return true;
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    return null;
                }
            });
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int response = chooser.showDialog(null, "Add");
            file = chooser.getSelectedFile();
            if (response == JFileChooser.APPROVE_OPTION && file != null) {
                btnAdd.setEnabled(true);
            } else {
                btnAdd.setEnabled(false);
            }
        });
        btnFileChooser.setToolTipText("Only .wav flies allowed!");
        
        btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> {
            Music music = new Music();
            music.setFile(file.getAbsolutePath());
            
            try {
                String[] values = timeField.getText().split("/");
                int hour = Integer.parseInt(values[0]);
                int min = Integer.parseInt(values[1]);
                int sec = Integer.parseInt(values[2]);
                int frame = Integer.parseInt(values[3]);
                music.setStartingTime(new Timecode(hour, min, sec, frame, WorkerThread.getFramerate()));
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
                timeField.setBackground(Color.RED);
                return;
            }
            
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = stream.getFormat();
                long lengthMillis = (long) ((stream.getFrameLength() + 0.0) / format.getFrameSize() * 1000);
                music.setLength(lengthMillis);
                stream.close();
            } catch (UnsupportedAudioFileException | IOException e1) {
                e1.printStackTrace();
                return;
            }
            
            this.comboBox.addItem(music);
            
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        btnAdd.setEnabled(false);
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblStartTime)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(timeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnFileChooser)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(77, Short.MAX_VALUE))
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(timeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblStartTime)
                        .addComponent(btnFileChooser)
                        .addComponent(btnAdd))
                    .addContainerGap(230, Short.MAX_VALUE))
        );
        contentPane.setLayout(gl_contentPane);
    }
}
