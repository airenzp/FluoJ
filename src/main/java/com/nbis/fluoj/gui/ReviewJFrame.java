package com.nbis.fluoj.gui;

import ij.ImagePlus;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import com.nbis.fluoj.persistence.Imageresource;
import com.nbis.fluoj.persistence.Sample;
import com.nbis.fluoj.persistence.Scell;
import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

/*
 * 
 * Initializes classification process with Image segmentation through {@link scplugin.SCImageProcess}.
 * User must review particles {@link persistence.Type} assigned, using legend provided on {@link scplugin.SCPLegendJDialog}. Particles info is stored on 
 * {@link classifier.ImageLabel}. To merge changes "Save Changes" button is provided. "View Info" button
 * displays {@link scplugin.CTableJFrame} table. Classifier training is done through "Train Classifier" function, using information persisted for
 * {@link classifier.Sample} on {@link persistence.Classifiersession}. To reset information persisted for {@link classifier.Sample} "Reset DB" button is included.
 * 
 * */
public class ReviewJFrame extends OutputJFrame implements ActionListener
{

	private JComboBox sample_cb;
	private TypesPane typespn;
	private JButton update_bt;
	private JButton loadimg_bt;
	private OutputImageCanvas canvas;

	byte mode;
	private JButton closeimgs_bt;
	private JComboBox imagecb;
	private List<Scell> scells;
	private Imageresource ir;
	private CTableJFrame tablefr;
	private JButton cells_infobt;

	private JLabel thresholdlb;
	private JComboBox originalcb;
	private JComboBox winnercb;
	private JPanel filterpn;
	private JPanel buttons_pn;
	private List<Imageresource> images;

