package de.ncoder.elements.gui.frame;

import javax.swing.JScrollPane;

import de.ncoder.elements.Manager;
import de.ncoder.elements.gui.ElementsFrame;
import de.ncoder.elements.gui.component.MenuBar;
import de.ncoder.elements.gui.component.MenuClassList;

public class MenuFrame extends ElementsFrame {
	private static final long serialVersionUID = 1L;

	private MenuBar menuBar;
	private MenuClassList menuClassList;

	public MenuFrame(Manager manager) {
		super(manager);
	}

	@Override
	public void create() {
		menuBar = new MenuBar(manager);
		menuClassList = new MenuClassList(manager);
		menuBar.create();
		menuClassList.create();
		menuBar.refresh();
		menuClassList.refresh();
		setJMenuBar(menuBar);
		add(new JScrollPane(menuClassList));
		pack();
		setSize(150, 300);
		setLocationRelativeTo(manager.getGuiManager().getFrame(CanvasFrame.class));
		setLocation(manager.getGuiManager().getFrame(CanvasFrame.class).getWidth(), 0);
	}

	public void refresh() {
		menuBar.refresh();
		menuClassList.refresh();
		repaint();
	}

	public MenuBar getElementsMenuBar() {
		return menuBar;
	}

	public MenuClassList getMenuClassList() {
		return menuClassList;
	}
}
