package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Burnable;
import de.ncoder.elements.element.type.Meltable;
import de.ncoder.elements.engine.Element;

public class Magma extends Element {
	public Magma() {
		setColor(191, 29, 18);
		setFluidity(10);
	}

	@Override
	public void act() {
		super.act();
		switch (random.nextInt(4)) {
		case 0:
			interactWith(getX() + 1, getY());
			break;
		case 1:
			interactWith(getX(), getY() + 1);
			break;
		case 2:
			interactWith(getX() - 1, getY());
			break;
		case 3:
			interactWith(getX(), getY() - 1);
			break;
		}
	}

	@Override
	public void interactWithElement(Element e) {
		if (e instanceof Meltable) {
			((Meltable)e).melt(this);
		} else if (e instanceof Burnable) {
			((Burnable)e).burn(this);
		}
	}
}
