package com.nbis.fluoj.classifier;

import com.nbis.fluoj.persistence.Type;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ParticleStatistic {

    List<ParticlePoint> points;
    int area;
    double radius;
    double diameter;
    double noncircularity;
    int x, y;
    double pixelsmean;
    double pixelsdev;
    double brightness;
    double pbrightness;
    double ppixelsdev;
    private ImageProcessor ip;
    ArrayList<ParticlePoint> ppoints;
    double asymmetry;
    double ellipticity;
    double pdistcurtosis;
    double ppixelsmean;
    double polarity;
    double ppolarity;

    int roisarea = 0, roix, roiy, roiscount;
    double roisbrightness = 0;
    double roispixelsmean = 0;
    double roispixelsdev = 0;
    double roismaxarea;
    double roispolarity;

    private double cdistance;
    SegmentedParticle particle;
    List<ParticleStatistic> roistats;
    private Type type;

    ParticleStatistic(SegmentedParticle particle) {
        this.type = particle.getSample().getIdtype();
        this.particle = particle;
        this.points = particle.getPoints();
        this.ip = particle.getGrayIP();
    }
    
    void initFeatures()
    {
        area = points.size();
        ppoints = new ArrayList<ParticlePoint>();
        double distance;
        double psquares = 0, ppsquares = 0, pxmean = 0, pymean = 0;
        double iwcenterx = 0, iwcentery = 0, piwcenterx = 0, piwcentery = 0;
        pixelsmean = 0;

        for (ParticlePoint p : points) {
            x += p.x;
            y += p.y;
            iwcenterx += p.getPixel() * p.x;
            iwcentery += p.getPixel() * p.y;
            pixelsmean += p.getPixel();
            psquares += Math.pow(p.getPixel(), 2);
            if (isPerimeter(p, particle, ip.getWidth(), ip.getHeight()) && !ppoints.contains(p)) {
                ppoints.add(p);
                ppixelsmean += p.getPixel();
                ppsquares += Math.pow(p.getPixel(), 2);
                pxmean += p.x;
                pymean += p.y;
                piwcenterx += p.getPixel() * p.x;
                piwcentery += p.getPixel() * p.y;

            }

        }
        for (ParticlePoint p : ppoints) {
            for (ParticlePoint p2 : ppoints) {
                if (p.equals(p2)) {
                    continue;
                }
                distance = Math.sqrt(Math.pow(p.x - p2.x, 2) + Math.pow(p.y - p2.y, 2));
                if (distance > diameter) {
                    diameter = distance;
                }
            }
        }
        x = x / area;//xcenter
        y = y / area;//ycenter
        iwcenterx = iwcenterx / pixelsmean;
        iwcentery = iwcentery / pixelsmean;
        piwcenterx = piwcenterx / ppixelsmean;
        piwcentery = piwcentery / ppixelsmean;
        polarity = getDistance(iwcenterx, iwcentery, x, y);
        ppolarity = getDistance(piwcenterx, piwcentery, x, y);
        brightness = Math.sqrt(psquares / area);
        pbrightness = Math.sqrt(ppsquares / ppoints.size());
        pixelsmean = pixelsmean / area;
        ppixelsmean = ppixelsmean / ppoints.size();
        pixelsdev = Math.sqrt(psquares / area - Math.pow(pixelsmean, 2));
        ppixelsdev = Math.sqrt(ppsquares / ppoints.size() - Math.pow(ppixelsmean, 2));
        if (Double.isNaN(pixelsdev)) {
            pixelsdev = 0;//too small 
        }

        double radius2 = 0, pointsdev = 0, minradius = Double.POSITIVE_INFINITY, maxradius = Double.NEGATIVE_INFINITY;
        pxmean = pxmean / ppoints.size();
        pymean = pymean / ppoints.size();
        for (ParticlePoint p : ppoints) {
            distance = getDistance(p.x, p.y, x, y);
            if (distance < minradius && minradius > 0) {
                minradius = distance;
            }
            if (distance > maxradius) {
                maxradius = distance;
            }
            radius += distance;
            radius2 += Math.pow(distance, 2);
            pointsdev += Math.pow(Point.distance(p.x, p.y, pxmean, pymean), 2);
            asymmetry += Math.pow(Point.distance(p.x, p.y, pxmean, pymean), 3);

        }
        pointsdev = Math.sqrt(pointsdev / ppoints.size());
        asymmetry = asymmetry / ppoints.size();
        asymmetry = asymmetry / Math.pow(pointsdev, 3);
        asymmetry = asymmetry;
        radius = radius / ppoints.size();
        noncircularity = Math.sqrt(radius2 / ppoints.size() - Math.pow(radius, 2));
        ellipticity = (maxradius - minradius) / maxradius;
        for (ParticlePoint p : points) {
            pdistcurtosis += Math.pow(p.getPixel() - pixelsmean, 4);
        }

        pdistcurtosis = pdistcurtosis / points.size();
        pdistcurtosis = pdistcurtosis / Math.pow(pixelsdev, 4) - 3;
        if (Double.isNaN(pdistcurtosis)) {
            pdistcurtosis = 0;
        }
        initROIsFeatures();
    }

    void initROIsFeatures() {
        if (!particle.getROIS().isEmpty() && particle.getSample().getRoisMax() > 0) {
            roismaxarea = Double.NEGATIVE_INFINITY;
            roiscount = particle.getROIS().size();
            roistats = new ArrayList<ParticleStatistic>();
            ParticleStatistic roistat = null;
            double iwcenterx = 0, iwcentery = 0;
            // doing summary on properties of inner particles
            for (SegmentedParticle roi : particle.getROIS()) {
                roistat = new ParticleStatistic(roi);
                roistat.initFeatures();
                roistats.add(roistat);
                roisarea += roistat.area;
                if (roismaxarea < roistat.area) {
                    roismaxarea = roistat.area;
                }
                for (ParticlePoint p : roi.getPoints()) {
                    roix += roistat.x;
                    roiy += roistat.y;
                    roisbrightness += Math.pow(p.getPixel(), 2);
                    roispixelsmean += p.getPixel();
                    iwcenterx += p.getPixel() * p.x;
                    iwcentery += p.getPixel() * p.y;
                }
            }
            iwcenterx = iwcenterx / roispixelsmean;
            iwcentery = iwcentery / roispixelsmean;
            roispixelsmean = roispixelsmean / roisarea;
            roix = roix / roisarea;
            roiy = roiy / roisarea;
            roispixelsdev = Math.sqrt(roisbrightness / roisarea - Math.pow(roispixelsmean, 2));
            roisbrightness = Math.sqrt(roisbrightness / roisarea);
            cdistance = Math.sqrt(Math.pow(roix - x, 2) + Math.pow(roiy - y, 2));

            roispolarity = Math.sqrt(Math.pow(x - iwcenterx, 2) + Math.pow(y - iwcentery, 2));
        }

    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public double getAsymmetry(List<Integer> list) {
        java.util.Collections.sort(list);
        int median = list.get(list.size() / 2);
        int left = 0, right = 0;
        for (Integer value : list) {
            if (value < median) {
                left++;
            } else {
                right++;
            }
        }
        return (float) Math.abs(left - right) / list.size();
    }

//	public static boolean isPerimeter(Point point, List<? extends Point> points, int width, int height) {
//		int count = 0;
//		for (int y = point.y - 1; y <= point.y + 1; y++)
//			for (int x = point.x - 1; x <= point.x + 1; x++)
//				if (!(x == point.x && y == point.y))
//					if (FluoJImageProcessor.validPoint(x, y, width, height)
//							&& points.contains(new Point(x, y)))
//						count++;
//		return count < 7;
//
//	}
    public static boolean isPerimeter(Point point, SegmentedParticle particle, int width, int height) {
        int count = 0;
        for (int y = point.y - 1; y <= point.y + 1; y++) {
            for (int x = point.x - 1; x <= point.x + 1; x++) {
                if (!(x == point.x && y == point.y)) {
                    if (FluoJImageProcessor.validPoint(x, y, width, height)
                            && particle.getSegmentedIP().getPixel(x, y) == particle.getLabel()) {
                        count++;
                    }
                }
            }
        }

        return count > 0 && count < 7;

    }

    public Double getValue(int idfeature) {
        Double value = null;
        if (idfeature == EFeature.PIXELS_MEAN.id()) {
            value = pixelsmean;
        } else if (idfeature == EFeature.PIXELS_DEV.id()) {
            value = pixelsdev;
        } else if (idfeature == EFeature.RADIUS_VARIANCE.id()) {
            value = noncircularity;
        } else if (idfeature == EFeature.RADIUS.id()) {
            value = radius;
        } else if (idfeature == EFeature.DIAMETER.id()) {
            value = diameter;
        } else if (idfeature == EFeature.BRIGHTNESS.id()) {
            value = brightness;
        } else if (idfeature == EFeature.AREA.id()) {
            value = (double) area;
        } else if (idfeature == EFeature.PIXELS_VARIATION_COEFF.id()) {
            value = pixelsdev / pixelsmean;
        } else if (idfeature == EFeature.PBRIGHTNESS.id()) {
            value = pbrightness;
        } else if (idfeature == EFeature.PPIXELS_MEANDEV.id()) {
            value = ppixelsdev;
        } else if (idfeature == EFeature.PERIMETER.id()) {
            value = (double) points.size();
        } else if (idfeature == EFeature.ASYMMETRY.id()) {
            value = asymmetry;
        } else if (idfeature == EFeature.ELLIPTICITY.id()) {
            value = ellipticity;
        } else if (idfeature == EFeature.CURTOSIS.id()) {
            value = pdistcurtosis;
        } else if (idfeature == EFeature.PPIXELS_MEAN.id()) {
            value = ppixelsmean;
        } else if (idfeature == EFeature.POLARITY.id()) {
            value = polarity;
        } else if (idfeature == EFeature.PPOLARITY.id()) {
            value = ppolarity;
        } else if (idfeature == EFeature.ROIS_AREA_SUM.id()) {
            value = (double) getROISAreaSum();
        } else if (idfeature == EFeature.ROIS_PIXELS_DEV.id()) {
            value = getROISPixelsDev();
        } else if (idfeature == EFeature.ROIS_PIXELS_MEAN.id()) {
            value = getROISPixelsMean();
        } else if (idfeature == EFeature.CENTERS_DISTANCE.id()) {
            value = getCentersDistance();
        } else if (idfeature == EFeature.ROIS_NUMBER.id()) {
            value = (double) getROISCount();
        } else if (idfeature == EFeature.ROIS_POLARITY.id()) {
            value = getROISPolarity();
        } else if (idfeature == EFeature.ROIS_AREA_RATE.id()) {
            value = getAreaRate();
        } else if (idfeature == EFeature.ROIS_MAX_AREA.id()) {
            value = roismaxarea;
        }

        return value;
    }

    private Double getROISPixelsMean() {
        return roispixelsmean;
    }

    private Double getROISPixelsDev() {
        return roispixelsdev;
    }

    public int getROISAreaSum() {
        return roisarea;
    }

    /**
     * @return Distance between core center and particle center
     */
    public double getCentersDistance() {
        return cdistance;
    }

    public double getAreaRate() {
        return (float) roisarea / area * 100;
    }

    public int getROISCount() {
        return roiscount;
    }

    public List<ParticlePoint> getIPPoints() {
        List<ParticlePoint> ippoints = new ArrayList<ParticlePoint>();
        if(roistats != null)
            for (ParticleStatistic ils : roistats) {
                ippoints.addAll(ils.ppoints);
            }
        return ippoints;
    }

    private double getROISPolarity() {
        return roispolarity;
    }

    public SegmentedParticle getParticle() {
        return particle;
    }

    public Point getCenter() {
        return new Point(x, y);
    }

    /**
     *
     * @return Deviation on halo brightness average
     */
    public double getPixelsMeanDev() {
        return pixelsdev;
    }

    /**
     *
     * @return Biggest distance between particle points.
     */
    public double getDiameter() {
        return diameter;
    }

    /**
     *
     * @return Particle perimeter points
     */
    public List<ParticlePoint> getPpoints() {
        return ppoints;
    }
    
    public Integer getX0() {
        return particle.getPoints().get(0).x;
    }

    public Integer getY0() {
        return particle.getPoints().get(0).y;
    }
    
    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }


    public void setType(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }

   
}
