package com.nbis.fluoj.gui;

import ij.IJ;
import ij.gui.Toolbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;
import com.nbis.fluoj.classifier.SegmentedParticle;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;
import com.nbis.fluoj.classifier.ParticleStatistic;

/**
 * ImageCanvas customized to display {@link classifier.SegmentedParticle
 * ImageLabels} provided by {@link gui.TrainingJFrame} and register
 * {@link classifier.SegmentedParticle} {@link persistence.Type Types}
 * 
 * @author Airen
 * 
 */
public class FluoJTrainingCanvas extends FluoJImageCanvas {

	private TrainingJFrame parent;
	private List<SegmentedParticle> ils;

	public FluoJTrainingCanvas(TrainingJFrame parent) throws InvalidOperationOnResourceException {
		super(parent);
		this.ils = cip.getParticles();
		this.parent = parent;
		parent.updateCounts();
		
	}

	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (IJ.spaceBarDown() || Toolbar.getToolId() == Toolbar.MAGNIFIER || Toolbar.getToolId() == Toolbar.HAND) {
			return;
		}
		int x = super.offScreenX(e.getX());
		int y = super.offScreenY(e.getY());

		SegmentedParticle il = cip.getParticle(ils, new Point(x, y));
		if (il == null)
			return;

		ParticleStatistic ci = il.getParticleStatistic();
		if(!e.isControlDown())
			ci.setType(parent.getActiveType());
		repaint();
		parent.updateCounts();
	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1f));
		g2.setFont(new Font("Verdana", Font.BOLD, 10));
		g2.setColor(Color.blue);
		int x, y;
		String label;
		int x0 = (int) getSrcRect().getX();
		int y0 = (int) getSrcRect().getY();
		ParticleStatistic ci;
		for (SegmentedParticle il : ils) {
			ci = il.getParticleStatistic();
			if (ci.getType() == null)
				continue;
			label = ci.getType().getLabel();
			x = (int) ((il.getParticleStatistic().getX0() - x0) * magnification);
			y = (int) ((il.getParticleStatistic().getY0() - y0) * magnification);
			g2.setColor(new Color(ci.getType().getColor()));
			g2.drawString(label, x, y);
		}
	}

	public void resetClassification() {
		for (SegmentedParticle il : ils)
			il.getParticleStatistic().setType(null);
		repaint();
	}

	

}
