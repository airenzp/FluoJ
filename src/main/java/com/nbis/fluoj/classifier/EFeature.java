package com.nbis.fluoj.classifier;

import java.util.Arrays;


public enum EFeature
{
	NONCIRCULARITY(20),
	AREA(26), 
	PIXELSVARIATIONCOEFF(27),
	DIAMETER(12),
	BRIGHTNESS(24),
	RADIUS(21),
	PIXELSMEAN(3),
	PIXELSDEV(5),
	PBRIGHTNESS(30),
	PPIXELSMEANDEV(31),
	PERIMETER(32),
	ASYMMETRY(33), 
	ELLIPTICITY(34), 
	PIXELSDISTCURTOSIS(35), 
	PPIXELSMEAN(40),
	POLARITY(41), 
	PPOLARITY(45),
	ROISPIXELSDEV(4),
	ROISAREASUM(11),
	ROISPIXELSMEAN(13), 
	CENTERSDISTANCE(25),
	ROISCOUNT(28),
	ROISPOLARITY(29),
	ROISMAXAREA(48),
	AREARATE(46);
	
	public static EFeature[] mixed = new EFeature[]{ROISAREASUM, ROISPIXELSDEV, ROISPIXELSMEAN, ROISPOLARITY, CENTERSDISTANCE, ROISCOUNT, AREARATE, ROISMAXAREA};
	
	private final int id;
	
	EFeature(int id)
	{
		this.id = id;
	}
	
	public int id()
	{
		return id;
	}
	
	
	public static boolean isMixed(EFeature feature)
	{
		return Arrays.asList(mixed).contains(feature);
	}
   
	public static boolean isMixed(int idfeature)
	{
		for(EFeature e: mixed)
			if(e.id() == idfeature)
				return true;
		return false;
	}
   
	
}


