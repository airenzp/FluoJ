package com.nbis.fluoj.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Type;

public class AddSampleTypeJDialog extends JDialog {
	
	private JButton addbt;
	private JButton cancelbt;
	private JTextField nametf;
	private double position = 0.9;
	EditSampleTypesJDialog parent;
	private JTextField labeltf;
	private JComboBox dtypecb;
	private JButton colorbt;
	private Color color;
	protected JColorChooser colorChooser;
	private JFormattedTextField tmintf;
	
	private static Color[] colors = new Color[]{
		Color.BLUE, 
		Color.CYAN, 
		Color.MAGENTA, Color.ORANGE, 
		Color.PINK, Color.RED};

	public AddSampleTypeJDialog(EditSampleTypesJDialog parent, boolean modal) {
		super(parent, modal);
		setResizable(false);
		this.parent = parent;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Add Type");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.WEST;

		add(new JLabel("Name"),
				FluoJUtils.getConstraints(constraints, 0, 0, 1));
		nametf = new JTextField(20);		
		add(nametf, FluoJUtils.getConstraints(constraints, 1, 0, 1));
		
		add(new JLabel("Label"),
				FluoJUtils.getConstraints(constraints, 0, 1, 1));
		labeltf = new JTextField(10);
		add(labeltf, FluoJUtils.getConstraints(constraints, 1, 1, 1));
		add(new JLabel("Color"), FluoJUtils.getConstraints(constraints, 0, 2, 1));
		colorbt = new JButton();
		int index = (int) Math.floor(Math.random() * colors.length);
		color = colors[index];
		colorbt.setIcon(new ColorIcon(color));
		colorbt.setBorderPainted(false);
		add(colorbt, FluoJUtils.getConstraints(constraints, 1, 2, 1));
		add(new JLabel("Training Min"), FluoJUtils.getConstraints(constraints, 0, 3, 1));
		tmintf = new JFormattedTextField(NumberFormat.getIntegerInstance());
		tmintf.setValue(100);
		tmintf.setColumns(3);
		add(tmintf, FluoJUtils.getConstraints(constraints, 1, 3, 1));
		
		
		
		addbt = new JButton("Add");
		cancelbt = new JButton("Cancel");

		add(addbt, FluoJUtils.getConstraints(constraints, 0, 4, 1));
		add(cancelbt, FluoJUtils.getConstraints(constraints, 1, 4, 1));
		setListeners();
		pack();
		FluoJUtils.setLocation(position, this);
		setVisible(true);
	}

	private void setListeners() {
		colorbt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Set up the dialog that the button brings up.
				colorChooser = new JColorChooser();
				JDialog dialog = JColorChooser.createDialog(colorbt, "Pick a Color", true, // modal
						colorChooser, new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								AddSampleTypeJDialog.this.color = AddSampleTypeJDialog.this.colorChooser.getColor();
								AddSampleTypeJDialog.this.colorbt.setIcon(new ColorIcon(color));
							}
						}, // OK button handler
						null); // no CANCEL button handler
				dialog.setVisible(true);
			}

		});

		addbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nametf.getText();
				
				if (name.equals("")) 
					throw new IllegalArgumentException(Constants.getEmptyFieldMsg("Name"));
				String label = labeltf.getText();
				if(label.equals("") )
					throw new IllegalArgumentException(Constants.getEmptyFieldMsg("Label"));
				if(label.length() > 1 )
					throw new IllegalArgumentException(Constants.getIllegalValueMsg("Label", label, "Label should be a single character"));
				
				if (tmintf.getValue() == null)
					throw new IllegalArgumentException(Constants.getEmptyFieldMsg("Training Min"));
				com.nbis.fluoj.persistence.Type t = new com.nbis.fluoj.persistence.Type();
				t.setName(name);
				t.setLabel(label);
				t.setSample(AddSampleTypeJDialog.this.parent.getSample());
				t.setColor(color.getRGB());
				int trainingmin = ((Number) tmintf.getValue()).intValue();
				t.setTrainingmin(trainingmin);
				try
				{
					AddSampleTypeJDialog.this.parent.addType(t);
				}
				catch(IllegalArgumentException ex)
				{
					JOptionPane.showMessageDialog(AddSampleTypeJDialog.this, ex.getMessage());
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
