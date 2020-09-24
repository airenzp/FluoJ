package com.nbis.fluoj.classifier;

import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class ParticleStatistic
{
	List<ParticlePoint> points;
	int area;
	double radius;
	double diameter;
	double noncircularity;
	int x, y;
	double pixelsmean;
	double pixelsdev;
	double brightness;
	double pbrightness;
	double ppixelsdev;
	private ImageProcessor ip;
	ArrayList<ParticlePoint> ppoints;
	double asymmetry;
	double ellipticity;
	double pdistcurtosis;
	double ppixelsmean;
	double polarity;
	double ppolarity;
	
	
	ParticleStatistic(SegmentedParticle particle)
	{
		this.points = particle.getPoints();
		this.ip = particle.getGrayIP();
		if (points.isEmpty())
			throw new IllegalArgumentException("No points provided");
		area = points.size();
		ppoints = new ArrayList<ParticlePoint>();
		double distance;
		double psquares = 0, ppsquares = 0, pxmean = 0, pymean = 0; 
		double iwcenterx = 0, iwcentery = 0, piwcenterx = 0, piwcentery = 0;
		pixelsmean = 0;
		
		for (ParticlePoint p : points) {
			x += p.x;
			y += p.y;
			iwcenterx += p.getPixel() * p.x;
			iwcentery += p.getPixel() * p.y;
			pixelsmean += p.getPixel();
			psquares += Math.pow(p.getPixel(), 2);
			if (isPerimeter(p, particle, ip.getWidth(), ip.getHeight()) && !ppoints.contains(p))
			{
					ppoints.add(p);
					ppixelsmean += p.getPixel();
					ppsquares += Math.pow(p.getPixel(), 2);
					pxmean += p.x;
					pymean += p.y;
					piwcenterx += p.getPixel() * p.x;
					piwcentery += p.getPixel() * p.y;
					
			}
		
		}
		for (ParticlePoint p : ppoints) 
			for (ParticlePoint p2 : ppoints){
			if (p.equals(p2))
				continue;
			distance = Math.sqrt(Math.pow(p.x - p2.x, 2) + Math.pow(p.y - p2.y, 2));
			if (distance > diameter)
				diameter = distance;
		}
		x = x/area;//xcenter
		y = y/area;//ycenter
		iwcenterx = iwcenterx/pixelsmean;
		iwcentery = iwcentery/pixelsmean;
		piwcenterx = piwcenterx/ppixelsmean;
		piwcentery = piwcentery/ppixelsmean;
		polarity = getDistance(iwcenterx, iwcentery, x, y);
		ppolarity = getDistance(piwcenterx, piwcentery, x, y);
		brightness = Math.sqrt(psquares / area);
		pbrightness = Math.sqrt(ppsquares / ppoints.size());
		pixelsmean = pixelsmean/area;
		ppixelsmean = ppixelsmean/ppoints.size();
		pixelsdev = Math.sqrt(psquares / area - Math.pow(pixelsmean, 2));
		ppixelsdev = Math.sqrt(ppsquares / ppoints.size() - Math.pow(ppixelsmean, 2));
		if (Double.isNaN(pixelsdev))
			pixelsdev = 0;//too small 
		
		
		double radius2 = 0, pointsdev = 0, minradius = Double.POSITIVE_INFINITY, maxradius = Double.NEGATIVE_INFINITY;
		pxmean = pxmean/ppoints.size();
		pymean = pymean/ppoints.size();
		for (ParticlePoint p : ppoints) {
			distance = getDistance(p.x, p.y, x, y);
			if(distance < minradius && minradius > 0)
				minradius = distance;
			if(distance > maxradius)
				maxradius = distance;
			radius += distance;
			radius2 += Math.pow(distance, 2);
			pointsdev += Math.pow(Point.distance(p.x, p.y, pxmean, pymean), 2);
			asymmetry += Math.pow(Point.distance(p.x, p.y, pxmean, pymean), 3);
			
			
		}
		pointsdev = Math.sqrt(pointsdev/ppoints.size());
		asymmetry = asymmetry/ppoints.size();
		asymmetry = asymmetry/Math.pow(pointsdev, 3);
		asymmetry = asymmetry;
		radius = radius / ppoints.size();
		noncircularity = Math.sqrt(radius2/ppoints.size() - Math.pow(radius, 2));
		ellipticity = (maxradius - minradius)/maxradius;
		for (ParticlePoint p : points) {
			pdistcurtosis += Math.pow(p.getPixel() - pixelsmean, 4);
		}
		
		pdistcurtosis = pdistcurtosis/points.size();
		pdistcurtosis = pdistcurtosis/Math.pow(pixelsdev, 4) - 3;
		if(Double.isNaN(pdistcurtosis))
			pdistcurtosis = 0;
		
	
	}
	
	public static double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	public double getAsymmetry(List<Integer> list)
	{
		java.util.Collections.sort(list);
		int median = list.get(list.size()/2);
		int left = 0, right = 0;
		for(Integer value: list)
			if(value < median)
				left ++;
			else
				right++;
		return (float)Math.abs(left - right)/list.size();
	}
	
//	public static boolean isPerimeter(Point point, List<? extends Point> points, int width, int height) {
//		int count = 0;
//		for (int y = point.y - 1; y <= point.y + 1; y++)
//			for (int x = point.x - 1; x <= point.x + 1; x++)
//				if (!(x == point.x && y == point.y))
//					if (FluoJImageProcessor.validPoint(x, y, width, height)
//							&& points.contains(new Point(x, y)))
//						count++;
//		return count < 7;
//
//	}
	
	public static boolean isPerimeter(Point point, SegmentedParticle particle, int width, int height) {
		int count = 0;
		for (int y = point.y - 1; y <= point.y + 1; y++)
			for (int x = point.x - 1; x <= point.x + 1; x++)
				if (!(x == point.x && y == point.y))
				{
					if (FluoJImageProcessor.validPoint(x, y, width, height)
							&& particle.getSegmentedIP().getPixel(x,  y) == particle.getLabel())
						count++;
				}
		
		return count > 0 && count < 7;

	}
	
	public Double getValue(int idfeature) {
		Double value = null;
		if (idfeature == EFeature.PIXELSMEAN.id())
			value = pixelsmean;
		else if (idfeature == EFeature.PIXELSDEV.id())
			value = pixelsdev;
		else if (idfeature == EFeature.NONCIRCULARITY.id())
			value = noncircularity;
		else if (idfeature == EFeature.RADIUS.id())
			value = radius;
		else if (idfeature == EFeature.DIAMETER.id())
			value = diameter;
		else if (idfeature == EFeature.BRIGHTNESS.id())
			value = brightness;
		else if (idfeature == EFeature.AREA.id())
			value = (double)area;
		else if (idfeature == EFeature.PIXELSVARIATIONCOEFF.id())
			value = pixelsdev/pixelsmean;
		else if (idfeature == EFeature.PBRIGHTNESS.id())
			value = pbrightness;
		else if (idfeature == EFeature.PPIXELSMEANDEV.id())
			value = ppixelsdev;
		else if (idfeature == EFeature.PERIMETER.id())
			value = (double)points.size();
		else if (idfeature == EFeature.ASYMMETRY.id())
			value = asymmetry;
		else if (idfeature == EFeature.ELLIPTICITY.id())
			value = ellipticity;
		else if (idfeature == EFeature.PIXELSDISTCURTOSIS.id())
			value = pdistcurtosis;
		else if (idfeature == EFeature.PPIXELSMEAN.id())
			value = ppixelsmean;
		else if (idfeature == EFeature.POLARITY.id())
			value = polarity;
		else if (idfeature == EFeature.PPOLARITY.id())
			value = ppolarity;
		
		
		return value;
	}
	
	
	
	
	
	
}
