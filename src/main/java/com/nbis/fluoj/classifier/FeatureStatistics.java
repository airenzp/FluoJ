package com.nbis.fluoj.classifier;

import java.util.List;

public class FeatureStatistics {
	
	public int idtype;
	public int idfeature;
	public double avg;
	public double deviation;
	
	public FeatureStatistics(int idtype, int idfeature, double avg, double deviation)
	{
		this.idtype = idtype;
		this.idfeature = idfeature;
		this.avg = avg;
		this.deviation = deviation;
	}
	
	public static FeatureStatistics getStatistics(int idtype, int idfeature, List<FeatureStatistics> list)
	{
		FeatureStatistics cellstat;
		for(int i = 0; i < list.size(); i ++)
		{
			cellstat = list.get(i);
			if(cellstat.idtype == idtype && cellstat.idfeature == cellstat.idfeature)
				return cellstat;
		}
		return null;
	}
	

}
