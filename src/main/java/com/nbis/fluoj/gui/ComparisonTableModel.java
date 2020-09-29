package com.nbis.fluoj.gui;

import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.table.AbstractTableModel;

import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.ClassifierDB;
import com.nbis.fluoj.persistence.Type;

/**
 * TableModel used by {@link CLegendJDialog}
 * @author Airen
 *
 */
public class ComparisonTableModel extends AbstractTableModel {

	List<Type> types;
	String content = "Automatic\\Manual";
	Classifier classifier;
	Object[][] values;
	EntityManager em;
	
	public ComparisonTableModel(Classifier classifier)
	{
		this.classifier = classifier;
		em = ConfigurationDB.getEM();
		types = classifier.getSample().getTypeList();
		setValues();
	}
	
	private void setValues()
	{
		int size = types.size() + 3;
		values = new Object[size][size];
		
		int unknown_row = getRowCount() - 3;
		int total_row = getRowCount() - 2;
		int error_row = getRowCount() - 1;
		int total_column = getColumnCount() - 1;
		int unknown_column = getColumnCount() - 2;
		
		for(int i = 0 ; i < types.size(); i ++)//Iterating on rows
		{
			values[i][0] = types.get(i).getName();
			values[i][total_column] = classifier.getTotalFromWinner(types.get(i).getIdtype(), em);
			values[i][unknown_column] = classifier.getTotal(null, types.get(i).getIdtype());
		}
		
		for(int i = 1 ; i <= types.size() ; i ++)//Iterating on columns
		{
			values[unknown_row][i] = classifier.getTotal(types.get(i - 1).getIdtype(), null) ;
			values[total_row][i] = classifier.getTotalFromClass(types.get(i - 1).getIdtype(), em);
			values[error_row][i] = String.format("%.2f%%", classifier.getWinnerErrorPercent(types.get(i - 1).getIdtype(), em));
		}
		values[unknown_row][0] = "None";
		values[total_row][0] = "Total";
		values[error_row][0] = "Error";
		values[total_row][total_column] = classifier.getTotalMeasured();
		values[total_row][unknown_column] = classifier.getTotalFromClass(null, em);
		values[unknown_row][unknown_column] = classifier.getTotal(null, null);
		values[unknown_row][total_column] = classifier.getTotalFromWinner(null, em);
		values[error_row][total_column] = String.format("%.2f%%", classifier.getTotalErrorPercent());
		
		for(int rowIndex = 0; rowIndex < types.size() ; rowIndex ++)
			for(int columnIndex = 1; columnIndex <= types.size() ; columnIndex ++)
			{
					short original = types.get(columnIndex - 1).getIdtype();
					short winner = types.get(rowIndex).getIdtype();
					values[rowIndex][columnIndex] = classifier.getTotal(original, winner);
			}
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		if(columnIndex == 0)
			return content;
		if(columnIndex == getColumnCount() - 1)
			return "Total";
		if(columnIndex == getColumnCount() - 2)
			return "None";
		return types.get(columnIndex - 1).getName();
	}
	
	@Override
	public int getColumnCount() {
		
		return values.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return values.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return values[rowIndex][columnIndex];
		

	}

}
