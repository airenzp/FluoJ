package com.nbis.fluoj.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.nbis.fluoj.persistence.Filter;

public class AddSampleFilterJDialog extends JDialog {
	
	private JButton addbt;
	private JButton cancelbt;
	private JTextField commandtf;
	private ConfigurationJFrame parent;
	private JTextField optionstf;
	protected JColorChooser colorChooser;
	private List<Filter> filters;
	private PreprocessingPane filtersPane;
	private ConfigurationJFrame frame;
	
	public AddSampleFilterJDialog(PreprocessingPane filtersPane, boolean modal) {
		super(filtersPane.getConfigurationJFrame(), modal);
		this.filtersPane = filtersPane;
		this.frame = filtersPane.getConfigurationJFrame();
		filters = filtersPane.getFilters();
		setResizable(false);
		this.parent = filtersPane.getConfigurationJFrame();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Add Filter");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.WEST;

		add(new JLabel("Command"),
				FluoJUtils.getConstraints(constraints, 0, 0, 1));
		commandtf = new JTextField(20);		
		add(commandtf, FluoJUtils.getConstraints(constraints, 1, 0, 1));
		
		add(new JLabel("Options"),
				FluoJUtils.getConstraints(constraints, 0, 1, 1));
		optionstf = new JTextField(20);
		add(optionstf, FluoJUtils.getConstraints(constraints, 1, 1, 1));
		
		addbt = new JButton("Add");
		cancelbt = new JButton("Cancel");

		add(addbt, FluoJUtils.getConstraints(constraints, 0, 3, 1));
		add(cancelbt, FluoJUtils.getConstraints(constraints, 1, 3, 1));
		setListeners();
		pack();
		FluoJUtils.setLocation(FluoJJFrame.locationx, this);
		setVisible(true);
	}

	private void setListeners() {
		
		addbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String command = commandtf.getText();
				
				if (command.equals("")) {
					JOptionPane.showMessageDialog(AddSampleFilterJDialog.this,
							Constants.getEmptyFieldMsg("Command"));
					return;
				}
				String options = optionstf.getText();
				Filter sf = new Filter();
				sf.setCommand(command);
				sf.setOptions(options);
				if(frame.getSample() != null)
                                    sf.setIdsample(frame.getSample());
				try
				{
					filtersPane.addFilter(sf);
				}
				catch(IllegalArgumentException ex)
				{
					JOptionPane.showMessageDialog(AddSampleFilterJDialog.this, ex.getMessage());
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
