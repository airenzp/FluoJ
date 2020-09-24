package com.nbis.fluoj.classifier;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.Samplefeature;
import com.nbis.fluoj.persistence.Type;

/**
 * Class used by {@link classifier.SegmentedParticle} to retrieve/store
 * {@link persistence.Scellfeature Scellfeatures} obtained.
 * 
 * @author Airen
 * 
 */
// mapping database features
public class CellInfo
{

	List<ParticleStatistic> roistats;
	ParticleStatistic ils;
	int roisarea = 0, roix, roiy, roiscount;
	double roisbrightness = 0;
	double roispixelsmean = 0;
	double roispixelsdev = 0;
	double roismaxarea;
	private double cdistance;
	private Type type;
	private SegmentedParticle particle;
	private double roispolarity;

	public CellInfo(SegmentedParticle il, boolean typed)
	{
		if (il.getPoints().isEmpty())
			throw new IllegalArgumentException("No points provided");
		if ((il.getROIS() == null || il.getROIS().isEmpty()) && il.getSample().getRoismax() > 0)
			throw new IllegalArgumentException("No inner labels provided");
		this.particle = il;
		this.ils = new ParticleStatistic(il);
		initROIsFeatures();

		if (typed)
			this.type = il.getSample().getType();
	}

	void initROIsFeatures()
	{
		if (particle.getSample().getRoismax() > 0)
		{
			roismaxarea = Double.NEGATIVE_INFINITY;
			roiscount = particle.getROIS().size();
			roistats = new ArrayList<ParticleStatistic>();
			ParticleStatistic roistat = null;
			double iwcenterx = 0, iwcentery = 0;
			// doing summary on properties of inner particles
			for (SegmentedParticle roi : particle.getROIS())
			{
				roistat = new ParticleStatistic(roi);
				roistats.add(roistat);
				roisarea += roistat.area;
				if(roismaxarea < roistat.area)
					roismaxarea = roistat.area;
				for (ParticlePoint p : roi.getPoints())
				{
					roix += roistat.x;
					roiy += roistat.y;
					roisbrightness += Math.pow(p.getPixel(), 2);
					roispixelsmean += p.getPixel();
					iwcenterx += p.getPixel() * p.x;
					iwcentery += p.getPixel() * p.y;
				}
			}
			iwcenterx = iwcenterx/roispixelsmean;
			iwcentery = iwcentery/roispixelsmean;
			roispixelsmean = roispixelsmean/roisarea;
			roix = roix / roisarea;
			roiy = roiy / roisarea;
			roispixelsdev = Math.sqrt(roisbrightness/roisarea - Math.pow(roispixelsmean, 2));
			roisbrightness = Math.sqrt(roisbrightness/roisarea);
			cdistance = Math.sqrt(Math.pow(roix - ils.x, 2) + Math.pow(roiy - ils.y, 2));
			
			roispolarity = Math.sqrt(Math.pow(getX() - iwcenterx, 2) + Math.pow(getY() - iwcentery, 2));
		}

	}

	public double getAreaRate()
	{
		return (float) roisarea / ils.area * 100;
	}

	public double getEllipticity()
	{
		return ils.ellipticity;
	}

	public double getAsymmetry()
	{
		return ils.asymmetry;
	}

	public int getROISCount()
	{
		return roiscount;
	}

	public int getArea()
	{
		return ils.area;
	}

	public double getVc()
	{
		return ils.pixelsdev / ils.pixelsmean;
	}

	/**
	 * 
	 * @return Average radius for particle. Measured using distances from core
	 *         center
	 */
	public double getRadius()
	{
		return ils.radius;
	}

	/**
	 * 
	 * @return Average radius for particle. Measured using distances from core
	 *         center
	 */
	public double getNonCircularity()
	{
		return ils.noncircularity;
	}

	/**
	 * 
	 * @return Average point from core points
	 */
	public Point getCenter()
	{
		return new Point(ils.x, ils.y);
	}

	/**
	 * @return Distance between core center and particle center
	 */
	public double getCentersDistance()
	{
		return cdistance;
	}

	/**
	 * 
	 * @return Particle brightness. Obtained from square root applied to average
	 *         of pixels color squared
	 */
	public double getBrightness()
	{
		return ils.brightness;
	}

	public double getPBrightness()
	{
		return ils.pbrightness;
	}

	
	/**
	 * 
	 * @return Particle perimeter points
	 */
	public List<ParticlePoint> getPpoints()
	{
		return ils.ppoints;
	}

	public List<ParticlePoint> getIPPoints()
	{
		List<ParticlePoint> ippoints = new ArrayList<ParticlePoint>();
		for (ParticleStatistic ils : roistats)
			ippoints.addAll(ils.ppoints);
		return ippoints;
	}

	/**
	 * 
	 * @return True if particle is on Image border. False otherwise.
	 */
	public boolean isOnborder()
	{
		return particle.onBorder();
	}

	/**
	 * 
	 * @return x value for particle position. Used by
	 *         {@link gui.FluoJTrainingCanvas} and {@link gui.OutputImageCanvas}
	 *         to display {@link persistence.Type} id label.
	 */
	public int getX()
	{
		return ils.x;
	}

	/**
	 * 
	 * @return y value for particle position. Used by
	 *         {@link gui.FluoJTrainingCanvas} and {@link gui.OutputImageCanvas}
	 *         to display {@link persistence.Type} id label.
	 */
	public int getY()
	{
		return ils.y;
	}

	/**
	 * 
	 * @return Inner points quantity
	 */
	public int getROISAreaSum()
	{
		return roisarea;
	}

	public double getPixelsMean()
	{
		return ils.pixelsmean;
	}

	/**
	 * 
	 * @return Deviation on halo brightness average
	 */
	public double getPixelsMeanDev()
	{
		return ils.pixelsdev;
	}

	/**
	 * 
	 * @return Biggest distance between particle points.
	 */
	public double getDiameter()
	{
		return ils.diameter;
	}

	/**
	 * 
	 * @return Particle type
	 */
	public Type getType()
	{
		return type;
	}

	/**
	 * 
	 * @param type
	 *            Sets particle type
	 */

	public void setType(Type type)
	{
		this.type = type;
	}

	public double getValue(int idfeature)
	{
		Double value = ils.getValue(idfeature);
		if (value == null)
		{
			if (idfeature == EFeature.ROISAREASUM.id())
				value = (double) getROISAreaSum();
			else if (idfeature == EFeature.ROISPIXELSDEV.id())
				value = getROISPixelsDev();
			else if (idfeature == EFeature.ROISPIXELSMEAN.id())
				value = getROISPixelsMean();
			else if (idfeature == EFeature.CENTERSDISTANCE.id())
				value = getCentersDistance();
			else if (idfeature == EFeature.ROISCOUNT.id())
				value = (double) getROISCount();
			else if (idfeature == EFeature.ROISPOLARITY.id())
				value = getROISPolarity();
			else if (idfeature == EFeature.AREARATE.id())
				value = getAreaRate();
			else if (idfeature == EFeature.ROISMAXAREA.id())
				value = roismaxarea;
		}
		return value;
	}

	private Double getROISPixelsMean()
	{
		return roispixelsmean;
	}

	private Double getROISPixelsDev()
	{
		return roispixelsdev;
	}

	private double getROISPolarity()
	{
		return roispolarity;
	}

	public double getPPixelsDev()
	{
		return ils.ppixelsdev;
	}

	
	public Integer getX0()
	{
		return particle.getPoints().get(0).x;
	}

	public Integer getY0()
	{
		return particle.getPoints().get(0).y;
	}

}
