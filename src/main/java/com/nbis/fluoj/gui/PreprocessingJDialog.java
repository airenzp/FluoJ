package com.nbis.fluoj.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.persistence.EntityManager;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.nbis.fluoj.classifier.ConfigurationDB;

public class PreprocessingJDialog extends JDialog
{

	private ConfigurationJFrame frame;
	private JButton okbt;
	private EntityManager em;
	private PreprocessingPane filterspn;

	public PreprocessingJDialog(ConfigurationJFrame frame, boolean modal, EntityManager em)
	{
		super(frame, modal);
		this.frame = frame;
		this.em = em;
		initComponents();
	}

	private void initComponents()
	{
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Preprocess");
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		setLayout(new GridBagLayout());
		filterspn = new PreprocessingPane(frame, frame.getSample(), frame.getSample().getSamplefilterList());
		add(filterspn, FluoJUtils.getConstraints(constraints, 0, 0, 1));
		okbt = new JButton("Ok");
		filterspn.addButton(okbt);
		addListeners();
		pack();
		FluoJUtils.setLocation(FluoJJFrame.locationx, this);
		setVisible(true);
	}

	private void addListeners()
	{

		okbt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(filterspn.isChanged())
				{
					int result = frame.resetDB();
					if(result == JOptionPane.YES_OPTION)
						ConfigurationDB.persist(frame.getSample().getSamplefilterList(), em);
				}
				setVisible(false);
				dispose();

			}
		});
		
		
	}

}
