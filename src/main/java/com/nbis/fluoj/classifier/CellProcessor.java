package com.nbis.fluoj.classifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import com.nbis.fluoj.persistence.Cell;
import com.nbis.fluoj.persistence.Cellfeature;
import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.Ftprobability;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Samplefeature;
import com.nbis.fluoj.persistence.Type;

/**
 * CProcessor provides training functions. It is mainly used by
 * {@link gui.TrainingJFrame} and {@link gui.ReviewJFrame} to train Classifier.
 * Uses its own persistence API provided through {@link classifier.ProcessorDB}.
 * 
 * @author Airen
 * 
 */
public class CellProcessor {

	public Sample sample;
	private ProcessorDB pdb;
	public static final int marks = 18;

	public CellProcessor(Sample sample) {
		this.sample = sample;
		pdb = new ProcessorDB(sample);
	}

	public static void main(String[] args) {
		try {

			ConfigurationDB cdb = ConfigurationDB.getInstance();
			EntityManager em = ConfigurationDB.getEM();
			Sample sample = cdb.getSample(78, em);
			if (sample == null)
				throw new IllegalArgumentException("no sample provided");
			List<Samplefeature> features = sample.getSamplefeatureList();
			if (features.isEmpty())
				throw new IllegalArgumentException("no features provided");

			for (Samplefeature sf : sample.getSamplefeatureList()) {
				CellProcessor sc = new CellProcessor(sample);
				sc.exportHistograms(sf, ConfigurationDB.fluojdir, em);
			}
		}
		catch (InvalidOperationOnResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void processCells() {
		processCells(pdb.getEM());
	}

	/**
	 * Process {@link Cell} info persisted to generate histograms stored on
	 * {@link Ftprobability}. Statistics obtained are requested by
	 * {@link ClassifierDB} to provide classification data used on
	 * {@link CClassifier}.
	 * 
	 * @param em
	 *            {@link EntityManager} to be used
	 */
	public void processCells(EntityManager em) {
		Feature feature;
		Type type;
		pdb.deleteProbabilities(em);
		List<Samplefeature> sfeatures = sample.getSamplefeatureList();
		List<Type> types = sample.getTypeList();
		List<Ftprobability> ftps;
		for (int i = 0; i < types.size(); i++) {
			type = types.get(i);
			for (int j = 0; j < sfeatures.size(); j++) {
				if (sfeatures.get(j).getUseonclassification() == 0)
					continue;
				feature = sfeatures.get(j).getFeature();
				ftps = pdb.initializeHistogram(type, feature, em);
				// smoothHistogram(ftps);
				try {
					normalizeHistogram(ftps, type);

				}
				catch (IllegalArgumentException e) {
					break;
				}
				em.getTransaction().begin();
				try {
					for (int k = 0; k < ftps.size(); k++) {
						if (k % 20 == 0) {
							em.flush();
							em.clear();
						}
						em.merge(ftps.get(k));
					}
					em.getTransaction().commit();
				}
				catch (Exception e) {
					em.getTransaction().rollback();
					e.printStackTrace();
				}
			}
		}
		// printHistograms(em);
	}

	public void printHistograms(EntityManager em) {

		List<Type> types = sample.getTypeList();
		Type type;
		Feature f;
		for (int i = 0; i < types.size(); i++)
			for (Samplefeature sf : sample.getSamplefeatureList()) {
				type = types.get(i);
				f = sf.getFeature();
				System.out.println(String.format(" %s %s. Min:%s Max:%s", type.getName(), f.getFeature(), sf.getMin(), sf.getMax()));
				if (type.getFtprobabilityList().size() == 0) {
					// System.out.println(String.format("Type %s with no histogram",
					// type.getName()));
					continue;
				}
				List<Ftprobability> probs;
				Ftprobability ftp;
				probs = pdb.getProbabilities(type.getIdtype(), f.getIdfeature(), em);

				for (int k = 0; k < probs.size(); k++) {
					ftp = probs.get(k);
					if (ftp.getFrequence() != 0)
						System.out.println(String.format("On x: %s, freq: %s prob: %s", ftp.getX().floatValue(), ftp.getFrequence(), ftp
								.getProbability()));
				}
			}
	}

	/**
	 * Imports data from current session to {@link Cell} and {@link Cellfeature}
	 * tables usually used before {@link CellProcessor#processCells()}.
	 * 
	 * @param em
	 *            {@link EntityManager} to be used
	 */
	public void importTrainingData(EntityManager em) {
		pdb.importTrainingData(sample.getSession(), em);
	}

	/**
	 * Obtains probabilities from frequences and updates {@link Ftprobability}
	 * 
	 * @param ftps
	 */
	public void normalizeHistogram(List<Ftprobability> ftps, Type type) {
		double sum = 0;
		Ftprobability ftp;
		for (int i = 0; i < ftps.size(); i++) {
			ftp = ftps.get(i);
			sum += ftp.getFrequence();
		}
		if (sum < type.getTrainingmin()) {

			String msg = String.format("Min sample number is %s, provided %s", type.getTrainingmin(), sum);
			IllegalArgumentException e = new IllegalArgumentException(msg);
			//Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}

		for (int i = 0; i < ftps.size(); i++) {
			ftp = ftps.get(i);
			ftp.setProbability(ftp.getProbability() / sum);
		}

	}

	public void removeSessionDataBeforeDate(Date date, EntityManager em) {
		pdb.removeSessionDataBeforeDate(date, em);

	}

	public double[][] getHistograms(Samplefeature sf, String dir, EntityManager em) {
		double[][] histograms = null;
		try {
			if (sf.getUseonclassification() == 0)
				return null;
			String file = String.format("%s/%s-%s.txt", dir, sf.getSample().getName(), sf.getFeature().getFeature());
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			List<Object[]> hist;
			String line = "";
			double[] values;
			int idtype;
			double max = ConfigurationDB.getMaxForFeatureOnSample(sf.getSample().getIdsample(), sf.getFeature().getIdfeature(), em);
			double min = ConfigurationDB.getMinForFeatureOnSample(sf.getSample().getIdsample(), sf.getFeature().getIdfeature(), em);
			double step = (max - min) / (marks + 1);
			histograms = new double[sf.getSample().getTypeList().size() + 1][marks + 1];
			int i = 0;
			for (double x = min + step / 2; x < max; x += step, i++)
				histograms[0][i] = x;
			i = 0;
			for (Type type : sf.getSample().getTypeList()) {
				idtype = type.getIdtype();
				hist = pdb.getHistograms(idtype, sf.getFeature().getIdfeature(), em);// Returns
																						// pairs
																						// key
																						// value
				for (Object[] prob : hist)
					histograms[i][(Integer) prob[0]] = (Double) prob[1];

				i++;
			}
		}
		catch (Exception e) {
			
			throw new IllegalArgumentException(e.getMessage());
		}
		return histograms;
	}

	public void exportHistograms(Samplefeature sf, String dir, EntityManager em) throws InvalidOperationOnResourceException {
		try {
			if (sf.getUseonclassification() == 0)
				return;
			String file = String.format("%s/%s-%s.txt", dir, sf.getSample().getName(), sf.getFeature().getFeature());
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			List<Object[]> hist;
			String line = "";
			double[] values;
			int idtype;
			double max = ConfigurationDB.getMaxForFeatureOnSample(sf.getSample().getIdsample(), sf.getFeature().getIdfeature(), em);
			double min = ConfigurationDB.getMinForFeatureOnSample(sf.getSample().getIdsample(), sf.getFeature().getIdfeature(), em);
			double step = (max - min) / (marks + 1);
			for (double x = min + step / 2; x < max; x += step)
				line += String.format("%10.2f", x);
			writer.append(line);
			writer.append("\n");
			for (Type type : sf.getSample().getTypeList()) {
				idtype = type.getIdtype();
				line = "";
				hist = pdb.getHistograms(idtype, sf.getFeature().getIdfeature(), em);
				values = new double[marks + 1];
				for (Object[] prob : hist)
					values[(Integer) prob[0]] = (Double) prob[1];

				for (double prob : values)
					line += String.format("%10.2f", prob);
				writer.append(line);
				writer.append("\n");

			}
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperationOnResourceException(e.getMessage());
		}
	}
}
