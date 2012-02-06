package de.ncoder.elements.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.ncoder.elements.Manager;
import de.ncoder.elements.Manager.ExceptionListener;
import de.ncoder.elements.Manager.MessageListener;
import de.ncoder.elements.engine.Element;
import de.ncoder.elements.gui.action.AbstractAction;
import de.ncoder.elements.gui.action.ActionType;
import de.ncoder.elements.gui.action.SelectAction;
import de.ncoder.elements.gui.dialog.ExceptionDialog;
import de.ncoder.elements.gui.dialog.SizeDialog;
import de.ncoder.elements.gui.frame.CanvasFrame;
import de.ncoder.elements.gui.frame.InfoFrame;
import de.ncoder.elements.gui.frame.MapFrame;
import de.ncoder.elements.gui.frame.MenuFrame;
import de.ncoder.elements.gui.frame.ModFrame;
import de.ncoder.elements.utils.FileConverter;
import de.ncoder.nlib.Key;
import de.ncoder.nlib.gui.NFrame;

public class GUIManager {
	private Manager manager;
	private Map<String, NFrame> frames = new HashMap<String, NFrame>();
	private JFileChooser saveChooser;
	private Image defaultIcon;

	public GUIManager(Manager manager) {
		this.manager = manager;
		try {
			defaultIcon = ImageIO.read(getClass().getResource("/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Manager getManager() {
		return manager;
	}

	public void create() {
		try {
			addActions(getClass().getResourceAsStream(Manager.KEYS_FILE));
		} catch (Exception e) {
			manager.exception(e, "Key map couldn't be loaded.");
		}
		putFrame(new CanvasFrame(manager));
		putFrame(new MenuFrame(manager));
		putFrame(new MapFrame(manager));
		for (Entry<String, NFrame> e : getFrameSet()) {
			e.getValue().create();
		}
		addKeyListener(getKeyListener());
		manager.addExceptionListener(new ExceptionListener() {
			@Override
			public void exception(Exception exception, String message) {
				new ExceptionDialog(getParentFrame(), "Exception", message, exception).setVisible(true);
			}
		});
		manager.addMessageListener(new MessageListener() {
			@Override
			public void message(String message, String title) {
				JOptionPane.showMessageDialog(getParentFrame(), message, title, JOptionPane.PLAIN_MESSAGE);
			}
		});
		saveChooser = new JFileChooser(manager.getProperties().getProperty("default.folder"));
		saveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		setVisible(true);
	}

	public JFileChooser getFileChooser() {
		return saveChooser;
	}

	public Image getDefaultIcon() {
		return defaultIcon;
	}

	// -------------------------------LISTENERS---------------------------------

	private List<AbstractAction<?>> keyCommands = new ArrayList<AbstractAction<?>>();
	private long lastKeyEvent = 0;
	private ActionListener actionListener = new ActionListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			manager.updateXStream();
			Object o = manager.getXStream().fromXML(e.getActionCommand());
			if (o != null && o instanceof AbstractAction) {
				AbstractAction<?> a = (AbstractAction<?>) o;
				if (e.getWhen() > lastKeyEvent) {
					System.out.println(a.getType() + ": " + a.getAdditional());
					try {
						boolean chaught = true;
						switch (a.getType()) {
						case SELECT:
							setSelected((Class<? extends Element>) a.getAdditional());
							break;
						case NEW:
							manager.getWorld().clear();
							manager.getWorld().clearElementJobs();
							break;
						case PLAY_PAUSE:
							manager.getWorld().toggleActive();
							break;
						case TRACES:
							setCanvasTraces((Integer) a.getAdditional());
							break;
						case BRUSH:
							setCanvasBrushSize((Integer) a.getAdditional());
							break;
						case RESIZE:
							SizeDialog dia = new SizeDialog(getParentFrame(), manager);
							dia.setVisible(true);
							Dimension dim = dia.getNewSize();
							if (dim != null) {
								manager.getWorld().setSize(dim.width, dim.height);
								getFrame(CanvasFrame.class).repaint();
							}
							break;
						case STAT:
							System.out.println(manager.getWorld().getStatistics());
							break;
						case SAVE:
							save(getQuicksave());
							break;
						case LOAD:
							load(getQuicksave());
							break;
						case SAVE_AS:
							saveAs();
							break;
						case LOAD_FROM:
							loadFrom();
							break;
						case CONVERT:
							convert();
							break;
						case MODS:
							ModFrame modFrame = new ModFrame(manager);
							modFrame.create();
							modFrame.refresh();
							modFrame.setVisible(true);
							break;
						case INFO:
							InfoFrame infoFrame = new InfoFrame();
							infoFrame.create();
							infoFrame.setVisible(true);
							break;
						/*
						 * case SAVE:
						 * manager.getWorldManager().save(); break; case LOAD:
						 * try { manager.getWorldManager().load(); } catch
						 * (ConversionException ex) { manager.exception(ex,
						 * "The Level you tried to load seems to depend on a Modification not being available."
						 * ); } break; case SAVE_AS:
						 * manager.getWorldManager().saveAs(); break; case
						 * LOAD_FROM: try {
						 * manager.getWorldManager().loadFrom(); } catch
						 * (ConversionException ex) { manager.exception(ex,
						 * "The Level you tried to load seems to depend on a Modification not being available."
						 * ); } break; case CONVERT: if
						 * (manager.getWorldManager().convert()) {
						 * manager.message("Conversation complete.",
						 * "Converted"); } break; case PLAY_PAUSE:
						 * manager.getWorld().toggleActive(); break; case
						 * TRACES: setCanvasTraces((Integer) a.additional);
						 * break; case BRUSH: setCanvasBrushSize((Integer)
						 * a.additional); break; case MODS: ModFrame modFrame =
						 * new ModFrame(guiManager); modFrame.create();
						 * modFrame.refresh(); modFrame.setVisible(true); break;
						 */
						default:
							chaught = false;
							break;
						}
						if (chaught) {
							lastKeyEvent = e.getWhen();
						}
					} catch (Exception ex) {
						manager.exception(ex);
					}
				}
			}
		}
	};
	private KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			getActionListener().actionPerformed(new ActionEvent(e.getSource(), e.getID(), manager.getXStream().toXML(getAction(e)), e.getWhen(), e.getModifiers()));
		}
	};

	public void addKeyListener(KeyListener l) {
		for (Entry<String, NFrame> e : getFrameSet()) {
			e.getValue().addKeyListener(l);
		}
	}

	public void removeKeyListener(KeyListener l) {
		for (Entry<String, NFrame> e : getFrameSet()) {
			e.getValue().removeKeyListener(l);
		}
	}

	public KeyListener getKeyListener() {
		return keyListener;
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	public AbstractAction<?> getAction(Key k) {
		for (AbstractAction<?> a : keyCommands) {
			if (a.getKey().equals(k)) {
				return a;
			}
		}
		return null;
	}

	public AbstractAction<?> getAction(KeyEvent e) {
		return getAction(new Key(e));
	}

	public List<SelectAction> getSelectActions() {
		List<SelectAction> actions = new ArrayList<SelectAction>();
		for (AbstractAction<?> a : keyCommands) {
			if (a instanceof SelectAction) {
				actions.add((SelectAction) a);
			}
		}
		Collections.sort(actions, ((MenuFrame) getFrame(MenuFrame.class)).getMenuClassList().getComparator());
		return actions;
	}

	public AbstractAction<?> getActionFor(ActionType t) {
		for (AbstractAction<?> a : keyCommands) {
			if (a.getType() == t) {
				return a;
			}
		}
		return null;
	}

	public void addAction(AbstractAction<?> a) {
		keyCommands.add(a);
		if (getFrame(MenuFrame.class) != null) {
			getFrame(MenuFrame.class).repaint();
		}
	}

	public void removeAction(AbstractAction<?> a) {
		keyCommands.remove(a);
		if (getFrame(MenuFrame.class) != null) {
			getFrame(MenuFrame.class).repaint();
		}
	}

	public void removeAction(Key k) {
		keyCommands.remove(getAction(k));
		if (getFrame(MenuFrame.class) != null) {
			getFrame(MenuFrame.class).repaint();
		}
	}

	public void addActions(List<? extends AbstractAction<?>> l) {
		for (AbstractAction<?> a : l) {
			addAction(a);
		}
	}

	public void removeActions(List<? extends AbstractAction<?>> l) {
		for (AbstractAction<?> a : l) {
			removeAction(a);
		}
	}

	@SuppressWarnings("unchecked")
	public void addActions(InputStream from) {
		manager.updateXStream();
		List<AbstractAction<?>> newKeyCommands = (List<AbstractAction<?>>) manager.getXStream().fromXML(from);
		keyCommands.addAll(newKeyCommands);
	}

	@SuppressWarnings("unchecked")
	public void removeActions(InputStream from) {
		//FIXME Crashes when updateXStream is called between setActive and removeActions.
		List<AbstractAction<?>> newKeyCommands = (List<AbstractAction<?>>) manager.getXStream().fromXML(from);
		keyCommands.removeAll(newKeyCommands);
	}

	// -------------------------------FUNCTIONS--------------------------------

	public Class<? extends Element> getSelected() {
		SelectAction sa = ((MenuFrame) getFrame(MenuFrame.class)).getMenuClassList().getSelectedValue();
		if (sa != null) {
			return sa.additional;
		} else {
			return null;
		}
	}

	public void setSelected(Class<? extends Element> c) {
		((MenuFrame) getFrame(MenuFrame.class)).getMenuClassList().setSelectedValue(c, true);
	}

	public void setVisible(boolean visible) {
		for (Entry<String, NFrame> e : getFrameSet()) {
			e.getValue().setVisible(true);
		}
	}

	private File getQuicksave() {
		return new File(manager.getProperties().getProperty("quicksave.file"));
	}

	public void save(File file) {
		try {
			manager.getWorldManager().save(file);
		} catch (Exception e) {
			manager.exception(e, "File could not be saved.");
		}
	}

	public void load(File file) {
		try {
			manager.getWorldManager().load(file);
		} catch (Exception e) {
			manager.exception(e, "File could not be loaded.");
		}
	}

	public void saveAs() throws IOException {
		boolean wasActive = manager.getWorld().isActive();
		manager.getWorld().setActive(false);
		int result = getFileChooser().showSaveDialog(getParentFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			File selected = manager.getWorldManager().checkFile(getFileChooser().getSelectedFile());
			if (selected.exists()) {
				int confirm = JOptionPane.showConfirmDialog(getParentFrame(), "File " + selected.getPath() + " allready exists. Overwrite?", "File Exitsts", JOptionPane.YES_NO_OPTION);
				if (confirm != JOptionPane.YES_OPTION) {
					return;
				}
			} else {
				if (!selected.createNewFile()) {
					manager.message("File " + selected.getPath() + " could not be created.", "Creation failed");
					return;
				}
			}
			save(getFileChooser().getSelectedFile());
		}
		manager.getWorld().setActive(wasActive);
	}

	public void loadFrom() throws IOException {
		boolean wasActive = manager.getWorld().isActive();
		manager.getWorld().setActive(false);
		int result = getFileChooser().showOpenDialog(getParentFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			load(getFileChooser().getSelectedFile());
		} else {
			manager.getWorld().setActive(wasActive);
		}
	}

	public void convert() {
		boolean wasActive = manager.getWorld().isActive();
		manager.getWorld().setActive(false);
		int result = getFileChooser().showOpenDialog(getParentFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				FileConverter.convert(getFileChooser().getSelectedFile(), manager);
				manager.message("File converted.");
			} catch (IOException e) {
				manager.exception(e, "File could not be converted.");
			}
		} else {
			manager.getWorld().setActive(wasActive);
		}
	}

	// --------------------------------VALUES----------------------------------

	private int canvasElementWidth = 5;
	private int canvasElementHeight = 5;
	private int mapElementWidth = 1;
	private int mapElementHeight = 1;
	private int previewElementWidth = 5;
	private int previewElementHeight = 5;

	private int canvasOffsetX = 0;
	private int canvasOffsetY = 0;
	private int canvasSpeedX = 1;
	private int canvasSpeedY = 1;
	private int canvasTraces = 0;
	private int canvasBrushSize = 1;
	private Point canvasLastMousePosition;
	private Point canvasMousePosition;
	private Lock canvasMouseLock;

	private Color mapFrameColor = Color.WHITE;

	public int getCanvasElementWidth() {
		return canvasElementWidth;
	}

	public void setCanvasElementWidth(int canvasElementWidth) {
		this.canvasElementWidth = canvasElementWidth;
	}

	public int getCanvasElementHeight() {
		return canvasElementHeight;
	}

	public void setCanvasElementHeight(int canvasElementHeight) {
		this.canvasElementHeight = canvasElementHeight;
	}

	public int getMapElementWidth() {
		return mapElementWidth;
	}

	public void setMapElementWidth(int mapElementWidth) {
		this.mapElementWidth = mapElementWidth;
	}

	public int getMapElementHeight() {
		return mapElementHeight;
	}

	public void setMapElementHeight(int mapElementHeight) {
		this.mapElementHeight = mapElementHeight;
	}

	public int getPreviewElementWidth() {
		return previewElementWidth;
	}

	public void setPreviewElementWidth(int previewElementWidth) {
		this.previewElementWidth = previewElementWidth;
	}

	public int getPreviewElementHeight() {
		return previewElementHeight;
	}

	public void setPreviewElementHeight(int previewElementHeight) {
		this.previewElementHeight = previewElementHeight;
	}

	public int getCanvasOffsetX() {
		return canvasOffsetX;
	}

	public void setCanvasOffsetX(int canvasOffsetX) {
		this.canvasOffsetX = Math.max(0, Math.min(manager.getWorld().getElementsXCount() - (getFrame(CanvasFrame.class).getInnerWidth() / getCanvasElementWidth()), canvasOffsetX));
	}

	public int getCanvasOffsetY() {
		return canvasOffsetY;
	}

	public void setCanvasOffsetY(int canvasOffsetY) {
		this.canvasOffsetY = Math.max(0, Math.min(manager.getWorld().getElementsYCount() - (getFrame(CanvasFrame.class).getInnerHeight() / getCanvasElementHeight()), canvasOffsetY));
	}

	public int getCanvasSpeedX() {
		return canvasSpeedX;
	}

	public void setCanvasSpeedX(int canvasSpeedX) {
		this.canvasSpeedX = canvasSpeedX;
	}

	public int getCanvasSpeedY() {
		return canvasSpeedY;
	}

	public void setCanvasSpeedY(int canvasSpeedY) {
		this.canvasSpeedY = canvasSpeedY;
	}

	public int getCanvasTraces() {
		return canvasTraces;
	}

	public void setCanvasTraces(int canvasTraces) {
		this.canvasTraces = canvasTraces;
	}

	public int getCanvasBrushSize() {
		return canvasBrushSize;
	}

	public void setCanvasBrushSize(int canvasBrushSize) {
		this.canvasBrushSize = canvasBrushSize;
	}

	public Point getBrushPointMin() {
		int dist = (int) Math.floor(getCanvasBrushSize() / 2d);
		return new Point(getCanvasMousePosition().x - dist, getCanvasMousePosition().y - dist);
	}

	public Point getBrushPointMax() {
		int dist = (int) Math.floor(getCanvasBrushSize() / 2d);
		return new Point(getCanvasMousePosition().x + dist, getCanvasMousePosition().y + dist);
	}

	public Point getCanvasMousePosition() {
		return canvasMousePosition;
	}

	public void setCanvasMousePosition(Point canvasMousePosition) {
		setCanvasLastMousePosition(this.canvasMousePosition);
		this.canvasMousePosition = canvasMousePosition;
	}

	public Point getCanvasLastMousePosition() {
		return canvasLastMousePosition;
	}

	public void setCanvasLastMousePosition(Point canvasLastMousePosition) {
		this.canvasLastMousePosition = canvasLastMousePosition;
	}

	public Lock getCanvasMouseLock() {
		return canvasMouseLock;
	}

	public void setCanvasMouseLock(Lock canvasMouseLock) {
		if (canvasMouseLock == null) {
			setCanvasMouseLock(Lock.NONE);
		} else {
			this.canvasMouseLock = canvasMouseLock;
		}
	}

	public Color getMapFrameColor() {
		return mapFrameColor;
	}

	public void setMapFrameColor(Color mapFrameColor) {
		this.mapFrameColor = mapFrameColor;
	}

	// ----------------------------FRAME DELEGATES-----------------------------
	public NFrame getFrame(String name) {
		return frames.get(name);
	}

	public NFrame getFrame(Class<?> c) {
		return frames.get(c.getSimpleName());
	}

	public NFrame getParentFrame() {
		return getFrame(MenuFrame.class);
	}

	public int getFrameCount() {
		return frames.size();
	}

	public NFrame putFrame(String name, NFrame frame) {
		frame.setIconImage(getDefaultIcon());
		return frames.put(name, frame);
	}

	public NFrame putFrame(NFrame frame) {
		return putFrame(frame.getClass().getSimpleName(), frame);
	}

	public boolean hasFrames(String name) {
		return frames.containsKey(name);
	}

	public boolean hasFrame(NFrame frame) {
		return frames.containsValue(frame);
	}

	public Set<Entry<String, NFrame>> getFrameSet() {
		return frames.entrySet();
	}

	public void clearFrames() {
		frames.clear();
	}

	public NFrame removeFrame(String name) {
		return frames.remove(name);
	}

	// -------------------------------RESOURCES---------------------------------

	public static enum Lock {
		HORIZONTAL, VERTICAL, DOWN, UP, NONE, SET
	}
}
