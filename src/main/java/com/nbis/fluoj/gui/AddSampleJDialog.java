package com.nbis.fluoj.gui;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Filter;
import com.nbis.fluoj.persistence.Separation;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.Classifier;

public class AddSampleJDialog extends JDialog
{

	private JButton addbt;
	private JButton cancelbt;
	private JTextField nametf;
	private double position = 0.9;
	ConfigurationJFrame parent;
	private JComboBox dtypecb;
	private JTextField imagetf;
	private JButton browsebt;
	private JFormattedTextField thresholdtf;
	private int autothreshold;
	private ImagePlus imp;
	private JFormattedTextField roismaxtf;
	private JCheckBox fillchb;
	private JComboBox separationscb;
	private List<Separation> separations;
	private ArrayList<Filter> filters;
	private JFormattedTextField expansionradiustf;
	private PreprocessingPane filterspn;
	private JPanel segmentationpn;
	private Sample sample;
	private JButton autothresholdbt;
	private JFormattedTextField roisthresholdtf;
	private JButton roisautothresholdbt;

	public AddSampleJDialog(ConfigurationJFrame parent, boolean modal)
	{
		super(parent, modal);

		sample = new Sample();

		filters = new ArrayList<Filter>();
		Filter eightfilter = new Filter();// always must apply this
		eightfilter.setIdfilter((short)1);
		eightfilter.setCommand("8-bit");
		eightfilter.setOptions("");
		filters.add(eightfilter);
		sample.setFilterList(filters);

		setResizable(false);
		this.parent = parent;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Add Sample");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(8, 8, 8, 8);
		constraints.anchor = GridBagConstraints.WEST;

		add(new JLabel("Name"), FluoJUtils.getConstraints(constraints, 0, 0, 1));
		nametf = new JTextField(20);
		add(nametf, FluoJUtils.getConstraints(constraints, 1, 0, 1));

		add(new JLabel("Image"), FluoJUtils.getConstraints(constraints, 0, 1, 1));
		imagetf = new JTextField(20);
		add(imagetf, FluoJUtils.getConstraints(constraints, 1, 1, 1));
		browsebt = new JButton("Browse");
		add(browsebt, FluoJUtils.getConstraints(constraints, 2, 1, 1));

		filterspn = new PreprocessingPane(parent, null, filters);
		add(filterspn, FluoJUtils.getConstraints(constraints, 0, 2, 3));

		initSegmentationPane();
		add(segmentationpn, FluoJUtils.getConstraints(constraints, 0, 3, 3));
		

		addbt = new JButton("Add");
		cancelbt = new JButton("Cancel");
		JPanel buttonspn = new JPanel();
		buttonspn.add(addbt);
		buttonspn.add(cancelbt);
		add(buttonspn, FluoJUtils.getConstraints(constraints, 0, 9, 3));
		setListeners();
		pack();
		FluoJUtils.setLocation(position, this);
		setVisible(true);

	}

	private void initSegmentationPane()
	{
		segmentationpn = new JPanel(new GridBagLayout());
		segmentationpn.setBorder(BorderFactory.createTitledBorder("Segmentation"));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(8, 8, 8, 8);
		segmentationpn.add(new JLabel("Threshold"), FluoJUtils.getConstraints(constraints, 0, 0, 1));
		thresholdtf = new JFormattedTextField(NumberFormat.getIntegerInstance());
		thresholdtf.setColumns(3);
		segmentationpn.add(thresholdtf, FluoJUtils.getConstraints(constraints, 1, 0, 1));
		autothresholdbt = new JButton("Auto");
		autothresholdbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setAutoThreshold();

			}
		});
		segmentationpn.add(autothresholdbt, FluoJUtils.getConstraints(constraints, 2, 0, 1));
		
		segmentationpn.add(new JLabel("ROIs Threshold"), FluoJUtils.getConstraints(constraints, 0, 1, 1));
		roisthresholdtf = new JFormattedTextField(NumberFormat.getIntegerInstance());
		roisthresholdtf.setColumns(3);
		segmentationpn.add(roisthresholdtf, FluoJUtils.getConstraints(constraints, 1, 1, 1));
