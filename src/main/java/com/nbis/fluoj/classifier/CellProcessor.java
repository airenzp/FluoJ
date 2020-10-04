package com.nbis.fluoj.classifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import com.nbis.fluoj.persistence.Cell;
import com.nbis.fluoj.persistence.CellFeature;
import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.Probability;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
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
			List<SampleFeature> features = sample.getSampleFeatureList();
			if (features.isEmpty())
				throw new IllegalArgumentException("no features provided");

			for (SampleFeature sf : sample.getSampleFeatureList()) {
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
	 * {@link Probability}. Statistics obtained are requested by
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
		List<SampleFeature> sfeatures = sample.getSampleFeatureList();
		List<Type> types = sample.getTypeList();
		List<Probability> ftps;
		for (int i = 0; i < types.size(); i++) {
			type = types.get(i);
			for (int j = 0; j < sfeatures.size(); j++) {
				if (!sfeatures.get(j).getActive())
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
						
						em.merge(ftps.get(k));
					}
					em.getTransaction().commit();
				}
				catch (Exception e) {
                                    
					e.printStackTrace();
					em.getTransaction().rollback();
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
			for (SampleFeature sf : sample.getSampleFeatureList()) {
				type = types.get(i);
				f = sf.getFeature();
				System.out.println(String.format(" %s %s. Min:%s Max:%s", type.getName(), f.getName(), sf.getMin(), sf.getMax()));
				if (type.getProbabilityList().size() == 0) {
					// System.out.println(String.format("Type %s with no histogram",
					// type.getName()));
					continue;
				}
				List<Probability> probs;
				Probability ftp;
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
	 * Imports data from current session to {@link Cell} and {@link CellFeature}
	 * tables usually used before {@link CellProcessor#processCells()}.
	 * 
	 * @param em
	 *            {@link EntityManager} to be used
	 */
	public void importTrainingData(EntityManager em) {
		pdb.importTrainingData(sample.getIdsession(), em);
	}

	/**
	 * Obtains probabilities from frequences and updates {@link Probability}
	 * 
	 * @param ftps
	 */
	public void normalizeHistogram(List<Probability> ftps, Type type) {
		double sum = 0;
		Probability ftp;
		for (int i = 0; i < ftps.size(); i++) {
			ftp = ftps.get(i);
			sum += ftp.getFrequence();
		}
		if (sum < type.getTrainingMin()) {

			String msg = String.format("Min sample number is %s, provided %s", type.getTrainingMin(), sum);
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

	public double[][] getHistograms(SampleFeature sf, String dir, EntityManager em) {
		double[][] histograms = null;
		try {
			if (!sf.getActive())
				return null;
			String file = String.format("%s/%s-%s.txt", dir, sf.getSample().getName(), sf.getFeature().getName());
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

	public void exportHistograms(SampleFeature sf, String dir, EntityManager em) throws InvalidOperationOnResourceException {
		try {
			if (!sf.getActive())
				return;
			String file = String.format("%s/%s-%s.txt", dir, sf.getSample().getName(), sf.getFeature().getName());
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
