package de.ncoder.elements.element;

import java.awt.Color;
import java.awt.Graphics2D;

import de.ncoder.elements.element.type.Protected;
import de.ncoder.elements.engine.Element;

public class Spawner extends Element implements Protected {
	private int spawnAfter = 10;
	private Class<? extends Element> spawnType;
	private int spawnTimer = 0;

	public Spawner() {
		setColor(255, 255, 255);
		setGravity(0);
	}

	public Spawner(Class<? extends Element> spawnType) {
		this();
		this.spawnType = spawnType;
	}

	@Override
	public void paint(Graphics2D g, int width, int height) {
		super.paint(g, width, height);
		g.setColor(Color.BLACK);
		if (spawnType != null) {
			try {
				g.setColor(spawnType.newInstance().getColor());
			} catch (Exception e) {
			}
		}
		g.fillRect(1, 1, width - 2, height - 2);
	}

	public void act() {
		super.act();
		if (spawnTimer >= spawnAfter && spawnType != null) {
			try {
				Element spawn = spawnType.newInstance();
				int spawnX = spawn.getSpawnX();
				int spawnY = spawn.getSpawnY();
				if (spawnX == 0 && spawnY == 0) {
					spawnY = 1;
				}
				if (getWorld().getElementAt(getX() + spawnX, getY() + spawnY) == null) {
					getWorld().setElementAt(getX() + spawnX, getY() + spawnY, spawn);
				}
				spawnTimer = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		spawnTimer++;
	}

	public Class<? extends Element> getSpawnType() {
		return spawnType;
	}

	public void setSpawnType(Class<? extends Element> spawnType) {
		this.spawnType = spawnType;
	}
}
