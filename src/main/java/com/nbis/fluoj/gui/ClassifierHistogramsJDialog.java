package com.nbis.fluoj.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.CellProcessor;
import com.nbis.fluoj.classifier.ProcessorDB;

public class ClassifierHistogramsJDialog extends JDialog {

    /**
     * Creates new form ventanaGrafica
     */
    public ClassifierHistogramsJDialog(FluoJJFrame parent) {
        super(parent);
        ProcessorDB pdb = new ProcessorDB(parent.getSample());
        if (ConfigurationDB.isEmpty(parent.getSample())) {
            throw new IllegalArgumentException("Sample not trained");
        }
        List<Object[]> histogram;
        ChartPanel chartpn;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Classifier Histograms");
        GridBagConstraints constraints = new GridBagConstraints();
        JScrollPane sp = new JScrollPane();
        sp.setPreferredSize(new Dimension(800, 590));

        JPanel panel = new JPanel(new GridBagLayout());
        sp.setViewportView(panel);
        getContentPane().add(sp);
        int i = 0, j = 0, xpos;
        String title;
        double min, max, x, y;

        for (SampleFeature sf : parent.getSample().getSampleFeatureList()) {
            if (sf.getActive()) {
                max = ConfigurationDB.getMaxForFeatureOnSample(sf.getSample().getIdsample(), sf.getFeature().getIdfeature(), parent.em);
                min = ConfigurationDB.getMinForFeatureOnSample(sf.getSample().getIdsample(), sf.getFeature().getIdfeature(), parent.em);
                System.out.printf("%s %.2f %.2f \n", sf.getFeature().getName(), min, max);
                double step = (max - min) / (CellProcessor.marks + 1);
                for (com.nbis.fluoj.persistence.Type t : parent.getSample().getTypeList()) {
                    if (!t.getProbabilityList().isEmpty()) {
                        histogram = pdb.getHistograms(t.getIdtype(), sf.getFeature().getIdfeature(), parent.em);
                        title = t.getName();
                        XYSeries series = new XYSeries(title);

                        Object[] value;
                        for (int k = 0; k < histogram.size(); k++) {
                            value = histogram.get(k);
                            xpos = (Integer) value[0];
                            x = min + xpos * step - (step * 0.5);
                            y = ((Double) value[1]);//probability
                            System.out.printf("%.2f  %.2f\n", x, y);
                            series.add(x, y);
                        }

                        XYDataset histogramds = new XYSeriesCollection(series);

                        JFreeChart chart = ChartFactory.createXYLineChart(title, sf.getFeature().getName(), "Probability", histogramds, PlotOrientation.VERTICAL, false, false, true);
                        chart.getXYPlot().getDomainAxis().setRange(new Range(min, max));
                        chart.getXYPlot().getRangeAxis().setRange(new Range(0, 0.5));
                        chartpn = new ChartPanel(chart);
                        chartpn.setPreferredSize(new java.awt.Dimension(390, 285));
                        panel.add(chartpn, FluoJUtils.getConstraints(constraints, j, i, 1));
                        j++;
                    }
                }
                i++;
                j = 0;
            }
        }

        FluoJUtils.setLocation(0.5, this);
        pack();
        setVisible(true);
    }

}
