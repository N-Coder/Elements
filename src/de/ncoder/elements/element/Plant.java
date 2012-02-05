package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Burnable;
import de.ncoder.elements.engine.Element;

public class Plant extends Element implements Burnable{
	public Plant() {
		setColor(41, 138, 10);
		setGravity(0);
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
		if (e instanceof Water) {
			getWorld().replaceElement(e, new Plant());
		}
	}

	@Override
	public void interactWithSpace(int x, int y) {
		if (random.nextInt(25) == 0) {
			getWorld().setElementAt(x, y, new Plant());
		}
	}

	@Override
	public void burn(Element from) {
		getWorld().replaceElement(this, new Fire());
	}
}
