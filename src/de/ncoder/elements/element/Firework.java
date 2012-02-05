package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Burnable;
import de.ncoder.elements.engine.Element;

public class Firework extends Element implements Burnable {
	private int treshold;
	private boolean active;

	public Firework() {
		setColor(254, 120, 0);
		setActive(false);
		treshold = random.nextInt(20) + 10;
	}

	@Override
	public void act() {
		if (getY() < treshold) {
			switch (random.nextInt(3)) {
			case 0:
				explodeAll();
				break;
			case 1:
				explodeDiagonal();
				break;
			case 2:
				explodeStraight();
				break;
			}
			getWorld().removeElement(this);
		}
	}
	
	/*
	@Override
	public void applyPhysics() {
		int lastX = getX();
		int lastY = getY();
		super.applyPhysics();
		if(lastX==getX()&&lastY==getY()&&isActive()) {
			treshold = getWorld().getElementsYCount();
		}
	}
	*/

	public void explodeDiagonal() {
		interactWith(getX() - 1, getY() - 1);
		interactWith(getX() + 1, getY() - 1);
		interactWith(getX() - 1, getY() + 1);
		interactWith(getX() + 1, getY() + 1);
	}

	public void explodeStraight() {
		interactWith(getX() - 1, getY());
		interactWith(getX() + 1, getY());
		interactWith(getX(), getY() - 1);
		interactWith(getX(), getY() + 1);
	}

	public void explodeAll() {
		explodeDiagonal();
		explodeStraight();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		if (active) {
			setGravity(-1);
			setFluidity(1);
		} else {
			setGravity(0);
			setFluidity(0);
		}
	}

	@Override
	public int getSpawnY() {
		return -1;
	}
	
	@Override
	public void interactWithSpace(int x, int y) {
		getWorld().setElementAt(x, y, new FireworkExplosion(x - getX(), y - getY()));
	}

	@Override
	public void burn(Element from) {
		setActive(true);
	}
}
