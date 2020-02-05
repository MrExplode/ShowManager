package me.mrexplode.timecode.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class TrackPanel extends JPanel {

    private static final long serialVersionUID = -3730226112917338692L;
    
    private BufferedImage waveImage;
    private int boxWidth = 1;
    private JProgressBar progressBar;
    private JPanel self;
    
    public float[] samples;
    
    public TrackPanel() {
        this.progressBar = new JProgressBar();
        this.self = this;
        this.progressBar.setBorderPainted(false);
        //at this time the panel doesn't have size, probably
        this.progressBar.setBounds(0, 0, 100, 20);
        this.progressBar.setValue(50);
        this.progressBar.setUI(new TrackProgressUI());
        this.add(progressBar);
        this.progressBar.setOpaque(false);
        this.setOpaque(false);
        EventQueue.invokeLater(() -> {
            this.progressBar.setBounds(0, 0, this.getWidth(), this.getHeight());
            this.waveImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        });
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                progressBar.setBounds(0, 0, self.getWidth(), self.getHeight());
            }
        });
    }
    
    public void drawWaveform(float[] samples) {
        Graphics2D g2d = waveImage.createGraphics();
        
        int numSubsets = this.getWidth() / boxWidth;
        int subsetLength = samples.length / numSubsets;
        
        float[] subsets = new float[numSubsets];
        
        // find average(abs) of each box subset
        int s = 0;
        for (int i = 0; i < subsets.length; i++) {
            
            double sum = 0;
            for (int k = 0; k < subsetLength; k++) {
                sum += Math.abs(samples[s++]);
            }
            
            subsets[i] = (float) ( sum / subsetLength );
        }
        
        // find the peak so the waveform can be normalized
        // to the height of the image
        float normal = 0;
        for (float sample : subsets) {
            if (sample > normal)
                normal = sample;
        }
        
        // normalize and scale
        normal = 32768.0f / normal;
        for (int i = 0; i < subsets.length; i++) {
            subsets[i] *= normal;
            subsets[i] = ( subsets[i] / 32768.0f ) * ( this.getHeight() / 2 );
        }
        
        g2d.setColor(Color.GRAY);
        
        // convert to image coords and do actual drawing
        for (int i = 0; i < subsets.length; i++) {
            int sample = (int) subsets[i];
            
            int posY = ( this.getHeight() / 2 ) - sample;
            int negY = ( this.getHeight() / 2 ) + sample;
            
            int x = i * boxWidth;
            
            if (boxWidth == 1) {
                g2d.drawLine(x, posY, x, negY);
            } else {
                g2d.setColor(Color.GRAY);
                g2d.fillRect(x + 1, posY + 1, boxWidth - 1, negY - posY - 1);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(x, posY, boxWidth, negY - posY);
            }
        }
        
        g2d.dispose();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (samples != null) {
            waveImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
            drawWaveform(samples);
            g.drawImage(waveImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }
        
    }

}
