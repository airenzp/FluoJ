package com.nbis.fluoj.gui;

import ij.IJ;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;

import javax.swing.JButton;

public class FluoJUtils
{

	public static void setLocation(double position, Container w)
	{
		Dimension dim = w.getToolkit().getScreenSize();
		Rectangle abounds = w.getBounds();
		int x = (int) (position * (dim.width - abounds.width));
		int y = (dim.height - abounds.height) / 2;
		w.setLocation(x, y);

	}

	public static void setLocation(double positionx, double positiony, Container w)
	{
		Dimension dim = w.getToolkit().getScreenSize();
		Rectangle abounds = w.getBounds();
		int x = (int) (positionx * (dim.width - abounds.width));
		int y = (int) (positiony * (dim.height - abounds.height));
		w.setLocation(x, y);

	}

	public static GridBagConstraints getConstraints(GridBagConstraints constraints, int x, int y, int columns)
	{
		return getConstraints(constraints, x, y, columns, (columns > 1) ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE);
	}

	public static GridBagConstraints getConstraints(GridBagConstraints constraints, int x, int y, int columns, int fill)
	{
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = columns;
		constraints.fill = fill;
		return constraints;
	}

	public static void openURI(String string)
	{
		// TODO Auto-generated method stub

	}

	public static void addFluoJTool()
	{

		// System.out.println("adding FluoJ tool");
		if (IJ.getInstance() != null)
		{
			IJ.run("Install...", "install=plugins/FluoJ/FluoJMacros.txt");
			IJ.setTool("FluoJ Tool");
		}
	}
}