	public static void main(String[] args)
	{

		// Creating a frame as recommended by JAVA api
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					ReviewJFrame cef = new ReviewJFrame();
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
			}
		});
	}

	/*
	 * Constructor that receives classifier and allows image specification. -1
	 * image value means no image specified.
	 */
	public ReviewJFrame() throws InvalidOperationOnResourceException
	{
		super();
		initSamples();
		initComponents(cconfigurationdb.getRandomClassifier(samples));
		imagecb.setSelectedIndex(imagecb.getItemCount() - 1);
		loadImage();
	}

	/*
	 * Empty constructor. Uses current session classifier and no image
	 * specified.
	 */
	public ReviewJFrame(Classifier classifier, Imageresource ir) throws InvalidOperationOnResourceException
	{
		initSamples();
		initComponents(classifier);
		if (ir != null)
			imagecb.setSelectedItem(ir);
		else
			imagecb.setSelectedIndex(imagecb.getItemCount() - 1);
		loadImage();

	}

	private void initSamples() throws InvalidOperationOnResourceException
	{
		samples = cconfigurationdb.getTrainedSamples(em); 
		if (samples.isEmpty())
		{
			System.out.println("Empty samples");
			String msg = "No available samples to review";
			throw new InvalidOperationOnResourceException(msg);
		}
		
	}

	public ReviewJFrame(Classifier classifier, int idimage) throws InvalidOperationOnResourceException
	{
		initSamples();
		initComponents(classifier);
		if (idimage != -1)
			for (Imageresource ir : images)
				if (ir.getIdimage().equals(idimage))
				{
					imagecb.setSelectedItem(ir);
					break;
				}
		loadImage();

	}

	/*
	 * Initializes components using data from classifier
	 */
	private void initComponents(Classifier newclassifier) throws InvalidOperationOnResourceException
	{

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent winEvt)
			{
				close();
				closeSample();
			}
		});
		setTitle("Training Review");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		JLabel sample_lb = new JLabel("Sample:");
		JPanel samplepn = new JPanel();
		samplepn.add(sample_lb);

		sample_cb = new JComboBox(samples.toArray());
		sample_cb.setSelectedItem(newclassifier.sample);

		// event to switch sample
		sample_cb.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					setClassifier(cconfigurationdb.getPersistentClassifier((Sample) sample_cb.getSelectedItem()));
				}
				catch (InvalidOperationOnResourceException e1)
				{
					// if you can do this action there was a previous sample, so
					// exception will not be thrown, only message to the user
				}
				typespn.revalidate();
				pack();
			}
		});

		samplepn.add(sample_cb);
		samplepn.add(new JLabel("Image"));
		imagecb = new JComboBox();

		// event to choose image to review
		imagecb.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadimg_bt.requestFocus();
			}
		});
		samplepn.add(imagecb);
		loadimg_bt = new JButton("Load");
		// event to load image to review
		loadimg_bt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					loadImage();
					imagecb.requestFocus();
				}
				catch (InvalidOperationOnResourceException e1)
				{
					// if this event occurs and image could not be loaded, go to
					// previous img, wich exists because frame was loaded
					imagecb.setSelectedItem(ir);
				}
			}
		});
		samplepn.add(loadimg_bt);

		samplepn.add(new JLabel("Threshold:"));
		thresholdlb = new JLabel("   ");
		samplepn.add(thresholdlb);
		getContentPane().add(samplepn, FluoJUtils.getConstraints(constraints, 0, 0, 1));

		filterpn = new JPanel();
		filterpn.setBorder(BorderFactory.createTitledBorder("Filter"));
		originalcb = new JComboBox();
		originalcb.addActionListener(this);
		winnercb = new JComboBox();
		winnercb.addActionListener(this);
		filterpn.add(new JLabel("Manual:"));
		filterpn.add(originalcb);
		filterpn.add(new JLabel("Automatic:"));
		filterpn.add(winnercb);
		add(filterpn, FluoJUtils.getConstraints(constraints, 0, 1, 1, GridBagConstraints.HORIZONTAL));
		initButtons();
		getContentPane().add(buttons_pn, FluoJUtils.getConstraints(constraints, 0, 3, 1));
		setClassifier(newclassifier);

		add(typespn, FluoJUtils.getConstraints(constraints, 0, 2, 1, GridBagConstraints.HORIZONTAL));

		pack();
		FluoJUtils.setLocation(locationx, this);
		setAlwaysOnTop(true);
		setVisible(true);
		imagecb.requestFocus();
	}

	private void initButtons()
	{
		buttons_pn = new JPanel();
		update_bt = new JButton("Save Changes");
		update_bt.setEnabled(false);

		// event to save classification
		update_bt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveClassification();
			}
		});
		buttons_pn.add(update_bt);
		closeimgs_bt = new JButton("Close Image");
		closeimgs_bt.setEnabled(false);
		closeimgs_bt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				closeWindows();
			}
		});
		cells_infobt = new JButton("Classifier Data");
		// event to show frame of cells data
		cells_infobt.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				showCellsInfo();

			}
		});
		buttons_pn.add(closeimgs_bt);
		buttons_pn.add(cells_infobt);
	}

	/*
	 * Method that allows closing table frame and Image Window
	 */
	private void closeWindows()
	{
		if (canvas != null)
			canvas.close();
		if (tablefr != null)
		{
			tablefr.setVisible(false);
			tablefr.dispose();
		}
		update_bt.setEnabled(false);
		closeimgs_bt.setEnabled(false);
		cells_infobt.setEnabled(false);
	}

	// Creates tablefr. No new thread is used for this frame, because is not
	// independent of this window.
	protected void showCellsInfo()
	{
		tablefr = new CTableJFrame(classifier, canvas.getScells(), em);
	}

	protected void closeSample()
	{
		if (canvas != null)
			canvas.close();
		if (tablefr != null)
		{
			tablefr.setVisible(false);
			tablefr.dispose();
		}
	}

	protected int resetDB()
	{
		int result = super.resetDB();
		if (result == 0)
		{
			close();// There are no images to review
			closeSample();
		}
		return result;
	}

	protected void loadImage() throws InvalidOperationOnResourceException
	{
		closeWindows();
		ir = (Imageresource) imagecb.getSelectedItem();
		ImagePlus img = ir.getImagePlus();

		cip = new FluoJImageProcessor(img, sample, false);

		scells = classifier.getScells(ir.getIdimage(), em);
		typespn.updateCountsFromScell(scells);
		canvas = new OutputImageCanvas(this);
		canvas.displayImage();

		closeimgs_bt.setEnabled(true);
		cells_infobt.setEnabled(true);
		filterOutput();
	}

	List<Scell> getScells()
	{
		return scells;
	}



	private void setClassifier(Classifier newclassifier) throws InvalidOperationOnResourceException
	{
		if (newclassifier == null)
			newclassifier = new Classifier((Sample) sample_cb.getSelectedItem(), em);
		Sample newsample = newclassifier.getSample();

		if (classifier != null)
			closeSample();
		classifier = newclassifier;
		sample = newsample;
		images = classifier.getImages(em);
		if (images.isEmpty())
			throw new IllegalArgumentException("No sample images to review");
		ir = images.get(0);
		scells = classifier.getScells(ir.getIdimage(), em);
		fillClassifierComponents(images);
		loadImage();

	}

	private void fillClassifierComponents(List<Imageresource> images)
	{

		imagecb.setModel(new DefaultComboBoxModel(images.toArray()));

		if (typespn == null)
			typespn = new TypesPane(classifier, em);
		else
			typespn.initTypesPane(classifier, em);
		typespn.updateTotals();
		thresholdlb.setText(String.valueOf(sample.getThreshold()));
		List<com.nbis.fluoj.persistence.Type> types = new ArrayList<com.nbis.fluoj.persistence.Type>(classifier.getSample().getTypeList());
		com.nbis.fluoj.persistence.Type unknown = new com.nbis.fluoj.persistence.Type(null, "None");
		types.add(unknown);
		com.nbis.fluoj.persistence.Type all = new com.nbis.fluoj.persistence.Type(0, "Any");
		types.add(0, all);
		originalcb.setModel(new DefaultComboBoxModel(types.toArray()));
		winnercb.setModel(new DefaultComboBoxModel(types.toArray()));
		pack();
	}

	class ModeRbActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JRadioButton rb = ((JRadioButton) e.getSource());
			mode = Byte.parseByte(rb.getActionCommand());
		}
	}

	void enableUpdate()
	{
		update_bt.setEnabled(true);
	}

	private void saveClassification()
	{
		classifier.mergeSCells(scells, em);
		typespn.updateTotals();
		typespn.resetCounts();
		closeWindows();
		imagecb.requestFocus();
	}

	public com.nbis.fluoj.persistence.Type getActiveType()
	{
		return typespn.getActiveType();
	}

	public void updateAllCounts()
	{
		typespn.updateCountsFromScell(scells);

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		filterOutput();

	}
	
	private void filterOutput()
	{
		int original = ((com.nbis.fluoj.persistence.Type) originalcb.getSelectedItem()).getIdtype();
		int winner = ((com.nbis.fluoj.persistence.Type) winnercb.getSelectedItem()).getIdtype();
		canvas.reload(classifier.getScells(ir.getIdimage(), original, winner, em));
	}

}
