package com.nbis.fluoj.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * Used by {@link CLegendJDialog} to display colors on legend.
 * 
 * @author Airen
 * 
 */
public class ColorCellRenderer extends JLabel implements TableCellRenderer
{

    public ColorCellRenderer()
    {
	setOpaque(true); // MUST do this for background to show up.
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
	Color color = (Color) value;
	setBackground(color);
	setToolTipText("RGB value: " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue());

	return this;
    }

}
