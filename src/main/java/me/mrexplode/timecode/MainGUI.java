package me.mrexplode.timecode;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;


public class MainGUI extends JFrame {

    private JPanel contentPane;
    private JPanel panel;
    private JLabel timeDisplay;

    /**
     * Create the frame.
     */
    public MainGUI() {
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
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        panel = new JPanel();
        panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Current time", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGap(89)
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(89, Short.MAX_VALUE))
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(170, Short.MAX_VALUE))
        );
        
        timeDisplay = new JLabel("00 : 00 : 00 / 00");
        timeDisplay.setFont(new Font("Tahoma", Font.BOLD, 24));
        timeDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(timeDisplay, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
                    .addGap(7)
                    .addComponent(timeDisplay, GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE)
                    .addContainerGap())
        );
        panel.setLayout(gl_panel);
        contentPane.setLayout(gl_contentPane);
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
