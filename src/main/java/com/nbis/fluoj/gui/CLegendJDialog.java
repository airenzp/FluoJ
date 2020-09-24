package com.nbis.fluoj.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

/**
 * Displays {@link persistence.Type} colors legend on a table.
 * 
 * @author Airen
 *
 */
public class CLegendJDialog extends JDialog {
	
	public static void main(String[] args)
	{
		CLegendJDialog dialog;
		try {
			dialog = new CLegendJDialog(null, ConfigurationDB.getInstance().getRandomClassifier());
			dialog.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	
	public CLegendJDialog(JFrame parent, Classifier c)
	{
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Classifier Legend");
		JPanel pane = new JPanel();
		pane.setBorder(javax.swing.BorderFactory.createTitledBorder("Classifier Legend"));
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.gridx = 0;
		constraints.gridy = 0;
		JScrollPane sp = new JScrollPane();
		JTable table = new JTable(new CLegendTableModel(c));
		sp.setOpaque(true);
		table.setDefaultRenderer(Color.class, new ColorCellRenderer());
		sp.setViewportView(table);
		table.setPreferredScrollableViewportSize(new Dimension(400, 200));
		pane.add(sp);
		add(pane, constraints);
		
		JButton ok_bt = new JButton("Ok");
		ok_bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		constraints.gridy = 1;
		add(ok_bt, constraints);
		pack();
		centerScreen();
		setVisible(true);
	}
	
	protected void closeDialog() {
		setVisible(false);
		dispose();
	}


	// centers the dialog within the screen
	public void centerScreen() {
	  Dimension dim = getToolkit().getScreenSize();
	  Rectangle abounds = getBounds();
	  setLocation((dim.width - abounds.width) / 2,
	      (dim.height - abounds.height) / 2);
	}

}
