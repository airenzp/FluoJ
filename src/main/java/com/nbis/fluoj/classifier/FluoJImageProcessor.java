package com.nbis.fluoj.classifier;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.plugin.filter.MaximumFinder;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.Filter;
import com.nbis.fluoj.persistence.Scell;
import com.nbis.fluoj.persistence.ScellFeature;
import com.nbis.fluoj.persistence.ScellFeaturePK;
import counter.BinaryLabel8;

/**
 * Processes {@link ImagePlus} provided and extracts
 * {@link classifier.SegmentedParticle} list. Image is processed on constructor.
 * Segmentation uses {@link gui.Entropy_Threshold} and {@link MaximumFinder} to
 * remove background using low and high threshold and
 * {@link counter.BinaryLabel8} to label each particle pixel on segmented images
 * with a particle id generated. Segmented images are used by
 * {@link #filterParticles(ImagePlus processed_img)} to initialize
 * {@link SegmentedParticle} list. Once created, user can invoke
 * {@link FluoJImageProcessor#getCells()}, to map
 * {@link classifier.SegmentedParticle} list to {@link persistence.Scell} data,
 * or {@link FluoJImageProcessor#saveMeasures()} to persist and classify
 * {@link persistence.Scell Scells}.
 *
 *
 */
public class FluoJImageProcessor {

    private ImagePlus imp;
    private List<SegmentedParticle> particles;
    private List<SegmentedParticle> notvalid_particles;
    private ImageProcessor grayip;
    private ImagePlus grayimp;
    private Sample sample;
    private ImagePlus perimeterimg;
    private boolean typed;
    private int width, height;
    private boolean debug;

    /**
     * Processes {@link ImagePlus} provided to initialize
     * {@link classifier.SegmentedParticle} list. Segmentation uses
     * {@link gui.Entropy_Threshold} to remove background using a low threshold.
     * Image obtained is used by
     * {@link #filterParticles(ImagePlus processed_img)} to initialize
     * {@link SegmentedParticle} list.
     *
     * Initializes {@link SegmentedParticle} list. processed_img provides a low
     * threshold segmented image. Higher threshold segmented image is obtained
     * using null null null null null null     {@link MaximumFinder#findMaxima(FluoJImageProcessor, double, double, int, boolean, boolean)
	 * MaximumFinder}. Both images are labeled using
     * {@link counter.BinaryLabel8} to extract {@link SegmentedParticle} data.
     * From here iteration through images is done to create or find
     * {@link classifier.SegmentedParticle ImageLabel} for each pixel, and add
     * {@link classifier.ParticlePoint LabelPoints} and inner
     * {@link classifier.SegmentedParticle ImageLabels} for each
     * {@link SegmentedParticle}. This information is then reviewed, invalid
     * labels are removed, inner label
     * {@link classifier.SegmentedParticle#addNeighbors(ParticlePoint) neighbor}
     * points are added, and so on, to create final list with choosen
     * {@link classifier.SegmentedParticle ImageLabels}
     *
     *
     * @param img {@link ImagePlus} to be processed
     * @param classifier {@link classifier.Classifier} to be used on
     * {@link #saveMeasures()}
     * @param em EntityManager to be used
     * @param classified Determines if each {@link classifier.SegmentedParticle}
     * should include a default {@link persistence.Type}. true is used for
     * manual classification and false otherwise.
     *
     */
    public FluoJImageProcessor(ImagePlus imp, Sample sample, boolean typed) throws InvalidOperationOnResourceException {
        this(imp, sample, typed, false);
    }

