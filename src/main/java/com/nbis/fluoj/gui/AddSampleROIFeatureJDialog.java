package com.nbis.fluoj.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.nbis.fluoj.persistence.Feature;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.SampleFeaturePK;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

public class AddSampleROIFeatureJDialog extends JDialog {

	private JButton addbt;
	private JButton cancelbt;
	EditSampleFeaturesJDialog parent;
	private JFormattedTextField mintf;
	private JComboBox featurecb;
	private JFormattedTextField maxtf;
	private Feature feature;
	private Sample sample;
	private FluoJImageProcessor cip;
	private JButton loadimgbt;
	private List<SampleFeature> scfs;
	private SampleFeature sf;
	private ConfigurationJFrame frame;
	private JLabel descriptionlb;

	public AddSampleROIFeatureJDialog(EditSampleFeaturesJDialog parent,
			boolean modal, EntityManager em)
			throws InvalidOperationOnResourceException {
		super(parent, modal);
		setResizable(false);
		this.parent = parent;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Add Sample Core Feature");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.WEST;

		this.sample = parent.getSample();
		scfs = new ArrayList<SampleFeature>(sample.getSampleFeatureList());
		frame = (ConfigurationJFrame) parent.getParent();

		add(new JLabel("Feature"),
				FluoJUtils.getConstraints(constraints, 0, 0, 1));

		
		featurecb = new JComboBox(ConfigurationDB.getAvailableCorefeatures(
				parent.getSample(), em).toArray());

		add(featurecb, FluoJUtils.getConstraints(constraints, 1, 0, 1));
		feature = (Feature) featurecb.getSelectedItem();
		add(new JLabel("Description"),
				FluoJUtils.getConstraints(constraints, 0, 1, 1));
		descriptionlb = new JLabel(feature.getDescription());
		add(descriptionlb, FluoJUtils.getConstraints(constraints, 1, 1, 1));
		cip = frame.getCImageProcess();
		sf = new SampleFeature();
		sf.setSampleFeaturePK(new SampleFeaturePK(parent.getSample()
				.getIdsample(), feature.getIdfeature()));
		sf.setFeature(feature);
		sf.setMin(cip.getROISMin(feature.getIdfeature()));
		sf.setMax(cip.getROISMax(feature.getIdfeature()));
		scfs.add(sf);
		add(new JLabel("Min"), FluoJUtils.getConstraints(constraints, 0, 2, 1));
		mintf = new JFormattedTextField(NumberFormat.getNumberInstance());
		mintf.setValue(sf.getMin());
		mintf.setColumns(5);
		add(mintf, FluoJUtils.getConstraints(constraints, 1, 2, 1));
		add(new JLabel("Max"), FluoJUtils.getConstraints(constraints, 0, 3, 1));
		maxtf = new JFormattedTextField(NumberFormat.getNumberInstance());
		maxtf.setValue(sf.getMax());
		maxtf.setColumns(5);
		add(maxtf, FluoJUtils.getConstraints(constraints, 1, 3, 1));
		
		
		loadimgbt = new JButton("Process Image");
		addbt = new JButton("Add");
		cancelbt = new JButton("Cancel");

		JPanel buttonspn = new JPanel();
		buttonspn.add(addbt);
		buttonspn.add(cancelbt);
		buttonspn.add(loadimgbt);
		add(buttonspn, FluoJUtils.getConstraints(constraints, 0, 5, 2));
		setListeners();
		pack();
		FluoJUtils.setLocation(parent.getLocationX(), this);
		setVisible(true);
	}

	private void setListeners() {

		mintf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				double min = ((Number) mintf.getValue()).doubleValue();
				sf.setMin(min);
			}
		});

		maxtf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				double max = ((Number) maxtf.getValue()).doubleValue();
				sf.setMax(max);
			}
		});

		loadimgbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				double min = ((Number) mintf.getValue()).doubleValue();
				double max = ((Number) maxtf.getValue()).doubleValue();
				sf.setMin(min);
				sf.setMax(max);
				frame.resetCImageProcess();
				try {
					frame.processImageParticles(sample.getSampleFeatureList());
				} catch (InvalidOperationOnResourceException e1) {
					JOptionPane.showMessageDialog(AddSampleROIFeatureJDialog.this, e1.getMessage());
				}
			}
		});

		featurecb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				feature = (Feature) featurecb.getSelectedItem();
				sf.setSampleFeaturePK(new SampleFeaturePK(sample.getIdsample(),
						feature.getIdfeature()));
				sf.setFeature(feature);
				sf.setMin(cip.getROISMin(feature.getIdfeature()));
				sf.setMax(cip.getROISMax(feature.getIdfeature()));
				mintf.setValue(sf.getMin());
				maxtf.setValue(sf.getMax());
				descriptionlb.setText(feature.getDescription());
				pack();
			}
		});

		addbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (mintf.getValue() == null)
						new IllegalArgumentException(Constants
								.getEmptyFieldMsg("Min"));
					if (maxtf.getValue() == null)
						new IllegalArgumentException(Constants
								.getEmptyFieldMsg("Max"));
					int result = frame.resetDB();
					if (result == JOptionPane.YES_OPTION) {
						double min = ((Number) mintf.getValue()).doubleValue();
						double max = ((Number) maxtf.getValue()).doubleValue();
						sf.setMin(min);
						sf.setMax(max);
						parent.addSampleFeature(sf);
					}
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(AddSampleROIFeatureJDialog.this,
							ex.getMessage());
					return;
				}
				setVisible(false);
				dispose();
			}
		});
		cancelbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
	}

}
