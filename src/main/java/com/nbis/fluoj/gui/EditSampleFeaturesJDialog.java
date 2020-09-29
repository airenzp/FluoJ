package com.nbis.fluoj.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

public class EditSampleFeaturesJDialog extends JDialog
{

	private EntityManager em;
	private JTable sfeaturestb;
	private FeaturesTableModel sfeaturesmd;
	private FluoJJFrame frame;
	private JButton okbt;
	private List<SampleFeature> sfeatures;
	private JButton addbt;
	private JButton deletebt;
	private int features;
	private Sample sample;
	private JTable scorefeaturestb;
	private CoreFeaturesTableModel scorefeaturesmd;
	private List<SampleFeature> scorefeatures;
	private JButton addcorebt;
	private JButton deletecorebt;
	private double positionx = 0.5;

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					ConfigurationJFrame frame = new ConfigurationJFrame();
				}
				catch (Exception e)
				{
					Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});
	}

	public EditSampleFeaturesJDialog(FluoJJFrame frame, boolean modal, EntityManager em)
	{
		super(frame, modal);
		this.frame = frame;
		this.em = em;
		sample = frame.getSample();
		sfeatures = frame.getSample().getSampleFeatureList();
		scorefeatures = frame.getSample().getSampleFeatureList();
		initComponents();
	}

	private void initComponents()
	{
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Features");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 5, 5);
		JPanel samplespn = new JPanel(new GridBagLayout());
		samplespn.setBorder(BorderFactory.createTitledBorder("Features"));

		JScrollPane sp = new JScrollPane();
		sp.setBorder(BorderFactory.createTitledBorder("Particle Features"));
		samplespn.add(sp, FluoJUtils.getConstraints(constraints, 0, 0, 3));
		sfeaturestb = new JTable();
		sfeaturestb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sfeaturestb.setOpaque(true);
		sfeaturesmd = new FeaturesTableModel();
		sfeaturestb.setModel(sfeaturesmd);
		
		sfeaturestb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sfeaturestb.getColumnModel().getColumn(0).setPreferredWidth(150);// Name
		sfeaturestb.getColumnModel().getColumn(1).setPreferredWidth(340);// Description
		sfeaturestb.getColumnModel().getColumn(2).setPreferredWidth(80);// Min
		sfeaturestb.getColumnModel().getColumn(3).setPreferredWidth(80);// Max
		sfeaturestb.getColumnModel().getColumn(4).setPreferredWidth(150);//
		sp.setViewportView(sfeaturestb);
		sfeaturestb.setPreferredScrollableViewportSize(new Dimension(800, 300));
		
		JPanel buttonspn = new JPanel();
		if (frame instanceof ConfigurationJFrame)
		{

			addbt = new JButton("Add");
			features = ConfigurationDB.getInstance().getFeatureTotal(em);
			if (sfeaturesmd.getRowCount() == features)
				addbt.setEnabled(false);
			buttonspn.add(addbt);
			addbt.setEnabled(frame instanceof ConfigurationJFrame);
			
			deletebt = new JButton("Delete");
			deletebt.setEnabled(false);
			buttonspn.add(deletebt);

			addbt.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						new AddSampleFeatureJDialog(EditSampleFeaturesJDialog.this, false, em);
					}
					catch (InvalidOperationOnResourceException e1)
					{
						JOptionPane.showMessageDialog(EditSampleFeaturesJDialog.this, e1.getMessage());
					}
				}
			});
			deletebt.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					deleteSampleFeature();
				}
			});
		}
		samplespn.add(buttonspn, FluoJUtils.getConstraints(constraints, 0, 1, 3));

		JScrollPane spcorefeatures = new JScrollPane();
		spcorefeatures.setBorder(BorderFactory.createTitledBorder("ROIS Features"));
		samplespn.add(spcorefeatures, FluoJUtils.getConstraints(constraints, 0, 2, 3));
		scorefeaturestb = new JTable();
		scorefeaturestb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scorefeaturestb.setOpaque(true);
		scorefeaturesmd = new CoreFeaturesTableModel();
		scorefeaturestb.setModel(scorefeaturesmd);
		scorefeaturestb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scorefeaturestb.getColumnModel().getColumn(0).setPreferredWidth(150);// Name
		scorefeaturestb.getColumnModel().getColumn(1).setPreferredWidth(340);// Description
		scorefeaturestb.getColumnModel().getColumn(2).setPreferredWidth(80);// Min
		scorefeaturestb.getColumnModel().getColumn(3).setPreferredWidth(80);// Max
		spcorefeatures.setViewportView(scorefeaturestb);
		scorefeaturestb.setPreferredScrollableViewportSize(new Dimension(650, 100));
		constraints.gridy = 1;
		add(samplespn, FluoJUtils.getConstraints(constraints, 0, 1, 1));

		JPanel corebuttonspn = new JPanel();
		if (frame instanceof ConfigurationJFrame)
		{

			addcorebt = new JButton("Add");
//			features = ConfigurationDB.getInstance().getFeatureTotal(em);
//			if (sfeaturesmd.getRowCount() == features)
//				addbt.setEnabled(false);
			corebuttonspn.add(addcorebt);
			addcorebt.setEnabled(frame instanceof ConfigurationJFrame);
			
			deletecorebt = new JButton("Delete");
			deletecorebt.setEnabled(false);
			corebuttonspn.add(deletecorebt);

			addcorebt.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						new AddSampleROIFeatureJDialog(EditSampleFeaturesJDialog.this, false, em);
					}
					catch (InvalidOperationOnResourceException e1)
					{
						JOptionPane.showMessageDialog(EditSampleFeaturesJDialog.this, e1.getMessage());
					}
				}
			});
			deletecorebt.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					deleteSampleCoreFeature();
				}
			});
		}
		samplespn.add(corebuttonspn, FluoJUtils.getConstraints(constraints, 0, 3, 3));
		
		
		okbt = new JButton("Ok");
		add(okbt, FluoJUtils.getConstraints(constraints, 0, 3, 1));
		
		addListeners();
		pack();
		FluoJUtils.setLocation(positionx, 0.1, this);
		setVisible(true);
	}

	private void addListeners()
	{
		sfeaturestb.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (frame instanceof ConfigurationJFrame)
				{
					if (sfeaturestb.getSelectedRow() != -1)
						deletebt.setEnabled(true);
					if (sfeaturesmd.getRowCount() == features)
						addbt.setEnabled(false);
				}
			}
		});

		sfeaturestb.addKeyListener(new KeyListener()
		{

			@Override
			public void keyTyped(KeyEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == 127 && sfeaturestb.getSelectedRow() != -1)
					deleteSampleFeature();
			}
		});
		
		scorefeaturestb.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (frame instanceof ConfigurationJFrame)
				{
					if (scorefeaturestb.getSelectedRow() != -1)
						deletecorebt.setEnabled(true);
//					if (scorefeaturesmd.getRowCount() == features)
//						addbt.setEnabled(false);
				}
			}
		});

		scorefeaturestb.addKeyListener(new KeyListener()
		{

			@Override
			public void keyTyped(KeyEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == 127 && scorefeaturestb.getSelectedRow() != -1)
					deleteSampleCoreFeature();
			}
		});

		okbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();

			}
		});

	}

	private void deleteSampleFeature()
	{
		int result = frame.resetDB();
		if (result == JOptionPane.YES_OPTION)
			try
			{
				int index = sfeaturestb.getSelectedRow();
				SampleFeature sf = sfeatures.get(index);
				sfeatures.remove(sf);
				ConfigurationDB.removeSampleFeature(sf, em);
				deletebt.setEnabled(false);
				sfeaturesmd.fireTableRowsDeleted(index, index);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(EditSampleFeaturesJDialog.this, ex.getMessage());
			}
	}
	
	private void deleteSampleCoreFeature()
	{
		int result = frame.resetDB();
		if (result == JOptionPane.YES_OPTION)
			try
			{
				int index = scorefeaturestb.getSelectedRow();
				SampleFeature sf = scorefeatures.get(index);
				scorefeatures.remove(sf);
				ConfigurationDB.remove(sf, em);
				deletecorebt.setEnabled(false);
				scorefeaturesmd.fireTableRowsDeleted(index, index);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(EditSampleFeaturesJDialog.this, ex.getMessage());
			}
	}

	class FeaturesTableModel extends AbstractTableModel
	{

		private String[] columns = new String[] { "Name", "Description", "Min", "Max", "Use on Classifier" };
		
	

		@Override
		public int getRowCount()
		{
			return sfeatures.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public Class getColumnClass(int column)
		{
			return getValueAt(0, column).getClass();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (columnIndex == 4)
				return true;
			if (columnIndex > 1 && frame instanceof ConfigurationJFrame)
				return true;
			return false;
		}

		@Override
		public String getColumnName(int index)
		{
			return columns[index];
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			SampleFeature f = sfeatures.get(rowIndex);
			if (columnIndex == 4)
			{

				Boolean use = (Boolean) value;
				f.setActive(use);

				sfeatures.set(sfeatures.indexOf(f), (SampleFeature)ConfigurationDB.merge(f, em));
				if (!frame.cconfigurationdb.isEmpty(sample))
				{
					int result = JOptionPane
							.showConfirmDialog(EditSampleFeaturesJDialog.this, "This operation changes classification training. \nDo you want to train classifier now ?", "Message", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION)
						frame.trainClassifier();
				}

			}
			else
			{
				int result = frame.resetDB();
				if (result == JOptionPane.YES_OPTION)// edition allowed
				{

					if (value == null || value.equals(""))
					{
						JOptionPane.showMessageDialog(EditSampleFeaturesJDialog.this, Constants.getEmptyFieldMsg(columns[columnIndex]));
						return;
					}
					if (columnIndex < 4)
					{
						double d = Double.parseDouble((String) value);
						if (columnIndex == 2)
							f.setMin(d);
						else if (columnIndex == 3)
						{
							f.setMax(d);
						}
					}
					sfeatures.set(sfeatures.indexOf(f), (SampleFeature)ConfigurationDB.merge(f, em));
				}
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{

			SampleFeature f = sfeatures.get(rowIndex);

			if (columnIndex == 0)
				return f.getFeature();
			if (columnIndex == 1)
				return f.getFeature().getDescription();
			if (columnIndex == 2)
				return String.format("%.2f", f.getMin());
			if (columnIndex == 3)
				return String.format("%.2f", f.getMax());
			if (columnIndex == 4)
				return f.getActive();

			return null;
		}

	}

	class CoreFeaturesTableModel extends AbstractTableModel
	{

		private String[] columns = new String[] { "Name", "Description", "Min", "Max" };

		@Override
		public int getRowCount()
		{
			return scorefeatures.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public Class getColumnClass(int column)
		{
			return getValueAt(0, column).getClass();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (columnIndex > 1 && frame instanceof ConfigurationJFrame)
				return true;
			return false;
		}

		@Override
		public String getColumnName(int index)
		{
			return columns[index];
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			SampleFeature f = scorefeatures.get(rowIndex);

			int result = frame.resetDB();
			if (result == JOptionPane.YES_OPTION)// edition allowed
			{

				if (value == null || value.equals(""))
				{
					JOptionPane.showMessageDialog(EditSampleFeaturesJDialog.this, Constants.getEmptyFieldMsg(columns[columnIndex]));
					return;
				}

				double d = Double.parseDouble((String) value);
				if (columnIndex == 2)
					f.setMin(d);
				else if (columnIndex == 3)
				{
					f.setMax(d);
				}

				scorefeatures.set(scorefeatures.indexOf(f), (SampleFeature)ConfigurationDB.merge(f, em));
				frame.resetCImageProcess();
			}

		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{

			SampleFeature f = scorefeatures.get(rowIndex);

			if (columnIndex == 0)
				return f.getFeature().getName();
			if (columnIndex == 1)
				return f.getFeature().getDescription();
			if (columnIndex == 2)
				return String.format("%.2f", f.getMin());
			if (columnIndex == 3)
				return String.format("%.2f", f.getMax());
			

			return null;
		}

	}

	public List<SampleFeature> getSampleFeatures()
	{
		return sfeatures;
	}

	public Sample getSample()
	{
		return frame.getSample();
	}

	public void addSampleFeature(SampleFeature sf)
	{
		int index = sfeatures.size();
		sfeatures.add(sf);
		ConfigurationDB.persist(sf, em);
		sfeaturesmd.fireTableRowsInserted(index, index);

	}

	

	public double getLocationX()
	{
		return positionx ;
	}

}
