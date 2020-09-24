package com.nbis.fluoj.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.process.ImageProcessor;

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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.ConfigurationDB;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

/**
 * Initializes classification process with Image segmentation through
 * {@link classifier.FluoJImageProcessor}. User must label particles detected with
 * an {@link persistence.Type}, using legend provided on
 * {@link gui.CLegendJDialog}. Particles info is stored on
 * {@link classifier.SegmentedParticle}, including a default classification to speed
 * classification process. To persist {@link classifier.SegmentedParticle} info
 * "Save Measures" button is provided. "Edit" button invokes
 * {@link FluoJ_.Review_} plugin. Classifier training is done through
 * "Train Classifier" function, using information persisted for
 * {@link classifier.Sample} on {@link persistence.Session}. To reset
 * information persisted for {@link classifier.Sample} "Reset DB" button is
 * included.
 * 
 * @author Airen
 * 
 */
public class TrainingJFrame extends FluoJJFrame {

	private ImagePlus img;
	private ImagePlus classifier_img;
	private JComboBox samplecb;
	private TypesPane typespn;
	private JButton initializebt;
	private JButton deleteallmarksbt;
	private JButton savebt;
	private FluoJTrainingCanvas canvas;

	private JButton resultsbt;
	private int idimage;
	private JButton closeimgsbt;
	private JPanel samplepn;
	private JPanel buttonspn;
	private List<Sample> tsamples;
	private JLabel thresholdlb;


