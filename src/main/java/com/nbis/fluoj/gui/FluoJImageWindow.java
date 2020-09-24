package com.nbis.fluoj.gui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import com.nbis.fluoj.classifier.FluoJImageProcessor;
import com.nbis.fluoj.classifier.InvalidOperationOnResourceException;

import ij.IJ;
import ij.ImagePlus;
import ij.Menus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;

public class FluoJImageWindow extends ImageWindow implements KeyListener
{

	private FluoJJFrame frame;
	private FluoJImageProcessor cip;

	public FluoJImageWindow(FluoJJFrame frame) throws InvalidOperationOnResourceException
	{
		super(frame.getCImageProcess().getPerimetersImg());

		this.frame = frame;
		this.cip = frame.getCImageProcess();
		initMenuBar();
	}

	public FluoJImageWindow(FluoJJFrame frame, ImageCanvas canvas) throws InvalidOperationOnResourceException
	{
		this(frame, frame.getCImageProcess(), canvas);

	}

	public FluoJImageWindow(FluoJJFrame frame, FluoJImageProcessor cip, ImageCanvas canvas) throws InvalidOperationOnResourceException
	{
		super(cip.getPerimetersImg(), canvas);
		this.frame = frame;
		this.cip = cip;
		initMenuBar();
		removeKeyListener(ij);
		addKeyListener(this);
	}

	private void initMenuBar()
	{
		MenuBar mb = new MenuBar();
		Menu menu = new Menu("Data");
		MenuItem datami = new MenuItem("Image Data");
		datami.setShortcut(new MenuShortcut(KeyEvent.VK_D));
		datami.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				showData();

			}
		});
		menu.add(datami);
		mb.add(menu);
		setMenuBar(mb);
	}

	protected void showData()
	{
		try
		{
			new ImageParticlesTableJDialog(frame, cip, false);
		}
		catch (InvalidOperationOnResourceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void windowActivated(WindowEvent e)
	{
		// if (IJ.isMacintosh())
		// this.setMenuBar(Menus.getMenuBar());
		if (IJ.debugMode)
			IJ.write(imp.getTitle() + ": Activated");
		if (!closed)
		{
			// ic.requestFocus();
			WindowManager.setCurrentWindow(this);
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		System.out.println("Key typed");
		if (frame instanceof TrainingJFrame)
		{

			String label = String.valueOf(e.getKeyChar());
			System.out.println(label);
			((TrainingJFrame) frame).setActiveType(label);
		}

	}

}