    public FluoJImageProcessor(ImagePlus imp, Sample sample, boolean typed, boolean debug) throws InvalidOperationOnResourceException {
        if (imp == null || imp.getProcessor() == null) {
            throw new InvalidOperationOnResourceException("Invalid path for image " + imp.getTitle());
        }
        this.sample = sample;
        this.typed = typed;
        int threshold = sample.getImageThreshold();
        try {
            count = 0;
            this.imp = imp;
            this.debug = debug;
            width = imp.getWidth();
            height = imp.getHeight();
            if (debug) {
                displayProcessImage(imp, "original");
            }
            grayimp = getProcessImage(sample.getFilterList(), imp);
            grayip = grayimp.getProcessor().convertToByte(false);

            if (debug) {
                displayProcessImage(grayimp, "preprocessed");
            }
            ImageProcessor segmentationip = grayimp.getProcessor().duplicate();
            segmentationip.threshold(threshold);// threshold applied

            System.out.printf("Threshold %s Applied\n", threshold);
            // Label the connected components
            // /////////////////////////////////////////

            ImagePlus segmentationimp = new ImagePlus("", segmentationip);
            if (debug) {
                displayProcessImage(segmentationimp.duplicate(), "particles threshold");
            }

            segmentationip.invertLut();
            if (sample.getFillHoles()) {
                run(segmentationimp, "Fill Holes", "");// fill holes applied
            }
            if (sample.getIdseparation().getIdseparation() == 1)// watershed
            {
                run(segmentationimp, "Watershed", "");
            } else if (sample.getIdseparation().getIdseparation() == 2) {

                ImageProcessor bordersip = grayip.duplicate();
                System.out.println("Duplicated original image for borders detection");
                ImagePlus bordersimp = new ImagePlus("", bordersip);

                run(bordersimp, "Gaussian Blur...", "radius=2");
                // run(bordersimp, "Skeletonize", "");
                run(bordersimp, "Convolve...", "text1=[-1 -1 -1 -1 -1\n-1 -1 -1 -1 -1\n-1 -1 24 -1 -1\n-1 -1 -1 -1 -1\n-1 -1 -1 -1 -1\n] normalize");
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (segmentationip.getPixel(x, y) == 255 && bordersip.getPixel(x, y) > 220) {
                            segmentationip.putPixel(x, y, 0);
                        }
                    }
                }
                if (debug) {
                    displayProcessImage(segmentationimp.duplicate(), "Separation utility " + sample.getIdseparation().getName());
                }
                // to remove noise introduced by convolve on particles

                run(segmentationimp, "Erode", "");
                run(segmentationimp, "Fill Holes", "");

            }
            segmentationip.invertLut();

            System.out.println("Start labelling image");
            segmentationip = segmentationip.convertToShort(false);
            // segmentationimp.duplicate().show();
            BinaryLabel8 labeller = new BinaryLabel8();
            labeller.setup("", segmentationimp);
            labeller.run(segmentationip);
            if (debug) {
                displayProcessImage(new ImagePlus("", segmentationip), "particles labelled");
            }
            ImageProcessor roisip = null;
            if (sample.getRoisThreshold() > 0) {
                roisip = grayimp.getProcessor().duplicate();
                roisip.threshold(sample.getRoisThreshold());// threshold
                // applied
                if (sample.getIdseparation().getIdseparation() == 1)// watershed
                {
                    roisip.invertLut();
                    run(new ImagePlus("", roisip), "Watershed", "");
                    roisip.invertLut();
                }
                roisip = roisip.convertToShort(false);
                labeller.setup("", new ImagePlus("", roisip));
                labeller.run(roisip);
                if (debug) {
                    displayProcessImage(new ImagePlus("", roisip), "ROIS ");
                }
            }

            particles = new ArrayList<SegmentedParticle>();
            int label = -1;
            SegmentedParticle il = null, roi = null;

