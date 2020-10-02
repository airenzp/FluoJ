package com.nbis.fluoj.classifier;

import java.util.Arrays;

public enum EFeature {
    AREA(1),
    PERIMETER(2),
    PIXELS_MEAN(3),
    PIXELS_DEV(4),
    PIXELS_VARIATION_COEFF(5),
    RADIUS(6),
    RADIUS_VARIANCE(7),
    DIAMETER(8),
    BRIGHTNESS(9),
    ASYMMETRY(10),
    ELLIPTICITY(11),
    CURTOSIS(12),
    POLARITY(13),
    CENTERS_DISTANCE(14),
    PPIXELS_MEAN(15),
    PPIXELS_MEANDEV(16),
    PBRIGHTNESS(17),
    PPOLARITY(18),
    ROIS_PIXELS_MEAN(19),
    ROIS_PIXELS_DEV(20),
    ROIS_AREA_SUM(21),
    ROIS_POLARITY(22),
    ROIS_NUMBER(23),
    ROIS_MAX_AREA(24),
    ROIS_AREA_RATE(25);

    public static EFeature[] mixed = new EFeature[]{ROIS_AREA_SUM, ROIS_PIXELS_DEV, ROIS_PIXELS_MEAN, ROIS_POLARITY, CENTERS_DISTANCE, ROIS_NUMBER, ROIS_AREA_RATE, ROIS_MAX_AREA};

    private final int id;

    EFeature(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static boolean isMixed(EFeature feature) {
        return Arrays.asList(mixed).contains(feature);
    }

    public static boolean isMixed(int idfeature) {
        for (EFeature e : mixed) {
            if (e.id() == idfeature) {
                return true;
            }
        }
        return false;
    }

}