//		roisautothresholdbt = new JButton("Auto");
//		roisautothresholdbt.addActionListener(new ActionListener()
//		{
//
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				setROIsAutoThreshold();
//
//			}
//		});
//		segmentationpn.add(roisautothresholdbt, WindowUtil.getConstraints(constraints, 2, 1, 1));

		segmentationpn.add(new JLabel("Fill Holes:"), FluoJUtils.getConstraints(constraints, 0, 2, 1));
		fillchb = new JCheckBox();
		fillchb.setSelected(true);
		segmentationpn.add(fillchb, FluoJUtils.getConstraints(constraints, 1, 2, 1));

		segmentationpn.add(new JLabel("Separate:"), FluoJUtils.getConstraints(constraints, 0, 3, 1));
		separations = ConfigurationDB.getIdseparations(parent.getEntityManager());
		separationscb = new JComboBox(separations.toArray());
		separationscb.setSelectedItem(separations.get(2));//none separation
		segmentationpn.add(separationscb, FluoJUtils.getConstraints(constraints, 1, 3, 1));

		segmentationpn.add(new JLabel("Expansion Radius:"), FluoJUtils.getConstraints(constraints, 0, 4, 1));
		expansionradiustf = new JFormattedTextField(NumberFormat.getIntegerInstance());
		expansionradiustf.setColumns(2);
		expansionradiustf.setValue(0);
		segmentationpn.add(expansionradiustf, FluoJUtils.getConstraints(constraints, 1, 4, 1));

		segmentationpn.add(new JLabel("ROIs Max:"), FluoJUtils.getConstraints(constraints, 0, 5, 1));
		roismaxtf = new JFormattedTextField(NumberFormat.getIntegerInstance());
		roismaxtf.setColumns(2);
		roismaxtf.setValue(0);
		segmentationpn.add(roismaxtf, FluoJUtils.getConstraints(constraints, 1, 5, 1));

	}

	
	private void setListeners()
	{

		thresholdtf.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (thresholdtf.getValue() == null)
					thresholdtf.setValue(autothreshold);

			}
		});

		browsebt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				getImageFile();

			}
		});

		addbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{

					String name = nametf.getText();

					if (name.equals("") || name == null)
						throw new IllegalArgumentException(Constants.getEmptyFieldMsg("Name"));
					
					String imagefile = imagetf.getText();
					if (imagefile.equals("") || imagefile == null)
						throw new IllegalArgumentException(Constants.getEmptyFieldMsg("Image"));
					if (roismaxtf.getValue() == null)
						throw new IllegalArgumentException(Constants.getEmptyFieldMsg("Inner Particles Max"));
					

					sample.setName(name);

					short threshold = ((Number) thresholdtf.getValue()).shortValue();
					sample.setImageThreshold(threshold);

					Short roisthreshold = (roisthresholdtf.getValue() != null)? ((Number) roisthresholdtf.getValue()).shortValue(): null;
					sample.setRoisThreshold(roisthreshold);
					sample.setRoisThreshold(((Number) roismaxtf.getValue()).shortValue());
					sample.setFillHoles((fillchb.isSelected()));
					sample.setIdseparation((Separation) separationscb.getSelectedItem());
					sample.setExpansionRadius(((Number) expansionradiustf.getValue()).shortValue());
					AddSampleJDialog.this.parent.addSample(sample, imp);

				}
				catch (IllegalArgumentException ex)
				{
					JOptionPane.showMessageDialog(AddSampleJDialog.this, ex.getMessage());
					return;
				}
				setVisible(false);
				dispose();
			}
		});
		cancelbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();

			}
		});
	}

	protected void getImageFile()
	{
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);

		try
		{
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
				imagetf.setText(file.getAbsolutePath());
				imp = FluoJImageProcessor.getProcessImage(filters, new ImagePlus(file.getAbsolutePath()));

			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage());
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}

	}

	void setAutoThreshold()
	{
		if (filterspn.isChanged())
			imp = FluoJImageProcessor.getProcessImage(filters, new ImagePlus(imagetf.getText()));
		if (imp == null)
			return;
		ImageProcessor ip = imp.duplicate().getProcessor().convertToByte(false);

		Entropy_Threshold thresholder = new Entropy_Threshold();
		thresholder.run(ip);
		autothreshold = thresholder.threshold;
		thresholdtf.setValue(autothreshold);
		if(roisthresholdtf.getValue() == null)
			roisthresholdtf.setValue(autothreshold + autothreshold/3);
	}
	
	protected void setROIsAutoThreshold()
	{
		// TODO Auto-generated method stub
		
	}


}
