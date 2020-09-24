package FluoJ_;

import com.nbis.fluoj.gui.ConfigurationJFrame;
import com.nbis.fluoj.gui.TrainingJFrame;
import ij.plugin.PlugIn;

import java.util.logging.Level;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.nbis.fluoj.classifier.Classifier;

public class Configuration_ implements PlugIn {

	public void run(String args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					// Set cross-platform Java L&F (also called "Metal")
					
					ConfigurationJFrame frame = new ConfigurationJFrame();
				} catch (Exception e) {
					Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});
	}

}
