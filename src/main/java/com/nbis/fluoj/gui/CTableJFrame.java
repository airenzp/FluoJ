package com.nbis.fluoj.gui;

import com.nbis.fluoj.classifier.CellTypeProbability;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.Scell;
import com.nbis.fluoj.persistence.ScellFeature;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.persistence.Probability;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.persistence.Type;

/**
 * Displays {@link persistence.Scell} info including
 * {@link persistence.ScellFeature}, {@link persistence.Probability}
 *
 * @author Airen
 *
 */
public class CTableJFrame extends JFrame {

    private Classifier classifier;
    private List<Scell> cells;
    private EntityManager em;
    private CellsTableModel model;

    public static void main(String[] args) {
        CLegendJDialog dialog;
        try {
            dialog = new CLegendJDialog(null, ConfigurationDB.getInstance().getRandomClassifier());
            dialog.setVisible(true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public CTableJFrame(Classifier classifier, int idimage, Short original, Short winner, EntityManager em) {

        this(classifier, classifier.getScells(idimage, original, winner, em), em);

    }

    public CTableJFrame(Classifier classifier, List<Scell> cells, EntityManager em) {
        try {
            this.classifier = classifier;
            this.cells = cells;
            this.em = em;
            initComponents();
        } catch (Exception e) {
            Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Cells Info");

        JPanel containerpn = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        JScrollPane sp = new JScrollPane();
        JTable table = new JTable();
        table.setOpaque(true);
        model = new CellsTableModel(classifier, cells, em);
        table.setModel(model);
        table.setPreferredScrollableViewportSize(new Dimension(1000, 500));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sp.setViewportView(table);
        containerpn.add(sp, FluoJUtils.getConstraints(constraints, 0, 0, 1));
        JButton exportdatabt = new JButton("Export Data");
        exportdatabt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exportData();

            }
        });
        containerpn.add(exportdatabt, FluoJUtils.getConstraints(constraints, 0, 1, 1));
        add(containerpn);

        pack();
        centerScreen();
        setVisible(true);
    }

    protected void exportData() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showSaveDialog(CTableJFrame.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                model.exportData(file.getAbsolutePath());
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(CTableJFrame.this, e1.getMessage());
            }
        }

    }

    public CTableJFrame(Classifier classifier, int idimage) {
        try {
            this.em = ConfigurationDB.getEM();
            this.classifier = classifier;
            this.cells = classifier.getScells(idimage, em);
            initComponents();
        } catch (Exception e) {
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
        setLocation(x, y);
    }

    class CellsTableModel extends AbstractTableModel {

        private List<Scell> scells;
        private List<String> column_names;
        private List<SampleFeature> sfs;
        List<List<CellTypeProbability>> probs;
        List<com.nbis.fluoj.persistence.Type> types;
        private Object[][] rows;

        public CellsTableModel(Classifier c, List<Scell> cells, EntityManager em) {
            sfs = new ArrayList<SampleFeature>();
            for (SampleFeature sf : c.getSample().getSampleFeatureList()) {
                if (sf.getActive()) {
                    sfs.add(sf);
                }
            }
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
            for (int i = 0; i < sfs.size(); i++) {
                column_names.add(sfs.get(i).getFeature().getName());
            }
            column_names.add("Probability");

            probs = new ArrayList<List<CellTypeProbability>>();
            for (int i = 0; i < cells.size(); i++) {
                probs.add(c.getProbs(cells.get(i).getIdscell(), em));
            }
            fillRows();

        }

        public void exportData(String file) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                String line = "";
                Object value;
                String column;
                for (int j = 1; j < getColumnCount(); j++) {
                    column = getColumnName(j);
                    if (column.length() >= 20) {
                        column = column.substring(0, 16) + "...";
                    }
                    line += String.format("%20s", column);
                }

                writer.append(line + "\n");

                for (int i = 0; i < getRowCount(); i++) {
                    line = "";
                    for (int j = 1; j < getColumnCount(); j++) {
                        value = getValueAt(i, j);
                        if (value instanceof Float) {
                            line += String.format("%20.2f", Float.valueOf(value.toString().replace(',', '.')));
                        } else {
                            column = (value == null) ? "" : value.toString();

                            if (column.length() >= 20) {
                                column = column.substring(0, 16) + "...";
                            }
                            line += String.format("%20s", column);

                        }
                    }
                    writer.append(line + "\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e.getMessage());

            }
        }

        @Override
        public String getColumnName(int index) {
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

        private void fillRows() {
            int magnification = types.size() + 1;
            rows = new Object[scells.size() * magnification][column_names.size()];
            Scell s;
            int index;
            Double prob;
            NumberFormat formatter;
            Short idtype = 0;
            List<CellTypeProbability> stprobs;
            for (int y = 0, j = 0; y < rows.length - magnification + 1; y += magnification, j++) {
                s = scells.get(j);
                rows[y][0] = s.getIdscell();
                rows[y][1] = s.getIdsession().getIdsession();
                rows[y][2] = s.getDate();
                rows[y][3] = s.getIdimage().getName();
                rows[y][4] = (s.getIdtype() != null) ? s.getIdtype().getName() : null;
                rows[y][5] = (s.getWinner() != null) ? s.getWinner().getName() : null;
                rows[y][6] = s.getX();
                rows[y][7] = s.getY();
                for (int x = 8, i = 0; x < 8 + sfs.size(); x++, i++) {
                    rows[y][x] = getFeatureValue(s.getScellFeatureList(), sfs.get(i).getFeature().getIdfeature());
                }
                for (int k = 0; k < types.size(); k++) {
                    rows[y + k + 1][4] = types.get(k).getName();
                    for (int x = 8, i = 0; x < 8 + sfs.size(); x++, i++) {

                        prob = classifier.getProbability(s.getIdscell(), sfs.get(i).getFeature().getIdfeature(), types.get(k).getIdtype(), em);
                        if (prob != null) {
                            formatter = new DecimalFormat("0.##E0");
                            rows[y + k + 1][x] = formatter.format(prob);
                        }
                    }
                    idtype = types.get(k).getIdtype();
                    stprobs = probs.get(j);
                    index = stprobs.indexOf(new CellTypeProbability(idtype));
                    if (index != -1) {
                        prob = stprobs.get(index).probability;
                        formatter = new DecimalFormat("0.##E0");
                        rows[y + k + 1][8 + sfs.size()] = formatter.format(prob);
                    }
                }

            }
        }

        private Object getFeatureValue(Collection<ScellFeature> values, Short idfeature) {
            Iterator<ScellFeature> iter = values.iterator();
            ScellFeature ssf;
            while (iter.hasNext()) {
                ssf = iter.next();
                if (ssf.getFeature().getIdfeature().equals(idfeature)) {
                    return String.format("%.2f", ssf.getValue());
                }
            }
            return null;
        }

    }

}
