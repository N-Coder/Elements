package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Burnable;
import de.ncoder.elements.engine.Element;

public class Fire extends Element {
	private int actCount;
	private int maxAct;

	public Fire() {
		setColor(242, 45, 0);
		setFluidity(100);
		setGravity(-1);
		maxAct = random.nextInt(4) + 2;
	}

	@Override
	public void act() {
		if (actCount > maxAct) {
			getWorld().removeElement(this);
		} else {
			super.act();
			if (random.nextInt(10) >= 2) {
				interactWith(getX() + 1, getY());
			}
			if (random.nextInt(10) >= 2) {
				interactWith(getX(), getY() + 1);
			}
			if (random.nextInt(10) >= 2) {
				interactWith(getX() - 1, getY());
			}
			if (random.nextInt(10) >= 2) {
				interactWith(getX(), getY() - 1);
			}
		}
		actCount++;
	}

	@Override
	public void interactWithElement(Element e) {
		if (e instanceof Burnable) {
			((Burnable) e).burn(this);
		}
	}
}
