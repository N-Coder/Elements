package de.ncoder.elements.element;

import java.awt.Color;
import java.awt.Graphics2D;

import de.ncoder.elements.engine.Element;

public class Delete extends Element {
	@Override
	public void act() {
		getWorld().removeElement(this);
	}
	
	@Override
	public void paintCursor(Graphics2D g, Element over, int width, int height) {
		paintPreview(g, width, height);
	}
	
	@Override
	public void paintPreview(Graphics2D g, int width, int height) {
		g.setColor(Color.WHITE);
		g.drawLine(0, 0, width - 1, height - 1);
		g.drawLine(0, height - 1, width - 1, 0);
	}
}
