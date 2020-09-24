package com.nbis.fluoj.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Samplefilter;
import com.nbis.fluoj.classifier.ConfigurationDB;

public class PreprocessingPane extends JPanel
{

	private List<Samplefilter> filters;
	private JTable filterstb;
	private FiltersTableModel filtersmd;
	private ConfigurationJFrame frame;
	private JButton addbt;
	private JButton deletebt;
	private JPanel buttonspn;
	private boolean changed = false;
	private Sample sample;

	public PreprocessingPane(ConfigurationJFrame frame, Sample sample, List<Samplefilter> filters)
	{
		this.sample = sample;
		this.frame = frame;
		this.filters = filters;
		initComponents();
	}

	List<Samplefilter> getFilters()
	{
		return filters;
	}

	public ConfigurationJFrame getConfigurationJFrame()
	{
		return frame;
	}

	private void initComponents()
	{
		setBorder(BorderFactory.createTitledBorder("Preprocess"));
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		setLayout(new GridBagLayout());

		JScrollPane sp = new JScrollPane();
		add(sp, FluoJUtils.getConstraints(constraints, 0, 0, 3));
		filterstb = new JTable();
		filterstb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filterstb.setOpaque(true);
		filtersmd = new FiltersTableModel();
		filterstb.setModel(filtersmd);
		sp.setViewportView(filterstb);
		sp.setPreferredSize(new Dimension(400, 160));
		constraints.gridy = 1;
		buttonspn = new JPanel();
		addbt = new JButton("Add");
		buttonspn.add(addbt);
		deletebt = new JButton("Delete");
		buttonspn.add(deletebt);

		add(buttonspn, FluoJUtils.getConstraints(constraints, 0, 1, 1));
		addListeners();
		setVisible(true);
	}

	private void addListeners()
	{

		addbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				new AddSampleFilterJDialog(PreprocessingPane.this, true);

			}
		});
		deletebt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				deleteSamplefilter();

			}
		});

	}

	void addButton(JButton bt)
	{
		buttonspn.add(bt);
	}

	private void deleteSamplefilter()
	{
		int index = filterstb.getSelectedRow();
		Samplefilter sf = filters.get(index);
		if (sf.getCommand().equals("8-bit"))
			return;

		filters.remove(sf);
		if(sample != null)
			ConfigurationDB.removeSampleFilterOnDB(sf, frame.getEntityManager());
		deletebt.setEnabled(false);
		filtersmd.fireTableRowsDeleted(index, index);
		changed = true;
		frame.resetCImageProcess();
	}

	public void addSamplefilter(Samplefilter sf)
	{
		int index = filters.size();
		filters.add(sf);

		filtersmd.fireTableRowsInserted(index, index);
		changed = true;
		frame.resetCImageProcess();
	}
	
	public Boolean isChanged()
	{
		return changed;
	}

	class FiltersTableModel extends AbstractTableModel
	{

		private String[] columns = new String[] { "Command", "Options" };

		@Override
		public int getRowCount()
		{
			return filters.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int index)
		{
			return columns[index];
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return true;
		}

		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			String text = (String) value;
			Samplefilter f = filters.get(rowIndex);
			if (columnIndex == 0)
				f.setCommand(text);
			if (columnIndex == 1)
				f.setCommandoptions(text);
			

		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			Samplefilter f = filters.get(rowIndex);
			if (columnIndex == 0)
				return f.getCommand();
			if (columnIndex == 1)
				return f.getCommandoptions();
			return null;

		}

	}

}
