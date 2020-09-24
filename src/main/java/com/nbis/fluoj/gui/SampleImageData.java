package com.nbis.fluoj.gui;

import com.nbis.fluoj.persistence.Sample;

public class SampleImageData
{
	Sample sample;
	String dir;
	String notes;
	
	public SampleImageData(Sample sample, String dir, String notes)
	{
		this.sample = sample;
		this.dir = dir;
		this.notes = notes;
	}
	
	public String toString()
	{
		return dir;
	}
}