/**
 * 
 */
package FluoJ_;

import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


import com.nbis.fluoj.classifier.Classifier;

import com.nbis.fluoj.gui.ReviewJFrame;
import com.nbis.fluoj.gui.ViewJFrame;
import ij.plugin.PlugIn;
import ij.plugin.frame.PlugInFrame;

/**
 * ImageJ plugin to provide Classifier training revision. Initializes
 * {@link gui.ReviewJFrame}
 * 
 * @author Airen
 * 
 */

public class Review_ implements PlugIn
{

    public void run(String args)
    {
	SwingUtilities.invokeLater(new Runnable()
	{

	    @Override
	    public void run()
	    {
		try
		{
		    ReviewJFrame f = new ReviewJFrame();
		    f.setVisible(true);
		}
		catch (Exception e)
		{
		    JOptionPane.showMessageDialog(null, e.getMessage());
		    Classifier.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	    }
	});
    }

}
