package com.nbis.fluoj.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


//On Edition should catch event and display File Chooser for Images Path
public class FileEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	
	private String file;
	private JFileChooser fchooser;
	private JButton browsebt;
	private AutomaticImageProcessingJFrame parent;

	public FileEditor(AutomaticImageProcessingJFrame parent) {
		this.parent = parent;
		browsebt = new JButton();
		browsebt.setBorderPainted(false);
		browsebt.setBackground(Color.white);
		browsebt.addActionListener(this);
		fchooser = new JFileChooser();
		fchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	

	/**
	 * Handles events from the editor button and from the dialog's OK button.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			
			int result = fchooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				file = fchooser.getSelectedFile().getAbsolutePath();
				browsebt.setText(file);
				parent.updateSID(file);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}
	}

	// Implement the one CellEditor method that AbstractCellEditor doesn't.
	public Object getCellEditorValue() {
		return file;
	}

	// Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		file = (String) value;
		browsebt.setText(file);
		return browsebt;
	}

}