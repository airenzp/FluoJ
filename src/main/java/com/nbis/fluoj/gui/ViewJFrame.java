package com.nbis.fluoj.gui;

import ij.ImagePlus;
import ij.gui.ImageWindow;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.SampleImage;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;
import com.nbis.fluoj.persistence.Scell;

/**
 * Displays {@link classifier.Sample} info filtered by {@link persistence.Type}
 * on a given Image. Output includes Image info and {@link gui.CTableJFrame}
 * table.
 *
 * @author Airen
 *
 */
public class ViewJFrame extends OutputJFrame {

    private JComboBox samplecb;
    private JComboBox imagecb;
    private JComboBox originalcb;
    private JComboBox winnercb;
    private JButton img_resultsbt;
    private JButton cells_infobt;
    private JButton editbt;
    private int idimage = -1;
    private Short original;
    private Short winner;
    private CTableJFrame tablefr;
    private ImageWindow iw;
    private List<Scell> scells;
    private OutputImageCanvas canvas;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    ViewJFrame f = new ViewJFrame(ConfigurationDB.getInstance().getRandomClassifier(), -1);
                    f.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ViewJFrame(Classifier classifier) throws InvalidOperationOnResourceException {
        this(classifier, -1);
    }

    public ViewJFrame(Classifier classifier, int idimage) throws InvalidOperationOnResourceException {
        super();
        initSamples();
        this.idimage = idimage;
        initComponents(classifier);
        if (idimage != -1) {
            imagecb.setSelectedItem(String.format("%s.tif", idimage));
        }
        this.scells = classifier.getScells(idimage, em);
    }

    public ViewJFrame() throws InvalidOperationOnResourceException {
        super();
        initSamples();
        initComponents(ConfigurationDB.getInstance().getRandomClassifier(samples));
    }

    private void initSamples() throws InvalidOperationOnResourceException {
        samples = cconfigurationdb.getSamples(em);
        if (samples.isEmpty()) {
            System.out.println("Empty samples");
            String msg = "No available samples to view";
            throw new InvalidOperationOnResourceException(msg);
        }
    }

