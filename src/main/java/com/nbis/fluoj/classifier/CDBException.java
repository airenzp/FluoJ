package com.nbis.fluoj.classifier;

import java.util.logging.Level;

import javax.swing.JOptionPane;


/**
 * Allows throwing database exceptions as unchecked exceptions and log the error
 * 
 * @author Airen
 *
 */
public class CDBException extends RuntimeException {
	
	public static String msg = "Classifier Database Exception";
	
	public CDBException()
	{
		super(msg);
		Classifier.getLogger().log(Level.INFO, msg, this);
	}
	
	public CDBException(String msg)
	{
		super(msg);
		Classifier.getLogger().log(Level.INFO, msg, this);
	}

}
