package com.nbis.fluoj.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import com.nbis.fluoj.persistence.*;

import com.nbis.fluoj.classifier.SegmentedParticle;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

import ij.IJ;

/**
 * ImageCanvas customized to display
 * {@link classifier.SegmentedParticle ImageLabels} corresponding to
 * {@link persistence.Scell Scells} provided directly or through
 * SCPClassifierEditor {@link gui.TrainingJFrame} and register
 * {@link classifier.SegmentedParticle} {@link persistence.Type Types}
 *
 * @author Airen
 *
 */
public class OutputImageCanvas extends FluoJImageCanvas {

    private List<Scell> scells;
    private List<SegmentedParticle> particles;


    /*
	 * Constructor receives parent frame and SCImageProcess instance for
	 * interacting with labels. Includes processing of the image, omitted by the
	 * other constructor
     */
    public OutputImageCanvas(OutputJFrame frame) throws InvalidOperationOnResourceException {
        super(frame);
        this.scells = frame.getScells();
        SegmentedParticle particle = null;
        particles = cip.getParticles();
        for (Scell ss : scells) {
            particle = cip.getParticle(particles, new Point(ss.getX(), ss.getY()));
            if (particle != null) {
                particle.setScell(ss);
            } else {
                System.out.printf("%s, %s\n", ss.getX(), ss.getY());
            }

        }

    }

    List<Scell> getScells() {
        return scells;
    }

    public void reload(List<Scell> scells) {
        this.scells = scells;
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        if (!IJ.getToolName().equals("FluoJ")) {
            super.mousePressed(e);
            return;
        }
        if (frame == null) {
            return;
        }
        int x = super.offScreenX(e.getX());
        int y = super.offScreenY(e.getY());

        SegmentedParticle particle = cip.getParticle(particles, new Point(x, y));

        Scell ss = particle.getScell();
        if (ss != null && frame instanceof ReviewJFrame) {
            ReviewJFrame rframe = (ReviewJFrame) frame;
            rframe.enableUpdate();
            if (!e.isControlDown())//user accessing particle info
            {
                ss.setIdtype(rframe.getActiveType());
            }
            repaint();
            rframe.updateAllCounts();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1f));
        g2.setFont(new Font("Verdana", Font.BOLD, 10));
        int x, y;
        String label;
        Color color;
        Scell s;
        int x0 = (int) getSrcRect().getX();
        int y0 = (int) getSrcRect().getY();
        for (int i = 0; i < scells.size(); i++) {
            s = scells.get(i);
            x = (int) ((s.getX() - x0) * magnification);
            y = (int) ((s.getY() - y0) * magnification);
            label = "_";
            color = Color.LIGHT_GRAY;
            if (s.getIdtype() != null) {
                label = s.getIdtype().getLabel();
                color = new Color(s.getIdtype().getColor());
            }
            drawLabel(label, color, g2, x, y - 10);
            label = "_";
            color = Color.LIGHT_GRAY;
            if (s.getWinner() != null) {
                label = s.getWinner().getLabel();
                color = new Color(s.getWinner().getColor());
            }
            drawLabel(label, color, g2, x, y);
        }
    }

    public void drawLabel(String label, Color color, Graphics2D g2, int x, int y) {
        Color original_color = g2.getColor();
        g2.setColor(color);
        g2.drawString(label, x, y);
        g2.setColor(original_color);
    }

//	public void resetClassification() {
//		
//		for (int i = 0; i < scells.size(); i++)
//			scells.get(i).setIdtype(null);
//		repaint();
//	}
}
