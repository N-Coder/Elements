package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Protected;
import de.ncoder.elements.engine.Element;

public class Hole extends Element implements Protected {
	public Hole() {
		setColor(50, 50, 50);
		setGravity(0);
	}

	@Override
	public void act() {
		interactWith(getX() - 1, getY());
		interactWith(getX() + 1, getY());
		interactWith(getX(), getY() + 1);
		interactWith(getX(), getY() - 1);

		interactWith(getX() - 1, getY()-1);
		interactWith(getX() + 1, getY()-1);
		interactWith(getX() - 1, getY()+1);
		interactWith(getX() + 1, getY()+1);
	}

	@Override
	public void interactWithElement(Element e) {
		if (!(e instanceof Protected)) {
			getWorld().removeElement(e);
		}
	}
}
