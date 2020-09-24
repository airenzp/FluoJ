package com.nbis.fluoj.classifier;



/**
 * Class used to represent {@link persistence.Type} probability
 * @author Airen
 *
 */
public class CellTypeProbability {
	
	public int idtype = -1;
	public double probability = -1;
	
	public CellTypeProbability(int idtype, double probability)
	{
		this.idtype = idtype;
		this.probability = probability;
	}
	
	public CellTypeProbability(int idtype)
	{
		this.idtype = idtype;
	}
	
	@Override
	public boolean equals(Object o)
	{
		 if (!(o instanceof CellTypeProbability)) {
	            return false;
	     }
		 CellTypeProbability stprob= (CellTypeProbability)o;
		 if(stprob.idtype == idtype && (stprob.probability == probability || probability == -1))
			 return true;
		 return false;
	}
	
	

}