            System.out.println("Start registering Particle points");
            // Determining inner labels associated to a label and associating
            // points to a label
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    label = segmentationip.getPixel(x, y);
                    if (label == 0) {
                        continue;
                    }
                    il = null;
                    for (SegmentedParticle iliter : particles) {
                        if (iliter.getLabel() == label) {
                            il = iliter;
                        }
                    }
                    if (il == null) {
                        il = new SegmentedParticle(label, typed, sample, grayip, segmentationip);
                        particles.add(il);
                        il.setParticleStatistic(new ParticleStatistic(il));
                    }
                    il.addPoint(new ParticlePoint(x, y, grayip.getPixel(x, y), label));
                    if (sample.getRoisMax() > 0) {

                        label = roisip.getPixel(x, y);
                        if (label == 0) {
                            continue;
                        }
                        roi = null;
                        for (SegmentedParticle iliter : il.getROIS()) {
                            if (iliter.getLabel() == label) {
                                roi = iliter;
                            }
                        }
                        if (roi == null) {
                            roi = new SegmentedParticle(label, typed, sample, grayip, roisip);
                            il.addROI(roi);
                        }
                        roi.addPoint(new ParticlePoint(x, y, grayip.getPixel(x, y), label));

                    }

                }

            }
            for (SegmentedParticle particle : particles) {
                addBorders(particle, sample.getExpansionRadius());
                List<SegmentedParticle> prois = new ArrayList<SegmentedParticle>();
                for (SegmentedParticle proi : particle.getROIS()) {
                    if (proi.getPoints().size() >= 50) {
                        prois.add(proi);
                    }
                }
                particle.setRois(prois);
                particle.initFeatures();
            }

            filterParticles(sample);
        } catch (Exception e) {
            Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private int count;

    public void run(ImagePlus img, String command, String args) {
        count++;
        IJ.run(img, command, args);
        String run = String.format("%d- run(\"%s\", \"%s\")", count, command, args);
        System.out.println(run);
        if (debug) {
            ImageWindow iw = new ImageWindow(img.duplicate());
            iw.setTitle(run);
        }
    }

    public void displayProcessImage(ImagePlus imp, String title) {
        count++;
        ImageWindow tempiw = new ImageWindow(imp);
        title = String.format("%d- %s", count, title);
        tempiw.setTitle(title);
        System.out.println(title);
    }

    public int getBordersThreshold(Sample sample) {
        return (int) (2.5 * sample.getImageThreshold());
    }

    public static boolean validPoint(int x, int y, int width, int height) {
        return !(y < 0 || y >= height || x < 0 || x >= width);
    }

    public static boolean onBorder(int x, int y, int width, int height) {
        return (y <= 0 || y >= height - 1 || x <= 0 || x >= width - 1);
    }

    public static ImagePlus getProcessImage(List<Filter> filters, ImagePlus img) {
        ImagePlus grayimp = img.duplicate();
        grayimp.setTitle("grayimp");
        for (Filter f : filters) {
            IJ.run(grayimp, f.getCommand(), f.getOptions());
        }

        return grayimp;
    }

    public boolean isValid(SegmentedParticle particle, Sample sample) {
        if (particle.onBorder() || !particle.isValid(sample)) {
            return false;
        }
        return true;
    }

    private void addBorders(SegmentedParticle il, int radius) {
        if (radius == 0) {
            return;
        }

        List<ParticlePoint> points = il.getPoints();
        List<ParticlePoint> ppoints = new ArrayList<ParticlePoint>();
        ParticlePoint bpoint;
        for (ParticlePoint point : points) {
            if (ParticleStatistic.isPerimeter(point, il, width, height)) {
                ppoints.add(point);
            }
        }
        // threshold
        for (ParticlePoint point : ppoints) {
            for (int y = point.y - radius; y <= point.y + radius; y++) {
                for (int x = point.x - radius; x <= point.x + radius; x++) {
                    bpoint = new ParticlePoint(x, y, grayip.getPixel(x, y));
                    if (!points.contains(bpoint)) {
                        points.add(bpoint);
                    }
                }
            }

        }
    }

    public SegmentedParticle getParticle(List<SegmentedParticle> particles, Point p) {
        SegmentedParticle result = null;
        double distance, min = Double.POSITIVE_INFINITY;
        for (SegmentedParticle particle : particles) {
            if (particle.isOwner(p)) {
                distance = ParticleStatistic.getDistance(particle.getParticleStatistic().getX0(), particle.getParticleStatistic().getY0(), p.x, p.y);
                if (distance < min) {
                    min = distance;
                    result = particle;
                }
            }
        }
        return result;
    }

    public List<Scell> getCells() {
        ArrayList<Scell> scells = new ArrayList<Scell>();
        Scell ss;
        for (SegmentedParticle il : particles) {
            if (typed && il.getParticleStatistic().getType() == null) {
                continue;
            }
            ss = getScell(il.getParticleStatistic());
            scells.add(ss);
        }
        return scells;
    }

    private Scell getScell(ParticleStatistic info) {
        Scell ss = new Scell();
        if (typed) {
            ss.setIdtype(info.getType());
        }
        ss.setX(info.getX0());
        ss.setY(info.getY0());
        double value = 0;
        ScellFeature ssf;
        int idfeature;
        ss.setScellFeatureList(new ArrayList<ScellFeature>());
        for (SampleFeature sf : sample.getSampleFeatureList()) {
            idfeature = sf.getFeature().getIdfeature();
            value = info.getValue(idfeature);
            ssf = new ScellFeature(new ScellFeaturePK());
            ssf.setFeature(sf.getFeature());
            ssf.setScell(ss);
            ssf.setValue(value);
            ss.getScellFeatureList().add(ssf);
        }
        return ss;
    }

    public double getMax(int idfeature) {
        double max = Double.NEGATIVE_INFINITY;
        double value;
        for (SegmentedParticle il : particles) {
            value = il.getParticleStatistic().getValue(idfeature);
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public double getMin(int idfeature) {
        double min = Double.POSITIVE_INFINITY;
        double value;
        for (SegmentedParticle il : particles) {
            value = il.getParticleStatistic().getValue(idfeature);
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public int saveMeasures(Classifier classifier) {
        int idimage = classifier.classifyCellsOnImage(getCells(), imp);
        return idimage;
    }

    public ImagePlus getPerimetersImg() {
        if (perimeterimg == null) {
            System.out.println("Starting to draw perimeters");
            ImageProcessor ipc = grayip.duplicate().convertToRGB();
            int[] color = new int[]{255, 255, 0};// perimeter is yellow
            int[] corecolor = new int[]{0, 255, 0};// core perimeter is green
            for (SegmentedParticle sp : particles) {

                for (Point point : sp.getParticleStatistic().getPpoints()) {
                    ipc.putPixel(point.x, point.y, color);
                }
                if (sample.getRoisMax() > 0) {
                    for (Point point : sp.getParticleStatistic().getIPPoints()) {
                        ipc.putPixel(point.x, point.y, corecolor);
                    }
                }
            }
            System.out.println("Drawing perimeters ended");
            perimeterimg = new ImagePlus("", ipc);
            new ImageConverter(perimeterimg).convertToRGB();
        }
        return perimeterimg;
    }

    public ImagePlus getImagePlus() {
        return grayimp;
    }

    public List<SegmentedParticle> getParticles() {
        return particles;
    }

    public Sample getSample() {
        return sample;
    }

    public void filterParticles(Sample sample) {
        List<SegmentedParticle> fparticles = new ArrayList<SegmentedParticle>();
        notvalid_particles = new ArrayList<SegmentedParticle>();
        for (SegmentedParticle particle : particles) {
            if (particle.onBorder()) {
                continue;
            }
            if (!particle.isValid(sample)) {
                notvalid_particles.add(particle);
                continue;
            }
            fparticles.add(particle);
        }
        particles = fparticles;
    }

    public List<SegmentedParticle> getNotValidParticles() {
        return notvalid_particles;
    }

}
