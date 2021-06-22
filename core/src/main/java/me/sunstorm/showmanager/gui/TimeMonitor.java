package me.sunstorm.showmanager.gui;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import me.sunstorm.showmanager.gui.general.Animator;


public class TimeMonitor extends JFrame {
    private JPanel contentPane;
    private JPanel timePanel;
    public JLabel timeDisplay;
    private Animator anim;

    /**
     * Create the frame.
     */
    public TimeMonitor() {
        setAutoRequestFocus(false);
        setAlwaysOnTop(true);
        setTitle("Time monitor");
        setBounds(100, 100, 589, 218);
        contentPane = new JPanel();
        contentPane.setBackground(UIManager.getColor("Button.background"));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        timePanel = new JPanel();
        timePanel.setBorder(new TitledBorder(null, "Current time", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.anim = new Animator(contentPane);
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(timePanel, GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(timePanel, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
        );
        
        timeDisplay = new JLabel("00 : 00 : 00 / 00");
        timeDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        timeDisplay.setFont(new Font("Tahoma", Font.BOLD, 60));
        GroupLayout gl_timePanel = new GroupLayout(timePanel);
        gl_timePanel.setHorizontalGroup(
            gl_timePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_timePanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(timeDisplay, GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_timePanel.setVerticalGroup(
            gl_timePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_timePanel.createSequentialGroup()
                    .addComponent(timeDisplay, GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addContainerGap())
        );
        timePanel.setLayout(gl_timePanel);
        contentPane.setLayout(gl_contentPane);
    }
    
    public Animator getAnimator() {
        return this.anim;
    }
    
}
