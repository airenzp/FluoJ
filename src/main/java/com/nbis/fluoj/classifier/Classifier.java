package com.nbis.fluoj.classifier;

import ij.ImagePlus;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.persistence.EntityManager;

import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.SampleImage;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.Scell;
import com.nbis.fluoj.persistence.Session;

/**
 * Implements {@link IClassifier}. Provides core classification functionality to
 * be used by clients.
 *
 * @see classifier.IClassifier
 */
public class Classifier implements IClassifier {

    public final Sample sample;
    private ClassifierDB cdb;
    private CClassifier sc;
    private static Logger logger;

    static String status_msg;

    // Constructor. Needs sample type
    public Classifier(Sample sample, String name, Date date, EntityManager em) {

        this.sample = sample;

        cdb = new ClassifierDB(sample, name, date, em);
        sc = new CClassifier(cdb);
    }

    // Constructor. Needs sample type
    public Classifier(Sample sample, EntityManager em) {
        // Determines DB to use
        this.sample = sample;

        cdb = new ClassifierDB(sample, em);
        sc = new CClassifier(cdb);
    }

    public Classifier(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Invalid Session Data");
        }
        // Determines DB to use
        this.sample = session.getIdsample();
        cdb = new ClassifierDB(session);
        sc = new CClassifier(cdb);
    }

    public static Logger getLogger() {
        try {
            if (logger == null) {
                FileHandler fh = new FileHandler(ConfigurationDB.fluojdir + "FluoJ.log", true);
                fh.setFormatter(new SimpleFormatter());
                logger = Logger.getLogger("FluoJLogger");
                logger.addHandler(fh);
            }
            return logger;
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#classify()Type
     */
    @Override
    public void classify() {
        EntityManager em = cdb.getEM();
        sc.classifyScells(em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getTotalFromWinner(int, EntityManager)
     */
    @Override
    public int getTotalFromWinner(Short idtype, EntityManager em) {
        int total = cdb.getScellTotalForWinner(idtype, em);
        return total;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getPercentClassified(int, EntityManager)
     */
    @Override
    public float getPercentClassified(Short idtype, EntityManager em) {
        return ((float) getTotalFromWinner(idtype, em) / getTotalOfScells(em)) * 100;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getTotalOfScells(EntityManager)
     */
    @Override
    public int getTotalOfScells(EntityManager em) {
        return 0;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getScells(EntityManager)
     */
    @Override
    public List<Scell> getScells(EntityManager em) {
        return cdb.getScells();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getSample()
     */
    @Override
    public Sample getSample() {
        // TODO Auto-generated method stub
        return sample;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getIdsession()
     */
    @Override
    public int getIdsession() {
        // TODO Auto-generated method stub
        return cdb.session.getIdsession();
    }

    public int persistCellsOnImage(List<Scell> scells, ImagePlus img, EntityManager em) {
        if (img == null) {
            IllegalArgumentException e = new IllegalArgumentException("No image provided");
            getLogger().log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
        SampleImage ir = cdb.saveImageResource(img, em);
        for (int i = 0; i < scells.size(); i++) {
            scells.get(i).setIdimage(ir);
        }
        cdb.persistScells(scells, em);
        return ir.getIdimage();

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getFeatures(EntityManager)
     */
    @Override
    public List<Feature> getFeatures(EntityManager em) {
        return cdb.getFeatures(em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#persistScells(List<Scell>)
     */
    @Override
    public void persistSCells(List<Scell> scells) {
        EntityManager em = cdb.getEM();
        cdb.persistScells(scells, em);
        em.close();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#persistScells(List<Scell>, EntityManager)
     */
    @Override
    public void persistSCells(List<Scell> scells, EntityManager em) {
        cdb.persistScells(scells, em);

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#persistScellsOnImages(List<ImagePlus>,
	 * EntityManager)
     */
    @Override
    public void persistCellsOnImages(List<ImagePlus> images, EntityManager em) {
        List<Scell> list;
        ImagePlus img;
        FluoJImageProcessor scip;
        for (int i = 0; i < images.size(); i++) {
            img = images.get(i);
            try {
                scip = new FluoJImageProcessor(img, sample, false);
            } catch (InvalidOperationOnResourceException e) {
                Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
                continue;
            }
            list = scip.getCells();
            persistCellsOnImage(list, img, em);
        }

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#persistScellsOnImage(ImagePlus,
	 * EntityManager)
     */
    @Override
    public int persistCellsOnImage(ImagePlus image, boolean classified, EntityManager em) {
        FluoJImageProcessor scip;
        try {
            scip = new FluoJImageProcessor(image, sample, classified);
        } catch (InvalidOperationOnResourceException e) {
            // Put error on logger and return, so Haloscore doesn�t have to
            // catch it.
            return -1;
        }
        List<Scell> list = scip.getCells();
        int idimage = persistCellsOnImage(list, image, em);
        return idimage;

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#classifyScellsOnImages(List<ImagePlus>,
	 * EntityManager)
     */
    @Override
    public void classifyCellsOnImages(List<ImagePlus> images, EntityManager em) {
        if (images == null) {
            throw new IllegalArgumentException();
        }
        ImagePlus img;
        for (int i = 0; i < images.size(); i++) {
            img = images.get(i);
            classifyCellsOnImage(img, em);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#addImageToClassify(ij.ImagePlus)
     */
    @Override
    public int classifyCellsOnImage(ImagePlus image) {
        EntityManager em = ConfigurationDB.getEM();
        int idimage = classifyCellsOnImage(image, em);
        em.close();
        return idimage;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#classifyScellsOnImage(ImagePlus,
	 * EntityManager)
     */
    @Override
    public int classifyCellsOnImage(ImagePlus image, EntityManager em) {
        FluoJImageProcessor scip;
        try {
            scip = new FluoJImageProcessor(image, sample, false);
        } catch (InvalidOperationOnResourceException e) {
            Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);// Put
            // error
            // on
            // logger,
            // so
            // Haloscore
            // doesn�t
            // have
            // to
            // catch
            // it.
            return -1;
        }
        List<Scell> list = scip.getCells();
        int idimage = persistCellsOnImage(list, image, em);
        em.clear();
        sc.classifyScellsOnImage(idimage, em);
        return idimage;
    }

    public int classifyCellsOnImage(List<Scell> scells, ImagePlus img, EntityManager em) {
        int idimage = persistCellsOnImage(scells, img, em);
        sc.classifyScellsOnImage(idimage, em);
        return idimage;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getTotalUnknown(EntityManager)
     */
    @Override
    public int getTotalUnknown(EntityManager em) {
        return cdb.getScellTotalForWinner(null, em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getId()
     */
    @Override
    public String getId() {
        return cdb.getId();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getScells(int, EntityManager)
     */
    @Override
    public List<Scell> getScells(int idimage, EntityManager em) {
        return cdb.getScellsOnImage(idimage, em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getScells(int idimage, Integer original,
	 * Integer winner, EntityManager em)
     */
    @Override
    public List<Scell> getScells(int idimage, Short original, Short winner, EntityManager em) {
        return cdb.getScellsOnImage(idimage, original, winner, em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getImages(EntityManager em)
     */
    @Override
    public List<SampleImage> getImages(EntityManager em) {
        return cdb.getImages();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getProbs(Integer idscell, EntityManager em)
     */
    @Override
    public List<CellTypeProbability> getProbs(Integer idscell, EntityManager em) {
        return cdb.getProbs(idscell, em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getTotalFromClass(Integer idtype,
	 * EntityManager em)
     */
    @Override
    public int getTotalFromClass(Short idtype, EntityManager em) {
        return cdb.getScellTotalForClass(idtype, em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getImagesCount(EntityManager em)
     */
    @Override
    public int getImagesCount(EntityManager em) {
        return cdb.getImagesCount(em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#mergeScells(List<Scell> scells, EntityManager
	 * em)
     */
    @Override
    public void mergeSCells(List<Scell> scells, EntityManager em) {
        cdb.mergeScells(scells, em);

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#deleteScell(int idscell, EntityManager em)
     */
    @Override
    public void deleteSCell(int idscell, EntityManager em) {
        cdb.deleteScell(idscell, em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#deleteScells(List<Integer> idscells,
	 * EntityManager em)
     */
    @Override
    public void deleteSCells(List<Integer> idscells, EntityManager em) {
        cdb.deleteScells(idscells, em);

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getTotalMeasured()
     */
    @Override
    public int getTotalMeasured() {
        return cdb.getScellTotal();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getTotal(Integer original, Integer winner)
     */
    @Override
    public int getTotal(Short original, Short winner) {
        return cdb.getScellTotal(original, winner);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getTotalErrorPercent()
     */
    @Override
    public float getTotalErrorPercent() {
        return cdb.getTotalErrorPercent();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#getWinnerErrorPercent(int idtype,
	 * EntityManager em)
     */
    @Override
    public float getWinnerErrorPercent(Short idtype, EntityManager em) {
        return cdb.getWinnerErrorPercent(idtype, em);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#exportData(File file, boolean original)
     */
    @Override
    public void exportAllData(File file, EntityManager em) {
        cdb.exportAllData(file, true, true, true, em);

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#exportToOpenDX(String directoryPath, boolean
	 * original)
     */
    @Override
    public void exportToOpenDX(String directoryPath, boolean original, boolean winner, EntityManager em) {
        cdb.exportToOpenDX(directoryPath, original, winner, em);
    }

    public int classifyCellsOnImage(List<Scell> scells, ImagePlus img) {
        EntityManager em = ConfigurationDB.getEM();
        int idimage = classifyCellsOnImage(scells, img, em);
        em.close();
        return idimage;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see classifier.IClassifier#classifyScellsOnImages(List<ImagePlus>
	 * images)
     */
    @Override
    public void classifyCellsOnImages(List<ImagePlus> images) {
        EntityManager em = ConfigurationDB.getEM();
        classifyCellsOnImages(images, em);
        em.close();

    }

    public Double getProbability(int idscell, int idfeature, int idtype, EntityManager em) {
        return cdb.getProbability(idscell, idfeature, idtype, em);
    }

    @Override
    public void removeOldSessionData() {
        cdb.removeSessionDataBeforeDate(ConfigurationDB.limitDateForData());

    }

    public void removeOldSessionData(Date date, EntityManager em) {
        cdb.removeSessionDataBeforeDate(date, em);

    }

    @Override
    public SimpleEntry<Double, Double>[] getHistogram(SampleFeature sfeature, EntityManager em) {
        return cdb.getHistogram(sfeature, em);
    }

}
