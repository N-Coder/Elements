package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Protected;
import de.ncoder.elements.engine.Element;

public class Acid extends Element {
	public Acid() {
		setColor(166, 225, 23);
		setFluidity(15);
	}

	@Override
	public void act() {
		super.act();
		interactWith(getX() + 1, getY());
		interactWith(getX() - 1, getY());
		interactWith(getX(), getY() + 1);
		interactWith(getX(), getY() - 1);
	}

	@Override
	public void interactWithElement(Element e) {
		if (e instanceof Earth) {
			getWorld().replaceElement(e, new Acid());
		} else if (!(e instanceof Acid) && !(e instanceof Protected) && !(e instanceof Stone)) {
			getWorld().replaceElement(e, null);
		}
	}
	
	
}
