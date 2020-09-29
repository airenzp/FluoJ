package com.nbis.fluoj.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Toolbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.SwingUtilities;
import com.nbis.fluoj.persistence.Type;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.CellInfo;
import com.nbis.fluoj.classifier.SegmentedParticle;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

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
		this.ils = cip.getFilteredParticles();
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

		SegmentedParticle il = cip.getMotif(ils, new Point(x, y));
		if (il == null)
			return;

		CellInfo ci = il.getCellInfo();
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
		CellInfo ci;
		for (SegmentedParticle il : ils) {
			ci = il.getCellInfo();
			if (ci.getIdtype() == null)
				continue;
			label = ci.getIdtype().getLabel();
			x = (int) ((il.getCellInfo().getX0() - x0) * magnification);
			y = (int) ((il.getCellInfo().getY0() - y0) * magnification);
			g2.setColor(new Color(ci.getIdtype().getColor()));
			g2.drawString(label, x, y);
		}
	}

	public void resetClassification() {
		for (SegmentedParticle il : ils)
			il.getCellInfo().setType(null);
		repaint();
	}

	

}
