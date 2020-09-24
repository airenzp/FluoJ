package com.nbis.fluoj.gui;

import java.util.List;

import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

import com.nbis.fluoj.persistence.Scell;


public abstract class OutputJFrame extends FluoJJFrame {
	
	
	public OutputJFrame() throws InvalidOperationOnResourceException {
		super();
		// TODO Auto-generated constructor stub
	}

	abstract List<Scell> getScells();
	
	
	public FluoJImageProcessor getCImageProcess()
	{
		return cip;
	}

}
