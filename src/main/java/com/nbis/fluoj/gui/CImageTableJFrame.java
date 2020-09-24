package com.nbis.fluoj.gui;

import ij.ImagePlus;
import ij.gui.ImageWindow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import com.nbis.fluoj.persistence.*;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;
import com.nbis.fluoj.classifier.CellTypeProbability;

/**
 * Displays {@link persistence.Scell} info including {@link persistence.Scellfeature}, {@link persistence.Ftprobability}
 * @author Airen
 *
 */
public class CImageTableJFrame extends JFrame {
	
	private Classifier classifier;
	private List<Scell> cells;
	private EntityManager em;
	
	
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
	
	
	public CImageTableJFrame(Classifier classifier, int idimage, Integer original, Integer winner)
	{
		try
		{
			this.classifier = classifier;
			em = ConfigurationDB.getEM();
			this.cells = classifier.getScells(idimage, original, winner, em);
			initComponents();
		}
		catch(Exception e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public CImageTableJFrame(List<Scell> cells)
	{
		try
		{
			this.cells = cells;
			initComponents();
		}
		catch(Exception e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private void initComponents()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Cells Info");
		JScrollPane sp = new JScrollPane();
		JTable table = new JTable();
		table.setOpaque(true);
		table.setModel(new CellsTableModel(classifier, cells, em));
		em.close();
		table.setPreferredScrollableViewportSize(new Dimension(1000, 500));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sp.setViewportView(table);
		add(sp);
		pack();
		centerScreen();
		setVisible(true);
	}
	
	public CImageTableJFrame(Classifier classifier, int idimage)
	{
		try
		{
			this.em = ConfigurationDB.getEM();
			this.classifier = classifier;
			this.cells = classifier.getScells(idimage, em);
			initComponents();
		}
		catch(Exception e)
		{
			Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	// centers the dialog within the screen
	public void centerScreen() {
	  Dimension dim = getToolkit().getScreenSize();
	  Rectangle abounds = getBounds();
	  int x = 99 * (dim.width - abounds.width) / 100;
	  int y = 99 * (dim.height - abounds.height) / 100;
	  setLocation(x,y);
	}
	
	class CellsTableModel extends AbstractTableModel
	{
		private List<Scell> scells;
		private List<String> column_names;
		private List<Feature> features;
		List<List<CellTypeProbability>> probs;
		List<com.nbis.fluoj.persistence.Type> types;
		private Object[][] rows;
		
		
		public CellsTableModel(Classifier c, List<Scell> cells, EntityManager em)
		{
			features = c.getFeatures(em);
			types = c.getSample().getTypeList();
			this.scells = cells;
			column_names = new ArrayList<String>();
			column_names.add("Id");
			column_names.add("Session");
			column_names.add("Date");
			column_names.add("Image");
			column_names.add("Class");
			column_names.add("Winner");
			column_names.add("X");
			column_names.add("Y");
			for(int i = 0; i < features.size(); i ++)
				column_names.add(features.get(i).getFeature());
			column_names.add("Probability");
			
			probs = new ArrayList<List<CellTypeProbability>>();
			for(int i = 0; i < cells.size(); i ++)
				probs.add(c.getProbs(cells.get(i).getIdscell(), em));
			fillRows();
			//c.close();
			
		}
		
		@Override
		public String getColumnName(int index)
		{
			return column_names.get(index);
		}

		@Override
		public int getColumnCount() {
			return column_names.size();
		}

		@Override
		public int getRowCount() {
			return rows.length;
		}

		@Override
		public Object getValueAt(int y, int x) {
			
			return rows[y][x];
		}
		
		
		private void fillRows()
		{
			int magnification = types.size() + 1;
			rows = new Object[scells.size() * magnification][column_names.size()];
			Scell s;
			int index;
			Double prob;
			NumberFormat formatter;
			Integer idtype = 0;
			List<CellTypeProbability> stprobs;
			for(int y = 0, j = 0; y < rows.length - magnification + 1; y += magnification, j ++)
			{
				s = scells.get(j);
				rows[y][0] = s.getIdscell();
				rows[y][1] = s.getSession().getIdsession();
				rows[y][2] = s.getDate();
				rows[y][3] = s.getImageresource().getName();
				rows[y][4] = (s.getType() != null)? s.getType().getName(): null;
				rows[y][5] = (s.getType1() != null) ? s.getType1().getName(): null;
				rows[y][6] = s.getXPosition();
				rows[y][7] = s.getYPosition();
				for(int x = 8, i = 0; x < 8 + features.size(); x ++, i ++)
					rows[y][x] = getFeatureValue(s.getScellfeatureList(), features.get(i).getIdfeature());
				for(int k = 0; k < types.size(); k ++)
				{
					rows[y + k + 1][4] = types.get(k).getName();
					for(int x = 8, i = 0; x < 8 + features.size(); x ++, i ++)
					{
						prob = classifier.getProbability(s.getIdscell(), features.get(i).getIdfeature(), types.get(k).getIdtype(), em);
						if(prob != null)
						{
							formatter = new DecimalFormat("0.##E0");
							rows[y + k + 1][x] =  formatter.format(prob);
						}
					}
					idtype = types.get(k).getIdtype();
					stprobs = probs.get(j);
					index = stprobs.indexOf(new CellTypeProbability(idtype));
					if(index != -1)
					{
						prob = stprobs.get(index).probability;
						formatter = new DecimalFormat("0.##E0");
						rows[y + k + 1][ 8 + features.size()] =  formatter.format(prob);
					}
				}
				
				
			}
		}
		
		private Object getFeatureValue(Collection<Scellfeature> values, Integer idfeature)
		{
			Iterator<Scellfeature> iter = values.iterator();
			Scellfeature ssf;
			while(iter.hasNext())
			{
				ssf = iter.next();
				if(ssf.getFeature().getIdfeature().equals(idfeature))
						return String.format("%.2f", ssf.getValue());
			}
			return null;
		}
		
		
		
		
		
	}

}
