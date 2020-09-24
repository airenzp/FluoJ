package com.nbis.fluoj.classifier;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.nbis.fluoj.persistence.*;

/**
 * Represents {@link Scell} Image data. Once points, core points, parent and
 * inner labels are registered, neighbor points can be added if necessary, Scell
 * {@link persistence.Feature Features} can be obtained, and so on.
 * 
 * @author Airen
 * 
 */
public class SegmentedParticle implements Comparable<SegmentedParticle>
{

	private int label;
	private List<ParticlePoint> points;

	private List<SegmentedParticle> rois;
	private Scell scell;
	private CellInfo info;
	private Sample sample;
	private boolean typed;
	private ImageProcessor grayip;
	private ImageProcessor segmentedip;

	public Sample getSample()
	{
		return sample;
	}

	public boolean isTyped()
	{
		return typed;
	}

	public void setSample(Sample sample)
	{
		this.sample = sample;
	}

	public Scell getScell()
	{
		return scell;
	}

	public void setScell(Scell scell)
	{
		this.scell = scell;
	}

	public SegmentedParticle(int label, List<ParticlePoint> points, boolean typed, Sample sample, ImageProcessor grayip, ImageProcessor segmentedip)
	{
		super();
		this.label = label;
		this.points = points;
		this.typed = typed;
		this.sample = sample;
		this.grayip = grayip;
		this.segmentedip = segmentedip;
		this.rois = new ArrayList<SegmentedParticle>();
	}

	public SegmentedParticle(int label, boolean typed, Sample sample, ImageProcessor grayip, ImageProcessor segmentedip)
	{
		this(label, new ArrayList<ParticlePoint>(), typed, sample, grayip, segmentedip);

	}

	public boolean isOwner(Point p)
	{

		if (points.contains(p))
			return true;
		return false;

	}

	public int getLabel()
	{
		return label;
	}

	public void setLabel(int label)
	{
		this.label = label;
	}

	public List<ParticlePoint> getPoints()
	{
		return points;
	}

	public List<SegmentedParticle> getROIS()
	{
		return rois;
	}

	public boolean onBorder()
	{
		for (ParticlePoint p : points)
			if (FluoJImageProcessor.onBorder(p.x, p.y, grayip.getWidth(), grayip.getHeight()))
				return true;
		return false;
	}

	public void addPoint(ParticlePoint point)
	{
		points.add(point);
	}

	public void addInnerIL(SegmentedParticle il)
	{
		if (rois == null)
			rois = new ArrayList<SegmentedParticle>();
		rois.add(il);
	}

	public boolean containsInnerLabel(int label)
	{
		if (rois == null)
			return false;
		for (SegmentedParticle il : rois)
			if (il.getLabel() == label)
				return true;
		return false;
	}

	public void filterROIS(List<Samplecorefeature> scfs)
	{
		List<SegmentedParticle> validrois = new ArrayList<SegmentedParticle>();
		if (rois == null || rois.isEmpty())
			return;
		ParticleStatistic ps;
		Double value;
		boolean valid;
		if (scfs != null && !scfs.isEmpty())
		{
			for (SegmentedParticle roi : rois)
			{
				valid = true;
				ps = new ParticleStatistic(roi);
				for (Samplecorefeature scf : scfs)
				{
					value = ps.getValue(scf.getFeature().getIdfeature());
					if (value < scf.getMin() || value > scf.getMax())
					{
						valid = false;
						break;
					}
				}
				if(valid)
					validrois.add(roi);
			}
			rois = validrois;
		}
	}

	public boolean isValid(List<Samplefeature> sfs)
	{
		if (sample.getRoismax() > 0)
		{
			if (rois == null || rois.isEmpty())
				return false;// label must have inner particles
			else if (rois.size() > sample.getRoismax())
				return false;
		}
		else if (rois != null || rois.isEmpty())
			return false;
		Double value;
		if (sfs != null)
			for (Samplefeature sf : sfs)
			{
				CellInfo ci = getCellInfo(typed);
				value = ci.getValue(sf.getFeature().getIdfeature());
				if (value < sf.getMin() || value > sf.getMax())
					return false;
			}

		return true;
	}

	public Point getCenterPoint(List<ParticlePoint> points)
	{
		if (points.size() == 0)
			return null;
		int x = 0, y = 0;
		for (ParticlePoint p : points)
		{
			x += p.x;
			y += p.y;
		}
		return new Point(x / points.size(), y / points.size());
	}

	public CellInfo getCellInfo()
	{
		if (info == null)
			info = new CellInfo(this, false);
		return info;
	}

	public CellInfo getCellInfo(boolean typed)
	{
		if (info == null)
			info = new CellInfo(this, typed);
		return info;
	}

	@Override
	public String toString()
	{
		CellInfo ci = getCellInfo(false);
		String str = String.format("%30s: %8s\n", "x", ci.getX0());
		str += String.format("%30s: %8s\n", "y", ci.getY0());
		str += String.format("%30s: %8s\n", "label", label);
		if (sample.getSamplefeatureList() != null)
			for (Samplefeature sf : sample.getSamplefeatureList())
				str += String.format("%30s: %8.2f\n", sf.getFeature().getFeature(), ci.getValue(sf.getFeature().getIdfeature()));
		return str;
	}

	@Override
	public int compareTo(SegmentedParticle il)
	{
		Point p2 = il.getCellInfo().getCenter();
		Point p1 = getCellInfo().getCenter();
		if (p2.y > p1.y)
			return -1;
		if (p2.y == p1.y)
		{
			if (p2.x > p1.x)
				return -1;
			if (p2.x == p1.x)
				return 0;
			return 1;
		}
		return 1;
	}

	public ImagePlus getImagePlus()
	{
		int radius = Math.max(45, (int) getCellInfo().getDiameter());
		Point p = getCellInfo().getCenter();
		int x0 = Math.max(p.x - radius, 0);
		int y0 = Math.max(p.y - radius, 0);
		Rectangle r = new Rectangle(x0, y0, radius * 2, radius * 2);
		grayip.setRoi(r);
		ImageProcessor processor = grayip.crop().convertToRGB();
		int[] color = new int[] { 255, 255, 0 };
		int[] corecolor = new int[] { 0, 255, 0 };
		for (ParticlePoint point : getCellInfo().getPpoints())
			processor.putPixel(point.x - x0, point.y - y0, color);
		if (sample.getRoismax() > 0)
			for (ParticlePoint point : getCellInfo().getIPPoints())
				processor.putPixel(point.x - x0, point.y - y0, corecolor);
		ImagePlus imp = new ImagePlus("", processor);
		return imp;
	}

	public Icon getIcon(int size)
	{

		Icon icon;
		Image img = getImagePlus().getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		return icon;
	}

	public Icon getIcon()
	{

		Icon icon;
		Image img = getImagePlus().getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		return icon;
	}

	public static ParticleStatistic getStatistic(SegmentedParticle il)
	{
		return new ParticleStatistic(il);
	}

	public ImageProcessor getGrayIP()
	{
		return grayip;
	}

	public ImageProcessor getSegmentedIP()
	{
		return segmentedip;
	}

}
