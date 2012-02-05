package de.ncoder.elements.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.ncoder.elements.element.Delete;

/**
 * The World containing all the Elements
 * 
 * @author Niko Fink
 */
public class World {
	/**
	 * Array containing all the Elements, where the first Arrays represent the x value, the second Arrays the y values.
	 * So an Element should be accessed using <code>elements[x][y]</code> or the <code>getElement(int, int)</code> function
	 * 
	 * @see {@link World#getElementAt(int, int)}
	 */
	private Element[][] elements;
	/**
	 * The changes which should be applied after the physics finished.
	 * 
	 * @see {@link World#setElementAt(int, int, Element)}
	 */
	private Map<Point, Element> elementJobs = new HashMap<Point, Element>();
	/**
	 * The background the World should be drawn with
	 */
	private Color background = new Color(0, 0, 0);
	/**
	 * If the physics are active and acts should be called
	 */
	private boolean active = true;

	/**
	 * New World with the default size 80x80.
	 */
	public World() {
		this(new Element[80][80]);
	}

	/**
	 * New World using the given Elements.
	 * 
	 * @param elements
	 */
	public World(Element[][] elements) {
		this.elements = elements;
	}

	public void paint(Graphics2D g, int elementWidth, int elementHeight) {
		Element e;
		for (int x = 0; x < getElementsXCount(); x++) {
			for (int y = 0; y < getElementsYCount(); y++) {
				if ((e = getElementAt(x, y)) != null) {
					g.translate(x * elementWidth, y * elementHeight);
					e.setInternalData(this, x, y);
					e.paint(g, elementWidth, elementHeight);
					g.translate(-x * elementWidth, -y * elementHeight);
				}
			}
		}
	}

