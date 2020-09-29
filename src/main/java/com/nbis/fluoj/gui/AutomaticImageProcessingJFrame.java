package com.nbis.fluoj.gui;


import ij.ImagePlus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;



public class AutomaticImageProcessingJFrame extends FluoJJFrame {
	
	private JButton results_bt;
	private JButton processbt;
	List<Classifier> classifiers;
	private JTable table;
	private ClientClassifierTableModel model;
	private JButton imageresultsbt;
	ArrayList<SampleImageData> sids;
	
	

	public AutomaticImageProcessingJFrame() throws InvalidOperationOnResourceException
	{
		super();
		
		sids = new ArrayList<SampleImageData>();
		samples = cconfigurationdb.getSamples(em);
		for(Sample s: samples)
			sids.add(new SampleImageData(s, "", ""));
		setResizable(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("Automatic Image Processing");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		
		
		
		
		JLabel slide_lb = new JLabel("Specify your sample images path to process:");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.WEST;
		add(slide_lb, constraints);
//		JComboBox slide_cb = new JComboBox(new Object[]{"Halotech 8-wells"});
//		constraints.gridx = 1;
//		add(slide_cb, constraints);
		
		JPanel pane = new JPanel();
		pane.setBorder(javax.swing.BorderFactory.createTitledBorder("Process"));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		add(pane, constraints);
		constraints.gridwidth = 1;
		JScrollPane sp = new JScrollPane();
		table = new JTable();
		model = new ClientClassifierTableModel();
		
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting())
					return;
//				boolean state = "Done".equalsIgnoreCase((String)model.getValueAt(table.getSelectedRow(), 4));
				setEnabledSampleActions(table.getSelectedRow() != -1);
				if(table.getSelectedRow() != -1)
					classifier = classifiers.get(table.getSelectedRow());
				
			}
		});
		table.setModel(model);
		table.getColumn("Images Path").setCellEditor(new FileEditor(this));//setting special editor to display file chooser
		table.setPreferredScrollableViewportSize(new Dimension(700, 100));
		
		sp.setViewportView(table);
		pane.add(sp);	
		
		processbt = new JButton("Process");
		processbt.setEnabled(false);
		processbt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Classifier c = classifiers.get(table.getSelectedRow());
				//new ComparisonJFrame(c.sample, c.getIdsession());
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						try {
						Classifier c = classifiers.get(table.getSelectedRow());
//						TrainingViewJFrame f = new TrainingViewJFrame(c);
//						f.setVisible(true);
							
							List<ImagePlus> images = model.getImages(table.getSelectedRow());
							for(int j = 0; j < images.size(); j ++)
								c.classifyCellsOnImage(images.get(j));
						
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			});
				
			}
		});
		
		imageresultsbt = new JButton("Image Results");
		imageresultsbt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable()
				{

					@Override
					public void run()
					{
						try
						{

							ViewJFrame f = new ViewJFrame(classifier);
							f.setVisible(true);
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
			}
		});
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(processbt, constraints);
		
		
		results_bt = new JButton("Summary");
		results_bt.setEnabled(false);
		results_bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				showClassifiersOutput();
			}
		});
		
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.EAST;
		JPanel resultspn = new JPanel();
		resultspn.add(imageresultsbt);
		resultspn.add(results_bt);
		add(resultspn, constraints);
		pack();
		FluoJUtils.setLocation(locationx, this);
		setVisible(true);
		setEnabledSampleActions(false);
		initClassifiers();
	}
	
	private void setEnabledSampleActions(boolean enable) {
		statisticsmi.setEnabled(enable);
		resetdbmi.setEnabled(enable);
		trainmi.setEnabled(enable);
		processbt.setEnabled(enable);
		imageresultsbt.setEnabled(enable);
	}
	
	private void initClassifiers()
	{
		SampleImageData sid;
		Classifier classifier;
		classifiers = new ArrayList<Classifier>();
		List<ImagePlus> images;
		EntityManager em = ConfigurationDB.getEM();
		for(int i = 0; i < model.getRowCount(); i ++)
		{
			sid = model.getSampleImageData(i);
			classifier = new Classifier(sid.sample, sid.notes, new Date(), em);
			if( i == 0)
				results_bt.setEnabled(true);
			classifiers.add(classifier);
			images = model.getImages(i);
			for(int j = 0; j < images.size(); j ++)
				classifier.classifyCellsOnImage(images.get(j));
		}

	}
	
	private void showClassifiersOutput()
	{
		new ClassifiersOutputJDialog(this, classifiers);
		int i = 0;
		for(Classifier c: classifiers)
		{
			if(!(sids.get(i).dir == null || sids.get(i).dir.equals("")))
				new SessionHistogramsJDialog(AutomaticImageProcessingJFrame.this, c);
			i ++;
		}
	}
	
	// centers the dialog within the screen [1.1]
	
	
	@Override
	public FluoJImageProcessor getCImageProcess()	throws InvalidOperationOnResourceException {
		return null;
	}

	
	public static void main(String[] args)
	{
		try {
			new AutomaticImageProcessingJFrame();
		} catch (InvalidOperationOnResourceException e) {
			JOptionPane.showMessageDialog(null, e.getStackTrace());
		}
	}
	
	class ClientClassifierTableModel extends AbstractTableModel {

		String[] columnNames = { "Sample", "Images Path", "Notes" };

		

		
		public SampleImageData getSampleImageData(int rowIndex) {
			return sids.get(rowIndex);
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return sids.size();
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SampleImageData sid = sids.get(rowIndex);
			if(columnIndex == 0)
				return sid.sample;
			if(columnIndex == 1)
				return sid.dir;
			if(columnIndex == 2)
				return sid.notes;
						
			return null;
		}
		
		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			SampleImageData sid = sids.get(rowIndex);
			if(columnIndex == 0)
				sid.sample = (Sample)value;
			if(columnIndex == 1)
				sid.dir = (String)value;
			if(columnIndex == 2)
				sid.notes = (String)value;
			
		}

		public List<ImagePlus> getImages(int rowIndex) {
			SampleImageData sid = sids.get(rowIndex);
			String dir = sid.dir;
			File folder = new File(dir);
		    File[] listOfFiles = folder.listFiles();
			return getImages(listOfFiles);
		}

		private List<ImagePlus> getImages(File[] files) {
			List<ImagePlus> images = new ArrayList<ImagePlus>();
			if (files == null)
				return images;
			String path;
			ImagePlus image;

			for (int i = 0; i < files.length; i++) {
				if(files[i].isHidden())
					continue;
						
				path = files[i].getAbsolutePath();
				image = new ImagePlus(path);
				images.add(image);
			}
			return images;
		}

		
		
		@Override
		public boolean isCellEditable(int row, int column)
		{
			if(column > 0)
				return true; 
			return false;
		}

	}

	public void updateSID(String file)
	{
		sids.get(table.getSelectedRow()).dir = file;
		
	}

	


}
