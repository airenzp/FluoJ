package com.nbis.fluoj.classifier;

import ij.ImagePlus;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import javax.persistence.EntityManager;

import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.SampleImage;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.Scell;
import com.nbis.fluoj.persistence.Cell;
import com.nbis.fluoj.persistence.Type;

/**
 * Classifier Interface to be used by client. Provides persistence and classification API.
 * Used by clients and ImageJ plugins {@link classifier.View_}, {@link classifier.Review_} and 
 * {@link classifier.Training_}. 
 * Implemented by {@link Classifier}.
 * @author Airen
 *
 */
public interface IClassifier {
	
	
	/**
	 * @return Sample associated with the classifier
	 */
	public Sample getSample();
	
	/**
	 * @return {@link persistence.Session} associated with the classifier
	 */
	public int getIdsession();
	
	/**
	 * Persists {@link Scell} list and {@link persistence.Image}
	 * @param scells {@link Scell} list to be persisted
	 * @param img {@link ImagePlus} to be processed
	 * @param em {@link EntityManager} to be used
	 * @return id generated for {@link persistence.Image} associated to {@link ImagePlus}
	 */
	public int persistCellsOnImage(List<Scell> scells, ImagePlus img, EntityManager em);
	
	/**
	 * Persists  {@link Scell} list provided
	 * @param scells {@link Scell} list to be persisted
	 */
	public void persistSCells(List<Scell> scells);
	
	/**
	 * Merges  {@link Scell} list
	 * @param scells {@link Scell} list to be merged
	 * @param em {@link EntityManager} to be used
	 */
	public void mergeSCells(List<Scell> scells, EntityManager em);
	
	/**
	 * Deletes {@link Scell} by id
	 * @param idscell {@link Scell} id to be deleted
	 * @param em {@link EntityManager} to be used
	 */
	public void deleteSCell(int idscell, EntityManager em);
	
	/**
	 * Deletes {@link Scell} specified by idscells
	 * @param idscells {@link Scell} list of ids to be deleted
	 * @param em {@link EntityManager} to be used
	 */
	public void deleteSCells(List<Integer> idscells, EntityManager em);
	
	/**
	 * Obtains {@link Scell} list from each {@link ImagePlus} provided using {@link classifier.FluoJImageProcessor}. 
	 * Persists {@link Scell} list and 
	 * {@link persistence.Image} for each {@link ImagePlus}. 
	 * @param images {@link ImagePlus} list to be processed
	 * @param em {@link EntityManager} to be used
	 */
	public void persistCellsOnImages(List<ImagePlus> images, EntityManager em);
	
	/**
	 * Obtains {@link Scell} list from {@link ImagePlus} provided using {@link classifier.FluoJImageProcessor}. 
	 * Persists {@link Scell} list and {@link persistence.Image} for {@link ImagePlus} image. 
	 * @param image {@link ImagePlus} to be processed
	 * @param em {@link EntityManager} to be used
	 * @return id generated for {@link persistence.Image} associated to {@link ImagePlus}
	 */
	public int persistCellsOnImage(ImagePlus image, boolean classified, EntityManager em);
		
	/**
	 * Classifies all scells registered on database. Uses its own {@link EntityManager}.
	 */
	public void classify();

	/**
	 * Persists {@link Scell} list and {@link persistence.Image} for {@link ImagePlus} image. 
	 * Classifies {@link Scell} list. 
	 * Uses its own EntityManager.
	 * @param scells {@link Scell} list
	 * @param img {@link ImagePlus} to be processed
	 * @return id generated for {@link persistence.Image} associated to {@link ImagePlus}
	 */
	public int classifyCellsOnImage(List<Scell> scells, ImagePlus img);
	
	/**
	 * Persists {@link Scell} list and {@link persistence.Image} for {@link ImagePlus} and 
	 * classifies data.
	 * @param scells {@link Scell} list
	 * @param img {@link ImagePlus} to be processed
	 * @param em {@link EntityManager} to be used
	 * @return id generated for {@link persistence.Image} associated to {@link ImagePlus}
	 */
	 
	public int classifyCellsOnImage(List<Scell> scells, ImagePlus img, EntityManager em);
	
	/**
	 * Processes {@link ImagePlus} list using {@link classifier.FluoJImageProcessor}, extracts cell data and 
	 * classifies data.
	 * @param images
	 * @param em {@link EntityManager} to be used
	 */
	public  void classifyCellsOnImages(List<ImagePlus> images, EntityManager em);
	
	/**
	 * Processes {@link ImagePlus} list using {@link classifier.FluoJImageProcessor}, extracts {@link Scell} data and 
	 * classifies data.
	 * @param images {@link ImagePlus} list to be processed
	 */
	public  void classifyCellsOnImages(List<ImagePlus> images);
	
	
	/**
	 * Processes image using {@link classifier.FluoJImageProcessor} and classifies {@link Scell} list obtained. 
	 * Uses its own {@link EntityManager}.
	 * @param image {@link ImagePlus} to be processed
	 * @return id generated for {@link persistence.Image} associated to {@link ImagePlus} image
	 */
	public int classifyCellsOnImage(ImagePlus image);
	
