package com.nbis.fluoj.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.nbis.fluoj.persistence.*;

import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.ParticleStatistic;
import com.nbis.fluoj.classifier.SegmentedParticle;
import com.nbis.fluoj.classifier.CellProcessor;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Toolbar;
import ij.process.ImageProcessor;

/**
 * ImageCanvas customized to display {@link classifier.SegmentedParticle ImageLabels}
 * corresponding to {@link persistence.Scell Scells} provided directly or
 * through SCPClassifierEditor {@link gui.TrainingJFrame} and register
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
		particles = cip.getFilteredParticles();
		for (Scell ss : scells) 
		{
			particle = cip.getMotif(particles,	new Point(ss.getXPosition(), ss.getYPosition()));
			if(particle != null)
				particle.setScell(ss);
			else
				System.out.printf("%s, %s\n", ss.getXPosition(), ss.getYPosition());

		}

	}
	
	List<Scell> getScells()
	{
		return scells;
	}
	
	public void reload(List<Scell> scells)
	{
		this.scells = scells;
		repaint();
	}

	public void mousePressed(MouseEvent e) {
		if (!IJ.getToolName().equals("FluoJ")) {
			super.mousePressed(e);
			return;
		}
		if (frame == null)
			return;
		int x = super.offScreenX(e.getX());
		int y = super.offScreenY(e.getY());

		SegmentedParticle particle = cip.getMotif(particles, new Point(x, y));
		
		Scell ss = particle.getScell();
		if(ss != null && frame instanceof ReviewJFrame)
		{
			ReviewJFrame rframe = (ReviewJFrame)frame;
			rframe.enableUpdate();
			if (!e.isControlDown())//user accessing particle info
			{
				ss.setType(rframe.getActiveType());
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
			x = (int) ((s.getXPosition() - x0) * magnification);
			y = (int) ((s.getYPosition() - y0) * magnification);
			label = "no";
			color = Color.LIGHT_GRAY;
			if (s.getType() != null)
			{
				label = s.getType().getLabel();
				color = new Color(s.getType().getColor());
			}
			g2.setColor(color);
			g2.drawString(label, x,	y - 10);
			label = "no";
			color = Color.LIGHT_GRAY;
			if (s.getType1() != null)
			{
				label = s.getType1().getLabel();
				color = new Color(s.getType1().getColor());
			}
			g2.setColor(color);
			g2.drawString(label, x, y);
		}
	}

	public void resetClassification() {
		
		for (int i = 0; i < scells.size(); i++)
			scells.get(i).setType(null);
		repaint();
	}

}
