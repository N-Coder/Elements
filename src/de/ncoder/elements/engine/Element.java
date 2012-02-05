package de.ncoder.elements.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import de.ncoder.elements.element.type.Penetrable;

public class Element {
	private transient int x;
	private transient int y;
	private transient World world;
	private transient int moving;
	private transient boolean falling;

	private int fluidity = 0;
	private int gravity = 1;
	private Color color;

	protected static Random random = new Random();

	private static boolean intelligentParticels = true;

	public void paint(Graphics2D g, int width, int height) {
		g.setColor(getColor());
		g.fillRect(0, 0, width, height);
	}

	public void paintPreview(Graphics2D g, int width, int height) {
		g.setColor(getColor());
		g.fillRect(0, 0, width, height);
	}

	public void paintCursor(Graphics2D g, Element over, int width, int height) {
		if (over != null && getColor().equals(over.getColor())) {
			g.setColor(Color.BLACK);
			g.drawRect(2, 2, width - 5, height - 5);
		} else {
			g.setColor(color);
			g.drawRect(0, 0, width - 1, height - 1);
		}
	}

	public void act() {
	}

	public void applyPhysics() {
		setMoving(0);
		applyGravity();
	}

	public void applyGravity() {
		if (getGravity() != 0
				&& getWorld().isInBounds(getX(), getY() + getGravity())) {
			falling = true;
			if (!fall()) {
				if (!avoid()) {
					if (!penetrate()) {
						falling = false;
					}
				}
			}
		}
	}

	public boolean fall() {
		if (getWorld().isAlwaysFree(getX(), getY() + getGravity())) {
			getWorld().moveElementTo(getX(), getY() + getGravity(), this);
			return true;
		} else {
			return false;
		}
	}

	public boolean avoid() {
		int direction = 0;
		if (getFluidity() > 0) {
			int distance = 1;
			// FIXME: prefers going right
			while (distance <= getFluidity()) {
				if (random.nextBoolean()) {
					if (avoid(distance, +1)) {
						direction = +1;
						break;
					}
					if (avoid(distance, -1)) {
						direction = -1;
						break;
					}
				} else {
					if (avoid(distance, -1)) {
						direction = -1;
						break;
					}
					if (avoid(distance, +1)) {
						direction = +1;
						break;
					}
				}
				distance++;
			}
			setMoving(direction);
		}
		return direction != 0;
	}

	public boolean avoid(int distance, int direction) {
		if (getWorld().isInBounds(getX() + distance * direction, getY())
				&& (!falling || random.nextInt(4) == 0)) {
			if (freeWay(getX() + direction, getX() + distance * direction,
					direction)
					&& getWorld().isAlwaysFree(getX() + direction, getY())
					&& getWorld().isAlwaysFree(getX() + distance * direction,
							getY() + getGravity())) {
				getWorld().switchElements(getX(), getY(), getX() + direction,
						getY());
				return true;
			}
		}
		return false;
	}

	public boolean freeWay(int start, int end, int direction) {
		if (end < start) {
			int s = start;
			start = end;
			end = s;
		}
		for (int x = start; x <= end && x < getWorld().getElementsXCount()
				&& x >= 0; x++) {
			if (!getWorld().isFree(x, getY())
					&& (getWorld().getElementAt(x, getY()).getMoving() == direction || !intelligentParticels)) {
				return false;
			}
		}
		return true;
	}

	public boolean penetrate() {
		if (!(this instanceof Penetrable)
				&& !getWorld().isFree(getX(), getY() + getGravity())
				&& getWorld().getElementAt(getX(), getY() + getGravity()) instanceof Penetrable) {
			getWorld().switchElements(this, getX(), getY() + getGravity());
			return true;
		}
		return false;
	}

	public void interactWith(int x, int y) {
		if (getWorld().isInBounds(x, y)) {
			Element e = getWorld().getElementAt(x, y);
			if (e != null) {
				if (e.isValid()) {
					interactWithElement(e);
				}
			} else {
				interactWithSpace(x, y);
			}
		}
	}

	public void interactWithElement(Element e) {
	}

	public void interactWithSpace(int x, int y) {
	}

	// ----------------------------GETTERS & SETTERS----------------------------

	public int getFluidity() {
		return fluidity;
	}

	public void setFluidity(int fluidity) {
		this.fluidity = fluidity;
	}

	public int getGravity() {
		return gravity;
	}

	public void setGravity(int gravity) {
		this.gravity = gravity;
	}

	public int getSpawnX() {
		return 0;
	}

	public int getSpawnY() {
		return getGravity();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setColor(int r, int g, int b) {
		setColor(new Color(r, g, b));
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public int getMoving() {
		return moving;
	}

	public void setMoving(int moving) {
		this.moving = moving;
	}

	public boolean isValid() {
		return getWorld() != null && getWorld().isInBounds(getX(), getY());
	}

	void setInternalData(World world, int x, int y) {
		this.world = world;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public World getWorld() {
		return world;
	}

	void clearInternalData() {
		setInternalData(null, -1, -1);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + hashCode() + ") at ("
				+ getX() + ", " + getY() + ")";
	}
}