	/**
	 * Processes image using {@link classifier.FluoJImageProcessor} and classifies {@link Scell} list obtained.
	 * @param image {@link ImagePlus} to be processed
	 * @param em {@link EntityManager} to be used
	 * @return id generated for {@link persistence.Image} associated to {@link ImagePlus} image
	 */
	public int classifyCellsOnImage(ImagePlus image, EntityManager em);

	/**
	 * @param idtype {@link Type} id
	 * @param em {@link EntityManager} to be used
	 * @return total of {@link Scell} classified as {@link Type} 
	 */
	public  int getTotalFromWinner(Short idtype, EntityManager em);
	
	
	
	/**
	 * @param idtype {@link Type} id
	 * @param em {@link EntityManager} to be used
	 * @return total of {@link Scell} classified by user as {@link Type} 
	 */
	public  int getTotalFromClass(Short idtype, EntityManager em);
	
	
	/**
	 * @param em {@link EntityManager} to be used
	 * @return total of unclassified {@link Scell}
	 */
	public  int getTotalUnknown(EntityManager em);

	/**
	 * @param idtype
	 * @param em {@link EntityManager} to be used
	 * @return percent of {@link Scell} classified as {@link Type}
	 */
	public  float getPercentClassified(Short idtype, EntityManager em);

	/**
	 * @param em {@link EntityManager} to be used
	 * @return total of {@link Scell} registered
	 */
	public  int getTotalOfScells(EntityManager em);
	
	/**
	 * @return total of particles measured
	 */
	public int getTotalMeasured();
	
	/**
	 * @param original {@link Type} id
	 * @param winner {@link Type} id
	 * @return total of particles classified by user as original and by software as winner. Allows nulls.
	 */
	public int getTotal(Short original, Short winner);
	
	/**
	 * @return total error percent
	 */
	public float getTotalErrorPercent();
	
	/**
	 * Returns total error percent on Scells of winner idscelltpe
	 * @param idtype
	 * @param em {@link EntityManager} to be used
	 * @return total error percent on {@link Scell} of winner idscelltpe
	 */
	public float getWinnerErrorPercent(Short idtype, EntityManager em);

	/**
	 * @param em {@link EntityManager} to be used
	 * @return {@link Scell} list registered
	 */
	public  List<Scell> getScells(EntityManager em);

	/**
	 * @param idimage {@link persistence.Image} id
	 * @param em {@link EntityManager} to be used
	 * @return {@link Scell} list registered
	 */
	public  List<Scell> getScells(int idimage, EntityManager em);
	
	/**
	 * @param idimage {@link persistence.Image} id
	 * @param original {@link Type} id
	 * @param winner {@link Type} id
	 * @param em {@link EntityManager} to be used
	 * @return {@link Scell} list registered for idimage
	 */
	public  List<Scell> getScells(int idimage, Short original, Short winner, EntityManager em);
	
	
	/**
	 * Exports data from database.
	 * @param file path to file 
	 * @param original original specifies whether winner or original will be used as {@link Type}
	 * @throws InvalidOperationOnResourceException
	 */
	public  void exportAllData(File file, EntityManager em); 
	
	/**
	 * Exports main features to OpenDX for visualization.
	 * @param directoryPath path to directory to export
	 * @param original specifies whether winner or original will be used as {@link Type}
	 * @throws InvalidOperationOnResourceException
	 */
	public void exportToOpenDX(String directoryPath, boolean original, boolean winner, EntityManager em);
	
	/**
	 * @param em {@link EntityManager} to be used
	 * @return {@link Feature} list to be used by Classifier
	 */
	public  List<Feature> getFeatures(EntityManager em);
	
	
	
	/**
	 * @return Classifier text id
	 */
	public String getId();
	
	/**
	 * @param em {@link EntityManager} to be used
	 * @return Classifier Image filenames.
	 */
	public List<SampleImage> getImages(EntityManager em);
	
	/**
	 * @param em {@link EntityManager} to be used
	 * @return Classifier Images count.
	 */
	public int getImagesCount(EntityManager em);
	
	/**
	 * @param idscell {@link Scell} id
	 * @param em {@link EntityManager} to be used
	 * @return {@link Probability} list for each {@link Type}
	 */
	public List<CellTypeProbability> getProbs(Integer idscell, EntityManager em);
	

	void persistSCells(List<Scell> scells, EntityManager em);
	
	void removeOldSessionData();
	
	public SimpleEntry<Double, Double>[] getHistogram(SampleFeature sfeature, EntityManager em);
}