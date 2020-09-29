package com.nbis.fluoj.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.nbis.fluoj.persistence.Type;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.ConfigurationDB;

public class ClassifiersOutputJDialog extends JDialog {

    private JButton ok_bt;
    private JButton export_bt;
    private List<Classifier> classifiers;
    private List<TableModel> models;
    private EntityManager em;

    public ClassifiersOutputJDialog(AutomaticImageProcessingJFrame parent_fr, List<Classifier> classifiers) {
        super(parent_fr);
        em = parent_fr.getEntityManager();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.classifiers = classifiers;
        Classifier classifier;
        setTitle("Classifier Output");
        JScrollPane container_sp = new JScrollPane();
        JPanel container_pn = new JPanel();
        container_sp.setViewportView(container_pn);
        getContentPane().add(container_sp);

        container_pn.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        JTable table;
        JScrollPane sp;
        JPanel pane;
        JLabel label;
        int index = 0;
        ClassifierOutputTableModel model;
        models = new ArrayList<TableModel>();
        for (int i = 0; i < classifiers.size(); i++) {
            classifier = classifiers.get(i);
            if (parent_fr.sids.get(i).dir == null || parent_fr.sids.get(i).dir.equals("")) {
                continue;
            }
            label = new JLabel("Sample: " + classifier.getSample());
            constraints.gridx = 0;
            constraints.gridy = index;
            container_pn.add(label, constraints);
            label = new JLabel("Classifier Output");
            constraints.gridx = 1;
            container_pn.add(label, constraints);

            pane = new JPanel();
            pane.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));
            constraints.gridx = 0;
            constraints.gridy = index + 1;
            constraints.gridwidth = 2;
            container_pn.add(pane, constraints);
            constraints.gridwidth = 1;
            sp = new JScrollPane();
            table = new JTable();
            model = new ClassifierOutputTableModel(classifier);
            models.add(model);
            table.setModel(model);

            table.setPreferredScrollableViewportSize(new Dimension(500, 112));
            sp.setViewportView(table);
            pane.add(sp);
            index += 2;
        }

        JPanel buttons_pn = new JPanel();
        ok_bt = new JButton("Ok");
        ok_bt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();

            }
        });
        buttons_pn.add(ok_bt);
        export_bt = new JButton("Export Data");
        export_bt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exportData();

            }
        });
        buttons_pn.add(export_bt);
        constraints.gridy = index;
        container_pn.add(buttons_pn, constraints);
        if (classifiers.size() > 3) {
            setSize(new Dimension(600, 500));
        } else {
            pack();
        }
        centerScreen();
        setVisible(true);

    }

    public void exportData() {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            exportData(file);
        }

    }

    // centers the dialog within the screen
    public void centerScreen() {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
    }

    public void exportData(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            TableModel model;
            for (int i = 0; i < models.size(); i++) {
                model = models.get(i);
                for (int c = 0; c < model.getColumnCount(); c++) {
                    writer.append(String.format("%20s", model.getColumnName(c)));
                }
                writer.append("\n");
                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 0; c < model.getColumnCount(); c++) {
                        //System.out.println(line);
                        writer.append(String.format("%20s", model.getValueAt(r, c).toString()));
                    }
                    writer.append("\n");
                }
                writer.append("\n");
            }
            writer.close();
        } catch (Exception e) {
            Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    class ClassifierOutputTableModel extends AbstractTableModel {

        private String[] columnNames = {"Type", "Total", "Percentage"};
        private List<com.nbis.fluoj.persistence.Type> types;
        private Classifier classifier;

        public ClassifierOutputTableModel(Classifier c) {
            types = new ArrayList<com.nbis.fluoj.persistence.Type>();
            types.addAll(c.getSample().getTypeList());
            types.add(ConfigurationDB.getNoneType());

            classifier = c;
        }

        private float getPercent(float value, int total) {
            if (total == 0) {
                return 0;
            }
            return ((float) value / total) * 100;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return types.size();
        }

        @Override
        public String getColumnName(int columnIndex) {

            return columnNames[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            com.nbis.fluoj.persistence.Type t = types.get(rowIndex);
            if (columnIndex == 0) {
                return t.getName();
            }
            int ttotal;
            ttotal = (t.getIdtype() == null) ? classifier.getTotalUnknown(em) : classifier.getTotalFromWinner(t.getIdtype(), em);
            if (columnIndex == 1) {
                return ttotal;
            }
            return String.format("%.2f", getPercent(ttotal, classifier.getTotalMeasured()));
        }

    }

}
