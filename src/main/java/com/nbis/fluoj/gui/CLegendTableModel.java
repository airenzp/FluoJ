package com.nbis.fluoj.gui;

import java.awt.Color;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.table.AbstractTableModel;

import com.nbis.fluoj.persistence.Type;

import com.nbis.fluoj.classifier.Classifier;


/**
 * Defines table model for {@link gui.CLegendJDialog} table
 * 
 * @author Airen
 *
 */
public class CLegendTableModel extends AbstractTableModel {
	
	String[] column_names;
	Object[][] rows;
	List<Type> types;
	public static final Color[] colors = new Color[]{
													new Color(255, 255, 0),
													new Color(100, 100, 100),
													new Color(200, 200, 200),
													new Color(50, 50, 50),
													new Color(250, 250, 250),
													new Color(0, 217, 217), 
													new Color(255, 0, 128),  
													new Color(100, 200, 0), 
													new Color(94, 187, 232), 
													new Color(0, 0, 255),
													new Color(150, 150, 150)};
	public static final Color none_color = new Color(64, 128, 128);
	
	public CLegendTableModel(Classifier c)
	{
		column_names = new String[]{"Class", "Label", "Color", "Id"};
		types = c.getSample().getTypeList();
		rows = new Object[types.size() + 1][column_names.length];
		Type st;
		int i;
		for( i = 0; i < types.size(); i ++)
		{
			st = types.get(i);
			rows[i][1] = st.getLabel();
			rows[i][0] = st.getName();
			rows[i][2] = getColor(st.getIdtype());
			rows[i][3] = st.getIdtype();
		}
		rows[i][1] = "no";
		rows[i][0] = "None";
		rows[i][2] = none_color;
		rows[i][3] = -1;
	}
	
	 public static Color getColor(int idtype)
	 {
		 if(idtype == -1)
			 return none_color;
		 int index = idtype % colors.length;
		 return colors[index];
	 }
	  
     public Class getColumnClass(int c) {
         return getValueAt(0, c).getClass();
     }

	
	@Override
	public String getColumnName(int index) {
		return column_names[index];
	}

	@Override
	public int getColumnCount() {
		return column_names.length;
	}

	@Override
	public int getRowCount() {
		return rows.length;
	}

	@Override
	public Object getValueAt(int y, int x) {
		return rows[y][x];
	}

}
