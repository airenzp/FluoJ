/*
� * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nbis.fluoj.classifier;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.classifier.CellTypeProbability;
import com.nbis.fluoj.persistence.SampleImage;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.Scell;
import com.nbis.fluoj.persistence.ScellFeature;
import com.nbis.fluoj.persistence.ScellFeaturePK;
import com.nbis.fluoj.persistence.Session;
import com.nbis.fluoj.persistence.Type;
/**
 * {@link classifier.ClassifierDB} provides specific functions for session data
 * management.
 * 
 * @author Airen
 * 
 */
public class ClassifierDB extends DB
{

	public final Sample sample;
	protected Session session;

	private String probColumnNQuery = "exp(sum(log(ftp.probability))) as prob";// equivalent
																				// to
																				// multiplication
																				// of
																				// ftp.probability
																				// values

	private String probXRestrictionNQuery = "floor((ssf.value - f.min) / ((f.max - f.min)/ ?)) = ftp.x";// allows																									// value

	private String probFromNQuery = "scell_feature ssf inner join scell ss on ssf.idscell = ss.idscell inner join session s on ss.idsession = s.idsession inner join type t on t.idsample = s.idsample inner join sample_feature f on ssf.idfeature  = f.idfeature and f.idsample = s.idsample inner join probability ftp on f.idfeature = ftp.idfeature and t.idtype = ftp.idtype";

	private String probFeatureRestrictionNQuery = "count(f.idfeature) = (select count(*) from sample_feature sf where sf.idsample = ? and active = true )";

	private String probNQuery = String
			.format("select ssf.idscell, ftp.idtype, %s from %s where %s and ss.idsession = ?  group by ssf.idscell, ftp.idtype having %s order by idscell", probColumnNQuery, probFromNQuery, probXRestrictionNQuery, probFeatureRestrictionNQuery);

	private String probOnImageNQuery = String
			.format("select ssf.idscell, ftp.idtype, %s from %s where %s and ss.idsession = ? and ss.idimage = ? group by ssf.idscell, ftp.idtype having %s order by idscell", probColumnNQuery, probFromNQuery, probXRestrictionNQuery, probFeatureRestrictionNQuery);

	private String probOnScellNQuery = String
			.format("select ftp.idtype, %s from %s where %s and ssf.idscell = ? group by ftp.idtype having %s order by idtype", probColumnNQuery, probFromNQuery, probXRestrictionNQuery, probFeatureRestrictionNQuery);

	private String probOnScellFeatureTypeNQuery = String
			.format("select ftp.probability as prob from %s where %s and ssf.idscell = ? and f.idfeature = ? and ftp.idtype = ?", probFromNQuery, probXRestrictionNQuery);

	private String scellsOnSessionQuery = "select ss from Scell ss where ss.idsession.idsession = :idsession";

	private String scellsOnImageQuery = "select ss from Scell ss where ss.idsession.idsession = :idsession and ss.idimage.idimage = :idimage";

	private String scellsOnImageOriginalNullWinnerNullQuery = "select s from Scell s where s.idtype is null and s.winner is null and s.idsession.idsession = :idsession and s.idimage.idimage = :idimage";

	private String scellsOnImageOriginalNullWinnerAnyQuery = "select s from Scell s where s.idtype is null and s.idsession.idsession = :idsession and s.idimage.idimage = :idimage";

	private String scellsOnImageOriginalAnyWinnerNullQuery = "select s from Scell s where s.winner is null and s.idsession.idsession = :idsession and s.idimage.idimage = :idimage";

	private String scellsOnImageOriginalNullWinnerQuery = "select s from Scell s where s.idtype is null and s.winner.idtype = :winner and s.idsession.idsession = :idsession and s.idimage.idimage = :idimage";

	private String scellsOnImageOriginalWinnerNullQuery = "select s from Scell s where s.winner is null and s.idtype.idtype = :original and s.idsession.idsession = :idsession and s.idimage.idimage = :idimage";