    public void initComponents(Classifier newclassifier) throws InvalidOperationOnResourceException {

        setResizable(false);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                close();
                closeSample();
                em.close();
            }
        });
        JPanel search_pn = new JPanel();
        search_pn.setLayout(new GridBagLayout());
        search_pn.setBorder(javax.swing.BorderFactory.createTitledBorder("Search"));

        setTitle("Training View");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.gridx = 0;
        constraints.gridy = 0;
        search_pn.add(new JLabel("Sample"), constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;

        samplecb = new JComboBox(samples.toArray());
        imagecb = new JComboBox();
        originalcb = new JComboBox();
        winnercb = new JComboBox();
        samplecb.setSelectedItem(newclassifier.getSample());
        setIdsample(newclassifier);

        samplecb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    setIdsample(ConfigurationDB.getInstance().getPersistentClassifier((Sample) samplecb.getSelectedItem()));
                } catch (InvalidOperationOnResourceException e1) {
                    // if you can do this action there was a previous sample, so
                    // exception will not be thrown, only message to the user
                }
            }
        });

        imagecb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                SampleImage ir = (SampleImage) imagecb.getSelectedItem();
                idimage = ir.getIdimage();
                scells = classifier.getScells(idimage, em);
            }
        });
        search_pn.add(samplecb, constraints);
        constraints.gridx = 2;
        search_pn.add(new JLabel("Image"), constraints);

        constraints.gridx = 3;
        search_pn.add(imagecb, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        search_pn.add(new JLabel("Original"), constraints);

        original = ((com.nbis.fluoj.persistence.Type) originalcb.getSelectedItem()).getIdtype();
        originalcb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                original = ((com.nbis.fluoj.persistence.Type) originalcb.getSelectedItem()).getIdtype();
            }
        });
        constraints.gridx = 1;
        search_pn.add(originalcb, constraints);

        constraints.gridx = 2;
        search_pn.add(new JLabel("Winner"), constraints);

        winner = ((com.nbis.fluoj.persistence.Type) winnercb.getSelectedItem()).getIdtype();
        winnercb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                winner = ((com.nbis.fluoj.persistence.Type) winnercb.getSelectedItem()).getIdtype();

            }
        });
        constraints.gridx = 3;
        search_pn.add(winnercb, constraints);

        JPanel pane = new JPanel();
        img_resultsbt = new JButton("Show Image");
        img_resultsbt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    showImageResults();
                } catch (InvalidOperationOnResourceException e) {
                    // TODO Auto-generated catch block
                    JOptionPane.showMessageDialog(ViewJFrame.this, e.getMessage());
                }

            }
        });
        cells_infobt = new JButton("Show Classifier Data");
        cells_infobt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                showCellsInfo();

            }
        });
        editbt = new JButton("Edit");
        editbt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                close();
                showClassifierEditor();

            }
        });
        setLayout(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(search_pn, constraints);
        constraints.gridy = 1;
        pane.add(img_resultsbt);
        pane.add(cells_infobt);
        pane.add(editbt);
        add(pane, constraints);
        // classifier.close();
        pack();
        positionWindow();
        setAlwaysOnTop(true);
    }

    private void validateImagesCount(Classifier classifier, EntityManager em) throws InvalidOperationOnResourceException {
        int images_count = classifier.getImagesCount(em);
        if (images_count == 0) {
            if (sample != null) {
                samplecb.setSelectedItem(sample);// leaving previous sample
            }			// classifier.close();
            String msg = "No Images Processed";
            JOptionPane.showMessageDialog(this, msg);
            throw new InvalidOperationOnResourceException(msg);

        }
    }

    protected void closeSample() {

        if (tablefr != null) {
            tablefr.setVisible(false);
            tablefr.dispose();
        }
        if (iw != null && !iw.isClosed()) {
            iw.close();
        }

    }

    protected void showClassifierEditor() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {

                    new ReviewJFrame(classifier, getSampleImage());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    protected void showCellsInfo() {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    tablefr = new CTableJFrame(classifier, idimage, original, winner, em);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void setIdsample(Classifier newclassifier) throws InvalidOperationOnResourceException {
        Sample newsample = newclassifier.getSample();
        validateImagesCount(newclassifier, em);
        if (classifier != null) {
            closeSample();
        }
        classifier = newclassifier;
        sample = newsample;
        imagecb.setModel(new DefaultComboBoxModel(classifier.getImages(em).toArray()));
        SampleImage ir = (SampleImage) imagecb.getSelectedItem();
        idimage = ir.getIdimage();
        this.scells = classifier.getScells(idimage, em);
        List<com.nbis.fluoj.persistence.Type> types = new ArrayList<com.nbis.fluoj.persistence.Type>(classifier.getSample().getTypeList());
        com.nbis.fluoj.persistence.Type unknown = new com.nbis.fluoj.persistence.Type(null);
        unknown.setName("None");
        types.add(unknown);
        com.nbis.fluoj.persistence.Type all = new com.nbis.fluoj.persistence.Type((short) 0);
        all.setName("Any");
        types.add(0, all);
        originalcb.setModel(new DefaultComboBoxModel(types.toArray()));
        winnercb.setModel(new DefaultComboBoxModel(types.toArray()));
        original = ((com.nbis.fluoj.persistence.Type) originalcb.getSelectedItem()).getIdtype();
        winner = ((com.nbis.fluoj.persistence.Type) winnercb.getSelectedItem()).getIdtype();
        pack();
        // classifier.close();
    }

    private void positionWindow() {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        int x = 95 * (dim.width - abounds.width) / 100;
        int y = (dim.height - abounds.height) / 10;
        setLocation(x, y);
    }

    public SampleImage getSampleImage() {
        return (SampleImage) imagecb.getSelectedItem();
    }

    public void showImageResults() throws InvalidOperationOnResourceException {
        SampleImage ir = (SampleImage) imagecb.getSelectedItem();
        cip = new FluoJImageProcessor(ConfigurationDB.getImagePlus(ir), sample, true);
        canvas = new OutputImageCanvas(this);
        canvas.reload(classifier.getScells(ir.getIdimage(), original, winner, em));
        iw = new FluoJImageWindow(this, canvas);

    }

    protected int resetDB() {
        int result = super.resetDB();
        if (result == 0) {
            close();// There are no images to review
            closeSample();
        }
        return result;
    }

    @Override
    List<Scell> getScells() {
        return scells;
    }

}
