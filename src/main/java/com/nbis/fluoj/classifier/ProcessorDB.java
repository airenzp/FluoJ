/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nbis.fluoj.classifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import com.nbis.fluoj.persistence.Cell;
import com.nbis.fluoj.persistence.Cellfeature;
import com.nbis.fluoj.persistence.CellfeaturePK;
import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.Ftprobability;
import com.nbis.fluoj.persistence.Imageresource;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Samplefeature;
import com.nbis.fluoj.persistence.Scell;
import com.nbis.fluoj.persistence.Scellfeature;
import com.nbis.fluoj.persistence.Session;
import com.nbis.fluoj.persistence.Type;

/**
 * {@link classifier.ClassifierDB} provides specific functions for training data
 * management.
 * 
 * @author Airen
 * 
 */
public class ProcessorDB extends DB {

	public Sample sample;

	public ProcessorDB(Sample sample) {
		this.sample = sample;
	}

	public static void main(String[] args) {
	}

	public List<Cell> getScells(EntityManager em) {

		try {
			return em.createNamedQuery("Cell.findAll").getResultList();

		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	public int getCellTotal(EntityManager em) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery cq = cb.createQuery(Long.class);
			cq.select(cb.count(cq.from(Cell.class)));
			Long total = (Long) em.createQuery(cq).getSingleResult();
			return total.intValue();
		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	public Integer getCellTotal(int idtype, EntityManager em) {
		try {
			Object result = em
					.createQuery(
							"select COUNT(s) from Cell s where s.type.idtype = :idtype")
					.setParameter("idstype", idtype).getSingleResult();
			return ((Long) result).intValue();
		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	public void importTrainingData(Session session, EntityManager em) {
		em.getTransaction().begin();
		try {
			em.createQuery("delete from Cell c where c.sample.idsample = :idsample").setParameter("idsample", session.getSample().getIdsample()).executeUpdate();
			if (session == null) {
				em.getTransaction().commit();
				return;
			}
			List<Scell> list = (List<Scell>) em
					.createQuery(
							"select ss from Scell ss where ss.session.idsession = :idsession")
					.setParameter("idsession", session.getIdsession())
					.getResultList();
			Scell sc;
			Cell c;
			Cellfeature cf;
			Scellfeature ssf;
			Iterator<Scellfeature> iter;
			for (int i = 0; i < list.size(); i++) {
				sc = list.get(i);
				if (sc.getType() == null)
					continue;
				c = new Cell();
				c.setDate(new Date());
				c.setImageresource(em.find(Imageresource.class, sc
						.getImageresource().getIdimage()));
				c.setType(em.find(Type.class, sc.getType().getIdtype()));
				c.setXPosition(sc.getXPosition());
				c.setYPosition(sc.getYPosition());
				c.setSample(sample);
				em.persist(c);
				em.flush();
				if (i % 20 == 0)
					em.clear();
				iter = sc.getScellfeatureList().iterator();
				while (iter.hasNext()) {
					ssf = iter.next();
					if(!useOnClassification(ssf.getFeature()))
						continue;
					cf = new Cellfeature();
					cf.setCellfeaturePK(new CellfeaturePK(c.getIdcell(), ssf
							.getFeature().getIdfeature()));
					cf.setValue(ssf.getValue());
					em.persist(cf);
					
				}

			}
			em.getTransaction().commit();
		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			em.getTransaction().rollback();
			throw new IllegalArgumentException(e);
		}
	}

	private boolean useOnClassification(Feature feature) {
		for(Samplefeature sf: sample.getSamplefeatureList())
			if(sf.getFeature().equals(feature) && sf.getUseonclassification() == 1)
				return true;
		
		return false;
	}

	// Featureontypeprobability///////////////////////////////////////////////////

	public List<Ftprobability> getProbabilities(int idtype, int idfeature,
			EntityManager em) {
		try {
			return em
					.createQuery(
							"select ftp from Ftprobability ftp where ftp.type.idtype = :idtype and ftp.feature.idfeature = :idfeature order by ftp.x")
					.setParameter("idtype", idtype)
					.setParameter("idfeature", idfeature).getResultList();

		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	public List<Ftprobability> initializeHistogram(Type type, Feature feature, EntityManager em) {
		try {
			List<Ftprobability> ftps = new ArrayList<Ftprobability>();
			Double min = ConfigurationDB.getMinForFeatureOnSample(
					sample.getIdsample(), feature.getIdfeature(), em);
			if (min == null)
				return ftps;
			Double max = ConfigurationDB.getMaxForFeatureOnSample(
					sample.getIdsample(), feature.getIdfeature(), em);
			String query = "select floor((cf.value - ?)/?) as x, count(*) as freq "
					+ "from cellfeature cf inner join samplefeature sf on cf.idfeature = sf.idfeature inner join cell c on c.idcell = cf.idcell "
					+ "where cf.idfeature = ? and c.class = ? "
					+ "group by x "
					+ "order by x";
			double scale = (max - min) / CellProcessor.marks;
			int idfeature = feature.getIdfeature();
			int idtype = type.getIdtype();
			List result = em.createNativeQuery(query).setParameter(1, min)
					.setParameter(2, scale).setParameter(3, idfeature)
					.setParameter(4, idtype).getResultList();

			Ftprobability ftp;
			Object[] item;
			Integer x, freq;
			// System.out.println(String.format("%s, %s %s %s", type.getType(),
			// feature.getFeature(), feature.getMin(), feature.getMax()));
			for (int k = 0; k < result.size(); k++) {

				item = (Object[]) result.get(k);
				x = ((Double) item[0]).intValue();
				freq = ((Long) item[1]).intValue();
				ftp = new Ftprobability();
				ftp.setFeature(feature);
				ftp.setType(type);
				ftp.setX(x);
				ftp.setFrequence(freq);
				ftp.setProbability(freq.doubleValue());
				ftps.add(ftp);
			}
			return ftps;
		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	public void deleteProbabilities(EntityManager em) {
		try {
			em.getTransaction().begin();
			List<Ftprobability> ftps = em
					.createQuery(
							"select ftp from Ftprobability ftp where ftp.type.sample.idsample = :idsample")
					.setParameter("idsample", sample.getIdsample())
					.getResultList();
			for (Ftprobability ftp : ftps)
				em.remove(ftp);
			em.getTransaction().commit();
		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			em.getTransaction().rollback();
			throw new IllegalArgumentException(e);
		}
	}
	
	
	public List<Object[]>  getHistograms(int idtype, int idfeature, EntityManager em)
	{
		try
		{
			List<Object[]> results = em.createQuery( "select ftp.x, ftp.probability from Ftprobability ftp " +
											"where ftp.type.idtype = :idtype and ftp.feature.idfeature = :idfeature " +
											"order by ftp.x").
											setParameter("idtype", idtype).
											setParameter("idfeature", idfeature).getResultList();
			//System.out.println(results.size());
			return results;
			
				
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
		
	}

}
