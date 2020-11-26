package me.mrexplode.showmanager.gui;

import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.vladsch.flexmark.util.html.ui.Color;

import me.mrexplode.showmanager.gui.entries.NetEntry;


@Deprecated
public class NetworkingGUI extends JFrame {

    private static final long serialVersionUID = 121637646326772536L;
    private JPanel contentPane;
    private JLabel com1Label;
    private JTextField com1PortField;
    private JLabel com2Label;
    private JTextField com2PortField;
    private JLabel com2InterfaceLabel;
    private JComboBox<NetEntry> comboBox;
    private JButton btnSet;
    private JButton btnHelp;
    private JLabel lblSegment;
    private JTextField packetSizeField;

    /**
     * Create the frame.
     */
    public NetworkingGUI(int com1Port, int com2Port, JComboBox<NetEntry> com2Interface, int packetSize, ServerGUI gui) {
        setTitle("Networking settings");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImages(ServerGUI.getIcons());
        setBounds(100, 100, 307, 186);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        com1Label = new JLabel("Com1 port:");
        com1Label.setToolTipText("This port is used to distribute internal events and other informations to clients");
        
        com1PortField = new JTextField();
        com1PortField.setColumns(10);
        com1PortField.setText(Integer.toString(com1Port));
        
        com2Label = new JLabel("Com2 port:");
        com2Label.setToolTipText("Com2 is a custom UDP connection, used to send the loaded track's waveform data to the clients.");
        
        com2PortField = new JTextField();
        com2PortField.setColumns(10);
        com2PortField.setText(Integer.toString(com2Port));
        
        com2InterfaceLabel = new JLabel("Com2 interface:");
        com2InterfaceLabel.setToolTipText("Com2 uses a custom UDP connection, so matching network interfaces are necessary");
        
        comboBox = com2Interface;
        
        btnSet = new JButton("Set");
        btnSet.addActionListener(e -> {
            try {
                //gui.com1Port = Integer.parseInt(com1PortField.getText());
            } catch (NumberFormatException exc) {
                com1PortField.setBackground(Color.RED);
                return;
            }
            try {
                //gui.com2Port = Integer.parseInt(com2PortField.getText());
            } catch (NumberFormatException exc) {
                com2PortField.setBackground(Color.RED);
                return;
            }
            try {
                //gui.packetSize = Integer.parseInt(packetSizeField.getText());
            } catch (NumberFormatException exc) {
                packetSizeField.setBackground(Color.RED);
                return;
            }
            //gui.com2InterfaceBox.setSelectedIndex(comboBox.getSelectedIndex());
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        
        btnHelp = new JButton("Help");
        btnHelp.addActionListener(e -> {
            JOptionPane.showConfirmDialog(null, "Just make sure that every client in the session has the same settings as the server.", "Help", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
        });
        
        lblSegment = new JLabel("Com2 packet size:");
        lblSegment.setToolTipText("udp packet size in bytes");
        
        packetSizeField = new JTextField();
        packetSizeField.setColumns(10);
        packetSizeField.setText(Integer.toString(packetSize));
        
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_contentPane.createSequentialGroup()
                                    .addComponent(com1Label)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(com1PortField, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                                .addGroup(gl_contentPane.createSequentialGroup()
                                    .addComponent(com2Label)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(com2PortField, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                .addComponent(btnHelp, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSet, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
                                .addComponent(lblSegment, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(com2InterfaceLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addComponent(packetSizeField, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                .addComponent(comboBox, 0, 180, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(com1Label)
                        .addComponent(com1PortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnHelp))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(com2Label)
                        .addComponent(com2PortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSet))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(com2InterfaceLabel)
                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblSegment)
                        .addComponent(packetSizeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contentPane.setLayout(gl_contentPane);
    }
}
