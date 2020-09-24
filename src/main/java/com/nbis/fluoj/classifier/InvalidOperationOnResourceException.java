package com.nbis.fluoj.classifier;

import java.util.logging.Level;

import javax.swing.JOptionPane;

/**
 * Allows throwing resource exceptions that can not be solved, but user does, and log the error
 * 
 * @author Airen
 *
 */
public class InvalidOperationOnResourceException extends Exception {
	
	public InvalidOperationOnResourceException(String msg)
	{
		super(msg);
		Classifier.getLogger().log(Level.INFO, msg, this);
	}
	

}
