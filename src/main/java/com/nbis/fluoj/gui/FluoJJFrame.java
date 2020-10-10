package com.nbis.fluoj.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleFeature;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.CellProcessor;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FluoJJFrame extends JFrame {

    protected EntityManager em;
    protected ConfigurationDB cconfigurationdb;
    protected List<Sample> samples;
    protected JMenuBar mb;
    protected Classifier classifier;
    protected FluoJImageProcessor cip;
    protected Sample sample;
    protected JMenuItem resetdbmi;
    protected JMenuItem statisticsmi;
    protected JMenuItem trainmi;
    protected JMenuItem exporthistmi;
    protected JMenuItem histogramsmi;
    protected JMenu filemn;
    protected JMenu windowmn;
    protected JMenu helpmn;
    protected JMenuItem trainingmi;
    protected JMenuItem classifiermi;
    private JMenuItem featuresmi;
    protected JMenuItem exportmi;
    public static final float locationx = 0.9f;
    private JMenuItem reviewmi;
    private JMenuItem configurationmi;
    TrainingJFrame training_frame;
    ConfigurationJFrame conf_frame;
    ReviewJFrame review_frame;
    AutomaticImageProcessingJFrame auto_processing_frame;
    private JMenuItem datami;

    public FluoJJFrame() throws InvalidOperationOnResourceException {

        loadConfiguration();
        initMenuBar();
        setJMenuBar(mb);
    }

    protected void loadConfiguration() throws InvalidOperationOnResourceException {
        setResizable(false);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        em = ConfigurationDB.getEM();
        cconfigurationdb = ConfigurationDB.getInstance();
        FluoJUtils.addFluoJTool();
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public void initMenuBar() {
        mb = new JMenuBar();

        // Setting menus
        filemn = new JMenu("Process");
        windowmn = new JMenu("Window");
        helpmn = new JMenu("Help");
        mb.add(windowmn);
        mb.add(filemn);
        mb.add(helpmn);

        // Setting menu items
        resetdbmi = new JMenuItem("Reset DB");
        resetdbmi.setMnemonic('R');
        if (!(this instanceof OutputJFrame)) {
            filemn.add(resetdbmi);
        }

        trainmi = new JMenuItem("Train Classifier");
        trainmi.setMnemonic('T');
        filemn.add(trainmi);

        exportmi = new JMenuItem("Export Data");
        exportmi.setMnemonic('E');
        filemn.add(exportmi);

        exporthistmi = new JMenuItem("Export Histograms");
        filemn.add(exporthistmi);

        configurationmi = new JMenuItem("Configuration");
        windowmn.add(configurationmi);

        trainingmi = new JMenuItem("Training");
        windowmn.add(trainingmi);

        classifiermi = new JMenuItem("Automatic Processing");
        windowmn.add(classifiermi);

        reviewmi = new JMenuItem("Review");
        windowmn.add(reviewmi);

        statisticsmi = new JMenuItem("Statistics");
        windowmn.add(statisticsmi);

        histogramsmi = new JMenuItem("Histograms");
        windowmn.add(histogramsmi);

        datami = new JMenuItem("Image Data");
        datami.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    loadImageData(getCImageProcess());
                } catch (InvalidOperationOnResourceException ex) {
                    Logger.getLogger(FluoJJFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        windowmn.add(datami);

        JMenuItem hcontentsmi = new JMenuItem("Contents...");
        helpmn.add(hcontentsmi);

        // Setting menu item listeners
        resetdbmi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cconfigurationdb.isEmpty(getSample())) {
                    JOptionPane.showMessageDialog(FluoJJFrame.this, "Sample is already empty");
                }
                resetDB();
            }
        });
        trainmi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                trainClassifier();
            }
        });

        exporthistmi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                String msg = "Export Successfull";
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showSaveDialog(FluoJJFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        CellProcessor sc = new CellProcessor(sample);
                        for (SampleFeature sf : getSample().getSampleFeatureList()) {
                            sc.exportHistograms(sf, fc.getSelectedFile().getAbsolutePath(), em);
                        }

                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(FluoJJFrame.this, e1.getMessage());
                    }
                }

            }
        });

        statisticsmi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showStatistics();
            }
        });
        configurationmi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conf_frame = new ConfigurationJFrame();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    Classifier.getLogger().log(Level.SEVERE, ex.getMessage(), e);
                }
            }
        });

        trainingmi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    training_frame = new TrainingJFrame();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    Classifier.getLogger().log(Level.SEVERE, ex.getMessage(), e);
                }
            }
        });

        classifiermi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    auto_processing_frame = new AutomaticImageProcessingJFrame();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    Classifier.getLogger().log(Level.SEVERE, ex.getMessage(), e);
                }
            }
        });

        reviewmi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    review_frame = new ReviewJFrame();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    Classifier.getLogger().log(Level.SEVERE, ex.getMessage(), e);
                }
            }
        });

        histogramsmi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (ConfigurationDB.isEmpty(getSample())) {
                        throw new IllegalArgumentException("Sample not trained");
                    }
                    //new SessionHistogramsJDialog(FluoJJFrame.this, classifier);

                    new ClassifierHistogramsJDialog(FluoJJFrame.this);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(FluoJJFrame.this, e1.getMessage());
                }
            }
        });

        hcontentsmi.setMnemonic('H');
        hcontentsmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        hcontentsmi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    String uri = "http://biocomp.cnb.csic.es/twiki/bin/view/FluoJ/WebHome";
                    if (!java.awt.Desktop.isDesktopSupported()) {
                        throw new IllegalArgumentException("Desktop is not supported (fatal)");
                    }

                    if (uri == null) {
                        throw new IllegalArgumentException("Usage: OpenURI [URI [URI ... ]]");
                    }

                    java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

                    if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {

                        throw new IllegalArgumentException("Desktop doesn't support the browse action (fatal)");
                    }
                    try {

                        java.net.URI myuri = new java.net.URI(uri);
                        desktop.browse(myuri);
                    } catch (Exception ex) {
                        throw new IllegalArgumentException(ex);
                    }
                } catch (Exception ex) {
                }
            }
        });

        exportmi.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                String msg = "Export Successfull";
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = fc.showSaveDialog(FluoJJFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        getClassifier().exportAllData(fc.getSelectedFile(), em);

                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(FluoJJFrame.this, e1.getMessage());
                    }
                }

            }
        });

    }

    protected void trainClassifier() {
        CellProcessor sp = new CellProcessor(sample);
        sp.importTrainingData(em);
        sp.processCells(em);
        getClassifier().classify();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ComparisonJFrame(classifier, em);
                if (!cconfigurationdb.isEmpty(sample)) {
                    new ClassifierHistogramsJDialog(FluoJJFrame.this);
                }
            }
        });
    }

    public void showLegend() {
        new CLegendJDialog(this, classifier);

    }

    protected void showStatistics() {
        new ComparisonJFrame(getClassifier(), em);

    }

    protected int resetDB() {
        int result = JOptionPane
                .showConfirmDialog(null, "All classification data will be lost with this action.\nAre you sure you want to reset database?", "Reset Database", JOptionPane.OK_CANCEL_OPTION);
        if (result == 0)// Yes
        {
            classifier = new Classifier(sample, em);
            trainClassifier();
            classifier.removeOldSessionData(new Date(), em);
            // JOptionPane.showMessageDialog(this,
            // "Database resetted successfully");

        }
        return result;
    }

    public Sample getSample() {
        return sample;
    }

    protected void close() {

        setVisible(false);
        dispose();
    }

    public abstract FluoJImageProcessor getCImageProcess() throws InvalidOperationOnResourceException;

    void resetCImageProcess() {
        cip = null;
    }

    public Classifier getClassifier() {

        return classifier;
    }

    public void loadImageData(FluoJImageProcessor cip) {
        try {
            new ImageParticlesTableJDialog(FluoJJFrame.this, cip, false);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
