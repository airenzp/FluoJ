package com.nbis.fluoj.classifier;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;


/**
 * Runnable defined to register database {@link EntityManagerFactory factories} for each {@link classifier.Sample} and close them
 * before application shutdown.
 * @author Airen
 *
 */
class Cleaner implements Runnable {
	
	private List<EntityManagerFactory> emfs;
	
	Cleaner()
	{
		this.emfs = new ArrayList<EntityManagerFactory>();
	}
	
	public void addEMF(EntityManagerFactory emf)
	{
		emfs.add(emf);
	}

	@Override
	public void run() {
		for(int i = 0; i < emfs.size(); i ++)
			if(emfs.get(i).isOpen())
				try
				{
						emfs.get(i).close();
				}
				catch(Exception e){};

	}

}
