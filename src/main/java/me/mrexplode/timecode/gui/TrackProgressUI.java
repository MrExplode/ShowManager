package me.mrexplode.timecode.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class TrackProgressUI extends BasicProgressBarUI {
    
    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int iStrokWidth = 3;
        g2d.setStroke(new BasicStroke(iStrokWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(Color.GRAY);
        g2d.setBackground(progressBar.getBackground());

        int width = progressBar.getWidth();
        int height = progressBar.getHeight();

        RoundRectangle2D outline = new RoundRectangle2D.Double((iStrokWidth / 2), (iStrokWidth / 2), width - iStrokWidth, height - iStrokWidth, height, height);
        g2d.draw(outline);
        
        int iInnerHeight = height - (iStrokWidth * 4);
        int iInnerWidth = width - (iStrokWidth * 4);

        double dProgress = progressBar.getPercentComplete();
        if (dProgress < 0) {
            dProgress = 0;
        } else if (dProgress > 1) {
            dProgress = 1;
        }

        iInnerWidth = (int) Math.round(iInnerWidth * dProgress);

        /*
        int x = iStrokWidth * 2;
        int y = iStrokWidth * 2;

        Point2D start = new Point2D.Double(x, y);
        Point2D end = new Point2D.Double(x, y + iInnerHeight);
        
        float[] dist = {0.0f, 0.25f, 1.0f};
        Color color = Color.GRAY;
        Color[] colors = {color, color.brighter(), color.darker()};
        LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);

        g2d.setPaint(p);
        */
        g2d.setColor(new Color(0.1f, 0.5f, 0.1f, 0.3f));

        /*
        RoundRectangle2D fill = new RoundRectangle2D.Double(iStrokWidth * 2, iStrokWidth * 2,
                iInnerWidth, iInnerHeight, iInnerHeight, iInnerHeight);
        Rectangle2D fill2 = new Rectangle2D.Double(iStrokWidth * 2, iStrokWidth * 2, iInnerWidth, iInnerHeight);*/
        HalfRoundedRectLeft fill3 = new HalfRoundedRectLeft(iStrokWidth * 2, iStrokWidth * 2, iInnerWidth, iInnerHeight, iInnerHeight);

        g2d.fill(fill3);
    
        g2d.dispose();
    }
    
}
class HalfRoundedRectLeft extends Path2D.Double {

    private static final long serialVersionUID = -8757540390223922228L;

    public HalfRoundedRectLeft(double x, double y, double w, double h, double arch) {
        moveTo(x + w, y + h);
        lineTo(x + arch, y + h);
        curveTo(x, y + h, x, y + h, x, y + h - arch);
        lineTo(x, y + arch);
        curveTo(x, y, x, y, x + arch, y);
        lineTo(x + w, y);
        closePath();
    }
    
}
