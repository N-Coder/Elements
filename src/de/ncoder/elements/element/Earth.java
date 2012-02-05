package de.ncoder.elements.element;

import de.ncoder.elements.element.type.Meltable;
import de.ncoder.elements.engine.Element;

public class Earth extends Element implements Meltable {
	public Earth() {
		setColor(48,24,0);
		setFluidity(1);
	}

	@Override
	public void melt(Element from) {
		getWorld().replaceElement(this, new Magma());
	}
}