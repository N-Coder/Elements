package de.ncoder.elements.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import de.ncoder.elements.Manager;
import de.ncoder.elements.gui.action.AbstractAction;
import de.ncoder.elements.gui.action.Action;
import de.ncoder.elements.gui.action.ActionType;

public class MenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private Manager manager;

	private JMenu world;
	private JMenu edit;
	private JMenu extra;

	private JMenu traces;
	private JMenu brush;

	private JCheckBoxMenuItem active;

	private JRadioButtonMenuItem tracesOff;
	private JRadioButtonMenuItem tracesSlight;
	private JRadioButtonMenuItem tracesStrong;

	private JRadioButtonMenuItem brush1;
	private JRadioButtonMenuItem brush3;
	private JRadioButtonMenuItem brush5;
	private JRadioButtonMenuItem brush10;

	private ButtonGroup tracesGroup;
	private ButtonGroup brushGroup;
	private ActionListener actionListener;
	private MenuListener menuListener;

	public MenuBar(Manager manager) {
		this.manager = manager;
	}

	public void create() {
		createListeners();
		createMenuWorld();
		createMenuEdit();
		createMenuExtra();
	}

	public void createListeners() {
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				manager.getGuiManager().getActionListener().actionPerformed(e);
				refresh();
			}
		};
		menuListener = new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				refresh();
			}

			@Override
			public void menuDeselected(MenuEvent e) {}

			@Override
			public void menuCanceled(MenuEvent e) {}
		};
	}

	public void createMenuWorld() {
		world = new JMenu("World");
		world.addMenuListener(menuListener);
		add(world);

		world.add(makeMenuItem(new JMenuItem(), "New", new Action<Object>(ActionType.NEW)));
		world.add(makeMenuItem(new JMenuItem(), "Resize", new Action<Object>(ActionType.RESIZE, null)));
		world.addSeparator();
		world.add(makeMenuItem(new JMenuItem(), "Save", new Action<Object>(ActionType.SAVE)));
		world.add(makeMenuItem(new JMenuItem(), "Load", new Action<Object>(ActionType.LOAD)));
		world.add(makeMenuItem(new JMenuItem(), "Save As", new Action<Object>(ActionType.SAVE_AS, null)));
		world.add(makeMenuItem(new JMenuItem(), "Load From", new Action<Object>(ActionType.LOAD_FROM, null)));
		world.addSeparator();
		active = (JCheckBoxMenuItem) makeMenuItem(new JCheckBoxMenuItem(), "Play", new Action<Object>(ActionType.PLAY_PAUSE, null));
		world.add(active);
	}

	public void createMenuEdit() {
		edit = new JMenu("View");
		edit.addMenuListener(menuListener);
		add(edit);

		traces = new JMenu("Traces");
		edit.add(traces);
		tracesGroup = new ButtonGroup();

		tracesOff = (JRadioButtonMenuItem) makeMenuItem(new JRadioButtonMenuItem(), "Off", new Action<Object>(ActionType.TRACES, 0));
		tracesGroup.add(tracesOff);
		traces.add(tracesOff);

		tracesSlight = (JRadioButtonMenuItem) makeMenuItem(new JRadioButtonMenuItem(), "Slight", new Action<Object>(ActionType.TRACES, 1));
		tracesGroup.add(tracesSlight);
		traces.add(tracesSlight);

		tracesStrong = (JRadioButtonMenuItem) makeMenuItem(new JRadioButtonMenuItem(), "Strong", new Action<Object>(ActionType.TRACES, 2));
		tracesGroup.add(tracesStrong);
		traces.add(tracesStrong);

		brush = new JMenu("Brush");
		edit.add(brush);
		brushGroup = new ButtonGroup();

		brush1 = (JRadioButtonMenuItem) makeMenuItem(new JRadioButtonMenuItem(), "Size 1", new Action<Object>(ActionType.BRUSH, 1));
		brushGroup.add(brush1);
		brush.add(brush1);

		brush3 = (JRadioButtonMenuItem) makeMenuItem(new JRadioButtonMenuItem(), "Size 3", new Action<Object>(ActionType.BRUSH, 3));
		brushGroup.add(brush3);
		brush.add(brush3);

		brush5 = (JRadioButtonMenuItem) makeMenuItem(new JRadioButtonMenuItem(), "Size 5", new Action<Object>(ActionType.BRUSH, 5));
		brushGroup.add(brush5);
		brush.add(brush5);

		brush10 = (JRadioButtonMenuItem) makeMenuItem(new JRadioButtonMenuItem(), "Size 10", new Action<Object>(ActionType.BRUSH, 10));
		brushGroup.add(brush10);
		brush.add(brush10);
	}

	public void createMenuExtra() {
		extra = new JMenu("Extra");
		extra.addMenuListener(menuListener);
		add(extra);

		extra.add(makeMenuItem(new JMenuItem(), "Mods", new Action<Object>(ActionType.MODS, null)));
		extra.add(makeMenuItem(new JMenuItem(), "Convert", new Action<Object>(ActionType.CONVERT, null)));
		extra.add(makeMenuItem(new JMenuItem(), "Info", new Action<Object>(ActionType.INFO, null)));
	}

	public void refresh() {
		switch (manager.getGuiManager().getCanvasTraces()) {
		case 0:
			tracesOff.setSelected(true);
			break;
		case 1:
			tracesSlight.setSelected(true);
			break;
		case 2:
		default:
			tracesStrong.setSelected(true);
			break;
		}
		switch (manager.getGuiManager().getCanvasBrushSize()) {
		case 0:
		case 1:
			brush1.setSelected(true);
			break;
		case 2:
		case 3:
			brush3.setSelected(true);
			break;
		case 4:
		case 5:
			brush5.setSelected(true);
			break;
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			brush10.setSelected(true);
			break;
		}
		active.setSelected(manager.getWorld().isActive());
	}

	public JMenuItem makeMenuItem(JMenuItem item, String title, AbstractAction<?> a) {
		item.setText(title);
		item.addActionListener(actionListener);
		item.setActionCommand(manager.getXStream().toXML(a));
		if (a.getKey() != null) {
			item.setAccelerator(a.getKey().toKeyStroke());
		}
		return item;
	}
}