	public TrainingJFrame() throws InvalidOperationOnResourceException {
		samples = cconfigurationdb.getSamples(em);
		idimage = -1;
		tsamples = new ArrayList<Sample>();
		for (Sample s : samples)
			if (!s.getTypeList().isEmpty())
				tsamples.add(s);
		if (tsamples.isEmpty())
			throw new IllegalArgumentException("There are no available samples for training. Configure samples first, including types");
		initComponents();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					new ImageWindow(new ImagePlus("/home/airen/Dropbox/guille/AVG_CEM -AP1  15.10.2012 Actin488-Dynamin565-Icam647.lif - Series001.tif"));
					// new ImageWindow(new
					// ImagePlus("/home/airen/Dropbox/Work/Classifier/workspace/CellClassifier_/dist/images/1475.tif"));
					TrainingJFrame ctf = new TrainingJFrame();
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
			}
		});
	}

	private void initComponents() throws InvalidOperationOnResourceException {
		setResizable(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				if (canvas != null)
					canvas.close();
				em.close();
			}

		});
		setTitle("Training");
		
		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		
		
		initSamplePane();
		getContentPane().add(samplepn, FluoJUtils.getConstraints(constraints, 0, 0, 1));

		
		// add(rightpn, WindowUtils.getConstraints(constraints, 1, 1, 1));

		setClassifier();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 1;
		add(typespn, constraints);

		initButtonsPane();
		getContentPane().add(buttonspn, FluoJUtils.getConstraints(constraints, 0, 3, 1));

		pack();
		positionWindow();
		setAlwaysOnTop(true);
		setVisible(true);
	}

	private void initSamplePane() {

		samplepn = new JPanel();
		JLabel samplelb = new JLabel("Sample:");
		samplepn.add(samplelb);

		samplecb = new JComboBox(tsamples.toArray());
		samplecb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (canvas != null)
					canvas.close();
				try {
					setClassifier();
				}
				catch (InvalidOperationOnResourceException e1) {
				}// if could not be loaded trainer will keep previous
				typespn.revalidate();
				pack();
			}
		});
		samplepn.add(samplecb);

		initializebt = new JButton("Init Image");
		initializebt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					initializeImage();
				}
				catch (InvalidOperationOnResourceException e1) {
					JOptionPane.showMessageDialog(TrainingJFrame.this, e1.getMessage());
				}
			}
		});
		samplepn.add(initializebt);

		samplepn.add(new JLabel("Threshold:"));
		thresholdlb = new JLabel("   ");
		samplepn.add(thresholdlb);
	}

	private void initButtonsPane() {
		buttonspn = new JPanel();
		deleteallmarksbt = new JButton("Delete Marks");
		deleteallmarksbt.setEnabled(false);
		deleteallmarksbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteAllMarks();
			}
		});
		buttonspn.add(deleteallmarksbt);
		savebt = new JButton("Save Measures");
		savebt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveMeasures();
			}
		});
		buttonspn.add(savebt);
		resultsbt = new JButton("Show Results");
		resultsbt.setEnabled(false);
		resultsbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {

							ReviewJFrame f = new ReviewJFrame(classifier, idimage);
							f.setVisible(true);
						}
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
		buttonspn.add(resultsbt);
		closeimgsbt = new JButton("Close Images");
		closeimgsbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (img == null)
					return;
				img.close();
				canvas.close();
				closeimgsbt.setEnabled(false);
			}
		});
		closeimgsbt.setEnabled(false);
		buttonspn.add(closeimgsbt);
	}

	// positions frame on the screen
	private void positionWindow() {
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		int x = 95 * (dim.width - abounds.width) / 100;
		setLocation(x, (dim.height - abounds.height) / 2);
	}

	private void setClassifier() throws InvalidOperationOnResourceException {
		Sample newsample = (Sample) samplecb.getSelectedItem();
		Classifier newclassifier = ConfigurationDB.getInstance().getPersistentClassifier(newsample);
		if (newclassifier == null)
			newclassifier = new Classifier(newsample, em);

		if (newsample.getTypeList().size() == 0) {
			String msg = Constants.getEmptyFieldMsg("types");
			JOptionPane.showMessageDialog(this, msg);
			if (sample != null)// Set to previous
			{
				samplecb.setSelectedItem(sample);
				return;
			}
			else
				throw new IllegalArgumentException(msg);
		}
		sample = newsample;
		classifier = newclassifier;
		if(typespn == null)
			typespn = new TypesPane(newclassifier, em);
		else 
			typespn.initTypesPane(newclassifier, em);
		typespn.updateTotals();
		thresholdlb.setText(String.valueOf(sample.getThreshold()));
	}

	

	
	private void deleteAllMarks() {
		if (canvas != null)
			canvas.resetClassification();
		typespn.resetCounts();
		
		
		savebt.setEnabled(false);
		deleteallmarksbt.setEnabled(false);
	}

	protected int resetDB() {
		int result = super.resetDB();
		if (result == 0)// Yes
		{

			resultsbt.setEnabled(false);
			typespn.resetTotals();
			setMarkerTitle();
		}
		return result;
	}

	private void saveMeasures() {
		if (cip == null)
			return;
		idimage = cip.saveMeasures(classifier);
		resultsbt.setEnabled(true);

		canvas.close();
		img.close();
		tryOpenNext(img);
		// classifier.close();
		savebt.setEnabled(false);
		deleteallmarksbt.setEnabled(false);
		closeimgsbt.setEnabled(false);
		setMarkerTitle();
		typespn.updateTotals();
		typespn.resetCounts();
	}

	private void tryOpenNext(ImagePlus img) {
		try {
			String directory = img.getOriginalFileInfo().directory;
			String fileName = img.getOriginalFileInfo().fileName;
			int dotindex = fileName.lastIndexOf(".");
			int next = Integer.parseInt(fileName.substring(0, dotindex)) + 1;

			String file = directory + next + fileName.substring(dotindex, fileName.length());
			System.out.println(file);
			if (new File(file).exists())
				new ImagePlus(file).show();
		}
		catch (Exception e) {
		}
	}

	private void setMarkerTitle() {
		int imgcount = classifier.getImagesCount(em);
		String title = (imgcount == 1) ? "Marker. 1 Image Processed" : String.format("Marker. %s Images Processed", imgcount);
		typespn.setBorder(javax.swing.BorderFactory.createTitledBorder(title));

	}

	/**
	 * Uses active image to initialize. Image is processed through
	 * {@link ImageProcessor}, particle border are highlighted and buttons are
	 * enabled.
	 * 
	 * @throws InvalidOperationOnResourceException
	 */
	private void initializeImage() throws InvalidOperationOnResourceException {
		resultsbt.setEnabled(false);
		img = WindowManager.getCurrentImage();
		if (img == null || img.getStackSize() > 1) {
			IJ.noImage();
			return;
		}

		ImageProcessor ip = img.getProcessor();
		ip.resetRoi();
		ip = ip.crop();
		classifier_img = new ImagePlus("Classifier - " + img.getTitle(), ip);
		cip = new FluoJImageProcessor(classifier_img, sample, true);
		updateCounts();
		closeimgsbt.setEnabled(true);
		deleteallmarksbt.setEnabled(true);
		savebt.setEnabled(true);
		requestFocus();

		canvas = new FluoJTrainingCanvas(this);
		canvas.displayImage();

	}

	@Override
	public FluoJImageProcessor getCImageProcess() throws InvalidOperationOnResourceException {
		return cip;
	}

	public com.nbis.fluoj.persistence.Type getActiveType() {
		return typespn.getActiveType();
	}

	public void updateCounts()
	{
		typespn.updateCountsFromImageParticles(cip.getFilteredParticles());
		
	}

	public void setActiveType(String label)
	{
		typespn.setActiveType(label);
		
	}

	

}
