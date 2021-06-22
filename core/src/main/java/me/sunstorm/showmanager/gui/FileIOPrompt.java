package me.sunstorm.showmanager.gui;

import java.awt.Color;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import me.sunstorm.showmanager.fileio.DataStructure;
import me.sunstorm.showmanager.fileio.TableIO;
import me.sunstorm.showmanager.gui.general.SchedulerTableModel;


public class FileIOPrompt extends JFrame {

    private boolean isImport;
    private SchedulerTableModel model;
    private File file = null;
    private static final long serialVersionUID = -6584756517122558963L;
    private JPanel contentPane;
    private JComboBox<DataStructure> comboBox;
    private JButton btnChooseFile;
    private JButton btnAction;
    private JLabel lblInfo;

    /**
     * Create the frame.
     */
    public FileIOPrompt(boolean isImport, SchedulerTableModel model) {
        this.isImport = isImport;
        this.model = model;
        setTitle(isImport ? "Import data" : "Export data");
        setIconImages(ServerGUI.getIcons());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 314, 104);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        comboBox = new JComboBox<>();
        for (DataStructure s : DataStructure.values()) {
            if (!this.isImport && s == DataStructure.REAPER_MARKER) {
                continue;
            }
            comboBox.addItem(s);
        }
        
        btnChooseFile = new JButton("Choose file");
        btnChooseFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileHidingEnabled(false);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory() || f.getName().endsWith(((DataStructure) comboBox.getSelectedItem()).getFileExtension())) {
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
            int response = chooser.showDialog(this, isImport ? "Import" : "Export");
            this.file = chooser.getSelectedFile();
            if (response == JFileChooser.APPROVE_OPTION && this.file != null) {
                btnAction.setEnabled(true);
            } else {
                btnAction.setEnabled(false);
            }
        });
        
        btnAction = new JButton(this.isImport ? "Import" : "Export");
        btnAction.addActionListener(e -> {
            TableIO io = new TableIO(this.model);
            if (this.isImport) {
                boolean success = io.importData(file, (DataStructure) comboBox.getSelectedItem());
                if (success) {
                    lblInfo.setForeground(Color.GREEN.darker());
                    lblInfo.setText("Importing successful");
                } else {
                    lblInfo.setForeground(Color.RED);
                    lblInfo.setText("Importing failed!");
                }
            } else {
                boolean success = io.exportData(file, (DataStructure) comboBox.getSelectedItem());
                if (success) {
                    lblInfo.setForeground(Color.GREEN.darker());
                    lblInfo.setText("Exporting successful");
                } else {
                    lblInfo.setForeground(Color.RED);
                    lblInfo.setText("Exporting failed!");
                }
            }
        });
        btnAction.setEnabled(false);
        
        lblInfo = new JLabel();
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(lblInfo, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
                            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnChooseFile)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnAction)))
                    .addContainerGap(125, Short.MAX_VALUE))
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnChooseFile)
                        .addComponent(btnAction))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblInfo)
                    .addContainerGap(33, Short.MAX_VALUE))
        );
        contentPane.setLayout(gl_contentPane);
    }
}
