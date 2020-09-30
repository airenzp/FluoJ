package com.nbis.fluoj.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import com.nbis.fluoj.persistence.SampleImage;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.persistence.Filter;
import com.nbis.fluoj.persistence.Separation;
import com.nbis.fluoj.persistence.Session;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

public class ConfigurationJFrame extends FluoJJFrame implements ActionListener {

    private JButton addbt;
    private JButton deletebt;
    private MyJTable samplestb;
    SampleTableModel samplesmd;
    private JButton edittypesbt;
    private JButton editfeaturesbt;
    private JLabel iconlb;

    int index;
    private FluoJImageWindow iw;
    private JButton processimgbt;
    private JButton activeimgbt;
    private JButton filtersbt;
    public static Session nonesession = ConfigurationDB.anysession;
    public static com.nbis.fluoj.persistence.Type nonetype = ConfigurationDB.nonetype;
    protected JCheckBoxMenuItem debugsegmentationmi;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    ConfigurationJFrame frame = new ConfigurationJFrame();
                } catch (Exception e) {
                    Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public ConfigurationJFrame() throws InvalidOperationOnResourceException {

        samples = cconfigurationdb.getSamples(em);
        nonesession = ConfigurationDB.anysession;
        nonetype = ConfigurationDB.nonetype;

        initComponents();
        if (!samples.isEmpty()) {
            samplestb.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    private void initComponents() {
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("FluoJ Configuration");

        debugsegmentationmi = new JCheckBoxMenuItem("Debug Segmentation");
        debugsegmentationmi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cconfigurationdb.setDebug(debugsegmentationmi.isSelected());
                resetCImageProcess();
            }
        });
        filemn.add(debugsegmentationmi);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        JPanel samplespn = new JPanel(new GridBagLayout());
        samplespn.setBorder(BorderFactory.createTitledBorder("Samples"));

        JScrollPane sp = new JScrollPane();
        samplespn.add(sp, FluoJUtils.getConstraints(constraints, 0, 0, 2));

        JPanel imgpn = new JPanel();
        imgpn.setBorder(BorderFactory
                .createTitledBorder(null, "Image", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_BOTTOM));
        iconlb = new JLabel(ConfigurationDB.getDefaultIcon());
        imgpn.add(iconlb);
        samplespn.add(imgpn, FluoJUtils.getConstraints(constraints, 2, 0, 1));

        index = -1;
        samplestb = new MyJTable();
        samplestb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        samplestb.setOpaque(true);
        samplestb.setPreferredScrollableViewportSize(new Dimension(640, 200));
        samplesmd = new SampleTableModel();
        samplestb.setModel(samplesmd);
        samplestb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        samplestb.getColumnModel().getColumn(0).setPreferredWidth(150);// Name
        samplestb.getColumnModel().getColumn(1).setPreferredWidth(60);// Session
        samplestb.getColumnModel().getColumn(2).setPreferredWidth(70);// Threshold
        samplestb.getColumnModel().getColumn(3).setPreferredWidth(100);// RoisThreshold
        samplestb.getColumnModel().getColumn(4).setPreferredWidth(65);//Fill Holes
        samplestb.getColumnModel().getColumn(5).setPreferredWidth(115);// Separation
        samplestb.getColumnModel().getColumn(6).setPreferredWidth(80);// Exp radius
        samplestb.getColumnModel().getColumn(7).setPreferredWidth(80);// rois
        samplestb.getColumnModel().getColumn(8).setPreferredWidth(100);//Default type

        // Threshold
        sp.setViewportView(samplestb);
        constraints.gridy = 1;
        add(samplespn, FluoJUtils.getConstraints(constraints, 0, 0, 1));
        JPanel buttonspn = new JPanel();
        addbt = new JButton("Add");
        buttonspn.add(addbt);
        deletebt = new JButton("Delete");
        buttonspn.add(deletebt);
        filtersbt = new JButton("Preprocessing");
        buttonspn.add(filtersbt);
        editfeaturesbt = new JButton("Features");
        buttonspn.add(editfeaturesbt);
        edittypesbt = new JButton("Types");
        buttonspn.add(edittypesbt);

        processimgbt = new JButton("Process Image");
        buttonspn.add(processimgbt);
        activeimgbt = new JButton("Try Active");
        buttonspn.add(activeimgbt);

        setEnabledSampleActions(false);
        add(buttonspn, FluoJUtils.getConstraints(constraints, 0, 1, 1));
        addListeners();
        pack();
        FluoJUtils.setLocation(0.5, 0.5, this);
        setVisible(true);
    }

    private void setEnabledSampleActions(boolean enable) {
        deletebt.setEnabled(enable);
        edittypesbt.setEnabled(enable);
        editfeaturesbt.setEnabled(enable);
        processimgbt.setEnabled(enable);
        activeimgbt.setEnabled(enable);
        statisticsmi.setEnabled(enable);
        resetdbmi.setEnabled(enable);
        trainmi.setEnabled(enable);
        filtersbt.setEnabled(enable);
        exporthistmi.setEnabled(enable);
        histogramsmi.setEnabled(enable);
        debugsegmentationmi.setEnabled(enable);
    }

    private void addListeners() {
        samplestb.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                index = samplestb.getSelectedRow();
                if (index != -1) {
                    sample = samples.get(index);
                    iconlb.setIcon(ConfigurationDB.getIcon(sample.getIdimage()));
                    classifier = null;

                } else {
                    sample = null;
                    iconlb.setIcon(ConfigurationDB.getDefaultIcon());
                }
                boolean enable = index != -1;
                setEnabledSampleActions(enable);
            }
        });
        samplestb.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 127 && samplestb.getSelectedRow() != -1) {
                    deleteSample();
                }
            }
        });
        addbt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new AddSampleJDialog(ConfigurationJFrame.this, true);

            }
        });
        deletebt.addActionListener(this);
        edittypesbt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new EditSampleTypesJDialog(ConfigurationJFrame.this, true, em);
            }
        });
        editfeaturesbt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // processImageInfo();
                new EditSampleFeaturesJDialog(ConfigurationJFrame.this, false, em);

            }
        });

        processimgbt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    processImageParticles();
                } catch (InvalidOperationOnResourceException e1) {
                    JOptionPane.showMessageDialog(ConfigurationJFrame.this, e1);
                }

            }
        });

        activeimgbt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ImagePlus img = WindowManager.getCurrentImage();
                if (img == null || img.getStackSize() > 1) {
                    IJ.noImage();
                    return;
                }
                try {
                    processImageParticles(new FluoJImageProcessor(img, sample, false, cconfigurationdb.isDebug()), sample.getSampleFeatureList(), sample.getSampleFeatureList());
                } catch (InvalidOperationOnResourceException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        });
        filtersbt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new PreprocessingJDialog(ConfigurationJFrame.this, true, em);
            }
        });

    }

    protected int resetDB() {
        if (cconfigurationdb.isEmpty(sample)) {
            return JOptionPane.YES_OPTION;
        }

        int result = super.resetDB();// asks user if resetting
        if (result == 0)// Yes
        {
            samplesmd.fireTableRowsUpdated(index, index);
        }

        return result;
    }

    public void addSample(Sample s, ImagePlus image) {
        samples.add(s);
        SampleImage ir = null;
        if (image != null) {
            ir = cconfigurationdb.saveImageResource(image, em);
        }
        s.setIdimage(ir);

        cconfigurationdb.persist(s, em);

        samplesmd.fireTableRowsInserted(samples.size(), samples.size());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteSample();
    }

    private void deleteSample() {
        try {
            int result = resetDB();
            if (result == JOptionPane.YES_OPTION) {

                sample = samples.get(index);
                cconfigurationdb.remove(sample, em);
                samples.remove(index);
                samplesmd.fireTableRowsDeleted(index, index);
                setEnabledSampleActions(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Classifier.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    void processImageParticles() throws InvalidOperationOnResourceException {

        processImageParticles(getCImageProcess(), sample.getSampleFeatureList(), sample.getSampleFeatureList());

    }

    void processImageParticles(List<SampleFeature> sfs, List<SampleFeature> scfs) throws InvalidOperationOnResourceException {

        processImageParticles(getCImageProcess(), sfs, scfs);

    }

    public void processImageParticles(FluoJImageProcessor cip, List<SampleFeature> sfs, List<SampleFeature> scfs) throws InvalidOperationOnResourceException {
        cip.filterParticles(sfs, scfs);
        display(cip);

    }

    private void display(FluoJImageProcessor cip) {
        try {
            if (iw == null) {
                iw = new FluoJImageWindow(this, cip, new FluoJImageCanvas(this, cip));
            } else {
                if (!iw.isClosed()) {
                    iw.close();
                }
                iw = new FluoJImageWindow(this, cip, new FluoJImageCanvas(this, cip));
            }
            iw.setVisible(true);
            iw.setTitle("Image Processed");
        } catch (Exception e) {
            Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public int getIndex() {
        return index;
    }

    public Classifier getClassifier() {
        if (classifier == null) {
            if (getSample().getIdsession() == null) {
                classifier = new Classifier(sample, em);
                samplesmd.fireTableCellUpdated(index, 2);
            } else {
                classifier = new Classifier(sample.getIdsession());
            }
        }
        return classifier;
    }

    @Override
    public FluoJImageProcessor getCImageProcess() throws InvalidOperationOnResourceException {
        ImagePlus imp = ConfigurationDB.getImagePlus(sample.getIdimage());
        if (cip == null)// reseted or first time
        {
            cip = new FluoJImageProcessor(imp, sample, false, cconfigurationdb.isDebug());
        }
        if (cip.getSample() != sample) {
            cip = new FluoJImageProcessor(imp, sample, false, cconfigurationdb.isDebug());
        }
        return cip;
    }

    class SampleTableModel extends AbstractTableModel {

        private String[] columns = new String[]{"Name", "Session", "Threshold", "RoisThreshold",
            "Fill Holes", "Separate", "Expansion", "ROIsMax", "Default Type",};

        @Override
        public int getRowCount() {
            return samples.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return false;
            }

            return true;
        }

        @Override
        public Class getColumnClass(int column) {
            Object o = getValueAt(0, column);
            if (o == null) {
                if (column == 1) {
                    return Session.class;
                }
                if (column == 8) {
                    return com.nbis.fluoj.persistence.Type.class;
                }
                if (column == 5) {
                    return com.nbis.fluoj.persistence.Separation.class;
                }

                return Object.class;
            }
            return o.getClass();
        }

        @Override
        public String getColumnName(int index) {
            return columns[index];
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            try {
                Sample sample = samples.get(rowIndex);
                int result;
                if (columnIndex == 0) {
                    sample.setName((String) value);
                } else if (columnIndex == 1) {
                    Session s = (Session) value;
                    if (s != null && !s.equals(nonesession)) {
                        sample.setIdsession(s);
                    } else {
                        sample.setIdsession(null);
                    }
                } else if (columnIndex == 2) {
                    if (value == null) {
                        throw new IllegalArgumentException(Constants.getEmptyFieldMsg("threshold"));
                    }
                    result = resetDB();
                    if (result == JOptionPane.YES_OPTION) {
                        Short threshold = (Short) value;
                        sample.setImageThreshold(threshold);
                        resetCImageProcess();
                    }
                } else if (columnIndex == 3) {
                    if (value == null) {
                        throw new IllegalArgumentException(Constants.getEmptyFieldMsg("threshold"));
                    }
                    result = resetDB();
                    if (result == JOptionPane.YES_OPTION) {
                        short threshold = (Short) value;
                        sample.setRoisThreshold(threshold);
                        resetCImageProcess();
                    }
                } else if (columnIndex == 4) {
                    Boolean fill = (Boolean) value;
                    result = resetDB();
                    if (result == JOptionPane.YES_OPTION) {
                        sample.setFillHoles(fill);
                        resetCImageProcess();
                    }

                } else if (columnIndex == 5) {
                    Separation separation = (Separation) value;
                    result = resetDB();
                    if (result == JOptionPane.YES_OPTION) {
                        sample.setIdseparation(separation);
                        resetCImageProcess();
                    }

                } else if (columnIndex == 6) {
                    Short expansionradius = (Short) value;
                    result = resetDB();
                    if (result == JOptionPane.YES_OPTION) {
                        sample.setExpansionRadius(expansionradius);
                        resetCImageProcess();
                    }

                } else if (columnIndex == 7) {
                    Short roismax = (Short) value;
                    result = resetDB();
                    if (result == JOptionPane.YES_OPTION) {
                        sample.setRoisThreshold(roismax);
                        resetCImageProcess();
                    }

                } else if (columnIndex == 8) {
                    com.nbis.fluoj.persistence.Type type = (com.nbis.fluoj.persistence.Type) value;
                    if (type.getIdtype() != null) {
                        sample.setIdtype(type);
                    } else {
                        sample.setIdtype(null);
                    }
                }

                ConfigurationJFrame.this.sample = cconfigurationdb.mergeSample(sample, em);
            } catch (Exception e) {
                Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
                JOptionPane.showMessageDialog(ConfigurationJFrame.this, e.getMessage());
            }

        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            Sample sample = samples.get(rowIndex);

            if (columnIndex == 0) {
                return sample.getName();
            }
            if (columnIndex == 1) {
                if (sample.getIdsession() == null) {
                    return nonesession;
                } else {
                    return sample.getIdsession();
                }

            }

            if (columnIndex == 2) {
                return sample.getImageThreshold();
            }

            if (columnIndex == 3) {
                return sample.getRoisThreshold();
            }

            if (columnIndex == 4) {
                return (sample.getFillHoles());
            }
            if (columnIndex == 5) {
                return sample.getIdseparation();
            }

            if (columnIndex == 6) {
                return sample.getExpansionRadius();
            }
            if (columnIndex == 7) {
                return sample.getRoisThreshold();
            }

            if (columnIndex == 8) {
                if (sample.getIdtype() == null) {
                    return ConfigurationJFrame.nonetype;
                } else {
                    return sample.getIdtype();
                }

            }
            return null;
        }
    }

    class MyJTable extends JTable {

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            if (column == 1) {
                return new SessionTableCellEditor();
            }
            if (column == 9) {
                return new TypeTableCellEditor();
            }
            if (column == 5) {
                return new SeparateTableCellEditor();
            }
            return super.getCellEditor(row, column);
        }
    }

    class TypeTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JComboBox cb;
        private com.nbis.fluoj.persistence.Type type;
        private Sample sample;

        public TypeTableCellEditor() {
            cb = new JComboBox();
            cb.setOpaque(true);
            cb.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    type = (com.nbis.fluoj.persistence.Type) cb.getSelectedItem();
                    if ((type.equals(nonetype) && sample.getIdtype() == null) || type.equals(sample.getIdtype())) {
                        return;// nothing to do here
                    }
                    if (!type.equals(ConfigurationJFrame.nonetype)) {
                        sample.setIdtype(type);
                    } else {
                        sample.setIdtype(null);
                    }
                    ConfigurationJFrame.this.sample = cconfigurationdb.mergeSample(sample, em);

                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return type;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            sample = ConfigurationJFrame.this.samples.get(row);
            List<com.nbis.fluoj.persistence.Type> types = new ArrayList<com.nbis.fluoj.persistence.Type>(sample.getTypeList());
            types.add(0, nonetype);
            cb.setModel(new DefaultComboBoxModel(types.toArray()));
            if (sample.getIdtype() != null) {
                cb.setSelectedItem(sample.getIdtype());
            } else {
                cb.setSelectedItem(nonetype);
            }
            return cb;
        }

    }

    class SessionTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JComboBox cb;

        public SessionTableCellEditor() {
            cb = new JComboBox();
            cb.setOpaque(true);
            cb.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    Session session = (Session) cb.getSelectedItem();
                    if ((session.equals(nonesession) && sample.getIdsession() == null) || session.equals(sample.getIdsession())) {
                        return;// nothing to do here
                    }
                    if (!session.equals(ConfigurationJFrame.nonesession)) {
                        sample.setIdsession(session);
                    } else {
                        sample.setIdsession(null);
                    }
                    sample = cconfigurationdb.mergeSample(sample, em);
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            if (sample.getIdsession() == null) {
                return nonesession;
            }
            return sample.getIdsession();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            sample = samples.get(row);
            List<Session> sessions = new ArrayList<Session>(sample.getSessionList());
            sessions.add(0, nonesession);
            cb.setModel(new DefaultComboBoxModel(sessions.toArray()));
            if (sample.getIdsession() != null) {
                cb.setSelectedItem(sample.getIdsession());
            } else {
                cb.setSelectedItem(nonesession);
            }
            return cb;
        }

    }

    class SeparateTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JComboBox cb;
        private Separation separation;
        private Sample sample;

        public SeparateTableCellEditor() {
            cb = new JComboBox();
            cb.setOpaque(true);
            cb.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    separation = (Separation) cb.getSelectedItem();
                    sample.setIdseparation(separation);
                    ConfigurationJFrame.this.sample = cconfigurationdb.mergeSample(sample, em);

                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return separation;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            sample = ConfigurationJFrame.this.samples.get(row);
            cb.setModel(new DefaultComboBoxModel(cconfigurationdb.getIdseparations(em).toArray()));
            cb.setSelectedItem(sample.getIdseparation());
            return cb;
        }

    }

}