	public ClassifierDB(Sample sample, String name, Date date, EntityManager em)
	{
		this.sample = sample;
		try
		{
			removeSessionDataBeforeDate(ConfigurationDB.limitDateForData(), em);
			createSession(name, date, em);
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public ClassifierDB(Sample sample, EntityManager em)
	{
		this(sample, null, new Date(), em);
	}

	public ClassifierDB(int idsession) throws InvalidOperationOnResourceException
	{

		EntityManager em = ConfigurationDB.getEM();
		session = em.find(Session.class, idsession);
		if (session == null)
			throw new InvalidOperationOnResourceException("No such session:" + idsession);
		this.sample = session.getIdsample();
		removeSessionDataBeforeDate(ConfigurationDB.limitDateForData());
		em.close();
	}

	public ClassifierDB(Session session)
	{

		if (session == null)
			throw new IllegalArgumentException("No such session:");
		this.sample = session.getIdsample();
		this.session = session;
		removeSessionDataBeforeDate(ConfigurationDB.limitDateForData());
	}

	public Sample getSample()
	{
		return sample;
	}

	// ClassifierSession /////////////////////////////////////////
	public void createSession(String name, Date date, EntityManager em)
	{
		em.getTransaction().begin();
		try
		{

			session = new Session();
			session.setDate(date);
			session.setIdsample(sample);
			session.setName(name);
			em.persist(session);
			em.flush();// take this changes to database now, session is updated
						// with generated id
			em.refresh(session);
			sample.setIdsession(session);
			// em.merge(sample);
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}
	}

	public List<Scell> getScells()
	{
		EntityManager em = ConfigurationDB.getEM();
		List<Scell> scells = getScells(em);
		em.close();
		return scells;
	}

	public List<Scell> getScells(EntityManager em)
	{
		try
		{
			return (List<Scell>) em.createQuery(scellsOnSessionQuery).setParameter("idsession", session.getIdsession()).getResultList();

		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public List<Scell> getScellsOnImage(int idimage, EntityManager em)
	{
		try
		{
			return (List<Scell>) em.createQuery(scellsOnImageQuery).setParameter("idimage", idimage)
					.setParameter("idsession", session.getIdsession()).getResultList();

		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public List<Scell> getScellsOnImage(int idimage, Short original, Short winner, EntityManager em)
	{
		try
		{
			if (original == null && winner == null)
				return em.createQuery(scellsOnImageOriginalNullWinnerNullQuery).setParameter("idsession", session.getIdsession())
						.setParameter("idimage", idimage).getResultList();
			if (original == null)
			{
				if (winner == 0)
					return em.createQuery(scellsOnImageOriginalNullWinnerAnyQuery).setParameter("idsession", session.getIdsession())
							.setParameter("idimage", idimage).getResultList();
				return em.createQuery(scellsOnImageOriginalNullWinnerQuery).setParameter("idsession", session.getIdsession())
						.setParameter("idimage", idimage).setParameter("winner", winner).getResultList();
			}
			if (winner == null)
			{
				if (original == 0)
					return em.createQuery(scellsOnImageOriginalAnyWinnerNullQuery).setParameter("idsession", session.getIdsession())
							.setParameter("idimage", idimage).getResultList();
				return em.createQuery(scellsOnImageOriginalWinnerNullQuery).setParameter("idsession", session.getIdsession())
						.setParameter("idimage", idimage).setParameter("original", original).getResultList();
			}
			if (original == 0 && winner == 0)
				return getScellsOnImage(idimage, em);
			if (original == 0)
				return em
						.createQuery("select s from Scell s where s.winner.idtype = :winner and s.idsession.idsession = :idsession  and s.idimage.idimage = :idimage")
						.setParameter("idsession", session.getIdsession()).setParameter("winner", winner).setParameter("idimage", idimage)
						.getResultList();
			if (winner == 0)
				return em
						.createQuery("select s from Scell s where s.idtype.idtype = :original and s.idsession.idsession = :idsession  and s.idimage.idimage = :idimage")
						.setParameter("idsession", session.getIdsession()).setParameter("original", original).setParameter("idimage", idimage)
						.getResultList();

			return em
					.createQuery("select s from Scell s where s.idtype.idtype = :original and s.winner.idtype = :winner and s.idsession.idsession = :idsession  and s.idimage.idimage = :idimage")
					.setParameter("idsession", session.getIdsession()).setParameter("original", original).setParameter("winner", winner)
					.setParameter("idimage", idimage).getResultList();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public List<Integer> getScellIds()
	{
		try
		{
			EntityManager em = getEM();
			return (List<Integer>) em.createQuery("select ss.idscell from Scell ss where ss.idsession.idsession = :idsession")
					.setParameter("idsession", session.getIdsession()).getResultList();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public Query getUpdateWinnerQuery(int idscell, Short winner, EntityManager em)
	{
		return em.createNativeQuery("update scell set winner = ? , date = current_timestamp() where idscell = ?").setParameter(1, winner)
				.setParameter(2, idscell);
	}

	public Scell getScell(int idscell)
	{
		try
		{
			EntityManager em = getEM();
			List<Scell> list = em.createNamedQuery("Scell.findByIdscell").setParameter("idscell", idscell).getResultList();
			if (list.isEmpty())
				return null;
			return list.get(0);
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public List<Object[]> getClassificationData(EntityManager em)
	{
		try
		{
			// System.out.println(probNQuery);
			if (session != null)
				return em.createNativeQuery(probNQuery).setParameter(1, CellProcessor.marks).setParameter(2, session.getIdsession().intValue())
						.setParameter(3, sample.getIdsample()).getResultList();

		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
		return null;
	}

	public List<Object[]> getClassificationData(int idimage, EntityManager em)
	{
		try
		{
			List<Object[]> result = em.createNativeQuery(probOnImageNQuery).setParameter(1, CellProcessor.marks)
					.setParameter(2, session.getIdsession().intValue()).setParameter(3, idimage).setParameter(4, session.getIdsample().getIdsample())
					.getResultList();
			return result;

		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public Integer getScellTotal()
	{
		try
		{
			EntityManager em = getEM();
			Object result = em.createQuery("select COUNT(s) from Scell s where s.idsession.idsession = :idsession")
					.setParameter("idsession", session.getIdsession()).getSingleResult();
			return ((Long) result).intValue();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public Integer getScellTotalForClass(Short idtype, EntityManager em)
	{
		try
		{
			Object result;
			if (idtype == null)
				result = em.createQuery("select COUNT(s) from Scell s where s.idtype is null and s.idsession.idsession = :idsession")
						.setParameter("idsession", session.getIdsession()).getSingleResult();
			else
				result = em.createQuery("select COUNT(s) from Scell s where s.idtype.idtype = :idtype and s.idsession.idsession = :idsession")
						.setParameter("idsession", session.getIdsession()).setParameter("idtype", idtype).getSingleResult();
			return ((Long) result).intValue();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public Integer getScellTotalForWinner(Short idtype, EntityManager em)
	{
		try
		{
			Object result;
			if (idtype == null)
				result = em.createQuery("select COUNT(s) from Scell s where s.winner is null and s.idsession.idsession = :idsession")
						.setParameter("idsession", session.getIdsession()).getSingleResult();
			else
				result = em.createQuery("select COUNT(s) from Scell s where s.winner.idtype = :idtype and s.idsession.idsession = :idsession")
						.setParameter("idsession", session.getIdsession()).setParameter("idtype", idtype).getSingleResult();
			return ((Long) result).intValue();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public int getScellTotal(Short original, Short winner)
	{
		try
		{
			EntityManager em = getEM();
			if (original == null && winner == null)
				return ((Long) em
						.createQuery("select COUNT(s) from Scell s where s.idtype is null and s.winner is null and s.idsession.idsession = :idsession")
						.setParameter("idsession", session.getIdsession()).getSingleResult()).intValue();
			if (original == null)
				return ((Long) em
						.createQuery("select COUNT(s) from Scell s where s.idtype is null and s.winner.idtype = :winner and s.idsession.idsession = :idsession")
						.setParameter("idsession", session.getIdsession()).setParameter("winner", winner).getSingleResult()).intValue();
			if (winner == null)
				return ((Long) em
						.createQuery("select COUNT(s) from Scell s where s.winner is null and s.idtype.idtype = :original and s.idsession.idsession = :idsession")
						.setParameter("idsession", session.getIdsession()).setParameter("original", original).getSingleResult()).intValue();
			return ((Long) em
					.createQuery("select COUNT(s) from Scell s where s.idtype.idtype = :original and s.winner.idtype = :winner and s.idsession.idsession = :idsession")
					.setParameter("idsession", session.getIdsession()).setParameter("original", original).setParameter("winner", winner)
					.getSingleResult()).intValue();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public float getTotalErrorPercent()
	{
		try
		{
			EntityManager em = getEM();
			Long total = ((Long) em.createQuery("select COUNT(s) from Scell s where s.winner is not null and s.idtype is not null").getSingleResult())
					.longValue();
			Long error = ((Long) em
					.createQuery("select COUNT(s) from Scell s where s.winner.idtype <> s.idtype.idtype and s.winner is not null and s.idtype is not null and s.idsession.idsession = :idsession")
					.setParameter("idsession", session.getIdsession()).getSingleResult()).longValue();
			if (total == 0)
				return 0;
			return ((float) error / total) * 100;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public float getWinnerPercent(short idtype, EntityManager em)
	{
		try
		{
			int total = getScellTotalForClass(idtype, em);
			if (total == 0)
				return 100;
			return ((float) getScellTotal(idtype, idtype) / total) * 100;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public float getWinnerErrorPercent(Short idtype, EntityManager em)
	{
		try
		{
			List<Type> types = sample.getTypeList();
			float error = 0;
			for (int i = 0; i < types.size(); i++)
			{
				if (types.get(i).getIdtype() == idtype)
					continue;
				error += getScellTotal(idtype, types.get(i).getIdtype());
			}
			int total = getScellTotalForClass(idtype, em);
			if (total == 0)
				return 0;
			error = error / total * 100;
			return error;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public void persistScells(List<Scell> scells, EntityManager em)
	{
		List<ScellFeature> ssfs;
		Scell ss;
		try
		{
			em.getTransaction().begin();
			for (int i = 0; i < scells.size(); i++)
			{
				ss = scells.get(i);
				ss.setIdsession(session);
				ss.setDate(new Date());
				ssfs = ss.getScellFeatureList();
				ss.setScellFeatureList(null);
				if (ss.getIdtype() != null)
					ss.setIdtype(em.find(Type.class, ss.getIdtype().getIdtype()));// to
																				// avoid
				// persistence try to insert value
				em.persist(ss);
				em.merge(session);// could be loaded by another EntityManager

				// Separation of persists because of api trouble with identity
				// generation and child references
				em.flush();
				for (ScellFeature ssf : ssfs)
				{
					ssf.setScellFeaturePK(new ScellFeaturePK(ss.getIdscell(), ssf.getFeature().getIdfeature()));
					em.persist(ssf);
				}
				ss.setScellFeatureList(ssfs);
			}
			em.getTransaction().commit();
			// em.flush();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}
	}

	public List<CellTypeProbability> getProbs(int idscell, EntityManager em)
	{
		try
		{
			// int features_total = getFeatureTotal(em);
			Query q = em.createNativeQuery(probOnScellNQuery).setParameter(1, CellProcessor.marks).setParameter(2, idscell)
					.setParameter(3, session.getIdsample().getIdsample());
			List result = q.getResultList();
			Object[] item;
			short idtype;
			double prob;
			List<CellTypeProbability> probs = new ArrayList<CellTypeProbability>();
			for (int i = 0; i < result.size(); i++)
			{
				item = (Object[]) result.get(i);
				idtype = ((Integer) item[0]).shortValue();
				prob = (Double) item[1];
				probs.add(new CellTypeProbability(idtype, prob));
			}
			return probs;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public String getId()
	{
		String id = "";
		if (session.getName() != null)
			id += session.getName() + "-";
		id += session.getDate().toString();
		return id;

	}

	public List<SampleImage> getImages()
	{
		try
		{
			EntityManager em = getEM();
			return em
					.createQuery("select distinct(s.idimage)  from Scell s where s.idsession.idsession = :idsession order by s.idimage.name")
					.setParameter("idsession", session.getIdsession()).getResultList();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public int getImagesCount(EntityManager em)
	{
		try
		{
			return ((Long) em
					.createQuery("select count(ir.idimage) from SampleImage ir where ir.idimage = some (select s.idimage.idimage from Scell s where s.idsession.idsession = :idsession)")
					.setParameter("idsession", session.getIdsession()).getSingleResult()).intValue();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public List<Integer> getIdsessions(EntityManager em)
	{
		try
		{
			return em.createQuery("select distinct(s.session)  from Scell s").getResultList();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public void mergeScells(List<Scell> scells, EntityManager em)
	{
		try
		{

			em.getTransaction().begin();
			for (int i = 0; i < scells.size(); i++)
				em.merge(scells.get(i));
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}
	}

	public void deleteScell(int idscell, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			em.remove(em.find(Scell.class, idscell));
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}
	}

	public void deleteScells(List<Integer> idscells, EntityManager em)
	{
		try
		{
			if (idscells.size() == 0)
				return;
			Scell s;

			em.getTransaction().begin();
			for (int i = 0; i < idscells.size(); i++)
				em.createNativeQuery("delete from scell where idscell = ?").setParameter(1, idscells.get(i).intValue()).executeUpdate();
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}
	}

	public List<FeatureStatistics> getStatisticsForSession()
	{
		EntityManager em = getEM();
		try
		{
			List result = em
					.createNativeQuery("select ss.winner, ssf.idfeature, count(*), sum(ssf.value), sum(power(ssf.value, 2)) from ScellFeature ssf inner join scell sc on ssf.idscell = sc.idscell where sc.idsession = ? group by sc.winner, ssf.idfeature order by sc.winner, ssf.idfeature")
					.setParameter(1, session.getIdsession()).getResultList();
			Object[] item;
			FeatureStatistics scellstat;
			Double avg, deviation;
			Integer idfeature, idtype;
			Long count;
			ArrayList<FeatureStatistics> statistics = new ArrayList<FeatureStatistics>();
			for (int i = 0; i < result.size(); i++)
			{
				item = (Object[]) result.get(i);
				idtype = (Integer) item[0];
				if (idtype == null)
					idtype = 11;
				idfeature = (Integer) item[1];
				count = (Long) item[2];
				if (count == 0)
					return null;
				avg = ((Double) item[3] / count);
				deviation = Math.sqrt(((Double) item[4] / count) - Math.pow(avg, 2));
				scellstat = new FeatureStatistics(idtype, idfeature, avg, deviation);
				statistics.add(scellstat);
				// System.out.println(String.format("%s %.2f, %.2f", idfeature,
				// avg, deviation));
			}
			return statistics;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public Double getMaxValueOnSession(int idfeature)
	{
		EntityManager em = getEM();
		try
		{
			Object result = em
					.createQuery("select max(ssf.value) from ScellFeature ssf where ssf.scell.session.idsession = :idsession and ssf.idfeature.idfeature = :idfeature")
					.setParameter("idsession", session.getIdsession()).setParameter("idfeature", idfeature).getSingleResult();
			if (result != null)
				return (Double) result;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
		return null;
	}

	public Double getMinValueOnSession(int idfeature)
	{
		EntityManager em = getEM();
		try
		{
			Object result = em
					.createQuery("select min(ssf.value) from ScellFeature ssf where ssf.scell.session.idsession = :idsession and ssf.idfeature.idfeature = :idfeature")
					.setParameter("idsession", session.getIdsession()).setParameter("idfeature", idfeature).getSingleResult();
			if (result != null)
				return (Double) result;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
		return null;
	}

	public void exportAllData(File file, boolean original, boolean winner, boolean includeheader, EntityManager em)
	{
		exportAllData(file, false, sample.getSampleFeatureList(), original, winner, true, em);
	}

	public void exportAllData(File file, boolean scale, List<SampleFeature> sfeatures, boolean manual, boolean automatic, boolean includeheader, EntityManager em)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			List<Scell> scells = getScells();

			if (includeheader)
			{
				String column;
				String columns = "";
				columns += String.format("%50s", "ID");
				for (int j = 0; j < sfeatures.size(); j++)
				{
					
					column = sfeatures.get(j).getFeature().getName();
					if (column.length() >= 50)
						column = column.substring(0, 46) + "...";
					columns += String.format("%50s", column);
				}
				if (includeheader && manual)
					columns += String.format("%50s", "MANUAL");
				if (includeheader && automatic)
					columns += String.format("%50s", "AUTOMATIC");
				writer.append(columns + "\n");
			}
			String line = "";
			Scell s;
			ScellFeature ssf;
			int scalemax = 3000;
			Feature f;
			double value;
			List<Double> mins = new ArrayList<Double>();
			List<Double> maxs = new ArrayList<Double>();
			if (scale)
			{
				for (int i = 0; i < sfeatures.size(); i++)
				{
					
					f = sfeatures.get(i).getFeature();
					mins.add(getMinValueOnSession(f.getIdfeature()));
					maxs.add(getMaxValueOnSession(f.getIdfeature()));
				}
			}

			for (int i = 0; i < scells.size(); i++)
			{
				line = "";
				s = scells.get(i);
				if (includeheader)
					line += String.format("%50d", i + 1);

				for (int j = 0; j < sfeatures.size(); j++)
					for (int k = 0; k < s.getScellFeatureList().size(); k++)
					{
						ssf = s.getScellFeatureList().get(k);
						value = ssf.getValue();

						if (sfeatures.get(j).getFeature().getIdfeature().equals(ssf.getFeature().getIdfeature()))
						{
							f = ssf.getFeature();
							if (scale)
								value = (value - mins.get(i)) / (maxs.get(i) - mins.get(i)) * scalemax;
							line += String.format("%50.2f", value).replace(",", ".");
						}
					}

				if (manual)
					line += String.format("%50s", s.getIdtype());
				if (automatic)
					line += String.format("%50s", s.getWinner());
				writer.append(line);
				writer.append(String.format("\n"));
			}
			writer.close();
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public void exportToOpenDX(String directoryPath, boolean original, boolean winner, EntityManager em)
	{
		if (sample.getSampleFeatureList().size() < 4)
			throw new IllegalArgumentException("Must have a minimum of 4 sample features");
		try
		{
			File file = new File(directoryPath);
			file.mkdir();// Creates if not exists
			String filepath = String.format("%s\\%s.general", directoryPath, session.getIdsession());
			file = new File(filepath);
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String content = String
					.format("file = %s.dat\npoints = %s\nformat = ascii\ninterleaving = field\nfield = locations, radius, species\nstructure = 3-vector, scalar, scalar\ntype = float, float, int", session
							.getIdsession(), getScellTotal());
			writer.append(content);
			writer.close();
			List<SampleFeature> choosen = new ArrayList<SampleFeature>();
			int id;
			for (int i = 0; i < 4; i++)
				choosen.add(sample.getSampleFeatureList().get(i));
			filepath = String.format("%s//%s.dat", directoryPath, session.getIdsession());
			file = new File(filepath);
			file.createNewFile();
			exportAllData(file, true, choosen, original, winner, false, em);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e.getMessage());
		}

	}

	public Double getProbability(int idscell, int idfeature, int idtype, EntityManager em)
	{
		Query q = em.createNativeQuery(probOnScellFeatureTypeNQuery).setParameter(1, CellProcessor.marks).setParameter(2, idscell)
				.setParameter(3, idfeature).setParameter(4, idtype);
		List result = q.getResultList();
		if (result.size() == 0)
			return null;
		return (Double) result.get(0);
	}

	public SimpleEntry<Double, Double>[] getHistogram(SampleFeature sfeature, EntityManager em)
	{
		try
		{
			List<Scell> scells = getScells(em);
			SimpleEntry<Double, Double>[] histogram = new SimpleEntry[CellProcessor.marks];
			Double min = ConfigurationDB.getMinForFeatureOnSampleSession(sfeature.getSample().getIdsession().getIdsession(), sfeature.getFeature().getIdfeature(), em);
			if (min == null)
				return histogram;
			Double max = ConfigurationDB.getMaxForFeatureOnSampleSession(sfeature.getSample().getIdsession().getIdsession(), sfeature.getFeature().getIdfeature(), em);

			double scale = (max - min) / CellProcessor.marks;

			Integer x;
			Scell s;
			ScellFeature scf;
			double value;
			SimpleEntry<Double, Double> bin;
			for (int i = 0; i < scells.size(); i++)
			{
				s = scells.get(i);
				for (int k = 0; k < s.getScellFeatureList().size(); k++)
				{
					scf = s.getScellFeatureList().get(k);
					if (scf.getFeature().equals(sfeature.getFeature()))
					{
						value = scf.getValue();
						x = (int)((value - min)/scale);
						if(x == CellProcessor.marks)
							x = x - 1;
						bin = histogram[x];
						if(bin == null)
						{
							bin = new SimpleEntry<Double, Double>(x * scale, 1.);
							histogram[x] = bin;
						}
						bin.setValue(bin.getValue() + 1);
						
					}
				}
			}
			for (int i = 0; i < histogram.length; i++)
			{
				bin = histogram[i];
				if(bin != null)
				{
					value = bin.getValue()/scells.size();
					bin.setValue(value);
				}
			}
			return histogram;
		}
		catch (Exception e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

}
