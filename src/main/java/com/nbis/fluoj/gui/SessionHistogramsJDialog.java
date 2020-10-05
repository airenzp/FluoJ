package com.nbis.fluoj.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.JDialog;
import javax.swing.JFrame;
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
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.CellProcessor;
import com.nbis.fluoj.classifier.ProcessorDB;

public class SessionHistogramsJDialog extends JDialog
{

	private EntityManager em;
	/** Creates new form ventanaGrafica */
	private SimpleEntry<Double, Double>[] histogram;

	public SessionHistogramsJDialog(FluoJJFrame parent, Classifier classifier)
	{
		super(parent);
		this.em = parent.em;
		if (classifier == null)
			classifier = new Classifier(parent.getSample().getIdsession());
		ChartPanel chartpn;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Sample Session Histograms");
		GridBagConstraints constraints = new GridBagConstraints();
		JScrollPane sp = new JScrollPane();
		sp.setPreferredSize(new Dimension(800, 590));

		JPanel panel = new JPanel(new GridBagLayout());
		sp.setViewportView(panel);
		getContentPane().add(sp);
		int i = 0;
		String title;
		double x, y, min, max;
		SimpleEntry<Double, Double> bin;
		for (SampleFeature sf : classifier.getSample().getSampleFeatureList())
		{

			max = ConfigurationDB
					.getMaxForFeatureOnSampleSession(sf.getSample().getIdsession().getIdsession(), sf.getFeature().getIdfeature(), parent.em);
			min = ConfigurationDB
					.getMinForFeatureOnSampleSession(sf.getSample().getIdsession().getIdsession(), sf.getFeature().getIdfeature(), parent.em);
			histogram = classifier.getHistogram(sf, em);
			title = sf.getFeature().getName();
			XYSeries series = new XYSeries(title);

			for (int k = 0; k < histogram.length; k++)
			{
				bin = histogram[k];
				if (bin != null)
				{
					x = bin.getKey();
					y = bin.getValue();

					series.add(x, y);
				}
			}

			XYDataset histogramds = new XYSeriesCollection(series);

			JFreeChart chart = ChartFactory
					.createXYLineChart(title, sf.getFeature().getName(), "Probability", histogramds, PlotOrientation.VERTICAL, false, false, true);
			chart.getXYPlot().getDomainAxis().setRange(new Range(min, max));
			chart.getXYPlot().getRangeAxis().setRange(new Range(0, 0.5));
			chartpn = new ChartPanel(chart);
			chartpn.setPreferredSize(new java.awt.Dimension(390, 285));
			panel.add(chartpn, FluoJUtils.getConstraints(constraints, 0, i, 1));
			i++;
		}

		FluoJUtils.setLocation(0.5, this);
		pack();
		setVisible(true);
	}

}
