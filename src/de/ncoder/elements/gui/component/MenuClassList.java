package de.ncoder.elements.gui.component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Comparator;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import de.ncoder.elements.Manager;
import de.ncoder.elements.gui.action.AbstractAction;
import de.ncoder.elements.gui.action.SelectAction;

public class MenuClassList extends JList<SelectAction> implements KeyListener {
	private static final long serialVersionUID = 1L;
	private Manager manager;
	private DefaultListModel<SelectAction> model;
	private Comparator<AbstractAction<?>> comparator = new Comparator<AbstractAction<?>>() {
		@Override
		public int compare(AbstractAction<?> o1, AbstractAction<?> o2) {
			if (o1.getKey().isCtrlDown() != o2.getKey().isCtrlDown()) {
				if (!o1.getKey().isCtrlDown()) {
					return -30;
				} else {
					return 30;
				}
			}
			if (o1.getKey().isShiftDown() != o2.getKey().isShiftDown()) {
				if (!o1.getKey().isShiftDown()) {
					return -20;
				} else {
					return 20;
				}
			}
			if (o1.getKey().isAltDown() != o2.getKey().isAltDown()) {
				if (!o1.getKey().isAltDown()) {
					return -10;
				} else {
					return 10;
				}
			}
			return new Character(o1.getKey().getCharacter()).compareTo(o2.getKey().getCharacter());
		}
	};

	public MenuClassList(Manager manager) {
		this.manager = manager;
	}

	public void create() {
		setCellRenderer(new MenuClassListRenderer(manager));
		model = new DefaultListModel<SelectAction>();
		fetchActions();
		setModel(model);
		setBackground(null);
		manager.getGuiManager().addKeyListener(this);
	}

	public void addElement(SelectAction element) {
		model.addElement(element);
	}

	public void clearElements() {
		model.clear();
	}

	public SelectAction getElement(int index) {
		return model.get(index);
	}

	public SelectAction removeElement(int index) {
		return model.remove(index);
	}

	public void refresh() {
		fetchActions();
		repaint();
	}

	public void fetchActions() {
		clearElements();
		for (SelectAction a : manager.getGuiManager().getSelectActions()) {
			addElement(a);
		}
	}

	public Comparator<AbstractAction<?>> getComparator() {
		return comparator;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		AbstractAction<?> a = manager.getGuiManager().getAction(e);
		if (a instanceof SelectAction) {
			setSelectedValue(a, true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
}