	public void paintBackground(Graphics2D g, int elementWidth, int elementHeight) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getElementsXCount() * elementWidth, getElementsYCount() * elementHeight);
	}

	public void act() {
		applyElementPhysics();
		callElementsActs();
	}
	
	public void debugElementPhysics() {
		Map<Class<? extends Element>, Integer> stat1 = getStatistics();
		applyElementPhysics();
		Map<Class<? extends Element>, Integer> stat2 = getStatistics();
		Map<Class<? extends Element>, Integer> changed = new HashMap<Class<? extends Element>, Integer>();
		for (Entry<Class<? extends Element>, Integer> e : stat1.entrySet()) {
			changed.put(e.getKey(), (stat2.containsKey(e.getKey()) ? stat2.get(e.getKey()) : 0) - e.getValue());
		}
		for (Entry<Class<? extends Element>, Integer> e : stat2.entrySet()) {
			changed.put(e.getKey(), e.getValue() - (stat1.containsKey(e.getKey()) ? stat1.get(e.getKey()) : 0));
		}
		changed.remove(Delete.class);
		if (changed.size() > 0) {
			System.out.println(changed);
		}
	}

	public synchronized void applyElementPhysics() {
		if (active) {
			for (int x = 0; x < getElementsXCount(); x++) {
				for (int y = 0; y < getElementsYCount(); y++) {
					if (getElementAt(x, y) != null) {
						getElementAt(x, y).setInternalData(this, x, y);
						getElementAt(x, y).applyPhysics();
					}
				}
			}
		}
		executeElementJobs();
	}

	public synchronized void callElementsActs() {
		if (active) {
			for (int x = 0; x < getElementsXCount(); x++) {
				for (int y = 0; y < getElementsYCount(); y++) {
					if (getElementAt(x, y) != null) {
						getElementAt(x, y).setInternalData(this, x, y);
						getElementAt(x, y).act();
					}
				}
			}
		}
		executeElementJobs();
	}

	public synchronized void setSize(int width, int height) {
		Element[][] newElements;
		clearElementJobs();
		newElements = new Element[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (isInBounds(x, y)) {
					newElements[x][y] = getElementAt(x, y);
				}
			}
		}
		elements = newElements;
	}

	public Element getNextElementAt(int x, int y) {
		return elementJobs.get(new Point(x, y));
	}

	public Element getElementAt(int x, int y) {
		return elements[x][y];
	}

	public boolean isAlwaysFree(int x, int y) {
		return getElementAt(x, y) == null && getNextElementAt(x, y) == null;
	}

	public boolean isFree(int x, int y) {
		return getElementAt(x, y) == null;
	}

	public Element[] getElementsX(int x) {
		return elements[x];
	}

	public Element[] getElementsY(int y) {
		if (getElementsXCount() > 0) {
			Element[] found = new Element[getElementsXCount()];
			for (int x = 0; x < getElementsXCount(); x++) {
				found[x] = elements[x][y];
			}
			return found;
		} else {
			return new Element[0];
		}
	}

	public synchronized void setElementAt(int x, int y, Element e) {
		if (!isInBounds(x, y)) {
			throw new ArrayIndexOutOfBoundsException("Won't be able to set Element at (" + x + ", " + y + ") to " + e);
		}
		elementJobs.put(new Point(x, y), e);
	}

	public void moveElementTo(int x, int y, Element e) {
		setElementAt(e.getX(), e.getY(), null);
		setElementAt(x, y, e);
	}

	public void replaceElement(Element oldE, Element newE) {
		setElementAt(oldE.getX(), oldE.getY(), newE);
	}

	public void switchElements(Element a, Element b) {
		replaceElement(a, b);
		replaceElement(b, a);
	}

	public void switchElements(Element a, int x2, int y2) {
		Element b = getElementAt(x2, y2);
		int x1 = a.getX();
		int y1 = a.getY();
		setElementAt(x1, y1, b);
		setElementAt(x2, y2, a);
	}

	public void switchElements(int x1, int y1, int x2, int y2) {
		Element a = getElementAt(x1, y1);
		Element b = getElementAt(x2, y2);
		setElementAt(x1, y1, b);
		setElementAt(x2, y2, a);
	}

	public void removeElement(Element e) {
		setElementAt(e.getX(), e.getY(), null);
	}

	private synchronized void executeElementJob(int x, int y, Element e) {
		if (elements[x][y] != null) {
			elements[x][y].clearInternalData();
		}
		elements[x][y] = e;
		if (e != null) {
			e.setInternalData(this, x, y);
		}
	}

	public synchronized void executeElementJobs() {
		for (Entry<Point, Element> entry : elementJobs.entrySet()) {
			executeElementJob(entry.getKey().x, entry.getKey().y, entry.getValue());
		}
		elementJobs.clear();
	}

	public synchronized void clear() {
		elements = new Element[getElementsXCount()][getElementsYCount()];
	}

	public void clearElementJobs() {
		elementJobs.clear();
	}

	public int getElementsXCount() {
		return elements.length;
	}

	public int getElementsYCount() {
		return (elements.length > 0 ? elements[0].length : 0);
	}

	public boolean isInBounds(int x, int y) {
		return x >= 0 && x < getElementsXCount() && y >= 0 && y < getElementsYCount();
	}

	public boolean touchesBorder(int x, int y) {
		return touchesXBorder(x) || touchesYBorder(y);
	}

	public boolean touchesXBorder(int x) {
		return x == 0 || x == getElementsXCount() - 1;
	}

	public boolean touchesYBorder(int y) {
		return y == 0 || y == getElementsYCount() - 1;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void toggleActive() {
		active = !active;
	}

	public synchronized String toString() {
		StringBuilder bob = new StringBuilder();
		bob.append("[\n");
		for (int x = 0; x < getElementsXCount(); x++) {
			bob.append("[");
			for (int y = 0; y < getElementsYCount(); y++) {
				if (getElementAt(x, y) == null) {
					bob.append("null");
				} else {
					bob.append(getElementAt(x, y).getClass().getSimpleName());
				}
				if (y < getElementsYCount() - 1) {
					bob.append(",	");
				}
			}
			bob.append("]\n");
		}
		bob.append("]");
		return bob.toString();
	}

	public synchronized Map<Class<? extends Element>, Integer> getStatistics() {
		Map<Class<? extends Element>, Integer> stat = new HashMap<Class<? extends Element>, Integer>();
		for (int x = 0; x < getElementsXCount(); x++) {
			for (int y = 0; y < getElementsYCount(); y++) {
				Class<? extends Element> c = Delete.class;
				if (getElementAt(x, y) != null) {
					c = getElementAt(x, y).getClass();
				}
				int val = 0;
				if (stat.get(c) != null) {
					val = stat.get(c);
				}
				stat.put(c, val + 1);
			}
		}
		return stat;
	}
}
