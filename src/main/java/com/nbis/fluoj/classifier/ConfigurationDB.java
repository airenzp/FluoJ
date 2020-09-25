package com.nbis.fluoj.classifier;

import com.nbis.fluoj.gui.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Samplecorefeature;
import com.nbis.fluoj.persistence.Samplefeature;
import com.nbis.fluoj.persistence.Samplefilter;
import com.nbis.fluoj.persistence.Separation;
import com.nbis.fluoj.persistence.Session;
import com.nbis.fluoj.persistence.Type;

/**
 * Manages Configuration data for Classification, such as {@link Sample},
 * persistent {@link Session Sessions} and {@link Classifier Classifiers} and so
 * on. Extends of {@link DB}.
 * 
 * @author airen
 * 
 */

public class ConfigurationDB extends DB
{

	private static ConfigurationDB cdb;
	private static Session dsession;
	private static Type dtype;
	public static final Session anysession = ConfigurationDB.getNoneSession();
	public static final Type nonetype = ConfigurationDB.getNoneType();
	public static final String fluojdir = "plugins" + File.separator + "FluoJ" + File.separator;
	public static final String imagesdir = fluojdir + "images";
	public static final String resourcesdir = fluojdir + "resources";
	public static final String propertiesfile = fluojdir + "FluoJ.properties";
	private static Properties props;
	private static String unrelatedFeaturesQuery = "Select f from Feature f where f.idfeature not in "
			+ "(select sf.feature.idfeature from Samplefeature sf where sf.sample.idsample = :idsample)";
	private static String unrelatedCoreFeaturesQuery = "Select f from Feature f where f.idfeature not in "
			+ "(select sf.feature.idfeature from Samplecorefeature sf where sf.sample.idsample = :idsample)";

	public static void main(String[] args)
	{
		EntityManager em = ConfigurationDB.getEM();
		ConfigurationDB.getInstance().getSamples(em);
		em.close();
	}

	public static ConfigurationDB getInstance()
	{
		if (cdb == null)
			cdb = new ConfigurationDB();
		return cdb;
	}

	private boolean debug = false;

	/**
	 * Obtains persistent {@link Session} associated to idsample.
	 * 
	 * @param idsample
	 *            {@link Sample} id
	 * @param em
	 *            {@link EntityManager} to be used
	 * @return Obtains persistent {@link Session} associated to idsample.
	 */
	public Session getPersistentSession(int idsample, EntityManager em)
	{
		Sample s = getSample(idsample, em);
		return s.getSession();
	}

	public void setPersistentSession()
	{

	}

