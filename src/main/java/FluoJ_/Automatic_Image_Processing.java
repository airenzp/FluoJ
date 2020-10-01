package FluoJ_;

import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import com.nbis.fluoj.classifier.Classifier;
import com.nbis.fluoj.gui.AutomaticImageProcessingJFrame;
import com.nbis.fluoj.gui.TrainingJFrame;
import ij.plugin.PlugIn;

/**
 * ImageJ plugin to provide Classifier training. Initializes
 * {@link gui.TrainingJFrame}
 * 
 * @author Airen
 * 
 */
public class Automatic_Image_Processing implements PlugIn {

	public void run(String args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					AutomaticImageProcessingJFrame f = new AutomaticImageProcessingJFrame();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
					Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});
	}

}
