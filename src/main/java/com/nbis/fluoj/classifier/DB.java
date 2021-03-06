package com.nbis.fluoj.classifier;

import com.nbis.fluoj.gui.Constants;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JOptionPane;

import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.SampleImage;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Session;
import com.nbis.fluoj.persistence.Type;
import java.util.HashMap;
import org.eclipse.persistence.jpa.PersistenceProvider;

/**
 * DB is general class for classifier database persistence API. Inheriting
 * classes {@link classifier.ClassifierDB} and {@link classifier.ProcessorDB}
 * provide specific functions for session and training management respectively.
 * 
 * @author Airen
 * 
 */
public abstract class DB {

	public static final String pu = "com.nbis_FluoJ2_jar_1.0-SNAPSHOTPU"; //"ClassifierPU";
	private static EntityManagerFactory emf = null;
	private static Cleaner cleaner;

	public static EntityManager getEM() {

		try {
			if (cleaner == null) {
				cleaner = new Cleaner();
				Runtime.getRuntime().addShutdownHook(new Thread(cleaner));
			}
			if (emf == null || !emf.isOpen()) {
                                HashMap properties = new HashMap();
				emf = new PersistenceProvider().createEntityManagerFactory(pu, properties);
				cleaner.addEMF(emf);
			}
			emf.getCache().evictAll();//to force new entity manager to read information from db
			return emf.createEntityManager();
		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			String msg = "No database found or locked";
			JOptionPane.showMessageDialog(null, msg);
			throw new IllegalArgumentException(msg);

		}
	}

	// Feature
	// ///////////////////////////////////////////////////////////////////
	public static List<Feature> getFeatures(EntityManager em) {
		try {
			return (List<Feature>) em.createQuery("select f from Feature f order by f.idfeature").getResultList();

		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public Integer getFeatureTotal(EntityManager em) {
		try {
			return ((Long) em.createQuery("select count(f) from Feature f").getSingleResult()).intValue();
		} catch (Exception e) {
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	// Image////////////////////////////////////////////////

	public SampleImage saveImageResource(ImagePlus img, Sample sample, EntityManager em) {
		if (img == null) throw new IllegalArgumentException(Constants.getEmptyFieldMsg("image"));
		String dir;
		SampleImage ir = null;
		FileSaver saver = new FileSaver(img);
		try {
			em.getTransaction().begin();
			ir = new SampleImage();
                        ir.setIdsample(sample);
			em.persist(ir);
			em.flush();
			ir.setName(ir.getIdimage() + ".tif");
			ir.setDate(new Date());
			dir = ConfigurationDB.imagesdir + File.separator;
			new File(dir).mkdir();// Creates if not exists
			saver.saveAsTiff(dir + ir.getName());
			ir = em.merge(ir);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
		return ir;
	}

	/**
	 * Used to remove session data created before date. Limit date provided is
	 * usually {@link Classifier#limitDateForData()}.
	 * 
	 * @param date
	 * @return persistent {@link Session}
	 */

	public void removeSessionDataBeforeDate(Date date) {
		EntityManager em = getEM();
		removeSessionDataBeforeDate(date, em);
		em.close();
	}

	public void removeSessionDataBeforeDate(Date date, EntityManager em) {
		em.getTransaction().begin();

		try {
			em.createQuery("delete from Session s where (s.date <= :date or s.date is null) and (s.idsample.idsession is null or not (s.idsample.idsession = s))")
					.setParameter("date", date).executeUpdate();
			//takes active sessions images for any sample on Scells
			String noactiveimages =  " and ir.idimage not in (select ss.idimage.idimage from Scell ss where ss.idsession in "
					+ "(Select sa.idsession from Sample sa where sa.idsession is not null) ) ";

			//excludes sample image and cells images
			String norefimages =  " and ir.idimage not in (select c.idimage.idimage from Cell c) " +
									 "and ir.idimage not in (select s.idimage.idimage from Sample s)";

			List<SampleImage> irs = em.createQuery("select ir from SampleImage ir where ir.date <= :date" + noactiveimages
							+  norefimages).setParameter("date", date).getResultList();
			SampleImage ir;
			String file;
			for (int i = 0; i < irs.size(); i++) {
				ir = irs.get(i);
				file = ConfigurationDB.getPath(ir);
				System.out.println(file);
				new File(file).delete();
				em.remove(ir);
			}

			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw new CDBException(e.getMessage());
		}
	}
        
       
}
