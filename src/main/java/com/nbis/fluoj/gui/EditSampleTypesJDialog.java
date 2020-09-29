package com.nbis.fluoj.gui;

import java.awt.Color;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Type;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.Classifier;

public class EditSampleTypesJDialog extends JDialog implements ActionListener
{

	private EntityManager em;
	private JButton addbt;
	private JButton deletebt;
	private List<com.nbis.fluoj.persistence.Type> types;
	private JTable typestb;
	private TypesTableModel typesmd;
	private ConfigurationJFrame frame;
	private JButton okbt;
	private ConfigurationDB configurationdb;

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

	public EditSampleTypesJDialog(ConfigurationJFrame frame, boolean modal, EntityManager em)
	{
		super(frame, modal);
		this.frame = frame;
		this.em = em;
		this.configurationdb = ConfigurationDB.getInstance();
		types = frame.getSample().getTypeList();
		initComponents();
	}

	private void initComponents()
	{
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Types");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		JPanel samplespn = new JPanel(new GridBagLayout());
		samplespn.setBorder(BorderFactory.createTitledBorder("Types"));

		JScrollPane sp = new JScrollPane();
		samplespn.add(sp, FluoJUtils.getConstraints(constraints, 0, 0, 3));
		typestb = new JTable();
		typestb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		typesmd = new TypesTableModel();
		typestb.setModel(typesmd);
		typestb.setDefaultRenderer(Color.class, new ColorRenderer());
		typestb.setDefaultEditor(Color.class, new ColorEditor());
		typestb.setOpaque(true);

		sp.setViewportView(typestb);
		sp.setPreferredSize(new Dimension(350, 160));
		constraints.gridy = 1;
		add(samplespn, FluoJUtils.getConstraints(constraints, 0, 0, 1));
		JPanel buttonspn = new JPanel();
		addbt = new JButton("Add");
		buttonspn.add(addbt);
		deletebt = new JButton("Delete");
		deletebt.setEnabled(false);
		buttonspn.add(deletebt);
		okbt = new JButton("Ok");
		buttonspn.add(okbt);
		add(buttonspn, FluoJUtils.getConstraints(constraints, 0, 1, 1));
		addListeners();
		pack();
		FluoJUtils.setLocation(0.9, this);
		setVisible(true);
	}

	private void addListeners()
	{
		typestb.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				EditSampleTypesJDialog.this.deletebt.setEnabled(true);
			}
		});
		typestb.addKeyListener(new KeyListener()
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
				if (e.getKeyCode() == 127 && typestb.getSelectedRow() != -1)
					deleteSampleType();
			}
		});
		addbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				new AddSampleTypeJDialog(EditSampleTypesJDialog.this, true);
			}
		});
		deletebt.addActionListener(this);
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

	protected void deleteSampleType()
	{
		try
		{
			int result = frame.resetDB();
			if (result == JOptionPane.YES_OPTION)// edition allowed
			{
				int index = typestb.getSelectedRow();
				com.nbis.fluoj.persistence.Type t = types.get(index);
				configurationdb.remove(t, em);
				types.remove(index);

				typesmd.fireTableRowsDeleted(index, index);
				deletebt.setEnabled(false);
				frame.samplesmd.fireTableRowsUpdated(frame.getIndex(), frame.getIndex());
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(EditSampleTypesJDialog.this, ex.getMessage());
			Classifier.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}

	}

	class TypesTableModel extends AbstractTableModel
	{

		private String[] columns = new String[] { "Name", "Label", "Color", "Training Min" };

		public TypesTableModel()
		{
		}

		@Override
		public int getRowCount()
		{
			return types.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return true;
		}

		@Override
		public String getColumnName(int index)
		{
			return columns[index];
		}

		@Override
		public Class getColumnClass(int column)
		{
			return getValueAt(0, column).getClass();
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			com.nbis.fluoj.persistence.Type type = types.get(rowIndex);
			if (value == null || value.equals(""))
			{
				JOptionPane.showMessageDialog(EditSampleTypesJDialog.this, Constants.getEmptyFieldMsg(columns[columnIndex]));
				return;
			}

			if (columnIndex == 1 && ((String) value).length() > 1)
			{
				JOptionPane.showMessageDialog(EditSampleTypesJDialog.this, Constants
						.getIllegalValueMsg("Label", ((String) value), "Label should be a single character"));
				return;
			}
			if (columnIndex == 0)
				type.setName((String) value);
			else if (columnIndex == 1)
			{
				type.setLabel((String) value);
			}
			else if (columnIndex == 2)
				type.setColor(((Color) value).getRGB());
			else if (columnIndex == 3)
			{
				int trainingmin = (Integer) value;
				type.setTrainingMin(trainingmin);
			}
			types.set(types.indexOf(type), configurationdb.merge(type, em));
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{

			com.nbis.fluoj.persistence.Type t = types.get(rowIndex);

			if (columnIndex == 0)
				return t.getName();
			if (columnIndex == 1)
				return t.getLabel();
			if (columnIndex == 2)
				return new Color(t.getColor());
			if (columnIndex == 3)
				return t.getTrainingMin();
			return null;
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		deleteSampleType();
	}

	public void addType(com.nbis.fluoj.persistence.Type t)
	{
		int index = types.size();
		types.add(t);
		configurationdb.persist(t, em);
		typesmd.fireTableRowsInserted(index, index);
		frame.samplesmd.fireTableRowsUpdated(frame.getIndex(), frame.getIndex());
	}

	public Sample getSample()
	{
		return frame.getSample();
	}

}
