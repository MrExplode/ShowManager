package me.mrexplode.timecode.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import me.mrexplode.timecode.MusicThread;
import me.mrexplode.timecode.Timecode;
import me.mrexplode.timecode.Tracker;
import me.mrexplode.timecode.WorkerThread;


public class TrackPanel extends JPanel {

    private static final long serialVersionUID = -3730226112917338692L;
    
    private BufferedImage waveImage;
    private int boxWidth = 1;
    private JProgressBar progressBar;
    private JPanel self;
    private MusicThread musicThread;
    private WorkerThread workerThread;
    
    private float[] samples;
    
    public TrackPanel() {
        this.progressBar = new JProgressBar();
        this.self = this;
        this.progressBar.setBorderPainted(false);
        //at this time the panel doesn't have size, probably
        this.progressBar.setBounds(0, 0, 100, 20);
        this.progressBar.setValue(0);
        this.progressBar.setMaximum(1000);
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
        this.progressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (getMusicThread() != null && getWorkerThread() != null) {
                    int x = e.getX();
                    int width = progressBar.getWidth();
                    int max = progressBar.getMaximum();
                    int newValue = (int) Math.round(((double) x / (double) width) * max);
                    progressBar.setValue(newValue);
                    Tracker tracker = getMusicThread().getTracker();
                    long length = tracker.getEnd().subtract(tracker.getStart()).millis();
                    long val = (length / 1000) * newValue;
                    Timecode newTime = new Timecode(val);
                    getWorkerThread().setTime(newTime);
                }
            }
        });
    }
    
    public void dependencies(MusicThread m, WorkerThread w) {
        this.musicThread = m;
        this.workerThread = w;
    }
    
    private MusicThread getMusicThread() {
        return this.musicThread;
    }
    
    private WorkerThread getWorkerThread() {
        return this.workerThread;
    }
    
    public void setSamples(float[] samples) {
        this.samples = samples;
        this.repaint();
    }
    
    public void setValue(int n) {
        this.progressBar.setValue(n);
    }
    
    private void drawWaveform(float[] samples) {
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
