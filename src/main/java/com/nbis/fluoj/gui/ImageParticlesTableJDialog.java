package com.nbis.fluoj.gui;

import ij.ImagePlus;
import ij.io.FileSaver;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.ParticleStatistic;
import com.nbis.fluoj.classifier.SegmentedParticle;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

public class ImageParticlesTableJDialog extends JDialog {

	private JTable labelstb;
	private ImageLabelsTableModel labelsmd;
	private JButton okbt;
	private List<SegmentedParticle> labels;
	private JButton exportbt;
	private FluoJJFrame frame;

	public ImageParticlesTableJDialog(FluoJJFrame f,  FluoJImageProcessor cip, boolean modal)
			throws InvalidOperationOnResourceException {
		super(f, modal);
		this.frame  = f;
		this.labels = cip.getFilteredParticles();
		Collections.sort(labels);
		initComponents();
	}
	
	
	private void initComponents() {
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Cells Features");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		JPanel samplespn = new JPanel(new GridBagLayout());
		samplespn.setBorder(BorderFactory.createTitledBorder("Cells Features"));

		JScrollPane sp = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		samplespn.add(sp, FluoJUtils.getConstraints(constraints, 0, 0, 3));
		labelsmd = new ImageLabelsTableModel();
		labelstb = new JTable(labelsmd);
		labelstb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		labelstb.getTableHeader().addMouseMotionListener(new MouseMotionAdapter(){
			   public void mouseMoved(MouseEvent e){
			        Point p = e.getPoint(); 
			        int column = labelstb.getColumnModel().getColumnIndexAtX(p.x);
			        labelstb.getTableHeader().setToolTipText(labelsmd.getColumnName(column));
			    }//end MouseMoved
			}); // end MouseMotionAdapter

		//labelstb.setPreferredScrollableViewportSize(new Dimension(700, 200));
		labelstb.getColumnModel().getColumn(0).setPreferredWidth(50);// Image
		labelstb.getColumnModel().getColumn(1).setPreferredWidth(30);// #
		labelstb.getColumnModel().getColumn(2).setPreferredWidth(30);// Y
		labelstb.getColumnModel().getColumn(3).setPreferredWidth(30);//X
		int size;
		for(int i = 4; i < labelsmd.getColumnCount(); i ++)
		{
			size = 100;//(labelsmd.getColumnName(i).length() > 15)? 110: 70;
			labelstb.getColumnModel().getColumn(i).setPreferredWidth(size);// Threshold
		}
		labelstb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		labelstb.setOpaque(true);
		
		sp.setViewportView(labelstb);
		sp.setPreferredSize(new Dimension(600, 400));
		labelstb.getColumnModel().getColumn(0).setWidth(55);
		labelstb.setRowHeight(50);

		constraints.gridy = 1;
		add(samplespn, FluoJUtils.getConstraints(constraints, 0, 0, 1));
		add(new JLabel("Total: " + labels.size()),
				FluoJUtils.getConstraints(constraints, 0, 1, 1));
		JPanel buttonspn = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		okbt = new JButton("Ok");
		okbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();

			}
		});
		buttonspn.add(okbt);
		exportbt = new JButton("Export Data");
		exportbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showSaveDialog(ImageParticlesTableJDialog.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						labelsmd.exportData(file.getAbsolutePath());
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(
								ImageParticlesTableJDialog.this, e1.getMessage());
					}
				}
			}
		});
		buttonspn.add(exportbt);
		add(buttonspn, FluoJUtils.getConstraints(constraints, 0, 2, 1));
		pack();
		FluoJUtils.setLocation(0.9, this);
		setVisible(true);
	}

	class ImageLabelsTableModel extends AbstractTableModel {

		private List<SampleFeature> sfeatures;

		public ImageLabelsTableModel() {
			sfeatures = frame.getSample().getSampleFeatureList();
		}

		public void exportData(String dir) throws IOException {
			String file = dir + File.separator + "export.txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String line = "";
			Object value;
			String column;
			for (int j = 1; j < getColumnCount(); j++) {
				column = getColumnName(j);
				if (column.length() >= 20)
					column = column.substring(0, 16) + "...";
				line += String.format("%20s", column);
			}
			writer.append(line + "\n");
			FileSaver saver;
			ImagePlus imp;
			for (int i = 0; i < getRowCount(); i++) {
				line = "";
				imp = labels.get(i).getImagePlus();
				saver = new FileSaver(imp);
				saver.saveAsJpeg(dir + File.separator + (i + 1) + ".jpeg");
				for (int j = 1; j < getColumnCount(); j++) {
					value = getValueAt(i, j);
					line += String.format("%20.2f",
							Float.valueOf(value.toString().replace(',', '.')));
				}
				writer.append(line + "\n");
			}
			writer.close();
		}

		@Override
		public int getRowCount() {
			return labels.size();
		}

		@Override
		public int getColumnCount() {
			return sfeatures.size() + 4;
		}

		@Override
		public Class getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		}

		@Override
		public String getColumnName(int index) {
			if(index == -1)
				return "";

			if (index == 0)
				return "Image";
			if (index == 1)
				return "#";
			if (index == 2)
				return "Y";
			if (index == 3)
				return "X";
			return sfeatures.get(index - 4).getFeature().getName();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SegmentedParticle il = labels.get(rowIndex);
			ParticleStatistic info = il.getParticleStatistic();
			if (columnIndex == 0)
				return il.getIcon();
			if (columnIndex == 1)
				return rowIndex + 1;
			if (columnIndex == 2)
				return info.getY();
			if (columnIndex == 3)
				return info.getX();
			int idfeature = sfeatures.get(columnIndex - 4).getFeature().getIdfeature();
			return String.format("%.2f", info.getValue(idfeature));
		}
		
		

	}

	
}
