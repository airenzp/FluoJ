package com.nbis.fluoj.gui;

import com.nbis.fluoj.gui.TypesPane.TypeRow;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.nbis.fluoj.persistence.Scell;
import com.nbis.fluoj.persistence.Type;

import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.SegmentedParticle;

public class TypesPane extends JPanel
{

	private ButtonGroup typesbg;
	private ArrayList<TypeRow> typerows;
	private Type type;
	private Classifier classifier;
	private List<Type> types;
	private EntityManager em;

	public TypesPane(Classifier classifier, EntityManager em)
	{
		setLayout(new GridBagLayout());
		initTypesPane(classifier, em);
	}
	
	void initTypesPane(Classifier classifier, EntityManager em)
	{
		this.classifier = classifier;
		this.types = classifier.getSample().getTypeList();
		this.em = em;
		int imgcount = classifier.getImagesCount(em);
		String title = (imgcount == 1) ? "Marker. 1 Image Processed" : String.format("Marker. %s Images Processed", imgcount);
		setBorder(javax.swing.BorderFactory.createTitledBorder(title));
		removeAll();
		typesbg = new ButtonGroup();
		int total;
		typerows = new ArrayList<TypeRow>();
		TypeRow typerow;
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 20, 0, 20);
		constraints.gridy = 0;
		constraints.gridx = 1;
		add(new JLabel("Label"), constraints);
		constraints.gridx = 2;
		add(new JLabel("Count"), constraints);
		constraints.gridx = 3;
		add(new JLabel("Total"), constraints);
		int i;
		com.nbis.fluoj.persistence.Type type;
		for (i = 0; i < types.size(); i++)
		{
			type = types.get(i);
			typerow = new TypeRow(type, 0, 0);
			typerows.add(typerow);
			constraints.gridx = 0;
			constraints.gridy = i + 1;

			typerow.rb.addActionListener(new TypeRbActionListener());
			typesbg.add(typerow.rb);
			add(typerow.rb, constraints);
			constraints.gridx = 1;
			add(typerow.labellb, constraints);

			constraints.gridx = 2;
			add(typerow.countlb, constraints);

			constraints.gridx = 3;
			add(typerow.totallb, constraints);
			if (i == 0)
			{
				typesbg.setSelected(typerow.rb.getModel(), true);
				this.type = types.get(0);
			}
		}
	}
	

	class TypeRbActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton rb = ((JRadioButton) e.getSource());
			setActiveType(rb.getActionCommand());
		}
	}


	class TypeRow
	{
		JLabel labellb;
		JRadioButton rb;
		JLabel countlb;
		JLabel totallb;
		int count;
		int total;
		Type type;

		public TypeRow(Type type, int count, int total)
		{

			
			this.labellb = new JLabel(type.getLabel());
			labellb.setForeground(new Color(type.getColor()));
			this.rb = new JRadioButton(type.getName());
			rb.setActionCommand(type.getLabel());
			this.countlb = new JLabel(String.valueOf(count));
			this.totallb = new JLabel(String.valueOf(total));
			this.count = count;
			this.total = total;
			this.type = type;
		}

		public void setCount(int count)
		{
			this.count = count;
			countlb.setText(String.valueOf(count));
		}

		public void setTotal(int total)
		{
			this.total = total;
			totallb.setText(String.valueOf(total));
		}

	}
	
	TypeRow getIdtypeRow(String label) {
		for (TypeRow tr : typerows)
			if (tr.type.getLabel().equals(label))
				return tr;
		return null;
	}

	public void setActiveType(String label) {
		TypeRow tr = getIdtypeRow(label);
		if (tr != null) {
			System.out.println("Setting sample type: " + tr.type.getName());
			typesbg.setSelected(tr.rb.getModel(), true);
			type = tr.type;
			
		}
	}
	
	
	
	public void resetCounts()
	{
		for (TypeRow tr : typerows)
			tr.setCount(0);
		
	}

	public void resetTotals()
	{
		for (TypeRow tr : typerows)
			tr.setTotal(0);
		
	}
	
	void updateCountsFromImageParticles(List<SegmentedParticle> particles) {

		int[] count = new int[types.size()];
		for (SegmentedParticle il : particles)
			for (int i = 0; i < types.size(); i++)
				if (il.getParticleStatistic().getType() != null && il.getParticleStatistic().getType().getIdtype() == types.get(i).getIdtype())
					count[i]++;
		for (int i = 0; i < types.size(); i++)
			typerows.get(i).setCount(count[i]);
		
	}
	
	void updateCountsFromScell(List<Scell> scells) {
		int count, id;
		for (int k = 0; k < typerows.size(); k++)
		{
			id = types.get(k).getIdtype();
			count = 0;
			for (int i = 0; i < scells.size(); i++)
			{
				if (scells.get(i).getIdtype() != null && scells.get(i).getIdtype().getIdtype() == id)
					count++;
				if (scells.get(i).getIdtype() == null && -1 == id)
					count++;
			}
			typerows.get(k).setCount(count);
		}
		
	}
	
	void updateTotals()
	{
		int total;
		for (TypeRow tr: typerows)
		{
			total = classifier.getTotalFromClass(tr.type.getIdtype(), em);
			tr.setTotal(total);
			
		}
	}

	public com.nbis.fluoj.persistence.Type getActiveType()
	{
		return type;
	}

	

}