	/**
	 * Obtains {@link Sample} associated to idsample.
	 * 
	 * @param idsample
	 *            {@link Sample} id
	 * @param em
	 *            {@link EntityManager} to be used
	 * @return Obtains {@link Sample} associated to idsample.
	 */
	public Sample getSample(int idsample, EntityManager em)
	{
		try
		{
			Sample s = em.find(Sample.class, idsample);
			if (s == null)
				throw new InvalidOperationOnResourceException("No such sample:" + idsample);
			return s;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	/**
	 * Provides {@link Sample} list registered
	 * 
	 * @param em
	 *            {@link EntityManager} to be used
	 * @return Provides {@link Sample} list registered
	 */
	public List<Sample> getSamples(EntityManager em)
	{
		try
		{
			return em.createQuery("select s from Sample s order by s.idsample desc").getResultList();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	/**
	 * Obtains random {@link Sample}
	 * 
	 * @return Obtains random {@link Sample}
	 */
	public Sample getRandomSample()
	{
		EntityManager em = getEM();
		List<Sample> samples = getSamples(em);
		int index = (int) Math.floor(Math.random() * samples.size());
		Sample s = samples.get(index);
		em.close();
		return s;
	}

	public Sample getRandomSample(List<Sample> samples)
	{
		if (samples.isEmpty())
			return null;
		int index = (int) Math.floor(Math.random() * samples.size());
		Sample s = samples.get(index);
		return s;
	}

	public Classifier getRandomClassifier(List<Sample> samples)
	{
		return getPersistentClassifier(getInstance().getRandomSample(samples));
	}

	public Classifier getRandomClassifier()
	{
		return getPersistentClassifier(getInstance().getRandomSample());
	}

	public Sample mergeSample(Sample sample, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			sample = em.merge(sample);
			em.getTransaction().commit();
			return sample;
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}
	}

	public Classifier getPersistentClassifier(Sample sample)
	{
		if (sample.getSession() == null)
			return null;
		return new Classifier(sample.getSession());
	}

	public List<Session> getPersistentSessions(EntityManager em)
	{
		try
		{
			return em.createQuery("Select sa.session from Sample sa where sa.session is not null").getResultList();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());

		}
	}

	public Classifier getPersistentClassifier(int idsample, EntityManager em)
	{
		Sample s = getSample(idsample, em);
		return new Classifier(s.getSession());
	}

	public void persist(Sample s, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			if (s.getSamplefilterList() != null)
			{
				List<Samplefilter> filters = s.getSamplefilterList();
				s.setSamplefilterList(null);

				em.persist(s);

				em.flush();
				if (!filters.isEmpty())
					for (Samplefilter sf : filters)
					{
						sf.getSamplefilterPK().setIdsample(s.getIdsample());
						em.persist(sf);

					}
				s.setSamplefilterList(filters);
			}
			else
				em.persist(s);
			em.getTransaction().commit();
			// System.out.println(s.getIdsample());
			// filter as
			// mandatory
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

	}

	public static Session getNoneSession()
	{
		if (dsession == null)
		{
			dsession = new Session();
			dsession.setName("None");
		}
		return dsession;
	}

	public static Type getNoneType()
	{
		if (dtype == null)
		{
			dtype = new Type(null, "None");
		}
		return dtype;
	}

	public void remove(Sample s, EntityManager em)
	{

		try
		{
			// execute update must be called inside a transaction. Previous
			// versions using remove where not working and this one does
			int idsample = s.getIdsample().intValue();
			em.getTransaction().begin();
			em.createNativeQuery("update SAMPLE set PSESSION = NULL where SAMPLE.IDSAMPLE = ?").setParameter(1, idsample).executeUpdate();

			em.createNativeQuery("delete from SESSION where SESSION.IDSAMPLE = ?").setParameter(1, idsample).executeUpdate();
			new File(s.getImageresource().getPath()).delete();
			em.createNativeQuery("delete from SAMPLE where SAMPLE.IDSAMPLE = ?").setParameter(1, idsample).executeUpdate();
			em.getTransaction().commit();

		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

	}


	public void remove(Type t, EntityManager em)
	{
		if (t.getSample().getType() != null && t.getSample().getType().equals(t))
			throw new IllegalArgumentException(Constants.getReferencedFieldMsg("default type", t.getName()));
		try
		{
			em.getTransaction().begin();
			em.remove(em.find(Type.class, t.getIdtype()));
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

	}

	public static Double getMinForFeatureOnSample(int idsample, int idfeature, EntityManager em)
	{
		Object result = em
				.createQuery("select min(cf.value) from Cellfeature cf where cf.cell.sample.idsample = :idsample and cf.feature.idfeature = :idfeature")
				.setParameter("idfeature", idfeature).setParameter("idsample", idsample).getSingleResult();
		if (result != null)
			return (Double) result;
		return null;
	}

	public static Double getMaxForFeatureOnSample(int idsample, int idfeature, EntityManager em)
	{
		Object result = em
				.createQuery("select max(cf.value) from Cellfeature cf where cf.cell.sample.idsample = :idsample and cf.feature.idfeature = :idfeature")
				.setParameter("idfeature", idfeature).setParameter("idsample", idsample).getSingleResult();
		if (result != null)
			return (Double) result;
		return null;
	}

	
	public static void persist(Samplefeature sf, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			em.persist(sf);
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

	}

	public static void persist(List<Samplefilter> sfs, EntityManager em)
	{
		try
		{
			if (sfs == null || sfs.isEmpty())
				return;
			int idsample = sfs.get(0).getSamplefilterPK().getIdsample();
			em.getTransaction().begin();
			em.createQuery("delete from Samplefilter sf where sf.sample.idsample = :idsample").setParameter("idsample", idsample);
			for (Samplefilter sf : sfs)
				em.persist(sf);

			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

	}

	

	public static void removeSamplefeature(Samplefeature sf, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			em.remove(sf);
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

	}

	public List<Sample> getReviewSamples(EntityManager em)
	{
		List<Sample> samples = new ArrayList<Sample>();
		for (Sample sample : getSamples(em))
			if (!isEmpty(sample) && !sample.getTypeList().isEmpty())
				samples.add(sample);
		return samples;
	}

	// public void persistSampleFilterOnDB(Integer idsample, Integer idfilter,
	// EntityManager em) {
	// try {
	//
	// // haciendo merge del sample no guardaba la relación, no existe la
	// // relacion como entidad, asi que hice
	// // una query nativa para resolver.
	// em.getTransaction().begin();
	// em.createNativeQuery("INSERT INTO SAMPLEFILTER(IDSAMPLE, IDFILTER) VALUES(?, ?)").setParameter(1,
	// idsample.intValue())
	// .setParameter(2, idfilter.intValue()).executeUpdate();
	// em.refresh(em.find(Sample.class, idsample));
	// em.getTransaction().commit();
	// } catch (Exception e) {
	// em.getTransaction().rollback();
	// throw new CDBException(e.getMessage());
	// }
	//
	// }

	public static void removeSampleFilterOnDB(Samplefilter sf, EntityManager em)
	{
		try
		{

			em.getTransaction().begin();
			em.remove(sf);
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

	}

	/**
	 * Loads attributes defined on Classifier.properties. Properties include:
	 * session_hours. session_hours is used to obtain {@link Classifier}
	 * {@link #limitDateForData()}
	 * 
	 * @return Properties with attributes specified on Classifier.properties
	 */
	public static Properties getProps()
	{
		String file_name = ConfigurationDB.propertiesfile;
		String session_hours = "session_hours";

		try
		{
			try
			{
				if (props == null)
				{
					props = new Properties();
					props.load(new FileInputStream(file_name));
				}
			}
			catch (FileNotFoundException e)
			{
				new File(file_name).createNewFile();
				props.load(new FileInputStream(file_name));

			}
			if (props.get(session_hours) == null)
				props.put(session_hours, "6");

			props.store(new FileOutputStream(propertiesfile), null);
			return props;
		}
		catch (IOException e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public static void saveProps()
	{

		try
		{
			getProps().store(new FileOutputStream(propertiesfile), null);
		}
		catch (IOException e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Provides date limit to keep session data. Date is calculated substracting
	 * as many hours as defined on session_hours attribute configured on
	 * Classifier.properties
	 * 
	 * @return date limit to keep session data
	 */
	public static Date limitDateForData()
	{
		Calendar c = Calendar.getInstance();
		String session_hours = getProps().getProperty("session_hours");
		if (session_hours == null)
			c.add(Calendar.HOUR_OF_DAY, -6);
		else
			c.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(session_hours));
		Date limit = c.getTime();
		// System.out.println(limit);
		return limit;
	}

	public List<Sample> getTrainedSamples(EntityManager em)
	{
		return em
				.createQuery("Select sa from Sample sa where sa.session is not null and sa.session.idsession in (select sc.session.idsession from Scell sc)")
				.getResultList();
	}

	public static List<Separation> getSeparations(EntityManager em)
	{
		try
		{
			return em.createQuery("select s from Separation s").getResultList();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public static boolean isEmpty(Sample sample)
	{
		if(sample == null)
			return true;
		Sample msample;
		try
		{
			EntityManager em = ConfigurationDB.getEM();
			em.getTransaction().begin();
			msample = em.find(Sample.class, sample.getIdsample());
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new CDBException(e.getMessage());
		}
		if (!msample.getCellList().isEmpty())
			return false;
		for (Session s : msample.getSessionList())
			if (s.getScellList() != null && !s.getScellList().isEmpty())
				return false;
		return true;
	}

	public static Object merge(Object o, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			o = em.merge(o);
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
		return o;
	}
	

	public Type merge(Type type, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			type = em.merge(type);
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
		return type;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;

	}

	public boolean isDebug()
	{
		return debug;

	}

	public static void remove(Object o, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			em.remove(o);
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

		
	}
	
	public static void persist(Object o, EntityManager em)
	{
		try
		{
			em.getTransaction().begin();
			em.persist(o);
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}

	}

	
	public static List<Feature> getAvailableFeatures(Sample sample, EntityManager em)
	{
		try
		{
			List<Feature> features = em.createQuery(unrelatedFeaturesQuery).setParameter("idsample", sample.getIdsample()).getResultList();

			if (sample.getRoismax() != 0)
				return features;
			//choose not mixed features
			List<Feature> result = new ArrayList<Feature>();
			for (Feature f : features)
				if (!f.isMixed())
					result.add(f);
				

			return result;

		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}

	}

	public static List<Feature> getAvailableCorefeatures(Sample sample, EntityManager em)
	{
		try
		{
			List<Feature> features = em.createQuery(unrelatedCoreFeaturesQuery).setParameter("idsample", sample.getIdsample()).getResultList();

			//choose not mixed features
			List<Feature> result = new ArrayList<Feature>();
			for (Feature f : features)
				if (!f.isMixed())
					result.add(f);
			return result;
		}
		catch (Exception e)
		{
			throw new CDBException(e.getMessage());
		}
	}

	public static Double getMinForFeatureOnSampleSession(Integer idsession, Integer idfeature, EntityManager em)
	{
		Object result = em
				.createQuery("select min(scf.value) from Scellfeature scf where scf.scell.session.idsession = :idsession and scf.feature.idfeature = :idfeature")
				.setParameter("idfeature", idfeature).setParameter("idsession", idsession).getSingleResult();
		if (result != null)
			return (Double) result;
		return null;
	}
	
	public static Double getMaxForFeatureOnSampleSession(Integer idsession, Integer idfeature, EntityManager em)
	{
		Object result = em
				.createQuery("select max(scf.value) from Scellfeature scf where scf.scell.session.idsession = :idsession and scf.feature.idfeature = :idfeature")
				.setParameter("idfeature", idfeature).setParameter("idsession", idsession).getSingleResult();
		if (result != null)
			return (Double) result;
		return null;
	}

}