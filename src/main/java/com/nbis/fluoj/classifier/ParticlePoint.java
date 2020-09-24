package com.nbis.fluoj.classifier;

import java.awt.Point;

public class ParticlePoint extends Point{
	
	private int pixel;
	private int label;

	public int getPixel() {
		return pixel;
	}

	public void setPixel(int pixel) {
		this.pixel = pixel;
	}

	
	public ParticlePoint(int x, int y, int pixel) {
		super(x, y);
		this.pixel = pixel;
		this.label = -1;
	}
	
	public ParticlePoint(int x, int y, int pixel, int label) {
		super(x, y);
		this.pixel = pixel;
		this.label = label;
		
	}
	
	
	public int getLabel() {
		return label;
	}

	public ParticlePoint(Point p, int pixel) {
		super(p);
		this.pixel = pixel;
	}

	public void setLabel(int label) {
		this.label = label;
		
	}
	
	public String toString()
	{
		return super.toString() + String.format("\npixel:%s label:%s", pixel, label);
	}

	

}
