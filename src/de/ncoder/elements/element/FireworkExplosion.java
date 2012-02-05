package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Burnable;
import de.ncoder.elements.element.type.Protected;
import de.ncoder.elements.engine.Element;

public class FireworkExplosion extends Element {
	private int actCount;
	private int maxAct;
	private int xDir;
	private int yDir;

	public FireworkExplosion() {
		this(random.nextInt(5) - 2, random.nextInt(5) - 2);
	}

	public FireworkExplosion(int xDir, int yDir) {
		this.xDir = xDir;
		this.yDir = yDir;
		maxAct = random.nextInt(10)+15;
		setGravity(0);
		setFluidity(0);
		setColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));
	}

	@Override
	public void act() {
		actCount++;
		if (actCount > maxAct || getWorld().touchesBorder(getX(), getY())) {
			getWorld().removeElement(this);
		} else {
			interactWith(getX() + xDir, getY() + yDir);
		}
	}

	@Override
	public void interactWithSpace(int x, int y) {
		getWorld().moveElementTo(x, y, this);
	}

	@Override
	public void interactWithElement(Element e) {
		if (e instanceof FireworkExplosion) {
			getWorld().switchElements(this, e);
		} else if (e instanceof Burnable) {
			((Burnable) e).burn(this);
		} else if(!(e instanceof Protected)){
			getWorld().removeElement(this);
		}
	}
}
