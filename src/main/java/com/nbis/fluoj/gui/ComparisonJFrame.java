package com.nbis.fluoj.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableModel;

import com.nbis.fluoj.persistence.Sample;

import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;
import com.nbis.fluoj.classifier.ClassifierDB;

/**
 * Provides comparison matrix for Classifier. Main tool used to evaluate
 * training.
 * 
 * @author Airen
 * 
 */
public class ComparisonJFrame extends JFrame
{

	private Sample sample;
	private Classifier classifier;
	JButton okbt;
//	JButton export_opendxbt;
	JButton exportbt;
	private EntityManager em;

	public ComparisonJFrame(Classifier c, EntityManager em)
	{
		this.em = em;
		// Determines DB to use
		this.sample = c.getSample();
		this.classifier = c;
		initComponents();
	}

	public ComparisonJFrame(Sample sample, EntityManager em) throws InvalidOperationOnResourceException
	{
		this.em = em;

		// Determines DB to use
		this.sample = sample;
		this.classifier = ConfigurationDB.getInstance().getPersistentClassifier(sample);
		initComponents();
	}

	private void initComponents()
	{
		setResizable(false);
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		setTitle("Comparison of Results on Classifier");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.WEST;
		JPanel sessionpane = new JPanel();
		sessionpane.add(new JLabel(String.format("Sample:%s", sample)));
		sessionpane.add(new JLabel(String.format("Session:%s", classifier.getSession())));
		add(sessionpane, constraints);
		constraints.gridy = 1;
		JPanel pane = new JPanel();
		pane.setBorder(javax.swing.BorderFactory.createTitledBorder(getTitle()));
		add(pane, constraints);

		JScrollPane sp = new JScrollPane();
		JTable table = new JTable();
		ComparisonTableModel model = new ComparisonTableModel(classifier);
		table.setModel(model);
		table.setPreferredScrollableViewportSize(new Dimension(700, 200));
		sp.setViewportView(table);
		pane.add(sp, constraints);

		JPanel btspane = new JPanel();
		okbt = new JButton("Ok");

		okbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();
			}
		});
//		export_opendxbt = new JButton("Export to OpenDX");

//		export_opendxbt.addActionListener(new ActionListener()
//		{
//
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				exportToOpenDX();
//			}
//		});
//		btspane.add(export_opendxbt);
		exportbt = new JButton("Export Data");

		exportbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				exportData();
			}
		});
		btspane.add(exportbt);
		btspane.add(okbt);
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.EAST;
		add(btspane, constraints);
		pack();
		centerScreen();
		setVisible(true);
	}



	protected void exportData()
	{
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);

		try
		{
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
				classifier.exportAllData(file, em);
				JOptionPane.showMessageDialog(this, "Export Successfull");
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

	}

	// centers the dialog within the screen [1.1]
	public void centerScreen()
	{
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
	}

}
