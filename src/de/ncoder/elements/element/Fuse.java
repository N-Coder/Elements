package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Burnable;
import de.ncoder.elements.element.type.Protected;
import de.ncoder.elements.engine.Element;

public class Fuse extends Element implements Burnable{
	public boolean active;

	public Fuse() {
		setActive(false);
		setFluidity(0);
		setGravity(0);
	}

	@Override
	public void act() {
		super.act();
		if (active) {
			interactWith(getX() + 1, getY());
			interactWith(getX() - 1, getY());
			interactWith(getX(), getY() + 1);
			interactWith(getX(), getY() - 1);
			getWorld().replaceElement(this, new Fire());
		}
	}

	@Override
	public void interactWithElement(Element e) {
		if (active) {
			if (e instanceof Fuse) {
				((Fuse) e).setActive(true);
			} else if (e instanceof Protected) {

			} else {
				getWorld().replaceElement(e, new Fire());
			}
		}
	}

	@Override
	public void interactWithSpace(int x, int y) {
		if (active) {
			getWorld().setElementAt(x, y, new Fire());
		}
	}

	public void setActive(boolean active) {
		this.active = active;
		if (active) {
			setColor(248, 238, 177);
		} else {
			setColor(192, 168, 120);
		}
	}

	@Override
	public void burn(Element from) {
		setActive(true);
	}
}
