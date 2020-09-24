/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nbis.fluoj.classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;

import com.nbis.fluoj.persistence.*;

/**
 * CellClassifier provides classification functions. It is mainly used by
 * {@link Classifier} along with {@link classifier.ClassifierDB} to provide user
 * with classification and persistence API
 * 
 * @author Airen
 * 
 */
public class CClassifier
{

	ClassifierDB cdb;
	private int features;

	public CClassifier(ClassifierDB cdb)
	{
		this.cdb = cdb;
		features = 0;
		for (Samplefeature sf : cdb.getSample().getSamplefeatureList())
			if (sf.getUseonclassification() == 1)
				features++;
	}

	public static void main(String[] args)
	{
		EntityManager em = ConfigurationDB.getEM();
		Classifier c = ConfigurationDB.getInstance().getPersistentClassifier(1, em);
		List<Scell> scells;
		Scellfeature ssf;

		em.getTransaction().begin();
		try
		{
			scells = c.getScells(em);
			for (Scell ss : scells)
			{
				for (Scellfeature ssf2 : ss.getScellfeatureList())
					if (ssf2.getFeature().getIdfeature() == 20)
					{
						ssf2.setValue(Math.pow(ssf2.getValue(), 2));
						em.merge(ssf2);
					}
			}

			em.getTransaction().commit();
			em.close();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	public void classifyScellsById(List<Integer> ids, EntityManager em)
	{
		Integer winner, id;
		em.getTransaction().begin();
		try
		{
			for (int k = 0; k < ids.size(); k++)
			{
				em.flush();
				em.clear();
				id = ids.get(k);
				winner = classifyScell(id, em);
				cdb.getUpdateWinnerQuery(id, winner, em).executeUpdate();
			}
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			em.getTransaction().rollback();
		}
	}

	public void classifyScells(List<Scell> scells, EntityManager em)
	{
		Scell s;
		Integer winner;
		em.getTransaction().begin();
		try
		{
			for (int k = 0; k < scells.size(); k++)
			{
				s = scells.get(k);
				winner = classifyScell(s.getIdscell(), em);
				if (winner != null)
					cdb.getUpdateWinnerQuery(s.getIdscell(), winner, em).executeUpdate();
			}
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			em.getTransaction().rollback();
		}
	}

	public void classifyScells(EntityManager em)
	{
		em.getTransaction().begin();
		try
		{
			em.createQuery("update Scell ss set ss.type1 = null where ss.session.idsession = :idsession")
					.setParameter("idsession", cdb.session.getIdsession()).executeUpdate();
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			em.getTransaction().rollback();
		}
		classifyScellsOnData(cdb.getClassificationData(em), em);
	}

	public void classifyScellsOnData(List<Object[]> data, EntityManager em)
	{
		if (data.size() == 0)
			return;
		Object[] item = null;
		int current = -1, next = -1;
		Integer idtype, winner = null;
		boolean end = false;
		Scell ss;
		Type st;
		List<CellTypeProbability> probs = new ArrayList<CellTypeProbability>();
		double prob;
		em.getTransaction().begin();
		try
		{
			for (int k = 0; k <= data.size(); k++)
			{
				if (k == data.size())
					end = true;
				else
				{
					item = data.get(k);
					next = (Integer) item[0];
					end = (k != 0 && next != current);
				}
				if (end)
				{
					winner = getWinner(probs);
					ss = (Scell) em.find(Scell.class, current);
					st = (winner != null) ? (Type) em.find(Type.class, winner) : null;
					ss.setType1(st);
					em.merge(ss);
					// scdb.getUpdateWinnerQuery(current, winner,
					// em).executeUpdate();
					if (k == data.size())
						break;
					probs.clear();
				}

				current = next;
				idtype = (Integer) item[1];
				prob = (Double) item[2];
				probs.add(new CellTypeProbability(idtype, prob));
			}
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}
	}

	public Integer classifyScell(int idscell, EntityManager em)
	{
		Integer winner = getWinner(idscell, em);
		return winner;
	}

	public void classifyScellsOnImage(int idimage, EntityManager em)
	{
		classifyScellsOnData(cdb.getClassificationData(idimage, em), em);
	}

	Integer getWinner(List<CellTypeProbability> probs)
	{
		Integer winner = null;
		double bestprob = 0, prob;
		CellTypeProbability stprob;

		bestprob = 0;
		for (int i = 0; i < probs.size(); i++)
		{
			stprob = probs.get(i);
			prob = probs.get(i).probability;

			if (prob > bestprob)
			{
				bestprob = prob;
				winner = stprob.idtype;
			}
		}
		if (similarProbs(probs, bestprob))
			return null;

		return winner;
	}

	public boolean similarProbs(List<CellTypeProbability> probs, double bestprob)
	{
		
//		int limit = Math.round(0.075f * features);
//		for(CellTypeProbability prob: probs)
//			if(prob.probability != bestprob && Math.abs(Math.getExponent((prob.probability/bestprob))) <= limit)
//				return true;
		return false;
	}

	public Integer getWinner(int idscell, EntityManager em)
	{
		try
		{
			return getWinner(cdb.getProbs(idscell, em));

		}
		catch (Exception e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

}
