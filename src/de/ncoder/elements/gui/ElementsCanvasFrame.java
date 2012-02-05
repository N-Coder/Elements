package de.ncoder.elements.gui;

import de.ncoder.elements.Manager;
import de.ncoder.nlib.gui.NCanvasFrame;

public class ElementsCanvasFrame extends NCanvasFrame{
	private static final long serialVersionUID = 1L;
	protected Manager manager;

	public ElementsCanvasFrame(Manager manager) {
		super();
		this.manager = manager;
	}
}
