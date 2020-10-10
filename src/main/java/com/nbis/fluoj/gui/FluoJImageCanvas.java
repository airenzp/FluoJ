package com.nbis.fluoj.gui;

import ij.IJ;
import ij.gui.ImageCanvas;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;
import com.nbis.fluoj.classifier.SegmentedParticle;

public class FluoJImageCanvas extends ImageCanvas {
	
	protected FluoJImageWindow iw;
	protected FluoJJFrame frame;
	protected FluoJImageProcessor cip;
	
	

	public FluoJImageCanvas(FluoJJFrame parent) throws InvalidOperationOnResourceException {
		super(parent.getCImageProcess().getPerimetersImg());
		this.frame = parent;
		this.cip = parent.getCImageProcess();
	}
	
	public FluoJImageCanvas(FluoJJFrame parent, FluoJImageProcessor cip) throws InvalidOperationOnResourceException {
		super(cip.getPerimetersImg());
		this.frame = parent;
		this.cip = cip;
		
	}
	



	public void displayImage() throws InvalidOperationOnResourceException {

		iw = new FluoJImageWindow(frame, this);
		iw.maximize();
		iw.setBounds(0, 0, iw.getWidth(), iw.getHeight());
	}

	public void close() {
		if (iw != null && !iw.isClosed())
			iw.close();
	}

	public void mousePressed(MouseEvent e) {
		if (IJ.getInstance() != null && !IJ.getToolName().equals("FluoJ"))
			super.mousePressed(e);
		int x = super.offScreenX(e.getX());
		int y = super.offScreenY(e.getY());
		SegmentedParticle particle = null;
		try {
			particle = frame.getCImageProcess().getParticle(cip.getParticles(), new Point(x, y));
                        System.out.println(particle);
		
		if(particle != null && e.isControlDown())
			JOptionPane.showMessageDialog(this, particle, "Particle Data", JOptionPane.INFORMATION_MESSAGE, particle.getIcon(200));
		} 
		catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage());
		}
	}

	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		if (IJ.getInstance() != null && !IJ.getToolName().equals("FluoJ"))
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	

	

}
