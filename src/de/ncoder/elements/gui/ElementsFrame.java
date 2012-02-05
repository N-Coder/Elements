package de.ncoder.elements.gui;

import de.ncoder.elements.Manager;
import de.ncoder.nlib.gui.NFrame;

public class ElementsFrame extends NFrame {
	private static final long serialVersionUID = 1L;
	protected Manager manager;

	public ElementsFrame(Manager manager) {
		super();
		this.manager = manager;
	}
}
