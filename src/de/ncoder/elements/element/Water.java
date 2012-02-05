package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Penetrable;
import de.ncoder.elements.engine.Element;

public class Water extends Element implements Penetrable{
	public Water() {
		setColor(37, 151, 198);
		setFluidity(100);
	}

	@Override
	public void act() {
		interactWith(getX() + 1, getY());
		interactWith(getX(), getY() + 1);
		interactWith(getX() - 1, getY());
		interactWith(getX(), getY() - 1);
	}

	@Override
	public void interactWithElement(Element e) {
		if (e instanceof Magma) {
			if (this.getY() < e.getY()) {
				getWorld().replaceElement(e, new Stone());
				getWorld().replaceElement(this, null);
			} else {
				getWorld().replaceElement(e, null);
				getWorld().replaceElement(this, new Stone());
			}
		} else if (e instanceof Fire) {
			getWorld().replaceElement(e, null);
		}
	}
}
